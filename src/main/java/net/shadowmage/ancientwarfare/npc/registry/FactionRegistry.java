package net.shadowmage.ancientwarfare.npc.registry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.JsonUtils;
import net.shadowmage.ancientwarfare.core.registry.IRegistryDataParser;
import net.shadowmage.ancientwarfare.core.util.parsing.JsonHelper;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

public class FactionRegistry {
	private FactionRegistry() {}

	private static final Map<String, FactionDefinition> factions = new HashMap<>();

	public static Set<String> getFactionNames() {
		return factions.keySet();
	}

	public static FactionDefinition getFaction(String name) {
		return factions.getOrDefault(name, EMPTY_FACTION);
	}

	public static Collection<FactionDefinition> getFactionDefinitions() {
		return factions.values();
	}

	public static class FactionParser implements IRegistryDataParser {
		private static final String PLAYER_DEFAULT_STANDING = "player_default_standing";
		private static final String STANDING_CAN_CHANGE = "standing_can_change";
		private static final String HOSTILE_TOWARDS_FACTIONS = "hostile_towards_factions";
		private static final String THEMED_BLOCKS = "themed_blocks";
		private static final String STANDING_CHANGES = "standing_changes";
		private static final String STANDING_SETTINGS = "standing_settings";

		@Override
		public String getName() {
			return "factions";
		}

		@Override
		public void parse(JsonObject json) {
			JsonObject defaults = JsonUtils.getJsonObject(json, "defaults");
			FactionDefinition defaultDefinition = new FactionDefinition(
					parseStandingSettings(defaults),
					parseHostileTowards(defaults).entrySet().stream().filter(Entry::getValue).map(Entry::getKey).collect(Collectors.toCollection(HashSet::new)),
					TargetRegistry.parseTargets(defaults).orElse(new HashSet<>()));

			JsonArray factionsArray = JsonUtils.getJsonArray(json, "factions");

			for (JsonElement e : factionsArray) {
				JsonObject faction = JsonUtils.getJsonObject(e, "faction");
				String factionName = JsonUtils.getString(faction, "name");
				FactionDefinition.CopyBuilder builder = defaultDefinition.copy(factionName, Integer.parseInt(JsonUtils.getString(faction, "color"), 16));
				if (faction.has(STANDING_SETTINGS)) {
					overrideStandingSettings(faction, builder);
				}
				builder.removeHostileTowards(factionName);
				if (faction.has(HOSTILE_TOWARDS_FACTIONS)) {
					Map<String, Boolean> hostileTowards = parseHostileTowards(faction);
					hostileTowards.entrySet().stream().filter(Entry::getValue).map(Entry::getKey).forEach(builder::addHostileTowards);
					hostileTowards.entrySet().stream().filter(entry -> !entry.getValue()).map(Entry::getKey).forEach(builder::removeHostileTowards);
				}

				TargetRegistry.parseTargets(faction).ifPresent(builder::overrideTargetList);

				if (faction.has(THEMED_BLOCKS)) {
					builder.overrideThemedBlocksTags(parseThemedBLocks(faction, factionName));
				}

				factions.put(factionName, builder.build());
			}
		}

		private void overrideStandingSettings(JsonObject faction, FactionDefinition.CopyBuilder builder) {
			JsonObject standingSettingsJson = JsonUtils.getJsonObject(faction, STANDING_SETTINGS);
			builder.setStandingSettings(standingSettings -> {
				if (standingSettingsJson.has(PLAYER_DEFAULT_STANDING)) {
					standingSettings.setPlayerDefaultStanding(JsonUtils.getInt(standingSettingsJson, PLAYER_DEFAULT_STANDING));
				}
				if (standingSettingsJson.has(STANDING_CAN_CHANGE)) {
					standingSettings.setStandingCanChange(JsonUtils.getBoolean(standingSettingsJson, STANDING_CAN_CHANGE));
				}
				if (standingSettingsJson.has(STANDING_CHANGES)) {
					standingSettings.overrideStandingChanges(parseStandingChanges(standingSettingsJson));
				}
			});
		}

		private FactionDefinition.StandingSettings parseStandingSettings(JsonObject factionSettings) {
			JsonObject standingSettings = JsonUtils.getJsonObject(factionSettings, STANDING_SETTINGS);
			return new FactionDefinition.StandingSettings(JsonUtils.getInt(standingSettings, PLAYER_DEFAULT_STANDING), JsonUtils.getBoolean(standingSettings, STANDING_CAN_CHANGE), parseStandingChanges(standingSettings));
		}

		private Map<String, Integer> parseStandingChanges(JsonObject json) {
			return JsonHelper.mapFromJson(json, STANDING_CHANGES, Entry::getKey, entry -> JsonUtils.getInt(entry.getValue(), entry.getKey()));
		}

		private Map<String, NBTTagCompound> parseThemedBLocks(JsonObject json, String factionName) {
			return JsonHelper.mapFromJson(json, THEMED_BLOCKS, Entry::getKey, entry -> getThemedBlocksNBT(entry.getValue(), factionName));
		}

		private NBTTagCompound getThemedBlocksNBT(JsonElement json, String factionName) {
			NBTTagCompound nbt;
			try {
				nbt = JsonToNBT.getTagFromJson(json.toString());
				nbt.setString("customData", factionName);
			}
			catch (NBTException e) {
				AncientWarfareNPC.LOG.error("Error parsing themed blocks nbt", e);
				nbt = new NBTTagCompound();
			}
			return nbt;
		}

		private Map<String, Boolean> parseHostileTowards(JsonObject json) {
			return JsonHelper.mapFromJson(json, HOSTILE_TOWARDS_FACTIONS, Entry::getKey, entry -> JsonUtils.getBoolean(entry.getValue(), ""));
		}

	}

	private static final FactionDefinition EMPTY_FACTION = new FactionDefinition(new FactionDefinition.StandingSettings(0, false, new HashMap<>()), new HashSet<>(), new HashSet<>()).copy("", -1).build();
}
