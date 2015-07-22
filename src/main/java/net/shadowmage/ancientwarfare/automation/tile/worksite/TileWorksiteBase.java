package net.shadowmage.ancientwarfare.automation.tile.worksite;

import cofh.api.energy.IEnergyHandler;
import cpw.mods.fml.common.Optional;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.scoreboard.Team;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.AncientWarfareAutomation;
import net.shadowmage.ancientwarfare.automation.chunkloader.AWChunkLoader;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.automation.item.ItemWorksiteUpgrade;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.IRotatableTile;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;
import net.shadowmage.ancientwarfare.core.interfaces.*;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueTile;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.TorqueCell;
import net.shadowmage.ancientwarfare.core.upgrade.WorksiteUpgrade;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

import java.util.EnumSet;

@Optional.Interface(iface = "cofh.api.energy.IEnergyHandler", modid = "CoFHCore", striprefs = true)
public abstract class TileWorksiteBase extends TileEntity implements IWorkSite, IInteractableTile, IOwnable, ITorqueTile, IRotatableTile, IEnergyHandler, IChunkLoaderTile {

    private String owningPlayer = "";

    private EntityPlayer owner;

    private double efficiencyBonusFactor = 0.f;

    private EnumSet<WorksiteUpgrade> upgrades = EnumSet.noneOf(WorksiteUpgrade.class);

    private ForgeDirection orientation = ForgeDirection.NORTH;

    private TorqueCell torqueCell;

    private int workRetryDelay = 20;

    private Ticket chunkTicket = null;

    public TileWorksiteBase() {
        torqueCell = new TorqueCell(32, 0, AWCoreStatics.energyPerWorkUnit * 3, 1);
    }

    //*************************************** COFH RF METHODS ***************************************//
    @Optional.Method(modid = "CoFHCore")
    @Override
    public final int getEnergyStored(ForgeDirection from) {
        return (int) (getTorqueStored(from) * AWAutomationStatics.torqueToRf);
    }

    @Optional.Method(modid = "CoFHCore")
    @Override
    public final int getMaxEnergyStored(ForgeDirection from) {
        return (int) (getMaxTorque(from) * AWAutomationStatics.torqueToRf);
    }

    @Optional.Method(modid = "CoFHCore")
    @Override
    public final boolean canConnectEnergy(ForgeDirection from) {
        return canOutputTorque(from) || canInputTorque(from);
    }

    @Optional.Method(modid = "CoFHCore")
    @Override
    public final int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
        return 0;
    }

    @Optional.Method(modid = "CoFHCore")
    @Override
    public final int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
        if (!canInputTorque(from)) {
            return 0;
        }
        if (simulate) {
            return Math.min(maxReceive, (int) (AWAutomationStatics.torqueToRf * getMaxTorqueInput(from)));
        }
        return (int) (AWAutomationStatics.torqueToRf * addTorque(from, (double) maxReceive * AWAutomationStatics.rfToTorque));
    }
//*************************************** UPGRADE HANDLING METHODS ***************************************//

    @Override
    public final EnumSet<WorksiteUpgrade> getUpgrades() {
        return upgrades;
    }

    @Override
    public EnumSet<WorksiteUpgrade> getValidUpgrades() {
        return EnumSet.of(
                WorksiteUpgrade.ENCHANTED_TOOLS_1,
                WorksiteUpgrade.ENCHANTED_TOOLS_2,
                WorksiteUpgrade.TOOL_QUALITY_1,
                WorksiteUpgrade.TOOL_QUALITY_2,
                WorksiteUpgrade.TOOL_QUALITY_3
        );
    }

    @Override
    public void onBlockBroken() {
        for (WorksiteUpgrade ug : this.upgrades) {
            InventoryTools.dropItemInWorld(worldObj, ItemWorksiteUpgrade.getStack(ug), xCoord, yCoord, zCoord);
        }
        efficiencyBonusFactor = 0;
        upgrades.clear();
        if (this.chunkTicket != null) {
            ForgeChunkManager.releaseTicket(chunkTicket);
            this.chunkTicket = null;
        }
    }

    @Override
    public void addUpgrade(WorksiteUpgrade upgrade) {
        upgrades.add(upgrade);
        updateEfficiency();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        markDirty();
        if (upgrade == WorksiteUpgrade.BASIC_CHUNK_LOADER || upgrade == WorksiteUpgrade.QUARRY_CHUNK_LOADER) {
            setupInitialTicket();//setup chunkloading for the worksite
        }
    }

    @Override
    public final void removeUpgrade(WorksiteUpgrade upgrade) {
        upgrades.remove(upgrade);
        updateEfficiency();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        markDirty();
        if (upgrade == WorksiteUpgrade.BASIC_CHUNK_LOADER || upgrade == WorksiteUpgrade.QUARRY_CHUNK_LOADER) {
            setTicket(null);//release any existing ticket
        }
    }

    public int getFortune() {
        return getUpgrades().contains(WorksiteUpgrade.ENCHANTED_TOOLS_2) ? 2 : getUpgrades().contains(WorksiteUpgrade.ENCHANTED_TOOLS_1) ? 1 : 0;
    }

