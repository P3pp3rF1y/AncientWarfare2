package net.shadowmage.ancientwarfare.npc.registry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.core.registry.IRegistryDataParser;
import net.shadowmage.ancientwarfare.core.util.RegistryTools;
import net.shadowmage.ancientwarfare.core.util.parsing.JsonHelper;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFaction;
import net.shadowmage.ancientwarfare.npc.entity.faction.attributes.AdditionalAttributes;
import net.shadowmage.ancientwarfare.npc.entity.faction.attributes.IAdditionalAttribute;
import net.shadowmage.ancientwarfare.npc.init.AWNPCEntities;

import java.util.Arrays;
import java.util.Collection;
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

	private static Map<String, Map<String, FactionNpcDefault>> factionNpcDefaults = new HashMap<>();
	private static Map<String, OwnedNpcDefault> ownedNpcDefaults = new HashMap<>();

	public static FactionNpcDefault getFactionNpcDefault(String faction, String npcType) {
		return factionNpcDefaults.get(faction).get(npcType);
	}

	public static FactionNpcDefault getFactionNpcDefault(NpcFaction npc) {
		return factionNpcDefaults.get(npc.getFaction()).get(npc.getNpcType());
	}

	public static OwnedNpcDefault getOwnedNpcDefault(NpcPlayerOwned npc) {
		return ownedNpcDefaults.get(npc.getNpcType());
	}

	private static final String FACTION_NPC_PREFIX = "faction.";

	public static Collection<OwnedNpcDefault> getOwnedNpcDefaults() {
		return ownedNpcDefaults.values();
	}

	public static class OwnedNpcDefaultsParser extends NpcDefaultsParserBase {
		@Override
		public String getName() {
			return "owned_npc_defaults";
		}

		@Override
		public void parse(JsonObject json) {
			OwnedNpcDefault overallDefault = parseDefaults(json);

			parseSubtypes(json, overallDefault);
		}

		private OwnedNpcDefault parseDefaults(JsonObject json) {
			JsonObject defaults = JsonUtils.getJsonObject(json, "defaults");
			return new OwnedNpcDefault(getTargetList(defaults), getAttributes(defaults), getExperienceDrop(defaults).orElse(0),
					getCanSwim(defaults).orElse(true), getCanBreakDoors(defaults).orElse(true), getEquipment(defaults));
		}

		private void parseSubtypes(JsonObject json, OwnedNpcDefault overallDefault) {
			ownedNpcDefaults.putAll(JsonHelper.mapFromJson(json, "npc_subtypes", Map.Entry::getKey,
					e -> getSubtypeDefault(e, s -> overallDefault)));
			fillRemainingSubtypeDefaults(overallDefault, ownedNpcDefaults);
		}

		private OwnedNpcDefault getSubtypeDefault(Map.Entry<String, JsonElement> entry, Function<String, OwnedNpcDefault> setKey) {
			JsonObject data = JsonUtils.getJsonObject(entry.getValue(), "");
			OwnedNpcDefault npcSubtypeDefault = setKey.apply(entry.getKey());
			npcSubtypeDefault = npcSubtypeDefault.setAttributes(getAttributes(data));
			npcSubtypeDefault = getExperienceDrop(data).map(npcSubtypeDefault::setExperienceDrop).orElse(npcSubtypeDefault);
			npcSubtypeDefault = getCanSwim(data).map(npcSubtypeDefault::setCanSwim).orElse(npcSubtypeDefault);
			npcSubtypeDefault = getCanBreakDoors(data).map(npcSubtypeDefault::setCanBreakDoors).orElse(npcSubtypeDefault);
			npcSubtypeDefault = npcSubtypeDefault.setEquipment(getEquipment(data));
			Set<String> overrideTargetList = getTargetList(data);
			if (!overrideTargetList.isEmpty()) {
				npcSubtypeDefault = npcSubtypeDefault.overrideTargets(overrideTargetList);
			}
			return npcSubtypeDefault;
		}

		private Set<String> getTargetList(JsonObject json) {
			if (!json.has("entities_to_target")) {
				return Collections.emptySet();
			}
			JsonArray targets = JsonUtils.getJsonArray(json, "entities_to_target");
			return StreamSupport.stream(targets.spliterator(), false).map(e -> JsonUtils.getString(e, ""))
					.collect(Collectors.toCollection(HashSet::new));
		}

		private void fillRemainingSubtypeDefaults(OwnedNpcDefault overallDefault, Map<String, OwnedNpcDefault> npcSubtypeDefaults) {
			for (String subtype : AWNPCEntities.getNpcMap().keySet().stream().filter(k -> !k.startsWith(FACTION_NPC_PREFIX)).collect(Collectors.toList())) {
				if (!npcSubtypeDefaults.keySet().contains(subtype)) {
					npcSubtypeDefaults.put(subtype, overallDefault);
				}
			}
		}
	}

	private abstract static class NpcDefaultsParserBase implements IRegistryDataParser {
		protected Optional<Boolean> getCanBreakDoors(JsonObject json) {
			return json.has("can_break_doors") ? Optional.of(JsonUtils.getBoolean(json, "can_break_doors")) : Optional.empty();
		}

		protected Optional<Boolean> getCanSwim(JsonObject json) {
			return json.has("can_swim") ? Optional.of(JsonUtils.getBoolean(json, "can_swim")) : Optional.empty();
		}

		protected Optional<Integer> getExperienceDrop(JsonObject json) {
			return json.has("experience_drop") ? Optional.of(JsonUtils.getInt(json, "experience_drop")) : Optional.empty();
		}

		protected Map<String, Double> getAttributes(JsonObject json) {
			if (!json.has("attributes")) {
				return Collections.emptyMap();
			}

			return JsonHelper.mapFromJson(json, "attributes", Map.Entry::getKey, e -> (double) JsonUtils.getFloat(e.getValue(), ""));
		}

		protected Map<Integer, Item> getEquipment(JsonObject json) {
			if (!json.has("equipment")) {
				return Collections.emptyMap();
			}
			return JsonHelper.mapFromJson(json, "equipment", e -> parseEquipmentSlot(e.getKey()),
					e -> RegistryTools.getItem(JsonUtils.getString(e.getValue(), "")));
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
	}

	public static class FactionNpcDefaultsParser extends NpcDefaultsParserBase {
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

		private FactionNpcDefault parseDefaults(JsonObject json) {
			JsonObject defaults = JsonUtils.getJsonObject(json, "defaults");
			return new FactionNpcDefault(getAttributes(defaults), getExperienceDrop(defaults).orElse(0),
					getCanSwim(defaults).orElse(true), getCanBreakDoors(defaults).orElse(true), getEquipment(defaults),
					getAdditionalAttributes(defaults), getEnabled(defaults).orElse(true), getLootTable(defaults).orElse(null));
		}

		private Map<IAdditionalAttribute<?>, Object> getAdditionalAttributes(JsonObject json) {
			if (!json.has("additional_attributes")) {
				return Collections.emptyMap();
			}
			//noinspection unchecked
			return JsonHelper.mapFromJson(json, "additional_attributes", e -> AdditionalAttributes.getByName(e.getKey()),
					e -> parseAttributeValue(AdditionalAttributes.getByName(e.getKey()), e.getValue().getAsString()));
		}

		private <T> T parseAttributeValue(IAdditionalAttribute<T> attribute, String value) {
			return attribute.parseValue(value).orElse(attribute.getValueClass().cast(null));
		}

		private void parseFactions(JsonObject json, Map<String, FactionNpcDefault> npcSubtypeDefaults) {
			JsonHelper.mapFromJson(json, "factions", factionNpcDefaults, Map.Entry::getKey, e -> getFactionDefaults(npcSubtypeDefaults, e));
			fillRemainingFactionDefaults(npcSubtypeDefaults);
		}

		private FactionNpcDefault getSubtypeDefault(Map.Entry<String, JsonElement> entry, Function<String, FactionNpcDefault> setKey) {
			JsonObject data = JsonUtils.getJsonObject(entry.getValue(), "");
			FactionNpcDefault npcSubtypeDefault = setKey.apply(entry.getKey());
			npcSubtypeDefault = npcSubtypeDefault.setAttributes(getAttributes(data));
			npcSubtypeDefault = getExperienceDrop(data).map(npcSubtypeDefault::setExperienceDrop).orElse(npcSubtypeDefault);
			npcSubtypeDefault = getCanSwim(data).map(npcSubtypeDefault::setCanSwim).orElse(npcSubtypeDefault);
			npcSubtypeDefault = getCanBreakDoors(data).map(npcSubtypeDefault::setCanBreakDoors).orElse(npcSubtypeDefault);
			npcSubtypeDefault = npcSubtypeDefault.setEquipment(getEquipment(data));
			npcSubtypeDefault = npcSubtypeDefault.setAdditionalAttributes(getAdditionalAttributes(data));
			npcSubtypeDefault = getEnabled(data).map(npcSubtypeDefault::setEnabled).orElse(npcSubtypeDefault);
			npcSubtypeDefault = getLootTable(data).map(npcSubtypeDefault::setLootTable).orElse(npcSubtypeDefault);
			return npcSubtypeDefault;
		}

		private Optional<Boolean> getEnabled(JsonObject data) {
			return data.has("enabled") ? Optional.of(JsonUtils.getBoolean(data, "enabled")) : Optional.empty();
		}

		private Optional<ResourceLocation> getLootTable(JsonObject data) {
			return data.has("loot_table") ? Optional.of(new ResourceLocation(JsonUtils.getString(data, "loot_table"))) : Optional.empty();
		}

		private Map<String, FactionNpcDefault> parseSubtypes(JsonObject json, FactionNpcDefault overallDefault) {
			Map<String, FactionNpcDefault> npcSubtypeDefaults = JsonHelper.mapFromJson(json, "npc_subtypes", Map.Entry::getKey,
					e -> getSubtypeDefault(e, s -> overallDefault));
			fillRemainingSubtypeDefaults(overallDefault, npcSubtypeDefaults);
			return npcSubtypeDefaults;
		}

		private Map<String, FactionNpcDefault> getFactionDefaults(Map<String, FactionNpcDefault> npcSubtypeDefaults, Map.Entry<String, JsonElement> entry) {
			Map<String, FactionNpcDefault> typeDefaults = JsonHelper.mapFromJson(entry.getValue(), Map.Entry::getKey,
					e -> getSubtypeDefault(e, npcSubtypeDefaults::get));

			for (Map.Entry<String, FactionNpcDefault> subtypeDefault : npcSubtypeDefaults.entrySet()) {
				if (!typeDefaults.keySet().contains(subtypeDefault.getKey())) {
					typeDefaults.put(subtypeDefault.getKey(), subtypeDefault.getValue());
				}
			}
			return typeDefaults;
		}

		private void fillRemainingFactionDefaults(Map<String, FactionNpcDefault> npcSubtypeDefaults) {
			for (String faction : FactionRegistry.getFactionNames()) {
				if (!factionNpcDefaults.keySet().contains(faction)) {
					Map<String, FactionNpcDefault> typeDefaults = new HashMap<>();
					npcSubtypeDefaults.forEach(typeDefaults::put);
					factionNpcDefaults.put(faction, typeDefaults);
				}
			}
		}

		private void fillRemainingSubtypeDefaults(FactionNpcDefault overallDefault, Map<String, FactionNpcDefault> npcSubtypeDefaults) {
			for (String subtype : AWNPCEntities.getNpcMap().keySet().stream().filter(k -> k.startsWith(FACTION_NPC_PREFIX)).map(k -> k.replace(FACTION_NPC_PREFIX, "")).collect(Collectors.toList())) {
				if (!npcSubtypeDefaults.keySet().contains(subtype)) {
					npcSubtypeDefaults.put(subtype, overallDefault);
				}
			}
		}
	}
}
