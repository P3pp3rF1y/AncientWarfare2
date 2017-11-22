package net.shadowmage.ancientwarfare.automation.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.automation.container.ContainerChunkLoaderDeluxe;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TileChunkLoaderDeluxe extends TileChunkLoaderSimple implements IInteractableTile {

    private final Set<ChunkPos> ccipSet = new HashSet<>();

    private final List<ContainerChunkLoaderDeluxe> viewers = new ArrayList<>();

    public TileChunkLoaderDeluxe() {

    }

    @Override
    public boolean onBlockClicked(EntityPlayer player, @Nullable EnumHand hand) {
        if (!player.world.isRemote) {
            NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_CHUNK_LOADER_DELUXE, pos);
        }
        return true;
    }

    public void addViewer(ContainerChunkLoaderDeluxe viewer) {
        viewers.add(viewer);
    }

    public void removeViewer(ContainerChunkLoaderDeluxe viewer) {
        viewers.remove(viewer);
    }

    public void addOrRemoveChunk(ChunkPos ccip) {
        if (ccipSet.contains(ccip)) {
            ccipSet.remove(ccip);
            ForgeChunkManager.unforceChunk(chunkTicket, ccip);
        } else {
            ccipSet.add(ccip);
            ForgeChunkManager.forceChunk(chunkTicket, ccip);
        }
        markDirty();
        informViewers();
    }

    private void informViewers() {
        for (ContainerChunkLoaderDeluxe viewer : viewers) {
            viewer.onChunkLoaderSetUpdated(ccipSet);
        }
    }

    public Set<ChunkPos> getForcedChunks() {
        return new HashSet<>(ccipSet);
    }

    @Override
    protected void forceTicketChunks() {
        int cx = pos.getX() >> 4;
        int cz = pos.getZ() >> 4;
        ChunkPos ccip1 = new ChunkPos(cx, cz);
        ForgeChunkManager.forceChunk(this.chunkTicket, ccip1);
        for (ChunkPos ccip : ccipSet) {
            ForgeChunkManager.forceChunk(this.chunkTicket, ccip);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        NBTTagList list = tag.getTagList("chunkList", Constants.NBT.TAG_COMPOUND);
        NBTTagCompound ccipTag;
        ChunkPos ccip;
        ccipSet.clear();
        for (int i = 0; i < list.tagCount(); i++) {
            ccipTag = list.getCompoundTagAt(i);
            ccip = new ChunkPos(ccipTag.getInteger("x") >> 4, ccipTag.getInteger("z") >> 4);
            ccipSet.add(ccip);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        NBTTagList list = new NBTTagList();
        NBTTagCompound ccipTag;
        for (ChunkPos chunkPos : this.ccipSet) {
            ccipTag = new NBTTagCompound();
            ccipTag.setInteger("x", chunkPos.getXStart());
            ccipTag.setInteger("z", chunkPos.getZStart());
            list.appendTag(ccipTag);
        }
        tag.setTag("chunkList", list);
        return tag;
    }

}
