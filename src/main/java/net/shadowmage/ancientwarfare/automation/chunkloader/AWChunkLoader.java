package net.shadowmage.ancientwarfare.automation.chunkloader;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager.LoadingCallback;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.interfaces.IChunkLoaderTile;

import java.util.List;

public final class AWChunkLoader implements LoadingCallback {

    public final static AWChunkLoader INSTANCE = new AWChunkLoader();
    private AWChunkLoader() {

    }

    @Override
    public void ticketsLoaded(List<Ticket> tickets, World world) {
        for (Ticket tk : tickets) {
            if (!tk.isPlayerTicket() && tk.getModId().startsWith(AncientWarfareCore.modID) && tk.getModData().hasKey("tilePosition")) {
                NBTTagCompound posTag = tk.getModData().getCompoundTag("tilePosition");
                int x = posTag.getInteger("x");
                int y = posTag.getInteger("y");
                int z = posTag.getInteger("z");
                TileEntity te = world.getTileEntity(x, y, z);
                if (te instanceof IChunkLoaderTile) {
                    ((IChunkLoaderTile) te).setTicket(tk);
                }
            }
        }
    }

    public void writeDataToTicket(Ticket tk, int x, int y, int z) {
        NBTTagCompound posTag = new NBTTagCompound();
        posTag.setInteger("x", x);
        posTag.setInteger("y", y);
        posTag.setInteger("z", z);
        tk.getModData().setTag("tilePosition", posTag);
    }

}
