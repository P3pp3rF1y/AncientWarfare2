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
import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFaction;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class NpcDefaultsRegistry {
	private NpcDefaultsRegistry() {}

	private static Map<String, Map<String, NpcDefault>> factionNpcDefaults = new HashMap<>();
	private static Map<String, NpcDefault> ownedNpcDefaults = new HashMap<>();

	public static NpcDefault getFactionNpcDefault(NpcFaction npc) {
		return factionNpcDefaults.get(npc.getFaction()).get(npc.getNpcType());
	}

	public static NpcDefault getOwnedNpcDefault(NpcPlayerOwned npc) {
		return ownedNpcDefaults.get(npc.getNpcType());
	}

	private static final String FACTION_NPC_PREFIX = "faction.";

	public static class OwnedNpcDefaultsParser extends NpcDefaultsParserBase {
		@Override
		public String getName() {
			return "owned_npc_defaults";
		}

		@Override
		public void parse(JsonObject json) {
			NpcDefault overallDefault = parseDefaults(json);

			parseSubtypes(json, overallDefault);
		}

		private void parseSubtypes(JsonObject json, NpcDefault overallDefault) {
			ownedNpcDefaults.putAll(JsonHelper.mapFromJson(json, "npc_subtypes", Map.Entry::getKey,
					e -> getSubtypeDefault(e, s -> overallDefault)));
			fillRemainingSubtypeDefaults(overallDefault, ownedNpcDefaults);
		}

		private void fillRemainingSubtypeDefaults(NpcDefault overallDefault, Map<String, NpcDefault> npcSubtypeDefaults) {
			for (String subtype : AWNPCEntityLoader.getNpcMap().keySet().stream().filter(k -> !k.startsWith(FACTION_NPC_PREFIX)).collect(Collectors.toList())) {
				if (!npcSubtypeDefaults.keySet().contains(subtype)) {
					npcSubtypeDefaults.put(subtype, overallDefault);
				}
			}
		}
	}

	private abstract static class NpcDefaultsParserBase implements IRegistryDataParser {

		protected NpcDefault getSubtypeDefault(Map.Entry<String, JsonElement> entry, Function<String, NpcDefault> setKey) {
			JsonObject data = JsonUtils.getJsonObject(entry.getValue(), "");
			NpcDefault npcSubtypeDefault = setKey.apply(entry.getKey());
			npcSubtypeDefault = npcSubtypeDefault.addTargets(getTargetList(data));
			npcSubtypeDefault = npcSubtypeDefault.setAttributes(getAttributes(data));
			npcSubtypeDefault = getExperienceDrop(data).map(npcSubtypeDefault::setExperienceDrop).orElse(npcSubtypeDefault);
			npcSubtypeDefault = getCanSwim(data).map(npcSubtypeDefault::setCanSwim).orElse(npcSubtypeDefault);
			npcSubtypeDefault = getCanBreakDoors(data).map(npcSubtypeDefault::setCanBreakDoors).orElse(npcSubtypeDefault);
			npcSubtypeDefault = npcSubtypeDefault.setEquipment(getEquipment(data));
			return npcSubtypeDefault;
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

			return JsonHelper.mapFromJson(json, "attributes", Map.Entry::getKey, e -> (double) JsonUtils.getFloat(e.getValue(), ""));
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

		private Set<ResourceLocationMatcher> getTargetList(JsonObject json) {
			if (!json.has("entities_to_target")) {
				return Collections.emptySet();
			}
			JsonArray targets = JsonUtils.getJsonArray(json, "entities_to_target");
			return StreamSupport.stream(targets.spliterator(), false).map(e -> new ResourceLocationMatcher(JsonUtils.getString(e, "")))
					.collect(Collectors.toCollection(HashSet::new));
		}

		protected NpcDefault parseDefaults(JsonObject json) {
			JsonObject defaults = JsonUtils.getJsonObject(json, "defaults");
			return new NpcDefault(getTargetList(defaults), getAttributes(defaults), getExperienceDrop(defaults).orElse(0),
					getCanSwim(defaults).orElse(true), getCanBreakDoors(defaults).orElse(true), getEquipment(defaults));
		}
	}

	public static class FactionNpcDefaultsParser extends NpcDefaultsParserBase {
		@Override
		public String getName() {
			return "faction_npc_defaults";
		}

		@Override
		public void parse(JsonObject json) {
			NpcDefault overallDefault = parseDefaults(json);

			Map<String, NpcDefault> npcSubtypeDefaults = parseSubtypes(json, overallDefault);

			parseFactions(json, npcSubtypeDefaults);
		}

		private void parseFactions(JsonObject json, Map<String, NpcDefault> npcSubtypeDefaults) {
			JsonHelper.mapFromJson(json, "factions", factionNpcDefaults, Map.Entry::getKey, e -> getFactionDefaults(npcSubtypeDefaults, e));
			fillRemainingFactionDefaults(npcSubtypeDefaults);
		}

		private Map<String, NpcDefault> parseSubtypes(JsonObject json, NpcDefault overallDefault) {
			Map<String, NpcDefault> npcSubtypeDefaults = JsonHelper.mapFromJson(json, "npc_subtypes", Map.Entry::getKey,
					e -> getSubtypeDefault(e, s -> overallDefault));
			fillRemainingSubtypeDefaults(overallDefault, npcSubtypeDefaults);
			return npcSubtypeDefaults;
		}

		private Map<String, NpcDefault> getFactionDefaults(Map<String, NpcDefault> npcSubtypeDefaults, Map.Entry<String, JsonElement> entry) {
			Map<String, NpcDefault> typeDefaults = JsonHelper.mapFromJson(entry.getValue(), Map.Entry::getKey,
					e -> getSubtypeDefault(e, npcSubtypeDefaults::get));

			for (Map.Entry<String, NpcDefault> subtypeDefault : npcSubtypeDefaults.entrySet()) {
				if (!typeDefaults.keySet().contains(subtypeDefault.getKey())) {
					typeDefaults.put(subtypeDefault.getKey(), subtypeDefault.getValue());
				}
			}
			return typeDefaults;
		}

		private void fillRemainingFactionDefaults(Map<String, NpcDefault> npcSubtypeDefaults) {
			for (String faction : FactionRegistry.getFactionNames()) {
				if (!factionNpcDefaults.keySet().contains(faction)) {
					Map<String, NpcDefault> typeDefaults = new HashMap<>();
					npcSubtypeDefaults.forEach(typeDefaults::put);
					factionNpcDefaults.put(faction, typeDefaults);
				}
			}
		}

		private void fillRemainingSubtypeDefaults(NpcDefault overallDefault, Map<String, NpcDefault> npcSubtypeDefaults) {
			for (String subtype : AWNPCEntityLoader.getNpcMap().keySet().stream().filter(k -> k.startsWith(FACTION_NPC_PREFIX)).map(k -> k.replace(FACTION_NPC_PREFIX, "")).collect(Collectors.toList())) {
				if (!npcSubtypeDefaults.keySet().contains(subtype)) {
					npcSubtypeDefaults.put(subtype, overallDefault);
				}
			}
		}
	}
}
