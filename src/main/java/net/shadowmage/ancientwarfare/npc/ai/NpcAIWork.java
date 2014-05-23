package net.shadowmage.ancientwarfare.npc.ai;

import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class NpcAIWork extends NpcAI
{

public NpcAIWork(NpcBase npc)
  {
  super(npc);
  //TODO need to design orders-slip first...
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


}
