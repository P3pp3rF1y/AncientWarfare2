package net.shadowmage.ancientwarfare.npc.ai;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.entity.NpcCourier;
import net.shadowmage.ancientwarfare.npc.orders.RoutingOrder;

public class NpcAICourier extends NpcAI
{

boolean init;
int routeIndex;
int ticksToWork;
int ticksToWorkPerStack = 50;//default npc work ticks, TODO adjust based on npc level?
int moveRetryDelay;
boolean atSite;
boolean startedWork;
double moveSpeed = 1.d;

RoutingOrder order;
ItemStack routeStack;

NpcCourier courier;
public NpcAICourier(NpcBase npc)
  {
  super(npc);
  courier = (NpcCourier)npc;
  this.setMutexBits(ATTACK+MOVE);
  }

@Override
public boolean shouldExecute()
  {
  if(!init)
    {
    init = true;
    routeStack = npc.ordersStack;
    order = RoutingOrder.getRoutingOrder(routeStack);
    if((order!=null && routeIndex>=order.getEntries().size()) || order==null){routeIndex=0;}
    }
  return courier.backpackInventory!=null && order!=null && !order.getEntries().isEmpty();
  }

@Override
public boolean continueExecuting()
  {
  return courier.backpackInventory!=null && order!=null && !order.getEntries().isEmpty();
  }

@Override
public void startExecuting()
  {  
  super.startExecuting();
  npc.addAITask(TASK_WORK);
  }

@Override
public void updateTask()
  {
  if(atSite)
    {
    workAtSite();
    }
  else
    {
    moveToSite();
    }
  super.updateTask();
  }

@Override
public void resetTask()
  {
  ticksToWork=0;
  atSite=false;
  npc.getNavigator().clearPathEntity();
  npc.removeAITask(TASK_WORK+TASK_MOVE);
  }

public void workAtSite()
  {
  if(!startedWork)
    {
    startWork();    
    }
  else
    {
    ticksToWork--;
    npc.swingItem();
    if(ticksToWork<=0)
      {
      setMoveToNextSite();
      startedWork=false;
      }
    }
  }

private void startWork()
  {
  IInventory target = getTargetInventory();
  IInventory npcInv = courier.backpackInventory;
  if(target!=null)
    {
    startedWork = true;
    int moved = order.handleRouteAction(order.getEntries().get(routeIndex), npcInv, target);
    courier.updateBackpackItemContents();
    ticksToWork = ticksToWorkPerStack + ticksToWorkPerStack * moved;
    npc.addExperience(moved*AWNPCStatics.npcXpFromMoveItem);
    }
  else
    {
    setMoveToNextSite();
    }
  }

private IInventory getTargetInventory()
  {
  BlockPosition pos = order.getEntries().get(routeIndex).getTarget();
  TileEntity te = npc.worldObj.getTileEntity(pos.x, pos.y, pos.z);
  if(te instanceof IInventory){return (IInventory)te;}
  return null;
  }

public void moveToSite()
  {
  moveRetryDelay--;
  BlockPosition pos = order.getEntries().get(routeIndex).getTarget();
  double distance = npc.getDistanceSq(pos.x, pos.y, pos.z);
  if(distance<4.d*4.d)
    {
    atSite=true;
    npc.removeAITask(TASK_MOVE);
    }
  else
    {
    if(moveRetryDelay<=0)
      {
      npc.addAITask(TASK_MOVE);
      npc.getNavigator().tryMoveToXYZ(pos.x+0.5d, pos.y, pos.z+0.5d, moveSpeed);
      moveRetryDelay=10;//base .5 second retry delay
      if(distance>256){moveRetryDelay+=10;}//add .5 seconds if distance>16
      if(distance>1024){moveRetryDelay+=20;}//add another 1 second if distance>32
      }
    }
  }

public void setMoveToNextSite()
  {
  atSite=false;
  startedWork=false;
  ticksToWork=0;
  routeIndex++;  
  if(routeIndex>=order.getEntries().size()){routeIndex=0;}
  AWLog.logDebug("setting route move to index: "+routeIndex);
  }

public void onOrdersChanged()
  {
  routeStack = npc.ordersStack;
  order = RoutingOrder.getRoutingOrder(routeStack);
  routeIndex = 0;
  atSite=false;
  ticksToWork=0;
  moveRetryDelay=0;
  }

public void readFromNBT(NBTTagCompound tag){}//TODO

public NBTTagCompound writeToNBT(NBTTagCompound tag){return tag;}//TODO

}
