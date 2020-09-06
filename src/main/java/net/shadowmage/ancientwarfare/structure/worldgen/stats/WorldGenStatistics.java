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
import java.util.function.Function;

public class WorldGenStatistics {
	private WorldGenStatistics() {}

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

	public static void recordStructureConsideredInRandom(String structureName, int weight, int totalWeight, String biomeName, String territoryName) {
		getStructureRecord(structureName).incrementTimesConsideredInRandom(biomeName, territoryName, weight == 0 ? 0 : (float) weight / totalWeight);
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
		changeMapKeyValue(map, key, 0, v -> v + 1);
	}

	private static <T> void addMapKeyValue(Map<T, Float> map, T key, float value) {
		changeMapKeyValue(map, key, (float) 0, v -> v + value);
	}

	private static <K, V> void changeMapKeyValue(Map<K, V> map, K key, V defaultValue, Function<V, V> changeValue) {
		map.put(key, changeValue.apply(map.getOrDefault(key, defaultValue)));
	}

	public static class StructureRecord {
		private final String name;
		private int timesGenerated = 0;
		private int timesConsideredInRandom = 0;
		private float totalChance = 0f;

		public Map<String, Integer> getBiomeGenerations() {
			return biomeGenerations;
		}

		public float getAverageChance() {
			return totalChance / timesConsideredInRandom;
		}

		public Map<String, GenerationChance> getBiomeChances() {
			return biomeChances;
		}

		public Map<String, GenerationChance> getTerritoryChances() {
			return territoryChances;
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
		private final Map<String, GenerationChance> biomeChances = new TreeMap<>();
		private final Map<String, Integer> territoryGenerations = new TreeMap<>();
		private final Map<String, GenerationChance> territoryChances = new TreeMap<>();
		private final Map<ValidationRejectionReason, Integer> validationRejectionReasons = new TreeMap<>();
		private final Map<PlacementRejectionReason, Integer> placementRejectionReasons = new TreeMap<>();

		StructureRecord(String structureName) {
			name = structureName;
		}

		void incrementTimesGenerated() {
			timesGenerated++;
		}

		void incrementTimesConsideredInRandom(String biomeName, String territoryName, float chance) {
			timesConsideredInRandom++;
			totalChance += chance;
			changeMapKeyValue(biomeChances, biomeName, new GenerationChance(), v -> v.addChance(chance));
			changeMapKeyValue(territoryChances, territoryName, new GenerationChance(), v -> v.addChance(chance));
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

		public static class GenerationChance {
			private float totalChance = 0f;

			public int getNumberOfGenerations() {
				return numberOfGenerations;
			}

			private int numberOfGenerations = 0;

			GenerationChance addChance(float chance) {
				totalChance += chance;
				numberOfGenerations++;
				return this;
			}

			public float getAverageChance() {
				return totalChance / numberOfGenerations;
			}
		}
	}
}
