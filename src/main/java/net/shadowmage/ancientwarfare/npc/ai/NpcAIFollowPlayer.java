package net.shadowmage.ancientwarfare.npc.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class NpcAIFollowPlayer extends NpcAI
{

private Entity target;
private double attackIgnoreDistance = 4.d*4.d;
private double followStopDistance = 4.d*4.d;
private int moveDelay = 0;

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
  moveDelay = 0;
  this.npc.addAITask(TASK_FOLLOW);
  }

/**
 * Resets the task
 */
public void resetTask()
  {
  this.target = null;
  moveDelay = 0;
  this.npc.removeAITask(TASK_FOLLOW + TASK_MOVE);
  }

/**
 * Updates the task
 */
public void updateTask()
  {
  this.npc.getLookHelper().setLookPositionWithEntity(this.target, 10.0F, (float)this.npc.getVerticalFaceSpeed());
  this.moveDelay--;  
  double distance = npc.getDistanceSqToEntity(target);
  if(distance > followStopDistance)
    {
    if(moveDelay<=0)
      {
      this.npc.addAITask(TASK_MOVE);
      this.npc.getNavigator().tryMoveToEntityLiving(target, 1.d);
      this.moveDelay = 10;
      if(distance>256){moveDelay+=10;}
      if(distance>1024){moveDelay+=20;}
      }    
    }
  else
    {
    this.npc.removeAITask(TASK_MOVE);   
    this.npc.getNavigator().clearPathEntity();
    }  
  }

}
