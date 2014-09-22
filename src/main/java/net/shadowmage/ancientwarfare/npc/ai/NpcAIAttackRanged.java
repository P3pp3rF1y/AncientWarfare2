package net.shadowmage.ancientwarfare.npc.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class NpcAIAttackRanged extends NpcAI
{

private final IRangedAttackMob rangedAttacker;
private int attackDelay = 35;
private double attackDistance = 16.d * 16.d;
private EntityLivingBase target;

public NpcAIAttackRanged(NpcBase npc)
    {
    super(npc);
    this.rangedAttacker = (IRangedAttackMob)npc;//will classcastexception if improperly used..
    this.moveSpeed = 1.d;
    }

@Override
public boolean shouldExecute()
  {
  return npc.getIsAIEnabled() && npc.getAttackTarget()!=null && !npc.getAttackTarget().isDead;
  }

/**
 * Returns whether an in-progress EntityAIBase should continue executing
 */
@Override
public boolean continueExecuting()
  {
  return npc.getIsAIEnabled() && target!=null && !target.isDead && target==npc.getAttackTarget();
  }

/**
 * Resets the task
 */
@Override
public void resetTask()
  {
  target = null;
  moveRetryDelay=0;
  attackDelay=0;
  npc.removeAITask(TASK_ATTACK + TASK_MOVE);
  }

@Override
public void startExecuting()
  {
  target = npc.getAttackTarget();
  moveRetryDelay=0;
  attackDelay=0;
  npc.addAITask(TASK_ATTACK);
  }

/**
 * Updates the task
 */
@Override
public void updateTask()
  {  
  double dist = this.npc.getDistanceSq(this.target.posX, this.target.posY, this.target.posZ);
  boolean canSee = this.npc.getEntitySenses().canSee(this.target);  

  this.npc.getLookHelper().setLookPositionWithEntity(this.target, 30.0F, 30.0F);
  if(dist>attackDistance || !canSee)
    {
    this.npc.addAITask(TASK_MOVE);
    this.moveToEntity(target, dist);    
    }
  else
    {
    npc.removeAITask(TASK_MOVE);
    this.npc.getNavigator().clearPathEntity();
    this.attackDelay--;
    if(this.attackDelay<=0)
      {
      float pwr = (float)(attackDistance / dist);
      pwr = pwr < 0.1f ? 0.1f : pwr>1.f ? 1.f : pwr;
      this.rangedAttacker.attackEntityWithRangedAttack(target, 1.f);
      this.attackDelay=35;
      }
    }  
  }
}
