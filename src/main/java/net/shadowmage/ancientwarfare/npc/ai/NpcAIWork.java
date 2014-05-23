package net.shadowmage.ancientwarfare.npc.ai;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite;
import net.shadowmage.ancientwarfare.core.interfaces.IWorker;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.entity.NpcWorker;
import net.shadowmage.ancientwarfare.npc.item.AWNpcItemLoader;
import net.shadowmage.ancientwarfare.npc.orders.WorkOrder;
import net.shadowmage.ancientwarfare.npc.orders.WorkOrder.WorkEntry;

public class NpcAIWork extends NpcAI
{

public int workIndex;//TODO read/write nbt
int ticksAtSite = 0;//TODO read/write nbt
boolean atSite = false;//TODO read/write nbt
WorkOrder order;
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
//  if(npc.getFoodRemaining()<=0){return false;} //TODO uncomment out once update AI is finished
  if(!init)
    {
    orderStack = npc.ordersStack;
    order = WorkOrder.getWorkOrder(orderStack);
    init = true;
    }
  if(orderStack!=null && order!=null && order.getEntries().size()>0)
    {
    AWLog.logDebug("starting work AI");
    return true;
    }
  return false;
  }

@Override
public boolean continueExecuting()
  { 
//  if(npc.getFoodRemaining()<=0){return false;} //TODO uncomment out once update AI is finished
  if(orderStack!=null && order!=null && order.getEntries().size()>0)
    {
    AWLog.logDebug("continuing work AI");
    return true;
    }
  return false;
  }

@Override
public void updateTask()
  {
  AWLog.logDebug("continuing work AI2");
  if(!atSite)
    {
    moveToWorksite();
    }
  else
    {
    workAtSite();
    }  
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
      AWLog.logDebug("should do work at site!!");      
      if(site.hasWork())
        {
        //do work at the worksite if has work
        AWLog.logDebug("adding energy to site from work");
        site.addEnergyFromWorker((IWorker) npc);
        ticksTilNextWorkUpdate=AWNPCStatics.npcWorkTicks;
        }
      else
        {
        AWLog.logDebug("site has no work...");
        //else chose appropriate action based on orders.priorityType
        if(shouldMoveFromNoWork(entry))
          {
          setMoveToNextSite();
          }        
        }
      if(shouldMoveFromTimeAtSite(entry))
        {
        AWLog.logDebug("moving to next site due to work timing");
        setMoveToNextSite();
        }  
      }
    else
      {
      AWLog.logDebug("moving to next site due to incompatble work types");
      setMoveToNextSite();
      }      
    }
  else
    {
    AWLog.logDebug("moving to next site due to invalid work site");
    setMoveToNextSite();
    }
  }

protected boolean shouldMoveFromNoWork(WorkEntry entry)
  {
  return order.getEntries().size()>1;//TODO vary based on order.priorityType
  }

protected boolean shouldMoveFromTimeAtSite(WorkEntry entry)
  {
  //TODO vary based on order.priorityType;
  if(entry.getWorkLength()>0 && ticksAtSite>entry.getWorkLength())
    {
    return true;
    }
  return false;
  }

protected void setMoveToNextSite()
  {
  atSite=false;
  ticksTilNextWorkUpdate=0;
  workIndex++;
  ticksAtSite=0;
  moveTickDelay=0;
  if(workIndex>=order.getEntries().size()){workIndex=0;}
  }

protected void moveToWorksite()
  {
  ticksAtSite = 0;
  if(moveTickDelay>0){moveTickDelay--;}
  if(moveTickDelay<=0)
    {
    WorkEntry entry = order.getEntries().get(workIndex);
    BlockPosition pos = entry.getPosition();
    AWLog.logDebug("commanding move to worksite...");
    //check distance to position    
    double dist = npc.getDistanceSq(pos.x+0.5d, pos.y, pos.z+0.5d);
    if(dist>5.d*5.d)
      {
      moveTickDelay = 5;
      //proceed to work site
      npc.getNavigator().tryMoveToXYZ(pos.x+0.5d, pos.y, pos.z+0.5d, 1.d);
      
      if(npc.getNavigator().noPath())
        {        
        AWLog.logDebug("navigator has no path!!");
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
  AWLog.logDebug("orders changed!!");
  orderStack = npc.ordersStack;
  order = WorkOrder.getWorkOrder(orderStack);
  workIndex = 0;
  ticksAtSite = 0;
  atSite = false;
  }

@Override
public void resetTask()
  {
  AWLog.logDebug("resetting work task..");
  super.resetTask();
  ticksAtSite = 0;
  atSite = false;
  }


}
