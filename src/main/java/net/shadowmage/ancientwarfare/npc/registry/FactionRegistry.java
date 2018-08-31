package net.shadowmage.ancientwarfare.npc.registry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.JsonUtils;
import net.shadowmage.ancientwarfare.core.registry.IRegistryDataParser;
import net.shadowmage.ancientwarfare.core.util.parsing.JsonHelper;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
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
		private static final String PLAYER_DEFAULT_STANDING = "player_default_standing";
		private static final String HOSTILE_TOWARDS_FACTIONS = "hostile_towards_factions";

		@Override
		public String getName() {
			return "factions";
		}

		@Override
		public void parse(JsonObject json) {
			JsonObject defaults = JsonUtils.getJsonObject(json, "defaults");
			FactionDefinition defaultDefinition = new FactionDefinition(JsonUtils.getInt(defaults, PLAYER_DEFAULT_STANDING),
					parseHostileTowards(defaults).entrySet().stream().filter(Entry::getValue).map(Entry::getKey).collect(Collectors.toCollection(HashSet::new)));

			JsonArray factionsArray = JsonUtils.getJsonArray(json, "factions");

			for (JsonElement e : factionsArray) {
				JsonObject faction = JsonUtils.getJsonObject(e, "faction");
				String factionName = JsonUtils.getString(faction, "name");
				FactionDefinition.CopyBuilder builder = defaultDefinition.copy(factionName, Integer.parseInt(JsonUtils.getString(faction, "color"), 16));
				if (faction.has(PLAYER_DEFAULT_STANDING)) {
					builder.setPlayerDefaultStanding(JsonUtils.getInt(faction, PLAYER_DEFAULT_STANDING));
				}
				builder.removeHostileTowards(factionName);
				if (faction.has(HOSTILE_TOWARDS_FACTIONS)) {
					Map<String, Boolean> hostileTowards = parseHostileTowards(faction);
					hostileTowards.entrySet().stream().filter(Entry::getValue).map(Entry::getKey).forEach(builder::addHostileTowards);
					hostileTowards.entrySet().stream().filter(entry -> !entry.getValue()).map(Entry::getKey).forEach(builder::removeHostileTowards);
				}

				factions.add(builder.build());
			}
		}

		private Map<String, Boolean> parseHostileTowards(JsonObject json) {
			return JsonHelper.mapFromJson(json, HOSTILE_TOWARDS_FACTIONS, Entry::getKey, entry -> JsonUtils.getBoolean(entry.getValue(), ""));
		}
	}

	private static final FactionDefinition EMPTY_FACTION = new FactionDefinition(0, new HashSet<>());
}