//*************************************** TILE UPDATE METHODS ***************************************//

    protected abstract boolean processWork();

    protected abstract boolean hasWorksiteWork();

    protected abstract void updateWorksite();

    @Override
    public final boolean canUpdate() {
        return true;
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (worldObj.isRemote) {
            return;
        }
        worldObj.theProfiler.startSection("AWWorksite");
        if (workRetryDelay > 0) {
            workRetryDelay--;
        } else {
            worldObj.theProfiler.endStartSection("Check For Work");
            double ePerUse = IWorkSite.WorksiteImplementation.getEnergyPerActivation(efficiencyBonusFactor);
            boolean hasWork = getTorqueStored(ForgeDirection.UNKNOWN) >= ePerUse && hasWorksiteWork();
            worldObj.theProfiler.endStartSection("Process Work");
            if (hasWork) {
                if (processWork()) {
                    torqueCell.setEnergy(torqueCell.getEnergy() - ePerUse);
                } else {
                    workRetryDelay = 20;
                }
            }
        }
        worldObj.theProfiler.endStartSection("WorksiteBaseUpdate");
        updateWorksite();
        worldObj.theProfiler.endSection();
        worldObj.theProfiler.endSection();
    }

    protected final void updateEfficiency() {
        efficiencyBonusFactor = IWorkSite.WorksiteImplementation.getEfficiencyFactor(upgrades);
    }

//*************************************** TILE INTERACTION METHODS ***************************************//

    @Override
    public final Team getTeam() {
        if (owningPlayer != null) {
            return worldObj.getScoreboard().getPlayersTeam(owningPlayer);
        }
        return null;
    }

    @Override
    public final String getOwnerName() {
        return owningPlayer;
    }

    public final EntityPlayer getOwnerAsPlayer() {
        if(owner==null || !owner.isEntityAlive() || owner.isEntityInvulnerable()) {
            owner = AncientWarfareCore.proxy.getFakePlayer(this.getWorldObj(), owningPlayer);
        }
        return owner;
    }

    @Override
    public final void setOwnerName(String name) {
        if (name == null) {
            name = "";
        }
        this.owningPlayer = name;
    }

//*************************************** TORQUE INTERACTION METHODS ***************************************//

    @Override
    public final float getClientOutputRotation(ForgeDirection from, float delta) {
        return 0;
    }

    @Override
    public final boolean useOutputRotation(ForgeDirection from) {
        return false;
    }

    @Override
    public final double getMaxTorqueOutput(ForgeDirection from) {
        return 0;
    }

    @Override
    public final boolean canOutputTorque(ForgeDirection towards) {
        return false;
    }

    @Override
    public final double drainTorque(ForgeDirection from, double energy) {
        return 0;
    }

    @Override
    public final void addEnergyFromWorker(IWorker worker) {
        addTorque(ForgeDirection.UNKNOWN, AWCoreStatics.energyPerWorkUnit * worker.getWorkEffectiveness(getWorkType()) * AWAutomationStatics.hand_cranked_generator_output);
    }

    @Override
    public final void addEnergyFromPlayer(EntityPlayer player) {
        addTorque(ForgeDirection.UNKNOWN, AWCoreStatics.energyPerWorkUnit * AWAutomationStatics.hand_cranked_generator_output);
    }

    @Override
    public final double addTorque(ForgeDirection from, double energy) {
        return torqueCell.addEnergy(energy);
    }

    @Override
    public final double getMaxTorque(ForgeDirection from) {
        return torqueCell.getMaxEnergy();
    }

    @Override
    public final double getTorqueStored(ForgeDirection from) {
        return torqueCell.getEnergy();
    }

    @Override
    public final double getMaxTorqueInput(ForgeDirection from) {
        return torqueCell.getMaxTickInput();
    }

    @Override
    public final boolean canInputTorque(ForgeDirection from) {
        return true;
    }

