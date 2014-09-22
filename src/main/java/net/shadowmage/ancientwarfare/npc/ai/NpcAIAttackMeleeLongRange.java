package net.shadowmage.ancientwarfare.npc.ai;

import net.minecraft.entity.Entity;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class NpcAIAttackMeleeLongRange extends NpcAI
{

Entity target;
int attackDelay = 0;

public NpcAIAttackMeleeLongRange(NpcBase npc)
  {
  super(npc);
  this.setMutexBits(ATTACK+MOVE);
  }

@Override
public boolean shouldExecute()
  {
  return npc.getIsAIEnabled() && npc.getAttackTarget()!=null && !npc.getAttackTarget().isDead;
  }

@Override
public boolean continueExecuting()
  {
  return npc.getIsAIEnabled() && target!=null && target==npc.getAttackTarget() && !npc.getAttackTarget().isDead;
  }

@Override
public void startExecuting()
  {
  npc.addAITask(TASK_ATTACK);
  target = npc.getAttackTarget();
  attackDelay = 0;
  moveRetryDelay = 0;
  }

@Override
public void updateTask()
  {
  npc.getLookHelper().setLookPositionWithEntity(target, 30.f, 30.f);
  double distanceToEntity = this.npc.getDistanceSq(target.posX, target.boundingBox.minY, target.posZ);
  double attackDistance = (double)((this.npc.width * this.npc.width * 2.0F * 2.0F) + (target.width * target.width * 2.0F * 2.0F));
  if(distanceToEntity>attackDistance)
    {
    npc.addAITask(TASK_MOVE);
    moveToEntity(target, distanceToEntity);
    }
  else
    {
    npc.removeAITask(TASK_MOVE);
    attackTarget();
    }
  }

private void attackTarget()
  {
  attackDelay--;
  if(attackDelay<=0)
    {
    npc.swingItem();
    npc.attackEntityAsMob(target);    
    this.attackDelay=20;//TODO set attack delay from npc-attributes? 
    int xp = AWNPCStatics.npcXpFromAttack;
    npc.addExperience(xp);
    }
  }

@Override
public void resetTask()
  {
  super.resetTask();
  npc.removeAITask(TASK_MOVE + TASK_ATTACK);
  target = null;
  attackDelay = 0;
  moveRetryDelay = 0;
  }

}
