package net.shadowmage.ancientwarfare.structure.worldgen.stats;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.structure.config.AWStructureStatics;
import net.shadowmage.ancientwarfare.structure.worldgen.Territory;
import net.shadowmage.ancientwarfare.structure.worldgen.TerritoryManager;

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

	public static void addTerritoryInfo(String territoryName, String biomeName) {
		if (!collectWorldGenStats) {
			return;
		}
		if (!territories.containsKey(territoryName)) {
			territories.put(territoryName, new TerritoryRecord(territoryName));
		}
		TerritoryRecord territoryRecord = territories.get(territoryName);
		territoryRecord.addBiomeGen(biomeName);
		territoryRecord.incrementTimesGenerated();
	}

	public static Optional<TerritoryRecord> getTerritory(String territoryName) {
		return Optional.ofNullable(territories.get(territoryName));
	}

	public static class TerritoryRecord {
		private final String name;
		private int timesGenerated = 0;
		private final Map<String, Integer> biomeGenerations = new TreeMap<>();

		TerritoryRecord(String name) {
			this.name = name;
		}

		void addBiomeGen(String biomeName) {
			biomeGenerations.put(biomeName, biomeGenerations.getOrDefault(biomeName, 0) + 1);
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

	public static class StructureRecord {
		private final String name;
		private int timesGenerated = 0;

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

		void addBiomeGen(String biomeName) {
			incrementMapValue(biomeGenerations, biomeName);
		}

		void addTerritoryGen(String territoryName) {
			incrementMapValue(territoryGenerations, territoryName);
		}

		void addValidationRejectionReason(ValidationRejectionReason validationRejectionReason) {
			incrementMapValue(validationRejectionReasons, validationRejectionReason);
		}

		private <T> void incrementMapValue(Map<T, Integer> map, T value) {
			map.put(value, map.getOrDefault(value, 0) + 1);
		}

		void addPlacementRejectionReason(PlacementRejectionReason placementRejectionReason) {
			incrementMapValue(placementRejectionReasons, placementRejectionReason);
		}

		public String getName() {
			return name;
		}

		public int getTimesGenerated() {
			return timesGenerated;
		}
	}
}
