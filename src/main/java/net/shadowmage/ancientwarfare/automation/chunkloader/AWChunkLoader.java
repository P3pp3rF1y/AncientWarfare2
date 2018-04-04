package net.shadowmage.ancientwarfare.automation.chunkloader;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
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
				TileEntity te = world.getTileEntity(BlockPos.fromLong(tk.getModData().getLong("tilePosition")));
				if (te instanceof IChunkLoaderTile) {
					((IChunkLoaderTile) te).setTicket(tk);
				}
			}
		}
	}

	public void writeDataToTicket(Ticket tk, BlockPos pos) {
		tk.getModData().setLong("tilePosition", pos.toLong());
	}

}
