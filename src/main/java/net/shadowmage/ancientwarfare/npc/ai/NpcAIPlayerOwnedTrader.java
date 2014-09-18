package net.shadowmage.ancientwarfare.npc.ai;

import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;
import net.shadowmage.ancientwarfare.npc.orders.TradeOrder;

/**
 * replaces upkeep ai for player owned trader.<br>
 * manages moving the trader through a trade route, stopping for specified time at each point;<br>
 * manages moving towards and withdrawing upkeep at specified point in trade route (or auto if no trade order present);<br>
 * manages depositing income and withdrawing new tradables from specified points<br>
 * @author Shadowmage
 *
 */
public class NpcAIPlayerOwnedTrader extends NpcAI
{

boolean upkeep, moving;
int delayCounter;

TradeOrder orders;

NpcPlayerOwned trader;
public NpcAIPlayerOwnedTrader(NpcBase npc)
  {
  super(npc);
  this.trader = (NpcPlayerOwned) npc;
  }

public void onOrdersUpdated()
  {
  orders = TradeOrder.getTradeOrder(npc.ordersStack);
  }

@Override
public boolean shouldExecute()
  {
  return orders!=null;
  }

}
