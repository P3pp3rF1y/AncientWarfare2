package net.shadowmage.ancientwarfare.npc.ai;

import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;
import net.shadowmage.ancientwarfare.npc.orders.TradeOrder;

/**
 * replaces upkeep ai for player owned trader when an orders item is equipped.<br>
 * manages moving the trader through a trade route, stopping for specified time at each point;<br>
 * manages moving towards and withdrawing upkeep at specified point in trade route (or auto if no trade order present);<br>
 * manages depositing income and withdrawing new tradables from specified points<br>
 * @author Shadowmage
 *
 */
public class NpcAIPlayerOwnedTrader extends NpcAI
{

/**
 * psuedocode
 * 
 * Start:
 * if(night/raining){goto shelter code-path}
 * else if(doing_upkeep){goto upkeep code-path}
 * else if(doing restock){goto restock code-path}
 * else if(waiting)
 *  {
 *  if(wait<delay){keep waiting}
 *  else{select next waypoint}
 *  }
 * else if(moving){continue moving}
 * 
 * Shelter:
 * if(at_shelter){keep waiting}
 * else if(chosen_shelter){move towards shelter}
 * else{shelter = closest waypoint or home if no waypoints}
 *   
 * Upkeep:
 * if(at_upkeep){do upkeep, set doing_upkeep=false, set doing_restock=true, deposit=true}
 * else if(has upkeep point){move towards upkeep point}
 * else{do nothing}
 * 
 * Restock:
 * if(deposit)
 *  {
 *  if(at_deposit){do deposit, set deposit=false}
 *  else if(has deposit){move to deposit}
 *  else{set deposit=false}//has no deposit point, disregard
 *  }
 * else//must be withdraw
 *  {
 *  if(at_withdraw){do withdraw, set restock=false, set next waypoint}
 *  else if(has withdraw){move to withdraw}
 *  else{set restock=false, set next waypoint}//has no deposit point, disregard
 *  }
 */

/**
 * state flags, to track what state the AI is currently in
 */
boolean upkeep, restock, deposit, moving, waiting;
/**
 * used to track how long to wait when in 'waiting' state
 */
int delayCounter;

/**
 * used to track waypoint index, to retrieve next waypoint and to retrieve upkeep status for current waypoint
 */
int waypointIndex;

/**
 * the currently selected waypoint to move towards, should never be null if valid orders item is present
 */
BlockPosition waypoint;

/**
 * currently selected shelter position, used by shelter code, should be null when not in use
 */
BlockPosition shelter;

/**
 * currently selected move-to point for upkeep or restock state, should be null when not in use
 */
BlockPosition upkeepPoint;

/**
 * convenience access fields;
 * trade orders is set/updated when orders item is changed or when entity is loaded from NBT
 */
TradeOrder orders;
NpcPlayerOwned trader;

public NpcAIPlayerOwnedTrader(NpcBase npc)
  {
  super(npc);
  this.trader = (NpcPlayerOwned) npc;
  setMutexBits(MOVE+ATTACK);
  }

public void onOrdersUpdated()
  {
  orders = TradeOrder.getTradeOrder(npc.ordersStack);
  waypoint = null;
  shelter = null;
  upkeepPoint = null;
  upkeep = false;
  restock = false;
  deposit = false;
  moving = false;
  waiting = false;
  waypointIndex = 0;
  delayCounter = 0;
  if(orders!=null)
    {
    waypoint = orders.getRoute().get(waypointIndex).getPosition();
    moving = true;
    }
  }

@Override
public boolean shouldExecute()
  {
  return orders!=null;
  }

@Override
public void startExecuting()
  {
  //NOOP
  }

@Override
public void resetTask()
  {
  //NOOP
  }

@Override
public boolean continueExecuting()
  {
  return orders!=null;
  }

@Override
public void updateTask() 
  {
  //TODO
  }

public void readFromNBT(NBTTagCompound tag){}//TODO
public NBTTagCompound writeToNBT(NBTTagCompound tag){return tag;}//TODO

}
