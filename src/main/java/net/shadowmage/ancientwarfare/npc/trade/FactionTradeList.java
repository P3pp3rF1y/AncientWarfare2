package net.shadowmage.ancientwarfare.npc.trade;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import java.util.Iterator;

public final class FactionTradeList extends TradeList<FactionTrade> {

	private long totalWorldTime = 0;

	@Override
	protected FactionTrade getNewTrade() {
		return new FactionTrade();
	}

	/*
	 * MUST be called from owning entity once per update tick.
	 */
	public void tick(World world) {
		totalWorldTime = world.getTotalWorldTime();
	}

	/*
	 * Should be called on server PRIOR to opening the trades GUI/container.<br>
	 * Will use the internal stored tick number value for updating the trades list.<br>
	 */
	public void updateTradesForView() {
		for (FactionTrade aTrade : points) {
			aTrade.updateTrade(totalWorldTime);
		}
	}

	/*
	 * removes any trades that have no input or output items.<br>
	 * should be called before the changed list is sent from client->server from setup GUI.
	 */
	public void removeEmptyTrades() {
		Iterator<FactionTrade> it = points.iterator();
		FactionTrade t;
		while (it.hasNext() && (t = it.next()) != null) {
			if (!t.hasItems()) {
				it.remove();
			}
		}
	}

	public boolean performTrade(EntityPlayer player, int tradeNum) {
		return get(tradeNum).performTrade(player, null);
	}
}
