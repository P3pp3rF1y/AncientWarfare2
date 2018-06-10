package net.shadowmage.ancientwarfare.npc.registry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.JsonUtils;
import net.shadowmage.ancientwarfare.core.registry.IRegistryDataParser;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class FactionRegistry {
	private FactionRegistry() {}

	private static Set<FactionDefinition> factions = new HashSet<>();

	public static Set<FactionDefinition> getFactions() {
		return factions;
	}

	public static Set<String> getFactionNames() {
		return factions.stream().map(FactionDefinition::getName).collect(Collectors.toCollection(HashSet::new));
	}

	public static FactionDefinition getFaction(String name) {
		return factions.stream().filter(f -> f.getName().equals(name)).findFirst().orElse(EMPTY_FACTION);
	}

	public static class FactionParser implements IRegistryDataParser {

		public static final String PLAYER_DEFAULT_STANDING = "player_default_standing";
		public static final String HOSTILE_TOWARDS_FACTIONS = "hostile_towards_factions";

		@Override
		public String getName() {
			return "factions";
		}

		@Override
		public void parse(JsonObject json) {
			JsonObject defaults = JsonUtils.getJsonObject(json, "defaults");
			FactionDefinition defaultDefinition = new FactionDefinition(JsonUtils.getInt(defaults, PLAYER_DEFAULT_STANDING),
					parseHostileTowards(defaults).entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).collect(Collectors.toCollection(HashSet::new)));

			JsonArray factionsArray = JsonUtils.getJsonArray(json, "factions");

			for (JsonElement e : factionsArray) {
				JsonObject faction = JsonUtils.getJsonObject(e, "faction");
				FactionDefinition.CopyBuilder builder = defaultDefinition.copy(JsonUtils.getString(faction, "name"), Integer.parseInt(JsonUtils.getString(faction, "color"), 16));
				if (faction.has(PLAYER_DEFAULT_STANDING)) {
					builder.setPlayerDefaultStanding(JsonUtils.getInt(faction, PLAYER_DEFAULT_STANDING));
				}
				if (faction.has(HOSTILE_TOWARDS_FACTIONS)) {
					Map<String, Boolean> hostileTowards = parseHostileTowards(faction);

					hostileTowards.entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).forEach(builder::addHostileTowards);
					hostileTowards.entrySet().stream().filter(entry -> !entry.getValue()).map(Map.Entry::getKey).forEach(builder::removeHostileTowards);
				}

				factions.add(builder.build());
			}
		}

		private Map<String, Boolean> parseHostileTowards(JsonObject json) {
			JsonArray array = JsonUtils.getJsonArray(json, HOSTILE_TOWARDS_FACTIONS);

			Map<String, Boolean> hostileTowards = new HashMap<>();

			for (JsonElement e : array) {
				Map.Entry<String, JsonElement> entry = JsonUtils.getJsonObject(e, "").entrySet().iterator().next();
				hostileTowards.put(entry.getKey(), JsonUtils.getBoolean(entry.getValue(), ""));
			}

			return hostileTowards;
		}
	}

	private static final FactionDefinition EMPTY_FACTION = new FactionDefinition(0, new HashSet<>());
}
