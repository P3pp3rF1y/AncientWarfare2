package net.shadowmage.ancientwarfare.npc.ai;

import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class NpcAIGetFood extends NpcAI
{

public NpcAIGetFood(NpcBase npc)
  {
  super(npc);
  this.setMutexBits(MOVE+ATTACK);
  }

@Override
public boolean shouldExecute()
  {
//  AWLog.logDebug("npc get food shouldExecute");
  return npc.getUpkeepPoint()!=null && npc.getFoodRemaining()==0 && npc.getUpkeepDimensionId()==npc.worldObj.provider.dimensionId;
  }

@Override
public boolean continueExecuting()
  {
//  AWLog.logDebug("npc get food continueExecuting");
  return npc.getUpkeepPoint()!=null && npc.getFoodRemaining()==0;
  }

/**
 * Execute a one shot task or start executing a continuous task
 */
@Override
public void startExecuting()
  {
//  AWLog.logDebug("npc get food starting executing");
  }

/**
 * Resets the task
 */
@Override
public void resetTask()
  {
//  AWLog.logDebug("npc get food resetting task");
  }

/**
 * Updates the task
 */
@Override
public void updateTask()
  {
//  AWLog.logDebug("npc get food update task");  
  }

}
