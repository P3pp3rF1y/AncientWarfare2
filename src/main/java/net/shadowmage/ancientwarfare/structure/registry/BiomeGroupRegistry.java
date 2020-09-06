package net.shadowmage.ancientwarfare.structure.registry;

import com.google.gson.JsonObject;
import net.minecraft.util.JsonUtils;
import net.shadowmage.ancientwarfare.core.registry.IRegistryDataParser;
import net.shadowmage.ancientwarfare.core.util.parsing.JsonHelper;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BiomeGroupRegistry {
	private BiomeGroupRegistry() {}

	private static final Map<String, Set<String>> GROUP_BIOMES = new HashMap<>();

	public static Set<String> getGroupBiomes(String biomeGroup) {
		return GROUP_BIOMES.getOrDefault(biomeGroup, Collections.emptySet());
	}

	public static Set<String> getBiomeGroups() {
		return GROUP_BIOMES.keySet();
	}

	public static class Parser implements IRegistryDataParser {

		@Override
		public String getName() {
			return "biome_groups";
		}

		@Override
		public void parse(JsonObject json) {
			GROUP_BIOMES.putAll(JsonHelper.mapFromJson(json, "biome_groups",
					Map.Entry::getKey,
					entry -> JsonHelper.setFromJson(entry.getValue(), element -> JsonUtils.getString(element, ""))
			));
		}
	}
}
