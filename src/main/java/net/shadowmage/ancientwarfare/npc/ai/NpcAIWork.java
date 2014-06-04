package net.shadowmage.ancientwarfare.npc.ai;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite;
import net.shadowmage.ancientwarfare.core.interfaces.IWorker;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.entity.NpcWorker;
import net.shadowmage.ancientwarfare.npc.orders.WorkOrder;
import net.shadowmage.ancientwarfare.npc.orders.WorkOrder.WorkEntry;
import net.shadowmage.ancientwarfare.npc.orders.WorkOrder.WorkPriorityType;

public class NpcAIWork extends NpcAI
{

public int ticksAtSite = 0;

public int workIndex;

public WorkOrder order;
ItemStack orderStack;
boolean init = false;

int moveRetryDelay = 0;

NpcWorker worker;

public NpcAIWork(NpcBase npc)
  {
  super(npc);
  if(!(npc instanceof NpcWorker))
    {
    throw new IllegalArgumentException("cannot instantiate work ai task on non-worker npc");
    }
  worker = (NpcWorker)npc;
  this.setMutexBits(MOVE+ATTACK);
  }

@Override
public boolean shouldExecute()
  {  
  if(npc.getFoodRemaining()<=0){return false;} 
  if(!init)
    {
    orderStack = npc.ordersStack;
    order = WorkOrder.getWorkOrder(orderStack);
    init = true;
    if(order==null || workIndex >= order.getEntries().size())
      {
      workIndex=0;
      }
    }
  if(!npc.worldObj.provider.hasNoSky && !npc.worldObj.isDaytime() && npc.hasHome())//dont work at night if has home point
    {
    return false;
    }
  if(orderStack!=null && order!=null && order.getEntries().size()>0)
    {
    return true;
    }
  return false;
  }

@Override
public boolean continueExecuting()
  { 
  if(npc.getFoodRemaining()<=0){return false;}
  if(!npc.worldObj.provider.hasNoSky && !npc.worldObj.isDaytime() && npc.hasHome())//dont work at night if has home point
    {
    return false;
    }
  if(orderStack!=null && order!=null && order.getEntries().size()>0)
    {
    return true;
    }
  return false;
  }

@Override
public void updateTask()
  {
  BlockPosition pos = order.getEntries().get(workIndex).getPosition();
  double dist = npc.getDistanceSq(pos.x, pos.y, pos.z);
  AWLog.logDebug("distance to site: "+dist);
  if(dist > 5.d*5.d)
    {
    AWLog.logDebug("moving to worksite..."+pos);
    npc.addAITask(TASK_MOVE);
    ticksAtSite=0;
    moveToWorksite(pos, dist);
    }
  else
    {
    AWLog.logDebug("working at site....."+pos);
    npc.getNavigator().clearPathEntity();
    npc.removeAITask(TASK_MOVE);
    workAtSite();
    }
  }

@Override
public void startExecuting()
  {
  npc.addAITask(TASK_WORK);
  }

protected void workAtSite()
  {
  ticksAtSite++;
  if(npc.ticksExisted%10==0){npc.swingItem();}
  if(ticksAtSite>=AWNPCStatics.npcWorkTicks)
    {
    ticksAtSite=0;
    WorkEntry entry = order.getEntries().get(workIndex);
    BlockPosition pos = entry.getPosition();
    TileEntity te = npc.worldObj.getTileEntity(pos.x, pos.y, pos.z);
    if(te instanceof IWorkSite)
      {
      IWorkSite site = (IWorkSite)te;
      if(((IWorker)npc).canWorkAt(site.getWorkType()))
        {      
        if(site.hasWork())
          {        
          npc.addExperience(AWNPCStatics.npcXpFromWork);
          site.addEnergyFromWorker((IWorker) npc);
          }
        else
          {
          if(shouldMoveFromNoWork(entry))
            {
            setMoveToNextSite();
            }        
          }
        if(shouldMoveFromTimeAtSite(entry))
          {
          setMoveToNextSite();
          }  
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
  }

protected boolean shouldMoveFromNoWork(WorkEntry entry)
  {
  if(order.getPriorityType()==WorkPriorityType.TIMED){return false;}
  else if(order.getPriorityType()==WorkPriorityType.ROUTE){return true;}
  else if(order.getPriorityType()==WorkPriorityType.PRIORITY_LIST){return true;}
  return order.getEntries().size()>1;
  }

protected boolean shouldMoveFromTimeAtSite(WorkEntry entry)
  {
  if(order.getPriorityType()==WorkPriorityType.TIMED){return ticksAtSite>entry.getWorkLength();}
  else if(order.getPriorityType()==WorkPriorityType.ROUTE){return false;}
  else if(order.getPriorityType()==WorkPriorityType.PRIORITY_LIST){return false;}
  return false;
  }

protected void setMoveToNextSite()
  {
  ticksAtSite=0;
  moveRetryDelay=0;
  if(order.getPriorityType()==WorkPriorityType.PRIORITY_LIST)    
    {
    workIndex=0;
    WorkEntry entry;
    BlockPosition pos;
    IWorkSite site;
    IWorker worker = (IWorker)npc;
    for(int i = 0; i < order.getEntries().size(); i++)
      {
      entry = order.getEntries().get(workIndex);
      pos = entry.getPosition();
      TileEntity te = npc.worldObj.getTileEntity(pos.x, pos.y, pos.z);
      if(te instanceof IWorkSite)
        {
        site = (IWorkSite)te;        
        if(worker.canWorkAt(site.getWorkType()) && site.hasWork())
          {
          workIndex = i;
          break;
          }
        }
      }
    }
  else
    {
    workIndex++;
    if(workIndex>=order.getEntries().size()){workIndex=0;}    
    }
  }

private void moveToWorksite(BlockPosition pos, double dist)
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

public void onOrdersChanged()
  {
  orderStack = npc.ordersStack;
  order = WorkOrder.getWorkOrder(orderStack);
  workIndex = 0;
  ticksAtSite = 0;
  }

@Override
public void resetTask()
  {
  ticksAtSite = 0;
  this.npc.removeAITask(TASK_WORK + TASK_MOVE);
  }

public void readFromNBT(NBTTagCompound tag)
  {
  ticksAtSite = tag.getInteger("ticksAtSite");
  workIndex = tag.getInteger("workIndex");
  }

public NBTTagCompound writeToNBT(NBTTagCompound tag)
  {
  tag.setInteger("ticksAtSite", ticksAtSite);
  tag.setInteger("workIndex", workIndex);
  return tag;
  }


}
