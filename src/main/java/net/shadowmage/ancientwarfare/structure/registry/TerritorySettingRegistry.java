package net.shadowmage.ancientwarfare.structure.registry;

import com.google.gson.JsonObject;
import net.minecraft.util.JsonUtils;
import net.shadowmage.ancientwarfare.core.registry.IRegistryDataParser;
import net.shadowmage.ancientwarfare.core.util.parsing.JsonHelper;

import java.util.HashMap;
import java.util.Map;

public class TerritorySettingRegistry {
	private TerritorySettingRegistry() {}

	private static TerritorySettings defaultSettings = new TerritorySettings(11, 1, 1, 1);
	private static Map<String, TerritorySettings> territorySettings = new HashMap<>();

	public static TerritorySettings getTerritorySettings(String territoryName) {
		return territorySettings.getOrDefault(territoryName, defaultSettings);
	}

	public static double getMaxTerritoryCenterDistanceSq(String territoryName) {
		int maxTerritoryChunkRadius = TerritorySettingRegistry.getTerritorySettings(territoryName).getMaxRadiusInChunks();
		return Math.pow((double) maxTerritoryChunkRadius * 16, 2);
	}

	public static class TerritorySettings {
		int getMaxRadiusInChunks() {
			return maxRadiusInChunks;
		}

		public float getPerChunkClusterValueMultiplier() {
			return perChunkClusterValueMultiplier;
		}

		public float getStructureGenerationChanceMultiplier() {
			return structureGenerationChanceMultiplier;
		}

		public float getTerritoryWeightMultiplier() {
			return territoryWeightMultiplier;
		}

		private int maxRadiusInChunks;
		private float perChunkClusterValueMultiplier;
		private float structureGenerationChanceMultiplier;
		private float territoryWeightMultiplier;

		private TerritorySettings(int maxRadiusInChunks, float perChunkClusterValueMultiplier, float structureGenerationChanceMultiplier, float territoryWeightMultiplier) {
			this.maxRadiusInChunks = maxRadiusInChunks;
			this.perChunkClusterValueMultiplier = perChunkClusterValueMultiplier;
			this.structureGenerationChanceMultiplier = structureGenerationChanceMultiplier;
			this.territoryWeightMultiplier = territoryWeightMultiplier;
		}

		public static class Builder {
			void setMaxRadiusInChunks(int maxRadiusInChunks) {
				this.maxRadiusInChunks = maxRadiusInChunks;
			}

			void setPerChunkClusterValueMultiplier(float perChunkClusterValueMultiplier) {
				this.perChunkClusterValueMultiplier = perChunkClusterValueMultiplier;
			}

			void setStructureGenerationChanceMultiplier(float structureGenerationChanceMultiplier) {
				this.structureGenerationChanceMultiplier = structureGenerationChanceMultiplier;
			}

			void setTerritoryWeightMultiplier(float territoryWeightMultiplier) {
				this.territoryWeightMultiplier = territoryWeightMultiplier;
			}

			private int maxRadiusInChunks;
			private float perChunkClusterValueMultiplier;
			private float structureGenerationChanceMultiplier;
			private float territoryWeightMultiplier;

			Builder() {}

			Builder(TerritorySettings settings) {
				maxRadiusInChunks = settings.maxRadiusInChunks;
				perChunkClusterValueMultiplier = settings.perChunkClusterValueMultiplier;
				structureGenerationChanceMultiplier = settings.structureGenerationChanceMultiplier;
				territoryWeightMultiplier = settings.territoryWeightMultiplier;
			}

			public TerritorySettings build() {
				return new TerritorySettings(maxRadiusInChunks, perChunkClusterValueMultiplier, structureGenerationChanceMultiplier, territoryWeightMultiplier);
			}
		}
	}

	public static class Parser implements IRegistryDataParser {
		private static void setDefaultSettings(TerritorySettings defaultSettings) {
			TerritorySettingRegistry.defaultSettings = defaultSettings;
		}

		@Override
		public String getName() {
			return "territory_settings";
		}

		@Override
		public void parse(JsonObject json) {
			JsonObject defaultJson = JsonUtils.getJsonObject(json, "defaults");
			setDefaultSettings(getTerritorySettings(defaultJson, new TerritorySettings.Builder()));

			JsonHelper.mapFromJson(json, "territories", TerritorySettingRegistry.territorySettings, Map.Entry::getKey, e -> {
				TerritorySettings.Builder builder = new TerritorySettings.Builder(defaultSettings);
				return getTerritorySettings(JsonUtils.getJsonObject(e.getValue(), ""), builder);
			});
		}

		private TerritorySettings getTerritorySettings(JsonObject json, TerritorySettings.Builder builder) {
			if (JsonUtils.hasField(json, "max_radius_in_chunks")) {
				builder.setMaxRadiusInChunks(JsonUtils.getInt(json, "max_radius_in_chunks"));
			}
			if (JsonUtils.hasField(json, "per_chunk_cluster_value_multiplier")) {
				builder.setPerChunkClusterValueMultiplier(JsonUtils.getFloat(json, "per_chunk_cluster_value_multiplier"));
			}
			if (JsonUtils.hasField(json, "structure_generation_chance_multiplier")) {
				builder.setStructureGenerationChanceMultiplier(JsonUtils.getFloat(json, "structure_generation_chance_multiplier"));
			}
			if (JsonUtils.hasField(json, "territory_weight_multiplier")) {
				builder.setTerritoryWeightMultiplier(JsonUtils.getFloat(json, "territory_weight_multiplier"));
			}
			return builder.build();
		}
	}

}
