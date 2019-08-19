package net.shadowmage.ancientwarfare.npc.registry;

import net.shadowmage.ancientwarfare.npc.trade.FactionTradeList;

import java.util.Collections;
import java.util.List;

public class FactionTradeListTemplate {
	private final String name;
	public static final FactionTradeListTemplate EMPTY = new FactionTradeListTemplate("empty", Collections.emptyList());

	private final List<FactionTradeTemplate> trades;

	public FactionTradeListTemplate(String name, List<FactionTradeTemplate> trades) {
		this.name = name;
		this.trades = trades;
	}

	public String getName() {
		return name;
	}

	public List<FactionTradeTemplate> getTrades() {
		return trades;
	}

	public FactionTradeList toTradeList() {
		FactionTradeList tradeList = new FactionTradeList();
		for (FactionTradeTemplate tradeTemplate : trades) {
			tradeList.add(tradeTemplate.toTrade());
		}
		return tradeList;
	}
}
