package net.shadowmage.ancientwarfare.npc.ai.faction;

import net.shadowmage.ancientwarfare.npc.ai.NpcAIMedicBase;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class NpcAIFactionPriest extends NpcAIMedicBase
{

public NpcAIFactionPriest(NpcBase npc)
  {
  super(npc);
  }

@Override
protected boolean isProperSubtype()
  {
  return true;
  }

}
