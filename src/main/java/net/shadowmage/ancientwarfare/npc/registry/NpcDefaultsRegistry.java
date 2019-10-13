package net.shadowmage.ancientwarfare.npc.registry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.core.registry.IRegistryDataParser;
import net.shadowmage.ancientwarfare.core.util.RegistryTools;
import net.shadowmage.ancientwarfare.core.util.parsing.JsonHelper;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFaction;
import net.shadowmage.ancientwarfare.npc.entity.faction.attributes.AdditionalAttributes;
import net.shadowmage.ancientwarfare.npc.entity.faction.attributes.IAdditionalAttribute;
import net.shadowmage.ancientwarfare.npc.init.AWNPCEntities;
import org.apache.commons.lang3.Range;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class NpcDefaultsRegistry {
	private static final String NPC_SUBTYPES_ELEMENT = "npc_subtypes";

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
			return new OwnedNpcDefault(TargetRegistry.parseTargets(defaults).orElse(new HashSet<>()), getAttributes(defaults), getExperienceDrop(defaults).orElse(0),
					getCanSwim(defaults).orElse(true), getCanBreakDoors(defaults).orElse(true), getEquipment(defaults));
		}

		private void parseSubtypes(JsonObject json, OwnedNpcDefault overallDefault) {
			ownedNpcDefaults.putAll(JsonHelper.mapFromJson(json, NPC_SUBTYPES_ELEMENT, Map.Entry::getKey,
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
			Optional<Set<String>> targets = TargetRegistry.parseTargets(data);
			if (targets.isPresent()) {
				npcSubtypeDefault = npcSubtypeDefault.overrideTargets(targets.get());
			}
			return npcSubtypeDefault;
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
					e -> parseEquipmentItem(e.getValue()));
		}

		private Item parseEquipmentItem(JsonElement itemJson) {
			try {
				return RegistryTools.getItem(JsonUtils.getString(itemJson, ""));
			}
			catch (MissingResourceException ex) {
				AncientWarfareNPC.LOG.error("Error parsing faction npc equipment: ", ex);
				return Items.AIR;
			}
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
		private static final String HEIGHT_ELEMENT = "height";
		private static final String THINNESS_ELEMENT = "thinness";

		@Override
		public String getName() {
			return "faction_npc_defaults";
		}

		@Override
		public void parse(JsonObject json) {
			Map<String, FactionNpcDefault> npcSubtypeDefaults = parseDefaultsWithSubtypes(json);
			parseFactions(json, npcSubtypeDefaults);
		}

		private Map<String, FactionNpcDefault> parseDefaultsWithSubtypes(JsonObject json) {
			JsonObject defaults = JsonUtils.getJsonObject(json, "defaults");
			FactionNpcDefault globalDefault = new FactionNpcDefault(getAttributes(defaults), getExperienceDrop(defaults).orElse(0),
					getCanSwim(defaults).orElse(true), getCanBreakDoors(defaults).orElse(true), getEquipment(defaults),
					getAdditionalAttributes(defaults), getEnabled(defaults).orElse(true), getLootTable(defaults).orElse(null),
					getHeightRange(defaults).orElse(Range.between(1.8f, 1.8f)), getThinness(defaults).orElse(1.0f));

			return parseSubtypes(defaults, globalDefault);
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
			JsonHelper.mapFromJson(json, "factions", factionNpcDefaults, Map.Entry::getKey, e -> {
				JsonObject factionElement = JsonUtils.getJsonObject(e.getValue(), "");
				Map<String, FactionNpcDefault> factionSubtypeDefaults = getFactionDefaults(factionElement, npcSubtypeDefaults);
				if (factionElement.has(NPC_SUBTYPES_ELEMENT)) {
					factionSubtypeDefaults = getFactionNpcDefaults(factionSubtypeDefaults, factionElement.get(NPC_SUBTYPES_ELEMENT));
				}
				return factionSubtypeDefaults;
			});
			fillRemainingFactionDefaults(npcSubtypeDefaults);
		}

		private FactionNpcDefault getSubtypeDefault(String subtype, JsonElement json, Function<String, FactionNpcDefault> getEntry) {
			JsonObject data = JsonUtils.getJsonObject(json, "");
			FactionNpcDefault npcSubtypeDefault = getEntry.apply(subtype);
			npcSubtypeDefault = npcSubtypeDefault.setAttributes(getAttributes(data));
			npcSubtypeDefault = getExperienceDrop(data).map(npcSubtypeDefault::setExperienceDrop).orElse(npcSubtypeDefault);
			npcSubtypeDefault = getCanSwim(data).map(npcSubtypeDefault::setCanSwim).orElse(npcSubtypeDefault);
			npcSubtypeDefault = getCanBreakDoors(data).map(npcSubtypeDefault::setCanBreakDoors).orElse(npcSubtypeDefault);
			npcSubtypeDefault = npcSubtypeDefault.setEquipment(getEquipment(data));
			npcSubtypeDefault = npcSubtypeDefault.setAdditionalAttributes(getAdditionalAttributes(data));
			npcSubtypeDefault = getEnabled(data).map(npcSubtypeDefault::setEnabled).orElse(npcSubtypeDefault);
			npcSubtypeDefault = getLootTable(data).map(npcSubtypeDefault::setLootTable).orElse(npcSubtypeDefault);
			npcSubtypeDefault = getHeightRange(data).map(npcSubtypeDefault::setHeightRange).orElse(npcSubtypeDefault);
			npcSubtypeDefault = getThinness(data).map(npcSubtypeDefault::setThinness).orElse(npcSubtypeDefault);
			return npcSubtypeDefault;
		}

		private Optional<Boolean> getEnabled(JsonObject data) {
			return data.has("enabled") ? Optional.of(JsonUtils.getBoolean(data, "enabled")) : Optional.empty();
		}

		private Optional<Range<Float>> getHeightRange(JsonObject data) {
			if (!data.has(HEIGHT_ELEMENT)) {
				return Optional.empty();
			}

			if (JsonUtils.isJsonPrimitive(data, HEIGHT_ELEMENT)) {
				return Optional.of(Range.between(JsonUtils.getFloat(data, HEIGHT_ELEMENT), JsonUtils.getFloat(data, HEIGHT_ELEMENT)));
			} else {
				JsonObject range = JsonUtils.getJsonObject(data, HEIGHT_ELEMENT);
				return Optional.of(Range.between(JsonUtils.getFloat(range, "min"), JsonUtils.getFloat(range, "max")));
			}
		}

		private Optional<Float> getThinness(JsonObject data) {
			if (!data.has(THINNESS_ELEMENT)) {
				return Optional.empty();
			}

			return Optional.of(JsonUtils.getFloat(data, THINNESS_ELEMENT));
		}

		private Optional<ResourceLocation> getLootTable(JsonObject data) {
			return data.has("loot_table") ? Optional.of(new ResourceLocation(JsonUtils.getString(data, "loot_table"))) : Optional.empty();
		}

		private Map<String, FactionNpcDefault> parseSubtypes(JsonObject json, FactionNpcDefault overallDefault) {
			Map<String, FactionNpcDefault> npcSubtypeDefaults = JsonHelper.mapFromJson(json, NPC_SUBTYPES_ELEMENT, Map.Entry::getKey,
					e -> getSubtypeDefault(e.getKey(), e.getValue(), s -> overallDefault));
			fillRemainingSubtypeDefaults(overallDefault, npcSubtypeDefaults);
			return npcSubtypeDefaults;
		}

		private Map<String, FactionNpcDefault> getFactionDefaults(JsonElement json, Map<String, FactionNpcDefault> npcSubtypeDefaults) {
			Map<String, FactionNpcDefault> ret = new HashMap<>();
			for (String subtype : npcSubtypeDefaults.keySet()) {
				ret.put(subtype, getSubtypeDefault(subtype, json, npcSubtypeDefaults::get));
			}
			return ret;
		}

		private Map<String, FactionNpcDefault> getFactionNpcDefaults(Map<String, FactionNpcDefault> npcSubtypeDefaults, JsonElement subtypesElement) {
			Map<String, FactionNpcDefault> typeDefaults = JsonHelper.mapFromJson(subtypesElement, Map.Entry::getKey,
					e -> getSubtypeDefault(e.getKey(), e.getValue(), npcSubtypeDefaults::get));

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
			for (String subtype : getFactionNpcSubtypes()) {
				if (!npcSubtypeDefaults.keySet().contains(subtype)) {
					npcSubtypeDefaults.put(subtype, overallDefault);
				}
			}
		}

		private List<String> getFactionNpcSubtypes() {
			return AWNPCEntities.getNpcMap().keySet().stream().filter(k -> k.startsWith(FACTION_NPC_PREFIX)).map(k -> k.replace(FACTION_NPC_PREFIX, "")).collect(Collectors.toList());
		}
	}
}
