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

public int workIndex;
public int ticksAtSite = 0;
public boolean atSite = false;
public WorkOrder order;
ItemStack orderStack;
boolean init = false;

int moveTickDelay = 0;
private int ticksTilNextWorkUpdate;

public NpcAIWork(NpcBase npc)
  {
  super(npc);
  if(!(npc instanceof NpcWorker))
    {
    throw new IllegalArgumentException("cannot instantiate work ai task on non-worker npc");
    }
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
  if(orderStack!=null && order!=null && order.getEntries().size()>0)
    {
    return true;
    }
  return false;
  }

@Override
public void updateTask()
  {
  if(!atSite)
    {
    npc.addAITask(TASK_MOVE);
    moveToWorksite();
    }
  else
    {
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
  npc.swingItem();
  if(ticksTilNextWorkUpdate>0)
    {
    ticksTilNextWorkUpdate--;
    return;
    }
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
        ticksTilNextWorkUpdate=AWNPCStatics.npcWorkTicks;
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
  atSite=false;
  ticksTilNextWorkUpdate=0;
  ticksAtSite=0;
  moveTickDelay=0;
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

protected void moveToWorksite()
  {
  ticksAtSite = 0;
  if(moveTickDelay>0){moveTickDelay--;}
  if(moveTickDelay<=0)
    {
    WorkEntry entry = order.getEntries().get(workIndex);
    BlockPosition pos = entry.getPosition();
    double dist = npc.getDistanceSq(pos.x+0.5d, pos.y, pos.z+0.5d);
    if(dist>5.d*5.d)
      {
      moveTickDelay = 5;
      npc.getNavigator().tryMoveToXYZ(pos.x+0.5d, pos.y, pos.z+0.5d, 1.d);  
      
      if(npc.getNavigator().noPath())
        {        
        AWLog.logDebug("navigator has no path!!  moving to next site early!!");
        setMoveToNextSite();
        }
      }
    else
      {
      moveTickDelay = 0;
      atSite = true;
      }    
    }
  }

public void onOrdersChanged()
  {
//  AWLog.logDebug("orders changed!!");
  orderStack = npc.ordersStack;
  order = WorkOrder.getWorkOrder(orderStack);
  workIndex = 0;
  ticksAtSite = 0;
  atSite = false;
  }

@Override
public void resetTask()
  {
//  AWLog.logDebug("resetting work task..");
  super.resetTask();
  ticksAtSite = 0;
  atSite = false;
  this.npc.removeAITask(TASK_WORK + TASK_WORK);
  }

public void readFromNBT(NBTTagCompound tag)
  {
  ticksAtSite = tag.getInteger("ticksAtSite");
  atSite = tag.getBoolean("atSite");
  workIndex = tag.getInteger("workIndex");
  }

public NBTTagCompound writeToNBT(NBTTagCompound tag)
  {
  tag.setInteger("ticksAtSite", ticksAtSite);
  tag.setBoolean("atSite", atSite);
  tag.setInteger("workIndex", workIndex);
  return tag;
  }


}
