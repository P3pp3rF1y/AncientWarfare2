package net.shadowmage.ancientwarfare.automation.tile.worksite;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraftforge.common.ForgeChunkManager;
import net.shadowmage.ancientwarfare.automation.AncientWarfareAutomation;
import net.shadowmage.ancientwarfare.automation.chunkloader.AWChunkLoader;
import net.shadowmage.ancientwarfare.core.interfaces.IBoundedSite;
import net.shadowmage.ancientwarfare.core.interfaces.IChunkLoaderTile;
import net.shadowmage.ancientwarfare.core.upgrade.WorksiteUpgrade;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.BlockTools;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;

public abstract class TileWorksiteBounded extends TileWorksiteBase implements IBoundedSite, IChunkLoaderTile {

    /**
     * minimum position of the work area bounding box, or a single block position if bbMax is not set
     * must not be null if this block has a work-area
     */
    private BlockPosition bbMin;

    /**
     * maximum position of the work bounding box.  May be null
     */
    private BlockPosition bbMax;

    private ForgeChunkManager.Ticket chunkTicket = null;

    @Override
    public EnumSet<WorksiteUpgrade> getValidUpgrades() {
        return EnumSet.of(
                WorksiteUpgrade.ENCHANTED_TOOLS_1,
                WorksiteUpgrade.ENCHANTED_TOOLS_2,
                WorksiteUpgrade.SIZE_MEDIUM,
                WorksiteUpgrade.SIZE_LARGE,
                WorksiteUpgrade.TOOL_QUALITY_1,
                WorksiteUpgrade.TOOL_QUALITY_2,
                WorksiteUpgrade.TOOL_QUALITY_3,
                WorksiteUpgrade.BASIC_CHUNK_LOADER
        );
    }

    @Override
    public final void onBlockBroken() {
        super.onBlockBroken();
        if (this.chunkTicket != null) {
            ForgeChunkManager.releaseTicket(chunkTicket);
            this.chunkTicket = null;
        }
    }

    @Override
    public void addUpgrade(WorksiteUpgrade upgrade) {
        super.addUpgrade(upgrade);
        if (upgrade == WorksiteUpgrade.BASIC_CHUNK_LOADER || upgrade == WorksiteUpgrade.QUARRY_CHUNK_LOADER) {
            setupInitialTicket();//setup chunkloading for the worksite
        }
    }

    @Override
    public final void removeUpgrade(WorksiteUpgrade upgrade) {
        super.removeUpgrade(upgrade);
        if (upgrade == WorksiteUpgrade.BASIC_CHUNK_LOADER || upgrade == WorksiteUpgrade.QUARRY_CHUNK_LOADER) {
            setTicket(null);//release any existing ticket
        }
    }

    @Override
    public final boolean hasWorkBounds() {
        return getWorkBoundsMin() != null && getWorkBoundsMax() != null;
    }

    @Override
    public final BlockPosition getWorkBoundsMin() {
        return bbMin;
    }

    @Override
    public final BlockPosition getWorkBoundsMax() {
        return bbMax;
    }

    @Override
    public final void setBounds(BlockPosition min, BlockPosition max) {
        setWorkBoundsMin(BlockTools.getMin(min, max));
        setWorkBoundsMax(BlockTools.getMax(min, max));
        onBoundsSet();
    }

    @Override
    public int getBoundsMaxWidth() {
        return getUpgrades().contains(WorksiteUpgrade.SIZE_LARGE) ? 16 : getUpgrades().contains(WorksiteUpgrade.SIZE_MEDIUM) ? 9 : 5;
    }

    @Override
    public int getBoundsMaxHeight() {
        return 1;
    }

