package net.shadowmage.ancientwarfare.npc.registry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.JsonUtils;
import net.shadowmage.ancientwarfare.core.registry.IRegistryDataParser;
import net.shadowmage.ancientwarfare.core.util.parsing.JsonHelper;
import net.shadowmage.ancientwarfare.core.util.parsing.ResourceLocationMatcher;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
		private static final String ENTITIES_TO_TARGET = "entities_to_target";

		@Override
		public String getName() {
			return "factions";
		}

		@Override
		public void parse(JsonObject json) {
			JsonObject defaults = JsonUtils.getJsonObject(json, "defaults");
			FactionDefinition defaultDefinition = new FactionDefinition(JsonUtils.getInt(defaults, PLAYER_DEFAULT_STANDING),
					parseHostileTowards(defaults).entrySet().stream().filter(Entry::getValue).map(Entry::getKey).collect(Collectors.toCollection(HashSet::new)),
					parseTargetList(defaults));

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
				if (faction.has(ENTITIES_TO_TARGET)) {
					builder.overrideTargetList(parseTargetList(faction));
				}

				factions.add(builder.build());
			}
		}

		private Map<String, Boolean> parseHostileTowards(JsonObject json) {
			return JsonHelper.mapFromJson(json, HOSTILE_TOWARDS_FACTIONS, Entry::getKey, entry -> JsonUtils.getBoolean(entry.getValue(), ""));
		}

		private Set<ResourceLocationMatcher> parseTargetList(JsonObject json) {
			if (!json.has(ENTITIES_TO_TARGET)) {
				return Collections.emptySet();
			}
			JsonArray targets = JsonUtils.getJsonArray(json, ENTITIES_TO_TARGET);
			return StreamSupport.stream(targets.spliterator(), false).map(e -> new ResourceLocationMatcher(JsonUtils.getString(e, "")))
					.collect(Collectors.toCollection(HashSet::new));
		}
	}

	private static final FactionDefinition EMPTY_FACTION = new FactionDefinition(0, new HashSet<>(), new HashSet<>());
}
