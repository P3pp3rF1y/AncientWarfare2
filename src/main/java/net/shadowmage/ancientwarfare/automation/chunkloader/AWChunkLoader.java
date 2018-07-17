package net.shadowmage.ancientwarfare.automation.chunkloader;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager.LoadingCallback;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.interfaces.IChunkLoaderTile;
import net.shadowmage.ancientwarfare.core.util.WorldTools;

import java.util.List;

public final class AWChunkLoader implements LoadingCallback {

	public static final AWChunkLoader INSTANCE = new AWChunkLoader();
	private static final String TILE_POSITION_TAG = "tilePosition";

	private AWChunkLoader() {

	}

	@Override
	public void ticketsLoaded(List<Ticket> tickets, World world) {
		for (Ticket tk : tickets) {
			if (!tk.isPlayerTicket() && tk.getModId().startsWith(AncientWarfareCore.MOD_ID) && tk.getModData().hasKey(TILE_POSITION_TAG)) {
				WorldTools.getTile(world, BlockPos.fromLong(tk.getModData().getLong(TILE_POSITION_TAG)), IChunkLoaderTile.class).ifPresent(t -> t.setTicket(tk));
			}
		}
	}

	public void writeDataToTicket(Ticket tk, BlockPos pos) {
		tk.getModData().setLong(TILE_POSITION_TAG, pos.toLong());
	}

}
