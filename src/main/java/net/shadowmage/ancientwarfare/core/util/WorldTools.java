package net.shadowmage.ancientwarfare.core.util;

import com.google.common.collect.Lists;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class WorldTools {
	private WorldTools() {}

	/*
	 * SERVER ONLY
	 */
	public static List<TileEntity> getTileEntitiesInArea(World world, int x1, int y1, int z1, int x2, int y2, int z2) {
		if (world instanceof WorldServer) {

			List<TileEntity> tileEntities = Lists.newArrayList();
			for (int x = (x1 >> 4); x <= (x2 >> 4); x++) {
				for (int z = (z1 >> 4); z <= (z2 >> 4); z++) {
					addValidTilesInChunkArea(world, x1, y1, z1, x2, y2, z2, tileEntities, x, z);
				}
			}
			return tileEntities;
		}
		return Collections.emptyList();
	}

	private static void addValidTilesInChunkArea(World world, int x1, int y1, int z1, int x2, int y2, int z2, List<TileEntity> tileEntities, int x, int z) {
		Chunk chunk = world.getChunkFromChunkCoords(x, z);
		for (TileEntity tile : chunk.getTileEntityMap().values()) {
			if (!tile.isInvalid() && isTileInArea(x1, y1, z1, x2, y2, z2, tile)) {
				tileEntities.add(tile);
			}
		}
	}

	private static boolean isTileInArea(int x1, int y1, int z1, int x2, int y2, int z2, TileEntity tile) {
		return tile.getPos().getX() >= x1 && tile.getPos().getY() >= y1 && tile.getPos().getZ() >= z1 && tile.getPos().getX() <= x2 && tile.getPos().getY() <= y2 && tile.getPos().getZ() <= z2;
	}


	public static <T extends TileEntity> Optional<T> getTile(IBlockAccess world, BlockPos pos, Class<T> teClass) {
		TileEntity te = world.getTileEntity(pos);

		if (teClass.isInstance(te)) {
			return Optional.of(teClass.cast(te));
		}

		return Optional.empty();
	}
}
