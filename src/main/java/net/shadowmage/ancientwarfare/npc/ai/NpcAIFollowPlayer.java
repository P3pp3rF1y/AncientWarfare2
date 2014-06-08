package net.shadowmage.ancientwarfare.npc.ai;

import net.minecraft.entity.Entity;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class NpcAIFollowPlayer extends NpcAI
{

private Entity target;
private double attackIgnoreDistance = 4.d*4.d;
private double followStopDistance = 4.d*4.d;

public NpcAIFollowPlayer(NpcBase npc)
  {
  super(npc);
  this.setMutexBits(MOVE+ATTACK);
  }

/**
 * Returns whether the EntityAIBase should begin execution.
 */
public boolean shouldExecute()
  {
  target = this.npc.getFollowingEntity();  
  if(target == null)
    {
    return false;
    }
  if(npc.getAttackTarget()!=null)
    {
    if(npc.getDistanceSqToEntity(target)<attackIgnoreDistance && npc.getDistanceSqToEntity(npc.getAttackTarget())<attackIgnoreDistance)
      {
      return false;
      }
    }
  return true;
  }

/**
 * Returns whether an in-progress EntityAIBase should continue executing
 */
public boolean continueExecuting()
  {
  target = this.npc.getFollowingEntity();  
  if(target == null)
    {
    return false;
    }
  if(npc.getAttackTarget()!=null)
    {
    if(npc.getDistanceSqToEntity(target)<attackIgnoreDistance && npc.getDistanceSqToEntity(npc.getAttackTarget())<attackIgnoreDistance)
      {
      return false;
      }
    }
  return true;
  }

/**
 * Execute a one shot task or start executing a continuous task
 */
public void startExecuting()
  {
  moveRetryDelay=0;
  this.npc.addAITask(TASK_FOLLOW);
  }

/**
 * Resets the task
 */
public void resetTask()
  {
  this.target = null;
  moveRetryDelay=0;
  this.npc.removeAITask(TASK_FOLLOW + TASK_MOVE);
  }

/**
 * Updates the task
 */
public void updateTask()
  {
  this.npc.getLookHelper().setLookPositionWithEntity(this.target, 10.0F, (float)this.npc.getVerticalFaceSpeed());
  double distance = npc.getDistanceSqToEntity(target);
  if(distance > followStopDistance)
    {
    this.npc.addAITask(TASK_MOVE);
    moveToEntity(target, distance);    
    }
  else
    {
    this.npc.removeAITask(TASK_MOVE);   
    this.npc.getNavigator().clearPathEntity();
    }  
  }

}
