package net.shadowmage.ancientwarfare.npc.ai;

import net.minecraft.entity.ai.EntityAIWander;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class NpcAIWander extends EntityAIWander
{
NpcBase npc;
public NpcAIWander(NpcBase npc, double par2)
  {
  super(npc, par2);
  this.npc = npc;
  }

@Override
public boolean shouldExecute()
  {
  if(!npc.getShouldWander()){return false;}
  return super.shouldExecute();
  }

@Override
public boolean continueExecuting()
  {
  if(!npc.getShouldWander()){return false;}
  return super.continueExecuting();
  }

@Override
public void startExecuting()
  {
  npc.addAITask(NpcAI.TASK_WANDER+NpcAI.TASK_MOVE);
  super.startExecuting();  
  }

@Override
public void resetTask()
  {
  npc.removeAITask(NpcAI.TASK_WANDER+NpcAI.TASK_MOVE);
  super.resetTask();
  }

}
