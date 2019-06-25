package net.shadowmage.ancientwarfare.npc.registry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.JsonUtils;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.registry.IRegistryDataParser;
import net.shadowmage.ancientwarfare.core.util.parsing.JsonHelper;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

public class FactionRegistry {
	private FactionRegistry() {}

	private static Map<String, FactionDefinition> factions = new HashMap<>();

	private static Map<String, Set<String>> targetLists = new HashMap<>();

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
		private static final String HOSTILE_TOWARDS_FACTIONS = "hostile_towards_factions";
		private static final String TARGETS = "targets";
		private static final String TARGET_LISTS = "lists";
		private static final String TARGETS_INCLUDE = "include";
		private static final String TARGETS_EXCLUDE = "exclude";
		private static final String THEMED_BLOCKS = "themed_blocks";

		@Override
		public String getName() {
			return "factions";
		}

		@Override
		public void parse(JsonObject json) {
			JsonObject defaults = JsonUtils.getJsonObject(json, "defaults");
			FactionDefinition defaultDefinition = new FactionDefinition(JsonUtils.getInt(defaults, PLAYER_DEFAULT_STANDING),
					parseHostileTowards(defaults).entrySet().stream().filter(Entry::getValue).map(Entry::getKey).collect(Collectors.toCollection(HashSet::new)),
					parseTargets(defaults));

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
				if (faction.has(TARGETS)) {
					builder.overrideTargetList(parseTargets(faction));
				}
				if (faction.has(THEMED_BLOCKS)) {
					builder.overrideThemedBlocksTags(parseThemedBLocks(faction, factionName));
				}

				factions.put(factionName, builder.build());
			}
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

		private Set<String> parseTargets(JsonObject json) {
			if (!json.has(TARGETS)) {
				return Collections.emptySet();
			}

			JsonObject targets = JsonUtils.getJsonObject(json, TARGETS);
			Set<String> entitiesToTarget = new HashSet<>();
			if (targets.has(TARGET_LISTS)) {
				Set<String> lists = JsonHelper.setFromJson(targets.get(TARGET_LISTS), e -> JsonUtils.getString(e, ""));

				for (String list : lists) {
					if (targetLists.containsKey(list)) {
						entitiesToTarget.addAll(targetLists.get(list));
					} else {
						AncientWarfareCore.LOG.error("Skipping unknown target list - {}", list);
					}
				}
			}

			if (targets.has(TARGETS_INCLUDE)) {
				entitiesToTarget.addAll(JsonHelper.setFromJson(targets.get(TARGETS_INCLUDE), e -> JsonUtils.getString(e, "")));
			}

			if (targets.has(TARGETS_EXCLUDE)) {
				entitiesToTarget.removeAll(JsonHelper.setFromJson(targets.get(TARGETS_EXCLUDE), e -> JsonUtils.getString(e, "")));
			}

			return entitiesToTarget;
		}
	}

	public static class TargetListParser implements IRegistryDataParser {
		@Override
		public String getName() {
			return "target_lists";
		}

		@SuppressWarnings("squid:S2696")
		@Override
		public void parse(JsonObject json) {
			targetLists = JsonHelper.mapFromJson(json, Map.Entry::getKey, entry -> JsonHelper.setFromJson(entry.getValue(), e -> JsonUtils.getString(e, "")));
		}
	}

	private static final FactionDefinition EMPTY_FACTION = new FactionDefinition(0, new HashSet<>(), new HashSet<>()).copy("", -1).build();
}
