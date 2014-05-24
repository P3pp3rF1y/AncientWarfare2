package net.shadowmage.ancientwarfare.npc.ai;

import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class NpcAIIdleWhenHungry extends NpcAI
{

public NpcAIIdleWhenHungry(NpcBase npc)
  {
  super(npc);
  this.setMutexBits(MOVE+ATTACK+HUNGRY);
  }

@Override
public boolean shouldExecute()
  {
  return npc.requiresUpkeep() && npc.getFoodRemaining()==0;
  }

@Override
public boolean continueExecuting()
  {
  return npc.requiresUpkeep() && npc.getFoodRemaining()==0;
  }

/**
 * Execute a one shot task or start executing a continuous task
 */
@Override
public void startExecuting()
  {
//  AWLog.logDebug("npc idle food starting executing");
  //TODO move towards home point
  }

/**
 * Resets the task
 */
@Override
public void resetTask()
  {
//  AWLog.logDebug("npc idle food resetting task");
  }

/**
 * Updates the task
 */
@Override
public void updateTask()
  {
//  AWLog.logDebug("npc idle food update task");  
  }


}
