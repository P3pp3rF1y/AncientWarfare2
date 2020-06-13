package net.shadowmage.ancientwarfare.structure.worldgen.stats;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.structure.config.AWStructureStatics;
import net.shadowmage.ancientwarfare.structure.worldgen.Territory;
import net.shadowmage.ancientwarfare.structure.worldgen.TerritoryManager;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

public class WorldGenStatistics {
	private static Map<String, TerritoryRecord> territories = new TreeMap<>();
	private static Map<String, StructureRecord> structures = new TreeMap<>();
	private static boolean collectWorldGenStats = AWStructureStatics.collectWorldGenStatistics;

	public static void startCollectingStatistics() {
		collectWorldGenStats = true;
	}

	public static void stopCollectingStatistics() {
		collectWorldGenStats = false;
	}

	public static void clearStatistics() {
		territories.clear();
		structures.clear();
	}

	public static Optional<StructureRecord> getStructure(String structureName) {
		return Optional.ofNullable(structures.get(structureName));
	}

	public static void addTerritoryInfo(String territoryName, String biomeName, float clusterValue) {
		if (!collectWorldGenStats) {
			return;
		}
		if (!territories.containsKey(territoryName)) {
			territories.put(territoryName, new TerritoryRecord(territoryName));
		}
		TerritoryRecord territoryRecord = territories.get(territoryName);
		territoryRecord.addBiomeGen(biomeName);
		territoryRecord.addClustervalue(biomeName, clusterValue);
		territoryRecord.incrementTimesGenerated();
	}

	public static Optional<TerritoryRecord> getTerritory(String territoryName) {
		return Optional.ofNullable(territories.get(territoryName));
	}

	public static Collection<StructureRecord> getStructures() {
		return structures.values();
	}

	public static Collection<TerritoryRecord> getTerritories() {
		return territories.values();
	}

	public static class TerritoryRecord {
		private final String name;
		private int timesGenerated = 0;
		private final Map<String, Integer> biomeGenerations = new TreeMap<>();
		private final Map<String, Float> biomeTotalClusterValues = new TreeMap<>();
		private float totalClusterValue = 0;

		TerritoryRecord(String name) {
			this.name = name;
		}

		void addBiomeGen(String biomeName) {
			incrementMapKey(biomeGenerations, biomeName);
		}

		void incrementTimesGenerated() {
			timesGenerated++;
		}

		public String getName() {
			return name;
		}

		public int getTimesGenerated() {
			return timesGenerated;
		}

		public Map<String, Integer> getBiomeGenerations() {
			return biomeGenerations;
		}

		public float getAverageBiomeClusterValue(String biomeName) {
			return biomeTotalClusterValues.get(biomeName) / biomeGenerations.get(biomeName);
		}

		void addClustervalue(String biomeName, float clusterValue) {
			addMapKeyValue(biomeTotalClusterValues, biomeName, clusterValue);
			totalClusterValue += clusterValue;
		}

		public float getAverageClusterValue() {
			return totalClusterValue / timesGenerated;
		}
	}

	public static void addStructurePlacementRejection(String structureName, PlacementRejectionReason placementRejectionReason) {
		if (!collectWorldGenStats) {
			return;
		}
		StructureRecord structureRecord = getStructureRecord(structureName);
		structureRecord.addPlacementRejectionReason(placementRejectionReason);
	}

	public static void addStructureValidationRejection(String structureName, ValidationRejectionReason validationRejectionReason) {
		if (!collectWorldGenStats) {
			return;
		}
		StructureRecord structureRecord = getStructureRecord(structureName);
		structureRecord.addValidationRejectionReason(validationRejectionReason);
	}

	public static void recordStructureConsideredInRandom(String structureName) {
		getStructureRecord(structureName).incrementTimesConsideredInRandom();
	}

	public static void addStructureGeneratedInfo(String structureName, World world, BlockPos pos) {
		if (!collectWorldGenStats) {
			return;
		}
		StructureRecord structureRecord = getStructureRecord(structureName);
		structureRecord.incrementTimesGenerated();
		//noinspection ConstantConditions
		structureRecord.addBiomeGen(world.getBiome(pos).getRegistryName().toString());
		structureRecord.addTerritoryGen(TerritoryManager.getTerritory(pos.getX() >> 4, pos.getZ() >> 4, world).map(Territory::getTerritoryName).orElse(""));
	}

	private static StructureRecord getStructureRecord(String structureName) {
		if (!structures.containsKey(structureName)) {
			structures.put(structureName, new StructureRecord(structureName));
		}
		return structures.get(structureName);
	}

	private static <T> void incrementMapKey(Map<T, Integer> map, T key) {
		map.put(key, map.getOrDefault(key, 0) + 1);
	}

	private static <T> void addMapKeyValue(Map<T, Float> map, T key, float value) {
		map.put(key, map.getOrDefault(key, 0f) + value);
	}

	public static class StructureRecord {
		private final String name;
		private int timesGenerated = 0;
		private int timesConsideredInRandom = 0;

		public Map<String, Integer> getBiomeGenerations() {
			return biomeGenerations;
		}

		public Map<String, Integer> getTerritoryGenerations() {
			return territoryGenerations;
		}

		public Map<ValidationRejectionReason, Integer> getValidationRejectionReasons() {
			return validationRejectionReasons;
		}

		public Map<PlacementRejectionReason, Integer> getPlacementRejectionReasons() {
			return placementRejectionReasons;
		}

		private final Map<String, Integer> biomeGenerations = new TreeMap<>();
		private final Map<String, Integer> territoryGenerations = new TreeMap<>();
		private final Map<ValidationRejectionReason, Integer> validationRejectionReasons = new TreeMap<>();
		private final Map<PlacementRejectionReason, Integer> placementRejectionReasons = new TreeMap<>();

		StructureRecord(String structureName) {
			name = structureName;
		}

		void incrementTimesGenerated() {
			timesGenerated++;
		}

		void incrementTimesConsideredInRandom() {
			timesConsideredInRandom++;
		}

		void addBiomeGen(String biomeName) {
			incrementMapKey(biomeGenerations, biomeName);
		}

		void addTerritoryGen(String territoryName) {
			incrementMapKey(territoryGenerations, territoryName);
		}

		void addValidationRejectionReason(ValidationRejectionReason validationRejectionReason) {
			incrementMapKey(validationRejectionReasons, validationRejectionReason);
		}

		void addPlacementRejectionReason(PlacementRejectionReason placementRejectionReason) {
			incrementMapKey(placementRejectionReasons, placementRejectionReason);
		}

		public String getName() {
			return name;
		}

		public int getTimesGenerated() {
			return timesGenerated;
		}

		public int getTimesConsideredInRandom() {
			return timesConsideredInRandom;
		}
	}
}
