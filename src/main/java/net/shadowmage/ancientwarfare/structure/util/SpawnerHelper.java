package net.shadowmage.ancientwarfare.structure.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.structure.init.AWStructureBlocks;
import net.shadowmage.ancientwarfare.structure.tile.SpawnerSettings;
import net.shadowmage.ancientwarfare.structure.tile.TileAdvancedSpawner;

public class SpawnerHelper {
	private SpawnerHelper() {}

	public static void createSpawner(IRespawnData respawnData, World world) {
		if (respawnData.canRespawn() && world.isAirBlock(respawnData.getRespawnPos())) {
			world.setBlockState(respawnData.getRespawnPos(), AWStructureBlocks.ADVANCED_SPAWNER.getDefaultState());
			WorldTools.getTile(world, respawnData.getRespawnPos(), TileAdvancedSpawner.class).ifPresent(te -> {
				SpawnerSettings settings = new SpawnerSettings();
				settings.readFromNBT(respawnData.getSpawnerSettings());
				te.setSettings(settings);
			});
		}
	}
}
