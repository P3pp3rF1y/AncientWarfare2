package net.shadowmage.ancientwarfare.npc.trade;

public class POTradeList extends TradeList<Trade> {
	@Override
	protected Trade getNewTrade() {
		return new POTrade();
	}
}
