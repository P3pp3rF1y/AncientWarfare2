package net.shadowmage.ancientwarfare.npc.ai;

import net.shadowmage.ancientwarfare.npc.entity.NpcBard;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class NpcAIBard extends NpcAI
{

NpcBard bard;
public NpcAIBard(NpcBase npc)
  {
  super(npc);
  this.bard = (NpcBard)npc;
  }

@Override
public boolean shouldExecute()
  {
  return npc.getIsAIEnabled();
  }

@Override
public boolean continueExecuting()
  {
  return npc.getIsAIEnabled();
  }

@Override
public void startExecuting()
  {
  
  }

@Override
public void updateTask()
  {
  
  }

}
