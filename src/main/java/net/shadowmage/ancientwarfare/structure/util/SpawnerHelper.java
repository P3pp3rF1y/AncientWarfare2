package net.shadowmage.ancientwarfare.structure.util;

import net.minecraft.util.ResourceLocation;
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
				//if entity spawned just a fraction of time before it must be set to be "disabled" so change it to zombie
				if (world.getTotalWorldTime() - respawnData.getSpawnTime() < 10
						&& !settings.getSpawnGroups().isEmpty() && !settings.getSpawnGroups().get(0).getEntitiesToSpawn().isEmpty()) {
					settings.getSpawnGroups().get(0).getEntitiesToSpawn().get(0).setEntityToSpawn(new ResourceLocation("zombie"));
				}
				te.setSettings(settings);
			});
		}
	}
}
