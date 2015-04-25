package net.shadowmage.ancientwarfare.automation.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;
import net.shadowmage.ancientwarfare.automation.AncientWarfareAutomation;
import net.shadowmage.ancientwarfare.core.interfaces.IChunkLoaderTile;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;

public class TileChunkLoaderSimple extends TileEntity implements IInteractableTile, IChunkLoaderTile {

    Ticket chunkTicket = null;

    public TileChunkLoaderSimple() {

    }

    @Override
    public boolean canUpdate() {
        return false;
    }

    public void releaseTicket() {
        if (chunkTicket != null) {
            for (ChunkCoordIntPair ccip : chunkTicket.getChunkList()) {
                ForgeChunkManager.unforceChunk(chunkTicket, ccip);
            }
            ForgeChunkManager.releaseTicket(chunkTicket);
        }
        chunkTicket = null;
    }

    @Override
    public void validate() {
        super.validate();
        releaseTicket();
    }

    @Override
    public void invalidate() {
        super.invalidate();
        releaseTicket();
    }

    @Override
    public void setTicket(Ticket tk) {
        if (this.chunkTicket != null) {
            ForgeChunkManager.releaseTicket(chunkTicket);
        }
        this.chunkTicket = tk;
        if (tk != null) {
            forceTicketChunks();
        }
    }

    public void setupInitialTicket() {
        this.chunkTicket = ForgeChunkManager.requestTicket(AncientWarfareAutomation.instance, worldObj, Type.NORMAL);
        if (this.chunkTicket != null) {
            writeDataToTicket();
            forceTicketChunks();
        }
    }

    protected void writeDataToTicket() {
        NBTTagCompound posTag = new NBTTagCompound();
        posTag.setInteger("x", xCoord);
        posTag.setInteger("y", yCoord);
        posTag.setInteger("z", zCoord);
        this.chunkTicket.getModData().setTag("tilePosition", posTag);
    }

    protected void forceTicketChunks() {
        int cx = xCoord >> 4;
        int cz = zCoord >> 4;
        for (int x = cx - 1; x <= cx + 1; x++) {
            for (int z = cz - 1; z <= cz + 1; z++) {
                ChunkCoordIntPair ccip = new ChunkCoordIntPair(x, z);
                ForgeChunkManager.forceChunk(this.chunkTicket, ccip);
            }
        }
//  AWLog.logDebug("ticket now has chunks: "+tk.getChunkList());
//  AWLog.logDebug("total forced chunks are: "+ForgeChunkManager.getPersistentChunksFor(worldObj));
    }

    @Override
    public boolean onBlockClicked(EntityPlayer player) {
        return false;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
    }

}
