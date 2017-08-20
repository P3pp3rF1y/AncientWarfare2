package net.shadowmage.ancientwarfare.automation.tile;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.ChunkPos;
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
        this.chunkTicket = ForgeChunkManager.requestTicket(AncientWarfareAutomation.instance, world, Type.NORMAL);
        if (this.chunkTicket != null) {
            writeDataToTicket();
            forceTicketChunks();
        }
    }

    protected void writeDataToTicket() {
        AWChunkLoader.INSTANCE.writeDataToTicket(chunkTicket, pos);
    }

    protected void forceTicketChunks() {
        int cx = pos.getX() >> 4;
        int cz = pos.getZ() >> 4;
        for (int x = cx - 1; x <= cx + 1; x++) {
            for (int z = cz - 1; z <= cz + 1; z++) {
                ChunkPos chunkPos = new ChunkPos(x, z);
                ForgeChunkManager.forceChunk(this.chunkTicket, chunkPos);
            }
        }
//TODO either uncomment and log chunk loading info or just remove this
        //  AWLog.logDebug("ticket now has chunks: "+tk.getChunkList());
//  AWLog.logDebug("total forced chunks are: "+ForgeChunkManager.getPersistentChunksFor(world));
    }
}
