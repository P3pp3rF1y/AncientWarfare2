package net.shadowmage.ancientwarfare.structure.tile;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.scoreboard.Team;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.api.AWBlocks;
import net.shadowmage.ancientwarfare.core.api.ModuleStatus;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;
import net.shadowmage.ancientwarfare.core.interfaces.IOwnable;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite;
import net.shadowmage.ancientwarfare.core.interfaces.IWorker;
import net.shadowmage.ancientwarfare.core.upgrade.WorksiteUpgrade;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBuilderTicked;

import java.util.EnumSet;
import java.util.UUID;

public class TileStructureBuilder extends TileEntity implements IWorkSite, IOwnable {

    protected UUID owningPlayer;
    private EntityPlayer owner;
    private String ownerName;

    StructureBuilderTicked builder;
    private boolean shouldRemove = false;
    public boolean isStarted = false;
    int workDelay = 20;

    double maxEnergyStored = 150;
    double maxInput = 50;
    private double storedEnergy;
    public StructureBB clientBB;

    public TileStructureBuilder() {
        maxEnergyStored = AWCoreStatics.energyPerWorkUnit * 3;
        maxInput = AWCoreStatics.energyPerWorkUnit;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        AxisAlignedBB bb = super.getRenderBoundingBox();
        if (clientBB != null) {
            bb.addCoord(clientBB.min.x - xCoord, clientBB.min.y - yCoord, clientBB.min.z - zCoord);
            bb.addCoord(clientBB.max.x - xCoord, clientBB.max.y - yCoord, clientBB.max.z - zCoord);
        }
        return bb;
    }

    @Override
    public EnumSet<WorksiteUpgrade> getUpgrades() {
        return EnumSet.noneOf(WorksiteUpgrade.class);
    }//NOOP

    @Override
    public EnumSet<WorksiteUpgrade> getValidUpgrades() {
        return EnumSet.noneOf(WorksiteUpgrade.class);
    }//NOOP

    @Override
    public void addUpgrade(WorksiteUpgrade upgrade) {
    }//NOOP

    @Override
    public void removeUpgrade(WorksiteUpgrade upgrade) {
    }//NOOP

    @Override
    public float getClientOutputRotation(ForgeDirection from, float delta) {
        return 0;
    }

    @Override
    public boolean useOutputRotation(ForgeDirection from) {
        return false;
    }

    @Override
    public double addTorque(ForgeDirection from, double energy) {
        if (canInputTorque(from)) {
            if (energy + getTorqueStored(null) > getMaxTorque(null)) {
                energy = getMaxTorque(null) - getTorqueStored(null);
            }
            if (energy > getMaxTorqueInput(null)) {
                energy = getMaxTorqueInput(null);
            }
            storedEnergy += energy;
            return energy;
        }
        return 0;
    }

    @Override
    public double getMaxTorque(ForgeDirection from) {
        return maxEnergyStored;
    }

    @Override
    public double getTorqueStored(ForgeDirection from) {
        return storedEnergy;
    }

    @Override
    public double getMaxTorqueInput(ForgeDirection from) {
        return maxInput;
    }

    @Override
    public boolean canInputTorque(ForgeDirection from) {
        return true;
    }

    @Override
    public void updateEntity() {
        if (!hasWorldObj() || worldObj.isRemote) {
            return;
        }
        if (shouldRemove || builder == null || builder.invalid || builder.isFinished()) {
            shouldRemove = true;
            worldObj.setBlockToAir(xCoord, yCoord, zCoord);
            return;
        }
        if (builder.getWorld() == null) {
            builder.setWorld(worldObj);
        }
        if (ModuleStatus.automationLoaded || ModuleStatus.npcsLoaded) {
            if (storedEnergy >= AWCoreStatics.energyPerWorkUnit) {
                storedEnergy -= AWCoreStatics.energyPerWorkUnit;
                processWork();
            }
        } else {
            if (workDelay-- <= 0) {
                processWork();
                workDelay = 20;
            }
        }
    }

    public void processWork() {
        isStarted = true;
        builder.tick(getOwnerAsPlayer());
    }

    /**
     * should be called immediately after the tile-entity is set into the world
     * from the ItemBlockStructureBuilder item onBlockPlaced code
     */
    @Override
    public void setOwner(EntityPlayer player) {
        this.owningPlayer = player.getUniqueID();
    }
    
    @Override
    public void setOwner(String ownerName, UUID ownerUuid) {
        this.ownerName = ownerName;
        this.owningPlayer = ownerUuid;
    }

