package net.shadowmage.ancientwarfare.structure.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.api.ModuleStatus;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;
import net.shadowmage.ancientwarfare.core.interfaces.IOwnable;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite;
import net.shadowmage.ancientwarfare.core.interfaces.IWorker;
import net.shadowmage.ancientwarfare.core.tile.TileUpdatable;
import net.shadowmage.ancientwarfare.core.upgrade.WorksiteUpgrade;
import net.shadowmage.ancientwarfare.structure.block.AWStructuresBlocks;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBuilderTicked;

import javax.annotation.Nonnull;
import java.util.EnumSet;
import java.util.UUID;

public class TileStructureBuilder extends TileUpdatable implements IWorkSite, IOwnable, ITickable {

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
            bb.expand(clientBB.min.getX() - pos.getX(), clientBB.min.getY() - pos.getY(), clientBB.min.getZ() - pos.getZ());
            bb.expand(clientBB.max.getX() - pos.getX(), clientBB.max.getY() - pos.getY(), clientBB.max.getZ() - pos.getZ());
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
    public float getClientOutputRotation(EnumFacing from, float delta) {
        return 0;
    }

    @Override
    public boolean useOutputRotation(EnumFacing from) {
        return false;
    }

    @Override
    public double addTorque(EnumFacing from, double energy) {
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
    public double getMaxTorque(EnumFacing from) {
        return maxEnergyStored;
    }

    @Override
    public double getTorqueStored(EnumFacing from) {
        return storedEnergy;
    }

    @Override
    public double getMaxTorqueInput(EnumFacing from) {
        return maxInput;
    }

    @Override
    public boolean canInputTorque(EnumFacing from) {
        return true;
    }

    @Override
    public void update() {
        if (!hasWorld() || world.isRemote) {
            return;
        }
        if (shouldRemove || builder == null || builder.invalid || builder.isFinished()) {
            shouldRemove = true;
            world.setBlockToAir(pos);
            return;
        }
        if (builder.getWorld() == null) {
            builder.setWorld(world);
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

    /*
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
        return getOwnerAsPlayer().getName();
    }
    
    @Override
    public UUID getOwnerUuid() {
        return owningPlayer;
    }

    public final EntityPlayer getOwnerAsPlayer() {
        if(owner==null || !owner.isEntityAlive() || (owner instanceof FakePlayer)) { //TODO this condition needs looking into - no idea why owner needs to be set everytime
            owner = AncientWarfareCore.proxy.getFakePlayer(this.getWorld(), null, owningPlayer);
        }
        return owner;
    }

    /*
     * should be called immediately after the tile-entity is set into the world
     * from the ItemBlockStructureBuilder item onBlockPlaced code<br>
     * the passed in builder must be valid (have a valid structure), and must not
     * be null
     */
    public void setBuilder(StructureBuilderTicked builder) {
        this.builder = builder;
    }

    public void onBlockBroken() {
        if (!world.isRemote && !isStarted && builder != null && builder.getTemplate() != null) {
            isStarted = true;//to prevent further drops
            @Nonnull ItemStack item = new ItemStack(AWStructuresBlocks.builderBlock);
            item.setTagInfo("structureName", new NBTTagString(builder.getTemplate().name));
        }
    }

    public void onBlockClicked(EntityPlayer player) {
        int pass = builder.getPass() + 1;
        int max = builder.getMaxPasses();
        float percent = builder.getPercentDoneWithPass() * 100.f;
        String perc = String.format("%.2f", percent)+"%";
        player.sendMessage(new TextComponentTranslation("guistrings.structure.builder.state", perc, pass, max));
    }

    @Override
    protected void writeUpdateNBT(NBTTagCompound tag) {
        super.writeUpdateNBT(tag);
        StructureBB bb = builder.getBoundingBox();
        if (bb != null) {
            tag.setLong("bbMin", bb.min.toLong());
            tag.setLong("bbMax", bb.max.toLong());
        }
    }

    @Override
    protected void handleUpdateNBT(NBTTagCompound tag) {
        super.handleUpdateNBT(tag);
        if (tag.hasKey("bbMin") && tag.hasKey("bbMax")) {
            clientBB = new StructureBB(BlockPos.fromLong(tag.getLong("bbMin")), BlockPos.fromLong(tag.getLong("bbMax")));
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
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
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
        return tag;
    }

    //******************************************WORKSITE************************************************//
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
            world.getScoreboard().getPlayersTeam(getOwnerName());
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
    public double getMaxTorqueOutput(EnumFacing from) {
        return 0;
    }

    @Override
    public boolean canOutputTorque(EnumFacing towards) {
        return false;
    }

    @Override
    public double drainTorque(EnumFacing from, double energy) {
        return 0;
    }

}
