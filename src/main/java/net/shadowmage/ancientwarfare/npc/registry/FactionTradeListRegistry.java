package net.shadowmage.ancientwarfare.npc.registry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.shadowmage.ancientwarfare.core.registry.IRegistryDataParser;
import net.shadowmage.ancientwarfare.core.util.parsing.JsonHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FactionTradeListRegistry {
	private FactionTradeListRegistry() {}

	private static Map<String, FactionTradeListTemplate> defaultTemplates;
	private static Map<String, Map<String, FactionTradeListTemplate>> factionTemplates;

	public static Map<String, FactionTradeListTemplate> getFactionDefaults(String factionName) {
		return factionTemplates.getOrDefault(factionName, new HashMap<>());
	}

	public static Map<String, FactionTradeListTemplate> getDefaults() {
		return defaultTemplates;
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
				List<ItemStack> input = JsonHelper.getItemStacks(JsonUtils.getJsonArray(trade, "input"));
				List<ItemStack> output = JsonHelper.getItemStacks(JsonUtils.getJsonArray(trade, "output"));

				ret.add(new FactionTradeTemplate(input, output, refillFrequency, maxTrades));
			}
			return ret;
		}
	}
}