    @Override
    public final void setTicket(ForgeChunkManager.Ticket tk) {
        if (chunkTicket != null) {
            ForgeChunkManager.releaseTicket(chunkTicket);
            chunkTicket = null;
        }
        this.chunkTicket = tk;
        if (this.chunkTicket == null) {
            return;
        }
        AWChunkLoader.INSTANCE.writeDataToTicket(chunkTicket, xCoord, yCoord, zCoord);
        ChunkCoordIntPair ccip = new ChunkCoordIntPair(xCoord >> 4, zCoord >> 4);
        ForgeChunkManager.forceChunk(chunkTicket, ccip);
        if (hasWorkBounds()) {
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

    public final void setupInitialTicket() {
        if (chunkTicket != null) {
            ForgeChunkManager.releaseTicket(chunkTicket);
        }
        if (getUpgrades().contains(WorksiteUpgrade.BASIC_CHUNK_LOADER) || getUpgrades().contains(WorksiteUpgrade.QUARRY_CHUNK_LOADER)) {
            setTicket(ForgeChunkManager.requestTicket(AncientWarfareAutomation.instance, worldObj, ForgeChunkManager.Type.NORMAL));
        }
    }

    /**
     * Used by user-set-blocks tile to set all default harvest-checks to true when bounds are FIRST set
     */
    protected void onBoundsSet() {

    }

    @Override
    public void onBoundsAdjusted() {
        //TODO implement to check target blocks, clear invalid ones
    }

    @Override
    public void onPostBoundsAdjusted() {
        setupInitialTicket();
    }

    public boolean isInBounds(BlockPosition pos) {
        return pos.x >= bbMin.x && pos.x <= bbMax.x && pos.z >= bbMin.z && pos.z <= bbMax.z;
    }

    protected void validateCollection(Collection<BlockPosition> blocks) {
        if(!hasWorkBounds()){
            blocks.clear();
            return;
        }
        Iterator<BlockPosition> it = blocks.iterator();
        BlockPosition pos;
        while (it.hasNext() && (pos = it.next()) != null) {
            if (!isInBounds(pos)) {
                it.remove();
            }
        }
    }

    @Override
    public final void setWorkBoundsMin(BlockPosition min) {
        if(min != bbMin){
            bbMin = min;
            markDirty();
        }
    }

    @Override
    public final void setWorkBoundsMax(BlockPosition max) {
        if(max != bbMax){
            bbMax = max;
            markDirty();
        }
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        if (hasWorkBounds()) {
            AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1);
            BlockPosition min = getWorkBoundsMin();
            bb.minX = min.x < bb.minX ? min.x : bb.minX;
            bb.minY = min.y < bb.minY ? min.y : bb.minY;
            bb.minZ = min.z < bb.minZ ? min.z : bb.minZ;
            BlockPosition max = getWorkBoundsMax();
            bb.maxX = max.x + 1 > bb.maxX ? max.x + 1 : bb.maxX;
            bb.maxY = max.y + 1 > bb.maxY ? max.y + 1 : bb.maxY;
            bb.maxZ = max.z + 1 > bb.maxZ ? max.z + 1 : bb.maxZ;
            return bb;
        }
        return super.getRenderBoundingBox();
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        if (tag.hasKey("bbMin")) {
            bbMin = new BlockPosition(tag.getCompoundTag("bbMin"));
        }
        if (tag.hasKey("bbMax")) {
            bbMax = new BlockPosition(tag.getCompoundTag("bbMax"));
        }
        if (bbMax == null) {
            setWorkBoundsMax(new BlockPosition(xCoord, yCoord, zCoord + 1));
        }
        if (bbMin == null) {
            setWorkBoundsMin(new BlockPosition(xCoord, yCoord, zCoord + 1));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        if (bbMin != null) {
            tag.setTag("bbMin", bbMin.writeToNBT(new NBTTagCompound()));
        }
        if (bbMax != null) {
            tag.setTag("bbMax", bbMax.writeToNBT(new NBTTagCompound()));
        }
    }

    @Override
    public NBTTagCompound getDescriptionPacketTag(NBTTagCompound tag) {
        super.getDescriptionPacketTag(tag);
        if (bbMin != null) {
            tag.setTag("bbMin", bbMin.writeToNBT(new NBTTagCompound()));
        }
        if (bbMax != null) {
            tag.setTag("bbMax", bbMax.writeToNBT(new NBTTagCompound()));
        }
        return tag;
    }

    @Override
    public final void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
        NBTTagCompound tag = pkt.func_148857_g();
        if (tag.hasKey("bbMin")) {
            bbMin = new BlockPosition(tag.getCompoundTag("bbMin"));
        }
        if (tag.hasKey("bbMax")) {
            bbMax = new BlockPosition(tag.getCompoundTag("bbMax"));
        }
    }

}