//*************************************** MISC METHODS ***************************************//

    @Override
    public void setTicket(Ticket tk) {
        if (chunkTicket != null) {
            ForgeChunkManager.releaseTicket(chunkTicket);
            chunkTicket = null;
        }
        this.chunkTicket = tk;
        if (this.chunkTicket == null) {
            return;
        }
        writeDataToTicket(chunkTicket);
        ChunkCoordIntPair ccip = new ChunkCoordIntPair(xCoord >> 4, zCoord >> 4);
        ForgeChunkManager.forceChunk(chunkTicket, ccip);
        if (this.hasWorkBounds()) {
            int minX = getWorkBoundsMin().x >> 4;
            int minZ = getWorkBoundsMin().z >> 4;
            int maxX = getWorkBoundsMax().x >> 4;
            int maxZ = getWorkBoundsMax().z >> 4;
            for (int x = minX; x <= maxX; x++) {
                for (int z = minZ; z <= maxZ; z++) {
                    ccip = new ChunkCoordIntPair(x, z);
                    ForgeChunkManager.forceChunk(chunkTicket, ccip);
                }
            }
        }
    }

    protected final void writeDataToTicket(Ticket tk) {
        AWChunkLoader.INSTANCE.writeDataToTicket(tk, xCoord, yCoord, zCoord);
    }

    public final void setupInitialTicket() {
        if (chunkTicket != null) {
            ForgeChunkManager.releaseTicket(chunkTicket);
        }
        if (getUpgrades().contains(WorksiteUpgrade.BASIC_CHUNK_LOADER) || getUpgrades().contains(WorksiteUpgrade.QUARRY_CHUNK_LOADER)) {
            setTicket(ForgeChunkManager.requestTicket(AncientWarfareAutomation.instance, worldObj, Type.NORMAL));
        }
    }

    @Override
    public void onPostBoundsAdjusted() {
        setupInitialTicket();
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return pass == 1;
    }

    @Override
    public String toString() {
        return "Worksite Base[" + torqueCell.getEnergy() + "]";
    }

    @Override
    public boolean hasWork() {
        return torqueCell.getEnergy() < torqueCell.getMaxEnergy() && worldObj.getBlockPowerInput(xCoord, yCoord, zCoord) == 0;
    }

    @Override
    public final ForgeDirection getPrimaryFacing() {
        return orientation;
    }

    @Override
    public final void setPrimaryFacing(ForgeDirection face) {
        orientation = face;
        this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        this.worldObj.func_147453_f(xCoord, yCoord, zCoord, getBlockType());//notify neighbors of tile change
    }

//*************************************** NBT AND PACKET DATA METHODS ***************************************//

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setDouble("storedEnergy", torqueCell.getEnergy());
        if (owningPlayer != null) {
            tag.setString("owner", owningPlayer);
        }
        if (!getUpgrades().isEmpty()) {
            int[] ug = new int[getUpgrades().size()];
            int i = 0;
            for (WorksiteUpgrade u : getUpgrades()) {
                ug[i] = u.ordinal();
                i++;
            }
            tag.setIntArray("upgrades", ug);
        }
        tag.setInteger("orientation", orientation.ordinal());
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        torqueCell.setEnergy(tag.getDouble("storedEnergy"));
        if (tag.hasKey("owner")) {
            setOwnerName(tag.getString("owner"));
        }
        if (tag.hasKey("upgrades")) {
            NBTBase upgradeTag = tag.getTag("upgrades");
            if (upgradeTag instanceof NBTTagIntArray) {
                int[] ug = tag.getIntArray("upgrades");
                for (int anUg : ug) {
                    upgrades.add(WorksiteUpgrade.values()[anUg]);
                }
            } else if (upgradeTag instanceof NBTTagList)//template parser reads int-arrays as a tag list for some reason
            {
                NBTTagList list = (NBTTagList) upgradeTag;
                for (int i = 0; i < list.tagCount(); i++) {
                    String st = list.getStringTagAt(i);
                    try {
                        int ug = Integer.parseInt(st);
                        upgrades.add(WorksiteUpgrade.values()[ug]);
                    } catch (NumberFormatException e) {
                    }
                }
            }
        }

        if (tag.hasKey("orientation")) {
            orientation = ForgeDirection.values()[tag.getInteger("orientation")];
        }
        updateEfficiency();
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        if (hasWorkBounds() && getWorkBoundsMin() != null && getWorkBoundsMax() != null) {
            AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1);
            BlockPosition min = getWorkBoundsMin();
            BlockPosition max = getWorkBoundsMax();
            bb.minX = min.x < bb.minX ? min.x : bb.minX;
            bb.minY = min.y < bb.minY ? min.y : bb.minY;
            bb.minZ = min.z < bb.minZ ? min.z : bb.minZ;
            bb.maxX = max.x + 1 > bb.maxX ? max.x + 1 : bb.maxX;
            bb.maxY = max.y + 1 > bb.maxY ? max.y + 1 : bb.maxY;
            bb.maxZ = max.z + 1 > bb.maxZ ? max.z + 1 : bb.maxZ;
            return bb;
        }
        return super.getRenderBoundingBox();
    }

    protected NBTTagCompound getDescriptionPacketTag(NBTTagCompound tag) {
        int[] ugs = new int[upgrades.size()];
        int i = 0;
        for (WorksiteUpgrade ug : upgrades) {
            ugs[i] = ug.ordinal();
            i++;
        }
        tag.setIntArray("upgrades", ugs);
        tag.setInteger("orientation", orientation.ordinal());
        return tag;
    }

    @Override
    public final Packet getDescriptionPacket() {
        NBTTagCompound tag = getDescriptionPacketTag(new NBTTagCompound());
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 0, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
        upgrades.clear();
        if (pkt.func_148857_g().hasKey("upgrades")) {
            int[] ugs = pkt.func_148857_g().getIntArray("upgrades");
            for (int ug : ugs) {
                upgrades.add(WorksiteUpgrade.values()[ug]);
            }
        }
        orientation = ForgeDirection.values()[pkt.func_148857_g().getInteger("orientation")];
        this.worldObj.func_147453_f(xCoord, yCoord, zCoord, getBlockType());
    }

}
