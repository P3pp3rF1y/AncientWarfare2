package net.shadowmage.ancientwarfare.structure.worldgen;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.shadowmage.ancientwarfare.structure.AncientWarfareStructure;
import net.shadowmage.ancientwarfare.structure.config.AWStructureStatics;
import net.shadowmage.ancientwarfare.structure.template.WorldGenStructureManager;
import net.shadowmage.ancientwarfare.structure.util.CollectionUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class TerritoryManager {
	private TerritoryManager() {}

	public static Optional<Territory> getTerritory(int chunkX, int chunkZ, World world) {
		Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);
		long chunkPosValue = ChunkPos.asLong(chunk.x, chunk.z);
		ITerritoryData territoryData = world.getCapability(CapabilityTerritoryData.TERRITORY_DATA, null);
		if (territoryData == null) {
			return Optional.empty();
		}

		if (!territoryData.isOwned(chunkPosValue)) {
			new TerritoryGenerator(world).generateTerritory(chunkX, chunkZ, chunkPosValue, territoryData);
		}

		return territoryData.getTerritory(chunkPosValue);
	}

	public static BlockPos getChunkCenterPos(int chunkX, int chunkZ) {
		int x = chunkX * 16 + 8;
		int z = chunkZ * 16 + 8;

		return new BlockPos(x, 1, z);
	}

	private static class TerritoryGenerator {
		private Map<Long, ProcessedChunk> chunksToProcess = new HashMap<>();
		private Map<Long, BlockPos> includedChunks = new HashMap<>();
		private Set<Long> visitedChunks = new HashSet<>();
		private World world;

		private TerritoryGenerator(World world) {
			this.world = world;
		}

		private void generateTerritory(int chunkX, int chunkZ, long chunkPosValue, ITerritoryData territoryData) {

			BlockPos firstPos = getChunkCenterPos(chunkX, chunkZ);
			Biome biome = world.getBiome(firstPos);
			Optional<List<String>> territoryNames = WorldGenStructureManager.INSTANCE.getBiomeTerritoryNames(biome);
			if (territoryNames.isPresent()) {
				String territoryName = getRandomTerritory(territoryNames.get());
				String territoryId = territoryName + territoryData.getNextTerritoryId(territoryName);

				includedChunks.put(chunkPosValue, firstPos);

				chunksToProcess.put(chunkPosValue, new ProcessedChunk(chunkPosValue, firstPos, chunkX, chunkZ));
				WorldGenStructureManager.INSTANCE.getTerritoryBiomes(territoryName).ifPresent(biomes -> processChunks(biomes, territoryData));

				if (includedChunks.size() < 3) {
					territoryId = getFirstDifferentTerritoryIdFromNeighbors(chunkX, chunkZ, territoryId, territoryData);
				}

				String finalTerritoryId = territoryId;
				includedChunks.forEach((hash, pos) -> territoryData.setOwned(hash, finalTerritoryId, territoryName));
			}
		}

		private BlockPos getTerritoryCenter() {
			return TerritoryManager.getTerritoryCenter(includedChunks.values());
		}

		private void addChunkToProcess(Set<Biome> biomes, BlockPos territoryCenterPos, ChunkPos chunkPos, ITerritoryData territoryData) {
			long chunkPosValue = ChunkPos.asLong(chunkPos.x, chunkPos.z);
			BlockPos centerPos = getChunkCenterPos(chunkPos.x, chunkPos.z);
			if (territoryData.isOwned(chunkPosValue) || visitedChunks.contains(chunkPosValue) || chunksToProcess.containsKey(chunkPosValue)) {
				return;
			}
			if (biomes.contains(world.getBiome(centerPos)) && centerPos.distanceSq(territoryCenterPos) < 1.1 * AWStructureStatics.maxTerritoryCenterDistanceSq) {
				chunksToProcess.put(chunkPosValue, new ProcessedChunk(chunkPosValue, centerPos, chunkPos.x, chunkPos.z));
			}
		}

		private void includeChunk(ProcessedChunk processedChunk) {
			includedChunks.put(processedChunk.getChunkPosValue(), processedChunk.getCenterPos());
			visitedChunks.add(processedChunk.getChunkPosValue());
			AncientWarfareStructure.LOG.debug("Chunk #{} {}, territory center: {}", includedChunks.entrySet().size(), processedChunk.getCenterPos(), getTerritoryCenter());
		}

		private void processChunks(Set<Biome> biomes, ITerritoryData territoryData) {
			while (!chunksToProcess.isEmpty()) {
				if (includedChunks.isEmpty() && chunksToProcess.size() == 1) {
					ProcessedChunk processedChunk = chunksToProcess.values().iterator().next();
					includeChunk(processedChunk);
				} else {
					processClosestChunk(biomes, territoryData);
				}
			}
		}

		private void processClosestChunk(Set<Biome> biomes, ITerritoryData territoryData) {
			ProcessedChunk closestChunkToProcess = null;
			double closestDist = Double.MAX_VALUE;
			BlockPos territoryCenterPos = getTerritoryCenter();
			for (ProcessedChunk processedChunk : chunksToProcess.values()) {
				double dist = territoryCenterPos.distanceSq(processedChunk.getCenterPos());
				if (dist < closestDist) {
					closestDist = dist;
					closestChunkToProcess = processedChunk;
				}
			}
			if (closestChunkToProcess != null) {
				if (closestDist < AWStructureStatics.maxTerritoryCenterDistanceSq) {
					includeChunk(closestChunkToProcess);

					addChunkToProcess(biomes, territoryCenterPos, new ChunkPos(closestChunkToProcess.getChunkX() + 1, closestChunkToProcess.getChunkZ()), territoryData);
					addChunkToProcess(biomes, territoryCenterPos, new ChunkPos(closestChunkToProcess.getChunkX() - 1, closestChunkToProcess.getChunkZ()), territoryData);
					addChunkToProcess(biomes, territoryCenterPos, new ChunkPos(closestChunkToProcess.getChunkX(), closestChunkToProcess.getChunkZ() + 1), territoryData);
					addChunkToProcess(biomes, territoryCenterPos, new ChunkPos(closestChunkToProcess.getChunkX(), closestChunkToProcess.getChunkZ() - 1), territoryData);
				}
				chunksToProcess.remove(closestChunkToProcess.getChunkPosValue());
			}
		}

		private Optional<String> getDifferentTerritoryId(int chunkX, int chunkZ, String territoryId, ITerritoryData territoryData) {
			Optional<Territory> territory = territoryData.getTerritory(ChunkPos.asLong(chunkX, chunkZ));
			if (territory.isPresent()) {
				String id = territory.get().getTerritoryId();
				if (!id.equals(territoryId)) {
					return Optional.of(id);
				}
			}
			return Optional.empty();
		}

		private String getFirstDifferentTerritoryIdFromNeighbors(int chunkX, int chunkZ, String territoryId, ITerritoryData territoryData) {
			return getDifferentTerritoryId(chunkX + 1, chunkZ, territoryId, territoryData)
					.orElse(getDifferentTerritoryId(chunkX - 1, chunkZ, territoryId, territoryData)
							.orElse(getDifferentTerritoryId(chunkX, chunkZ + 1, territoryId, territoryData)
									.orElse(getDifferentTerritoryId(chunkX, chunkZ - 1, territoryId, territoryData).orElse(territoryId))
							)
					);
		}

		private String getRandomTerritory(List<String> names) {
			return CollectionUtils.getWeightedRandomElement(world.rand, names,
					name -> WorldGenStructureManager.INSTANCE.getTerritoryTemplates(name).map(Set::size).orElse(0)
							+ WorldGenStructureManager.INSTANCE.getTerritoryTemplates(WorldGenStructureManager.GENERIC_TERRITORY_NAME).map(Set::size).orElse(0))
					.orElse(WorldGenStructureManager.GENERIC_TERRITORY_NAME);
		}
	}

	public static BlockPos getTerritoryCenter(Collection<BlockPos> positions) {
		BlockPos territoryCenterPos;
		double averageX = 0;
		double averageZ = 0;
		for (BlockPos pos : positions) {
			averageX += (float) pos.getX() / positions.size();
			averageZ += (float) pos.getZ() / positions.size();
		}
		territoryCenterPos = new BlockPos(averageX, 1, averageZ);
		return territoryCenterPos;
	}

	private static class ProcessedChunk {
		private long chunkPosValue;
		private BlockPos centerPos;
		private int chunkX;
		private int chunkZ;

		private ProcessedChunk(long chunkPosValue, BlockPos centerPos, int chunkX, int chunkZ) {
			this.chunkPosValue = chunkPosValue;
			this.centerPos = centerPos;
			this.chunkX = chunkX;
			this.chunkZ = chunkZ;
		}

		private long getChunkPosValue() {
			return chunkPosValue;
		}

		private BlockPos getCenterPos() {
			return centerPos;
		}

		private int getChunkX() {
			return chunkX;
		}

		private int getChunkZ() {
			return chunkZ;
		}
	}
}
