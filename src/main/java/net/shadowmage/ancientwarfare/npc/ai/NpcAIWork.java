package net.shadowmage.ancientwarfare.npc.ai;

import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class NpcAIWork extends NpcAI
{

public NpcAIWork(NpcBase npc)
  {
  super(npc);
  }

@Override
public boolean shouldExecute()
  {
  return false;
  }

}
