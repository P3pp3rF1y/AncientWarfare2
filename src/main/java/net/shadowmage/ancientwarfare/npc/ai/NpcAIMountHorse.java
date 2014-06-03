package net.shadowmage.ancientwarfare.npc.ai;

import java.util.Collections;
import java.util.List;

import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget.Sorter;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.util.AxisAlignedBB;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class NpcAIMountHorse extends NpcAI
{

int lastExecutedTick = -1;
EntityHorse target;
int moveRetryDelay = 0;
double moveSpeed = 1.d;
private final EntityAINearestAttackableTarget.Sorter sorter;

public NpcAIMountHorse(NpcBase npc)
  {
  super(npc);
  sorter = new Sorter(npc);
  this.setMutexBits(ATTACK+MOVE);
  }

@Override
public boolean shouldExecute()
  {
  return npc.ridingEntity==null && (lastExecutedTick==-1 || npc.ticksExisted-lastExecutedTick>200);
  }

@Override
public boolean continueExecuting()
  {
  return target!=null && npc.ridingEntity==null && target.riddenByEntity==null;
  }

@Override
public void startExecuting()
  {
  AWLog.logDebug("executing search for horses...");
  lastExecutedTick = npc.ticksExisted;
  target = null;
  AxisAlignedBB bb = npc.boundingBox.expand(40.d, 20.d, 40.d);//TODO set from npc follow-distance
  List<EntityHorse> horses = npc.worldObj.getEntitiesWithinAABB(EntityHorse.class, bb);
  if(horses.isEmpty()){return;}
  Collections.sort(horses, sorter);
  for(EntityHorse horse : horses)
    {
    if(horse.riddenByEntity==null)
      {
      target = horse;
      break;
      }
    }
  }

@Override
public void updateTask()
  {  
  if(target!=null)
    {
    if(target.riddenByEntity!=null)
      {
      target=null;
      return;
      }
    double dist = npc.getDistanceSqToEntity(target);
    if(dist>5.d*5.d)
      {
      moveRetryDelay--;   
      if(moveRetryDelay<=0)
        {
        npc.getNavigator().tryMoveToEntityLiving(target, moveSpeed);
        moveRetryDelay=10;//base .5 second retry delay
        if(dist>256){moveRetryDelay+=10;}//add .5 seconds if distance>16
        if(dist>1024){moveRetryDelay+=20;}//add another 1 second if distance>32
        }
      }
    else
      {
      npc.getNavigator().clearPathEntity();
      npc.mountEntity(target);
      target = null;
      }
    }
  }

@Override
public void resetTask()
  {  
  target = null;
  }

}
