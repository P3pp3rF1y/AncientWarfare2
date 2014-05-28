package net.shadowmage.ancientwarfare.npc.ai;

import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class NpcAIFollowCommand extends NpcAI
{

public NpcAIFollowCommand(NpcBase npc)
  {
  super(npc);
  this.setMutexBits(ATTACK+MOVE);
  }

@Override
public boolean shouldExecute()
  {
  return false;
  }

@Override
public boolean continueExecuting()
  {
  return super.continueExecuting();
  }

@Override
public void startExecuting()
  {
  // TODO Auto-generated method stub
  super.startExecuting();
  }

@Override
public void updateTask()
  {
  // TODO Auto-generated method stub
  super.updateTask();
  }

@Override
public void resetTask()
  {
  // TODO Auto-generated method stub
  super.resetTask();
  }

}
