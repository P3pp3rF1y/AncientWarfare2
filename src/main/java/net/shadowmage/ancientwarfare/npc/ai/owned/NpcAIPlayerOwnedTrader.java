package net.shadowmage.ancientwarfare.npc.ai.owned;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.shadowmage.ancientwarfare.core.inventory.InventoryBackpack;
import net.shadowmage.ancientwarfare.core.item.ItemBackpack;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.npc.ai.NpcAI;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.entity.NpcTrader;
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
 * state flags, to track what state the AI is currently in
 */
private boolean shelter, upkeep, restock, deposit, waiting, at_shelter, at_upkeep, at_deposit, at_withdraw, at_waypoint;

/**
 * used to track how long to wait when in 'waiting' state
 */
private int delayCounter;

/**
 * used to track waypoint index, to retrieve next waypoint and to retrieve upkeep status for current waypoint
 */
private int waypointIndex;

/**
 * the currently selected waypoint to move towards, should never be null if valid orders item is present
 */
private BlockPosition waypoint;

/**
 * currently selected shelter position, used by shelter code, should be null when not in use
 */
private BlockPosition shelterPoint;

/**
 * convenience access fields;
 * trade orders is set/updated when orders item is changed or when entity is loaded from NBT
 */
private TradeOrder orders;
private NpcTrader trader;

public NpcAIPlayerOwnedTrader(NpcBase npc)
  {
  super(npc);
  this.trader = (NpcTrader) npc;
  setMutexBits(MOVE+ATTACK);
  }

public void onOrdersUpdated()
  {
  orders = TradeOrder.getTradeOrder(npc.ordersStack);
  waypoint = null;
  shelterPoint = null;
  upkeep = false;
  restock = false;
  deposit = false;
  waiting = false;
  at_deposit=false;
  at_shelter=false;
  at_upkeep=false;
  at_waypoint=false;
  at_withdraw=false;
  waypointIndex = 0;
  delayCounter = 0;
  if(orders!=null && orders.getRoute().size()>0)
    {
    waypoint = orders.getRoute().get(waypointIndex).getPosition();
    }
  }

@Override
public boolean shouldExecute()
  {
  return orders!=null && waypoint!=null;
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
  return orders!=null && waypoint!=null;
  }

@Override
public void updateTask() 
  {
  if(trader.shouldBeAtHome() || shelter){updateShelter();}
  else if(upkeep){updateUpkeep();}
  else if(restock){updateRestock();}
  else {updatePatrol();}
  }

private void updateShelter()
  {
  npc.addAITask(TASK_GO_HOME);
  shelter=true;
  if(at_shelter)
    {
    if(!trader.shouldBeAtHome())
      {
      shelter=false;
      at_shelter=false;
      shelterPoint=null;
      upkeep=false;
      at_upkeep=false;
      deposit=false;
      restock=false;
      at_deposit=false;
      at_withdraw=false;
      waiting=false;
      delayCounter=0;
      npc.removeAITask(TASK_GO_HOME);
      }//end shelter code, return to previously current route point - if was interruped in the middle of upkeep, will restart upkeep
    }
  else if(shelterPoint==null)
    {    
    int index = waypointIndex-1;
    if(index<0){index=orders.getRoute().size()-1;}
    BlockPosition wp2 = orders.getRoute().get(index).getPosition();    
    double d1 = npc.getDistanceSq(waypoint);
    double d2 = npc.getDistanceSq(wp2);
    shelterPoint = d1<d2? waypoint : wp2;
    }
  else
    {
    double d = npc.getDistanceSq(shelterPoint);
    if(d > 9.d)
      {
      npc.addAITask(TASK_MOVE);
      moveToPosition(shelterPoint, d);      
      }
    else
      {
      npc.removeAITask(TASK_MOVE);
      at_shelter = true;      
      }
    }
  }

private void updateUpkeep()
  {
  npc.addAITask(TASK_UPKEEP);
  if(at_upkeep)
    {
    if(tryWithdrawUpkeep())
      {
      at_upkeep=false;
      upkeep = false;
      restock = true;
      deposit = true;
      npc.removeAITask(TASK_UPKEEP);
      npc.removeAITask(TASK_IDLE_HUNGRY);
      }
    }
  else if(npc.getUpkeepPoint()!=null)
    {
    double d = npc.getDistanceSq(npc.getUpkeepPoint());
    if(d>9.d)
      {
      npc.addAITask(TASK_MOVE);
      moveToPosition(npc.getUpkeepPoint(), d);
      }
    else
      {
      npc.removeAITask(TASK_MOVE);
      at_upkeep=true;
      }
    }
  else//no upkeep point, display no upkeep task/state icon
    {
    npc.addAITask(TASK_IDLE_HUNGRY);
    npc.removeAITask(TASK_UPKEEP);
    }
  }

