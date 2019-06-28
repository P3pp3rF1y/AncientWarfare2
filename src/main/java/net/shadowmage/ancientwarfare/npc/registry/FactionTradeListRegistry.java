package net.shadowmage.ancientwarfare.npc.registry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;
import net.shadowmage.ancientwarfare.core.registry.IRegistryDataParser;
import net.shadowmage.ancientwarfare.core.util.ItemTools;
import net.shadowmage.ancientwarfare.core.util.parsing.JsonHelper;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;

public class FactionTradeListRegistry {
	private static final String DEFAULT_REGISTRY_LOCATION = "npc/faction_trade_lists.json";

	private FactionTradeListRegistry() {}

	private static Map<String, FactionTradeListTemplate> defaultTemplates;
	private static Map<String, Map<String, FactionTradeListTemplate>> factionTemplates;

	public static Map<String, FactionTradeListTemplate> getFactionDefaults(String factionName) {
		return factionTemplates.getOrDefault(factionName, new HashMap<>());
	}

	public static Map<String, FactionTradeListTemplate> getDefaults() {
		return defaultTemplates;
	}

	public static void saveTradeList(FactionTradeListTemplate list) {
		defaultTemplates.put(list.getName(), list);
		saveTradeLists();
	}

	private static void saveTradeLists() {
		JsonObject parent = new JsonObject();
		parent.add("defaults", serializeTradeLists(defaultTemplates.values()));
		parent.add("faction_defaults", serializeFactionsTradeLists());

		File file = new File(AWCoreStatics.configPathForFiles + "registry/" + DEFAULT_REGISTRY_LOCATION);
		JsonHelper.saveJsonFile(parent, file);
	}

	private static JsonObject serializeFactionsTradeLists() {
		JsonObject factionTradeLists = new JsonObject();
		for (Map.Entry<String, Map<String, FactionTradeListTemplate>> entry : factionTemplates.entrySet()) {
			factionTradeLists.add(entry.getKey(), serializeTradeLists(entry.getValue().values()));
		}
		return factionTradeLists;
	}

	private static JsonArray serializeTradeLists(Collection<FactionTradeListTemplate> templateLists) {
		JsonArray tradeLists = new JsonArray();
		for (FactionTradeListTemplate templateList : templateLists) {
			tradeLists.add(serializeTradeList(templateList));
		}
		return tradeLists;
	}

	private static JsonObject serializeTradeList(FactionTradeListTemplate templateList) {
		JsonObject tradeList = new JsonObject();
		tradeList.addProperty("name", templateList.getName());
		tradeList.add("trades", serializeTrades(templateList));
		return tradeList;
	}

	private static JsonArray serializeTrades(FactionTradeListTemplate templateList) {
		JsonArray trades = new JsonArray();
		for (FactionTradeTemplate template : templateList.getTrades()) {
			trades.add(serializeTrade(template));
		}
		return trades;
	}

	private static JsonObject serializeTrade(FactionTradeTemplate template) {
		JsonObject trade = new JsonObject();
		trade.add("input", serializeStacks(template.getInput()));
		trade.add("output", serializeStacks(template.getOutput()));
		trade.addProperty("refill_frequency", template.getRefillFrequency());
		trade.addProperty("max_trades", template.getMaxTrades());
		return trade;
	}

	private static JsonArray serializeStacks(List<ItemStack> stacks) {
		JsonArray ret = new JsonArray();
		for (ItemStack stack : stacks) {
			if (!stack.isEmpty()) {
				ret.add(ItemTools.serializeToJson(stack));
			}
		}
		return ret;
	}

	public static void saveFactionTradeList(FactionTradeListTemplate list, String faction) {
		if (!factionTemplates.containsKey(faction)) {
			factionTemplates.put(faction, new HashMap<>());
		}
		factionTemplates.get(faction).put(list.getName(), list);
		saveTradeLists();
	}

	@SuppressWarnings("squid:S2696") //updating static maps above in a single on startup only so this is cool
	public static class Parser implements IRegistryDataParser {
		@Override
		public String getName() {
			return "faction_trade_lists";
		}

		@Override
		public void parse(JsonObject json) {
			defaultTemplates = parseTradeLists(JsonUtils.getJsonArray(json, "defaults"));
			parseFactionDefaults(json);
		}

		private void parseFactionDefaults(JsonObject json) {
			factionTemplates = JsonHelper.mapFromJson(json, "faction_defaults",
					Map.Entry::getKey,
					e -> parseTradeLists(JsonUtils.getJsonArray(e.getValue(), "trade_list")));
		}

		private Map<String, FactionTradeListTemplate> parseTradeLists(JsonArray tradeLists) {
			Map<String, FactionTradeListTemplate> ret = new HashMap<>();
			for (JsonElement tradeListElement : tradeLists) {
				JsonObject tradeList = JsonUtils.getJsonObject(tradeListElement, "trade_list");
				String name = JsonUtils.getString(tradeList, "name");
				JsonArray tradesArray = JsonUtils.getJsonArray(tradeList, "trades");
				List<FactionTradeTemplate> trades = parseTrades(tradesArray);
				ret.put(name, new FactionTradeListTemplate(name, trades));
			}
			return ret;
		}

		private List<FactionTradeTemplate> parseTrades(JsonArray trades) {
			List<FactionTradeTemplate> ret = new ArrayList<>();
			for (JsonElement tradeElement : trades) {
				JsonObject trade = JsonUtils.getJsonObject(tradeElement, "trade");
				int refillFrequency = JsonUtils.getInt(trade, "refill_frequency");
				int maxTrades = JsonUtils.getInt(trade, "max_trades");
				List<ItemStack> input;
				List<ItemStack> output;
				try {
					input = JsonHelper.getItemStacks(JsonUtils.getJsonArray(trade, "input"));
					output = JsonHelper.getItemStacks(JsonUtils.getJsonArray(trade, "output"));
				}
				catch (MissingResourceException ex) {
					AncientWarfareNPC.LOG.error("Error parsing trade: ", ex);
					continue;
				}

				ret.add(new FactionTradeTemplate(input, output, refillFrequency, maxTrades));
			}
			return ret;
		}
	}
}
