package net.shadowmage.ancientwarfare.automation.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.automation.container.ContainerChunkLoaderDeluxe;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TileChunkLoaderDeluxe extends TileChunkLoaderSimple {

    private final Set<ChunkCoordIntPair> ccipSet = new HashSet<ChunkCoordIntPair>();

    private final List<ContainerChunkLoaderDeluxe> viewers = new ArrayList<ContainerChunkLoaderDeluxe>();

    public TileChunkLoaderDeluxe() {

    }

    @Override
    public boolean onBlockClicked(EntityPlayer player) {
        if (!player.worldObj.isRemote) {
            NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_CHUNK_LOADER_DELUXE, xCoord, yCoord, zCoord);
        }
        return true;
    }

    public void addViewer(ContainerChunkLoaderDeluxe viewer) {
        viewers.add(viewer);
    }

    public void removeViewer(ContainerChunkLoaderDeluxe viewer) {
        viewers.remove(viewer);
    }

    public void addOrRemoveChunk(ChunkCoordIntPair ccip) {
        if (ccipSet.contains(ccip)) {
            ccipSet.remove(ccip);
            ForgeChunkManager.unforceChunk(chunkTicket, ccip);
        } else {
            ccipSet.add(ccip);
            ForgeChunkManager.forceChunk(chunkTicket, ccip);
        }
        informViewers();
    }

    private void informViewers() {
        for (ContainerChunkLoaderDeluxe viewer : viewers) {
            viewer.onChunkLoaderSetUpdated(ccipSet);
        }
    }

    public void setChunkSet(Set<ChunkCoordIntPair> ccips) {
        if (worldObj == null || worldObj.isRemote) {
            return;
        }
        Set<ChunkCoordIntPair> set = new HashSet<ChunkCoordIntPair>();
        set.addAll(ccipSet);
        set.removeAll(ccips);//at this point, set is the list of chunks that should be unforced
        ccipSet.removeAll(set);//remove them from the set, and unforce
        for (ChunkCoordIntPair ccip : set) {
            ForgeChunkManager.unforceChunk(chunkTicket, ccip);
        }
        set.clear();
        set.addAll(ccips);
        set.removeAll(ccipSet);//at this point, -set- contains all _new_ chunks...force them
        ccipSet.addAll(ccips);//also, go ahead and merge the sets
        for (ChunkCoordIntPair ccip : set) {
            ForgeChunkManager.forceChunk(chunkTicket, ccip);
        }
        informViewers();
    }

    public Set<ChunkCoordIntPair> getForcedChunks() {
        return ccipSet;
    }

    @Override
    protected void forceTicketChunks() {
        int cx = xCoord >> 4;
        int cz = zCoord >> 4;
        ChunkCoordIntPair ccip1 = new ChunkCoordIntPair(cx, cz);
        ForgeChunkManager.forceChunk(this.chunkTicket, ccip1);
        for (ChunkCoordIntPair ccip : ccipSet) {
            ForgeChunkManager.forceChunk(this.chunkTicket, ccip);
        }
//  AWLog.logDebug("ticket now has chunks: "+tk.getChunkList());
//  AWLog.logDebug("total forced chunks are: "+ForgeChunkManager.getPersistentChunksFor(worldObj));
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        NBTTagList list = tag.getTagList("chunkList", Constants.NBT.TAG_COMPOUND);
        NBTTagCompound ccipTag;
        ChunkCoordIntPair ccip;
        for (int i = 0; i < list.tagCount(); i++) {
            ccipTag = list.getCompoundTagAt(i);
            ccip = new ChunkCoordIntPair(ccipTag.getInteger("x"), ccipTag.getInteger("z"));
            ccipSet.add(ccip);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        NBTTagList list = new NBTTagList();
        NBTTagCompound ccipTag;
        for (ChunkCoordIntPair ccip : this.ccipSet) {
            ccipTag = new NBTTagCompound();
            ccipTag.setInteger("x", ccip.chunkXPos);
            ccipTag.setInteger("z", ccip.chunkZPos);
            list.appendTag(ccipTag);
        }
        tag.setTag("chunkList", list);
    }

}