protected boolean tryWithdrawUpkeep()
  {
  BlockPosition p = npc.getUpkeepPoint();
  TileEntity te = npc.worldObj.getTileEntity(p.x, p.y, p.z);
  if(te instanceof IInventory)
    {
    return trader.withdrawFood((IInventory) te, npc.getUpkeepBlockSide());
    }
  return false;
  }

private void updateRestock()
  {
  if(deposit){updateDeposit();}
  else{updateWithdraw();}
  }

private void updateDeposit()
  {
  if(at_deposit)
    {
    doDeposit();
    deposit=false;
    at_deposit=false;
    }
  else if(orders.getRestockData().getDepositPoint()!=null)
    {
    BlockPosition p = orders.getRestockData().getDepositPoint();
    double d = npc.getDistanceSq(p);
    if(d>9.d)
      {
      npc.addAITask(TASK_MOVE);
      moveToPosition(p, d);
      }
    else
      {
      npc.removeAITask(TASK_MOVE);
      at_deposit=true;
      }
    }
  else//no deposit point
    {
    deposit = false;//kick into withdraw mode
    }
  }

private void updateWithdraw()
  {
  if(at_withdraw)
    {
    doWithdraw();
    setNextWaypoint();
    restock = false;
    at_withdraw=false;
    }
  else if(orders.getRestockData().getWithdrawPoint()!=null)
    {
    BlockPosition p = orders.getRestockData().getWithdrawPoint();
    double d = npc.getDistanceSq(p);
    if(d>9.d)
      {
      npc.addAITask(TASK_MOVE);
      moveToPosition(p, d);
      }
    else
      {
      npc.removeAITask(TASK_MOVE);
      at_withdraw=true;
      }
    }
  else//no withdraw point
    {
    restock = false;
    setNextWaypoint();
    }
  }

private void updatePatrol()
  {  
  if(at_waypoint)
    {
    if(waiting)
      {      
      delayCounter++;
      if(delayCounter>=orders.getRoute().get(waypointIndex).getDelay())
        {
        delayCounter=0;
        waiting=false;
        at_waypoint=false;
        if(orders.getRoute().get(waypointIndex).shouldUpkeep())
          {
          upkeep=true;     
          }
        else
          {
          setNextWaypoint();
          }
        }
      }
    else
      {
      waiting = true;      
      delayCounter=0;
      }
    }
  else
    {
    npc.addAITask(TASK_MOVE);
    double d = npc.getDistanceSq(waypoint);
    if(d<9.d)
      {
      at_waypoint=true;
      waiting=false;
      npc.removeAITask(TASK_MOVE);
      }
    else
      {
      moveToPosition(waypoint, d);
      }
    }
  }

private void setNextWaypoint()
  {
  waypointIndex++;
  if(waypointIndex>=orders.getRoute().size()){waypointIndex=0;}
  waypoint = orders.getRoute().get(waypointIndex).getPosition();
  }

private void doDeposit()
  {
  ItemStack backpack = npc.getEquipmentInSlot(0);
  if(backpack!=null && backpack.getItem() instanceof ItemBackpack)
    {
    InventoryBackpack inv = ItemBackpack.getInventoryFor(backpack);
    BlockPosition pos = orders.getRestockData().getDepositPoint();
    TileEntity te = npc.worldObj.getTileEntity(pos.x, pos.y, pos.z);
    if(te instanceof IInventory)
      {
      IInventory dep = (IInventory)te;
      orders.getRestockData().doDeposit(inv, dep, orders.getRestockData().getDepositSide());
      ItemBackpack.writeBackpackToItem(inv, backpack);
      }
    }
  }

private void doWithdraw()
  {
  ItemStack backpack = npc.getEquipmentInSlot(0);
  if(backpack!=null && backpack.getItem() instanceof ItemBackpack)
    {
    InventoryBackpack inv = ItemBackpack.getInventoryFor(backpack);
    BlockPosition pos = orders.getRestockData().getWithdrawPoint();
    TileEntity te = npc.worldObj.getTileEntity(pos.x, pos.y, pos.z);
    if(te instanceof IInventory)
      {
      IInventory dep = (IInventory)te;
      orders.getRestockData().doWithdraw(inv, dep, orders.getRestockData().getWithdrawSide());
      ItemBackpack.writeBackpackToItem(inv, backpack);
      }
    }
  }

public void readFromNBT(NBTTagCompound tag)
  {
  orders = TradeOrder.getTradeOrder(npc.ordersStack);
  waypointIndex = tag.getInteger("waypoint");
  waypoint = (orders==null || orders.getRoute().size()==0) ? null : orders.getRoute().get(waypointIndex).getPosition();  
  }

public NBTTagCompound writeToNBT(NBTTagCompound tag)
  {
  tag.setInteger("waypoint", waypointIndex);
  return tag;
  }

}
