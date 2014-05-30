package net.shadowmage.ancientwarfare.npc.ai;

import net.minecraft.util.ChunkCoordinates;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class NpcAIIdleWhenHungry extends NpcAI
{

int moveTimer = 0;

public NpcAIIdleWhenHungry(NpcBase npc)
  {
  super(npc);
  this.setMutexBits(MOVE+ATTACK+HUNGRY);
  }

@Override
public boolean shouldExecute()
  {
  return npc.getAttackTarget()==null && npc.requiresUpkeep() && npc.getFoodRemaining()==0;
  }

@Override
public boolean continueExecuting()
  {
  return npc.getAttackTarget()==null && npc.requiresUpkeep() && npc.getFoodRemaining()==0;
  }

/**
 * Execute a one shot task or start executing a continuous task
 */
@Override
public void startExecuting()
  {
  npc.addAITask(TASK_IDLE_HUNGRY);
  moveTimer = 0;
  if(npc.hasHome())
    {
    ChunkCoordinates cc = npc.getHomePosition();
    npc.getNavigator().tryMoveToXYZ(cc.posX, cc.posY, cc.posZ, 1.0d);    
    }
  }

/**
 * Resets the task
 */
@Override
public void resetTask()
  {
  npc.removeAITask(TASK_IDLE_HUNGRY);
  }

/**
 * Updates the task
 */
@Override
public void updateTask()
  {
  if(npc.hasHome())    
    {
    moveTimer--;
    if(moveTimer<=0)
      {
      ChunkCoordinates cc = npc.getHomePosition();
      npc.getNavigator().tryMoveToXYZ(cc.posX, cc.posY, cc.posZ, 1.0d);
      moveTimer = 10;
      }    
    }
  }


}
