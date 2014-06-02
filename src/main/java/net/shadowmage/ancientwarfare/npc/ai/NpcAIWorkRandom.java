package net.shadowmage.ancientwarfare.npc.ai;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite;
import net.shadowmage.ancientwarfare.core.interfaces.IWorker;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.entity.NpcWorker;

public class NpcAIWorkRandom extends NpcAI
{

private int ticksAtSite = 0;
private boolean atSite = false;
private int moveRetryDelay = 0;
private int ticksTilNextWorkUpdate;
NpcWorker worker;
public NpcAIWorkRandom(NpcBase npc)
  {
  super(npc);
  worker = (NpcWorker)npc;
  this.setMutexBits(ATTACK+MOVE);
  }

@Override
public boolean shouldExecute()
  {
  if(npc.getFoodRemaining()<=0){return false;} 
  if(!npc.worldObj.provider.hasNoSky && !npc.worldObj.isDaytime() && npc.hasHome())//dont work at night if has home point
    {
    return false;
    }
  return npc.ordersStack==null && worker.autoWorkTarget!=null;
  }

@Override
public boolean continueExecuting()
  {
  if(npc.getFoodRemaining()<=0){return false;} 
  if(!npc.worldObj.provider.hasNoSky && !npc.worldObj.isDaytime() && npc.hasHome())//dont work at night if has home point
    {
    return false;
    }
  return npc.ordersStack==null && worker.autoWorkTarget!=null;
  }

@Override
public void startExecuting()
  {
  npc.addAITask(TASK_WORK);
  atSite=false;
  }

@Override
public void updateTask()
  {
  if(!atSite)
    {
    moveToWork();
    }
  else
    {
    npc.removeAITask(TASK_MOVE);
    workAtSite();
    }  
  }

@Override
public void resetTask()
  {
  npc.removeAITask(TASK_WORK+TASK_MOVE);
  atSite=false;
  }

private void moveToWork()
  {
  ticksAtSite = 0;
  moveRetryDelay--;
  if(moveRetryDelay<=0)
    {
    BlockPosition pos = worker.autoWorkTarget;
    double distance = npc.getDistanceSq(pos.x+0.5d, pos.y, pos.z+0.5d);
    if(distance>5.d*5.d)
      {
      npc.addAITask(TASK_MOVE);
      npc.getNavigator().tryMoveToXYZ(pos.x+0.5d, pos.y, pos.z+0.5d, 1.d);
      moveRetryDelay=10;//base .5 second retry delay
      if(distance>256){moveRetryDelay+=10;}//add .5 seconds if distance>16
      if(distance>1024){moveRetryDelay+=20;}//add another 1 second if distance>32
      }
    else
      {
      moveRetryDelay = 0;
      atSite = true;
      npc.removeAITask(TASK_MOVE);
      }    
    }
  }

private void workAtSite()
  {
  ticksAtSite++;
  npc.swingItem();
  if(ticksTilNextWorkUpdate>0)
    {
    ticksTilNextWorkUpdate--;
    return;
    }
  BlockPosition pos = worker.autoWorkTarget;
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
        atSite=false;
        worker.autoWorkTarget=null;
        }  
      }
    else
      {
      atSite=false;
      worker.autoWorkTarget=null;
      }      
    }
  else
    {
    atSite=false;
    worker.autoWorkTarget=null;
    }
  }

public void readFromNBT(NBTTagCompound tag)
  {
  ticksAtSite = tag.getInteger("ticksAtSite");
  atSite = tag.getBoolean("atSite");
  }

public NBTTagCompound writeToNBT(NBTTagCompound tag)
  {
  tag.setInteger("ticksAtSite", ticksAtSite);
  tag.setBoolean("atSite", atSite);
  return tag;
  }

}