    @Override
    public boolean isOwner(EntityPlayer player){
        return this.owningPlayer == null || this.owningPlayer.equals(player.getUniqueID());
    }

    @Override
    public String getOwnerName(){
        return getOwnerAsPlayer().getCommandSenderName();
    }
    
    @Override
    public UUID getOwnerUuid() {
        return owningPlayer;
    }

    public final EntityPlayer getOwnerAsPlayer() {
        if(owner==null || !owner.isEntityAlive() || owner.isEntityInvulnerable()) {
            owner = AncientWarfareCore.proxy.getFakePlayer(this.getWorldObj(), null, owningPlayer);
        }
        return owner;
    }

    /**
     * should be called immediately after the tile-entity is set into the world
     * from the ItemBlockStructureBuilder item onBlockPlaced code<br>
     * the passed in builder must be valid (have a valid structure), and must not
     * be null
     */
    public void setBuilder(StructureBuilderTicked builder) {
        this.builder = builder;
    }

    public void onBlockBroken() {
        if (!worldObj.isRemote && !isStarted && builder != null && builder.getTemplate() != null) {
            isStarted = true;//to prevent further drops
            ItemStack item = new ItemStack(AWBlocks.builderBlock);
            item.setTagInfo("structureName", new NBTTagString(builder.getTemplate().name));
        }
    }

    public void onBlockClicked(EntityPlayer player) {
        int pass = builder.getPass() + 1;
        int max = builder.getMaxPasses();
        float percent = builder.getPercentDoneWithPass() * 100.f;
        String perc = String.format("%.2f", percent)+"%";
        player.addChatMessage(new ChatComponentTranslation("guistrings.structure.builder.state", perc, pass, max));
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        StructureBB bb = builder.getBoundingBox();
        if (bb != null) {
            tag.setTag("bbMin", bb.min.writeToNBT(new NBTTagCompound()));
            tag.setTag("bbMax", bb.max.writeToNBT(new NBTTagCompound()));
        }
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
        NBTTagCompound tag = pkt.func_148857_g();
        if (tag.hasKey("bbMin") && tag.hasKey("bbMax")) {
            clientBB = new StructureBB(new BlockPosition(tag.getCompoundTag("bbMin")), new BlockPosition(tag.getCompoundTag("bbMax")));
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        if (tag.hasKey("builder")) {
            builder = new StructureBuilderTicked();
            builder.readFromNBT(tag.getCompoundTag("builder"));
        } else {
            this.shouldRemove = true;
        }
        this.isStarted = tag.getBoolean("started");
        this.storedEnergy = tag.getDouble("storedEnergy");
        if(tag.hasKey("ownerId")){
            this.owningPlayer = UUID.fromString(tag.getString("ownerId"));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        if (builder != null) {
            NBTTagCompound builderTag = new NBTTagCompound();
            builder.writeToNBT(builderTag);
            tag.setTag("builder", builderTag);
        }
        tag.setBoolean("started", isStarted);
        tag.setDouble("storedEnergy", storedEnergy);
        if(owningPlayer!=null){
            tag.setString("ownerId", owningPlayer.toString());
        }
    }

    //*******************************************WORKSITE************************************************//
    @Override
    public boolean hasWork() {
        return storedEnergy < maxEnergyStored;
    }

    @Override
    public WorkType getWorkType() {
        return WorkType.CRAFTING;
    }

    @Override
    public final Team getTeam() {
        if (owningPlayer != null) {
            worldObj.getScoreboard().getPlayersTeam(getOwnerName());
        }
        return null;
    }

    @Override
    public void addEnergyFromWorker(IWorker worker) {
        storedEnergy += AWCoreStatics.energyPerWorkUnit * worker.getWorkEffectiveness(getWorkType());
        if (storedEnergy > getMaxTorque(null)) {
            storedEnergy = getMaxTorque(null);
        }
    }

    @Override
    public void addEnergyFromPlayer(EntityPlayer player) {
        storedEnergy += AWCoreStatics.energyPerWorkUnit;
        if (storedEnergy > getMaxTorque(null)) {
            storedEnergy = getMaxTorque(null);
        }
    }

    @Override
    public double getMaxTorqueOutput(ForgeDirection from) {
        return 0;
    }

    @Override
    public boolean canOutputTorque(ForgeDirection towards) {
        return false;
    }

    @Override
    public double drainTorque(ForgeDirection from, double energy) {
        return 0;
    }

}
