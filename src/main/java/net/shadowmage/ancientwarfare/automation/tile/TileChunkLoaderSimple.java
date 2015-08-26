package net.shadowmage.ancientwarfare.automation.tile;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;
import net.shadowmage.ancientwarfare.automation.AncientWarfareAutomation;
import net.shadowmage.ancientwarfare.automation.chunkloader.AWChunkLoader;
import net.shadowmage.ancientwarfare.core.interfaces.IChunkLoaderTile;

public class TileChunkLoaderSimple extends TileEntity implements IChunkLoaderTile {

    Ticket chunkTicket = null;

    public TileChunkLoaderSimple() {

    }

    @Override
    public boolean canUpdate() {
        return false;
    }

    public void releaseTicket() {
        ForgeChunkManager.releaseTicket(chunkTicket);
        chunkTicket = null;
    }

    @Override
    public void invalidate() {
        super.invalidate();
        releaseTicket();
    }

    @Override
    public void setTicket(Ticket tk) {
        if (this.chunkTicket != tk) {
            releaseTicket();
            if (tk != null) {
                this.chunkTicket = tk;
                forceTicketChunks();
            }
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
        AWChunkLoader.INSTANCE.writeDataToTicket(chunkTicket, xCoord, yCoord, zCoord);
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
}
