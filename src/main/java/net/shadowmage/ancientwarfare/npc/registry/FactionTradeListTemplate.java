package net.shadowmage.ancientwarfare.npc.registry;

import net.shadowmage.ancientwarfare.npc.trade.FactionTradeList;

import java.util.List;

public class FactionTradeListTemplate {
	private final String name;
	private final List<FactionTradeTemplate> trades;

	public FactionTradeListTemplate(String name, List<FactionTradeTemplate> trades) {
		this.name = name;
		this.trades = trades;
	}

	public String getName() {
		return name;
	}

	public FactionTradeList toTradeList() {
		FactionTradeList tradeList = new FactionTradeList();
		for (FactionTradeTemplate tradeTemplate : trades) {
			tradeList.add(tradeTemplate.toTrade());
		}
		return tradeList;
	}
}
