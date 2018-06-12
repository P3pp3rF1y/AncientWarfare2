package net.shadowmage.ancientwarfare.npc.registry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.util.JsonUtils;
import net.shadowmage.ancientwarfare.core.registry.IRegistryDataParser;
import net.shadowmage.ancientwarfare.core.util.parsing.JsonHelper;
import net.shadowmage.ancientwarfare.core.util.parsing.ResourceLocationMatcher;
import net.shadowmage.ancientwarfare.npc.entity.AWNPCEntityLoader;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFaction;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class FactionRegistry {
	private FactionRegistry() {}

	private static Set<FactionDefinition> factions = new HashSet<>();
	private static Map<String, Map<String, FactionNpcDefault>> factionNpcDefaults = new HashMap<>();

	public static Set<FactionDefinition> getFactions() {
		return factions;
	}

	public static Set<String> getFactionNames() {
		return factions.stream().map(FactionDefinition::getName).collect(Collectors.toCollection(HashSet::new));
	}

	public static FactionDefinition getFaction(String name) {
		return factions.stream().filter(f -> f.getName().equals(name)).findFirst().orElse(EMPTY_FACTION);
	}

	public static FactionNpcDefault getFactionNpcDefault(NpcFaction npc) {
		return factionNpcDefaults.get(npc.getFaction()).get(npc.getNpcType());
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
				FactionDefinition.CopyBuilder builder = defaultDefinition.copy(JsonUtils.getString(faction, "name"), Integer.parseInt(JsonUtils.getString(faction, "color"), 16));
				if (faction.has(PLAYER_DEFAULT_STANDING)) {
					builder.setPlayerDefaultStanding(JsonUtils.getInt(faction, PLAYER_DEFAULT_STANDING));
				}
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

	public static class FactionNpcDefaultsParser implements IRegistryDataParser {

		@Override
		public String getName() {
			return "faction_npc_defaults";
		}

		@Override
		public void parse(JsonObject json) {
			FactionNpcDefault overallDefault = parseDefaults(json);

			Map<String, FactionNpcDefault> npcSubtypeDefaults = parseSubtypes(json, overallDefault);

			parseFactions(json, npcSubtypeDefaults);
		}

		private void parseFactions(JsonObject json, Map<String, FactionNpcDefault> npcSubtypeDefaults) {
			JsonHelper.mapFromJson(json, "factions", factionNpcDefaults, Entry::getKey, e -> getFactionDefaults(npcSubtypeDefaults, e));
			fillRemainingFactionDefaults(npcSubtypeDefaults);
		}

		private Map<String, FactionNpcDefault> parseSubtypes(JsonObject json, FactionNpcDefault overallDefault) {
			Map<String, FactionNpcDefault> npcSubtypeDefaults = JsonHelper.mapFromJson(json, "npc_subtypes", Entry::getKey,
					e -> getSubtypeDefault(e, overallDefault::setNpcSubtype));
			fillRemainingSubtypeDefaults(overallDefault, npcSubtypeDefaults);
			return npcSubtypeDefaults;
		}

		private FactionNpcDefault parseDefaults(JsonObject json) {
			JsonObject defaults = JsonUtils.getJsonObject(json, "defaults");
			return new FactionNpcDefault(getTargetList(defaults), getAttributes(defaults), getExperienceDrop(defaults).orElse(0),
					getCanSwim(defaults).orElse(true), getCanBreakDoors(defaults).orElse(true), getEquipment(defaults));
		}

		private Optional<Boolean> getCanBreakDoors(JsonObject json) {
			return json.has("can_break_doors") ? Optional.of(JsonUtils.getBoolean(json, "can_break_doors")) : Optional.empty();
		}

		private Optional<Boolean> getCanSwim(JsonObject json) {
			return json.has("can_swim") ? Optional.of(JsonUtils.getBoolean(json, "can_swim")) : Optional.empty();
		}

		private Optional<Integer> getExperienceDrop(JsonObject json) {
			return json.has("experience_drop") ? Optional.of(JsonUtils.getInt(json, "experience_drop")) : Optional.empty();
		}

		private Map<String, Double> getAttributes(JsonObject json) {
			if (!json.has("attributes")) {
				return Collections.emptyMap();
			}

			return JsonHelper.mapFromJson(json, "attributes", Entry::getKey, e -> (double) JsonUtils.getFloat(e.getValue(), ""));
		}

		private Map<Integer, Item> getEquipment(JsonObject json) {
			if (!json.has("equipment")) {
				return Collections.emptyMap();
			}
			return JsonHelper.mapFromJson(json, "equipment", e -> parseEquipmentSlot(e.getKey()),
					e -> JsonHelper.getItem(JsonUtils.getString(e.getValue(), "")));
		}

		private int parseEquipmentSlot(String slotName) {
			int slot = Arrays.stream(EntityEquipmentSlot.values()).filter(s -> s.getName().equals(slotName)).map(Enum::ordinal).findFirst().orElse(-1);
			if (slot > -1) {
				return slot;
			}
			switch (slotName) {
				case "work":
					return NpcBase.ORDER_SLOT;
				case "upkeep":
					return NpcBase.UPKEEP_SLOT;
				default:
					throw new JsonParseException("Invalid equipment slot name \"" + slotName + "\"");
			}
		}

		private Map<String, FactionNpcDefault> getFactionDefaults(Map<String, FactionNpcDefault> npcSubtypeDefaults, Entry<String, JsonElement> entry) {
			String faction = entry.getKey();
			Map<String, FactionNpcDefault> typeDefaults = JsonHelper.mapFromJson(entry.getValue(), Entry::getKey,
					e -> getSubtypeDefault(e, subtype -> npcSubtypeDefaults.get(subtype).setFaction(faction)));

			for (Entry<String, FactionNpcDefault> subtypeDefault : npcSubtypeDefaults.entrySet()) {
				if (!typeDefaults.keySet().contains(subtypeDefault.getKey())) {
					typeDefaults.put(subtypeDefault.getKey(), subtypeDefault.getValue().setFaction(faction));
				}
			}
			return typeDefaults;
		}

		private void fillRemainingFactionDefaults(Map<String, FactionNpcDefault> npcSubtypeDefaults) {
			for (String faction : FactionRegistry.getFactionNames()) {
				if (!factionNpcDefaults.keySet().contains(faction)) {
					Map<String, FactionNpcDefault> typeDefaults = new HashMap<>();
					npcSubtypeDefaults.forEach((key, value) -> typeDefaults.put(key, value.setFaction(faction)));
					factionNpcDefaults.put(faction, typeDefaults);
				}
			}
		}

		private void fillRemainingSubtypeDefaults(FactionNpcDefault overallDefault, Map<String, FactionNpcDefault> npcSubtypeDefaults) {
			for (String subtype : AWNPCEntityLoader.getNpcMap().keySet().stream().filter(k -> k.startsWith("faction.")).map(k -> k.replace("faction.", "")).collect(Collectors.toList())) {
				if (!npcSubtypeDefaults.keySet().contains(subtype)) {
					npcSubtypeDefaults.put(subtype, overallDefault.setNpcSubtype(subtype));
				}
			}
		}

		private FactionNpcDefault getSubtypeDefault(Entry<String, JsonElement> entry, Function<String, FactionNpcDefault> setKey) {
			JsonObject data = JsonUtils.getJsonObject(entry.getValue(), "");
			FactionNpcDefault npcSubtypeDefault = setKey.apply(entry.getKey());
			npcSubtypeDefault = npcSubtypeDefault.addTargets(getTargetList(data));
			npcSubtypeDefault = npcSubtypeDefault.setAttributes(getAttributes(data));
			npcSubtypeDefault = getExperienceDrop(data).map(npcSubtypeDefault::setExperienceDrop).orElse(npcSubtypeDefault);
			npcSubtypeDefault = getCanSwim(data).map(npcSubtypeDefault::setCanSwim).orElse(npcSubtypeDefault);
			npcSubtypeDefault = getCanBreakDoors(data).map(npcSubtypeDefault::setCanBreakDoors).orElse(npcSubtypeDefault);
			npcSubtypeDefault = npcSubtypeDefault.setEquipment(getEquipment(data));
			return npcSubtypeDefault;
		}

		private Set<ResourceLocationMatcher> getTargetList(JsonObject json) {
			if (!json.has("entities_to_target")) {
				return Collections.emptySet();
			}
			JsonArray targets = JsonUtils.getJsonArray(json, "entities_to_target");
			return StreamSupport.stream(targets.spliterator(), false).map(e -> new ResourceLocationMatcher(JsonUtils.getString(e, "")))
					.collect(Collectors.toCollection(HashSet::new));
		}
	}

	private static final FactionDefinition EMPTY_FACTION = new FactionDefinition(0, new HashSet<>());
}
