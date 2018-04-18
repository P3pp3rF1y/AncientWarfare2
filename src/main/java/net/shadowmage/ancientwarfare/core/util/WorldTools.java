package net.shadowmage.ancientwarfare.core.util;

import com.google.common.collect.Lists;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;

import java.util.Collections;
import java.util.List;

public class WorldTools {

	/*
	 * SERVER ONLY
	 */
	public static List<TileEntity> getTileEntitiesInArea(World world, int x1, int y1, int z1, int x2, int y2, int z2) {
		if (world instanceof WorldServer) {

			List<TileEntity> tileEntities = Lists.newArrayList();
			for (int x = (x1 >> 4); x <= (x2 >> 4); x++) {
				for (int z = (z1 >> 4); z <= (z2 >> 4); z++) {
					Chunk chunk = world.getChunkFromChunkCoords(x, z);
					if (chunk != null) {
						for (TileEntity entity : chunk.getTileEntityMap().values()) {
							if (!entity.isInvalid()) {
								if (entity.getPos().getX() >= x1 && entity.getPos().getY() >= y1 && entity.getPos().getZ() >= z1 && entity.getPos()
										.getX() <= x2 && entity.getPos().getY() <= y2 && entity.getPos().getZ() <= z2) {
									tileEntities.add(entity);
								}
							}
						}
					}
				}
			}
			return tileEntities;
		}
		return Collections.emptyList();
	}
}
