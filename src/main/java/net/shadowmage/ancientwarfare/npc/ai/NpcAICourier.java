package net.shadowmage.ancientwarfare.npc.ai;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.entity.NpcCourier;
import net.shadowmage.ancientwarfare.npc.orders.RoutingOrder;

public class NpcAICourier extends NpcAI
{

boolean init;
double moveSpeed = 1.d;
int moveRetryDelay;

int routeIndex;
int ticksToWork;
int ticksAtSite;

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
  if(!npc.worldObj.provider.hasNoSky && !npc.worldObj.isDaytime() && npc.hasHome())//dont work at night if has home point
    {
    return false;
    }
  return courier.backpackInventory!=null && order!=null && !order.getEntries().isEmpty();
  }

@Override
public boolean continueExecuting()
  {
  if(!npc.worldObj.provider.hasNoSky && !npc.worldObj.isDaytime() && npc.hasHome())//dont work at night if has home point
    {
    return false;
    }
  return courier.backpackInventory!=null && order!=null && !order.getEntries().isEmpty();
  }

@Override
public void startExecuting()
  {  
  npc.addAITask(TASK_WORK);
  }

@Override
public void updateTask()
  {
  BlockPosition pos = order.getEntries().get(routeIndex).getTarget();
  double dist = npc.getDistanceSq(pos.x, pos.y, pos.z);
  if(dist>5.d*5.d)
    {
    npc.addAITask(TASK_MOVE);
    ticksAtSite=0;
    moveToWork(pos, dist);
    }
  else
    {
    npc.getNavigator().clearPathEntity();
    npc.removeAITask(TASK_MOVE);
    workAtSite();
    }
  }

@Override
public void resetTask()
  {
  ticksToWork=0;
  ticksAtSite=0;
  npc.getNavigator().clearPathEntity();
  npc.removeAITask(TASK_WORK+TASK_MOVE);
  }

public void workAtSite()
  {
  if(ticksToWork==0)
    {
    startWork();    
    }
  else
    {
    ticksAtSite++;
    if(npc.ticksExisted%10==0){npc.swingItem();}
    if(ticksAtSite>ticksToWork)
      {
      setMoveToNextSite();
      }
    }
  }

private void moveToWork(BlockPosition pos, double dist)
  {
  moveRetryDelay--;
  if(moveRetryDelay<=0)
    {
    npc.getNavigator().tryMoveToXYZ(pos.x+0.5d, pos.y, pos.z+0.5d, 1.d);
    moveRetryDelay=10;//base .5 second retry delay
    if(dist>256){moveRetryDelay+=10;}//add .5 seconds if distance>16
    if(dist>1024){moveRetryDelay+=20;}//add another 1 second if distance>32    
    }
  }

private void startWork()
  {
  IInventory target = getTargetInventory();
  IInventory npcInv = courier.backpackInventory;
  if(target!=null)
    {
    ticksAtSite=0;
    int moved = order.handleRouteAction(order.getEntries().get(routeIndex), npcInv, target);
    courier.updateBackpackItemContents();
    if(moved>0)
      {
      ticksToWork = AWNPCStatics.npcCourierWorkTicks * moved;
      int lvl = npc.getLevelingStats().getLevel(npc.getNpcFullType());
      ticksToWork -= lvl * moved;
      if(ticksToWork<=0){ticksToWork=0;}
      npc.addExperience(moved*AWNPCStatics.npcXpFromMoveItem);      
      }
    else
      {
      setMoveToNextSite();
      }
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

public void setMoveToNextSite()
  {
  ticksAtSite=0;
  ticksToWork=0;
  moveRetryDelay=0;
  routeIndex++;  
  if(routeIndex>=order.getEntries().size()){routeIndex=0;}
  }

public void onOrdersChanged()
  {
  routeStack = npc.ordersStack;
  order = RoutingOrder.getRoutingOrder(routeStack);
  routeIndex = 0;
  ticksAtSite=0;
  ticksToWork=0;
  moveRetryDelay=0;
  }

public void readFromNBT(NBTTagCompound tag)
  {
  routeIndex = tag.getInteger("routeIndex");
  ticksAtSite = tag.getInteger("ticksAtSite");
  ticksToWork = tag.getInteger("ticksToWork");
  }

public NBTTagCompound writeToNBT(NBTTagCompound tag)
  {
  tag.setInteger("routeIndex", routeIndex);
  tag.setInteger("ticksAtSite", ticksAtSite);
  tag.setInteger("ticksToWork", ticksToWork);
  return tag;
  }

}
