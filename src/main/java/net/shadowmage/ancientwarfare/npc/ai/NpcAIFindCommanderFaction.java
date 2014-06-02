package net.shadowmage.ancientwarfare.npc.ai;

import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFaction;

public class NpcAIFindCommanderFaction extends NpcAIFindCommander
{

public NpcAIFindCommanderFaction(NpcBase npc)
  {
  super(npc);
  }

@Override
protected boolean isCommander(NpcBase npc)
  {
  return npc instanceof NpcFaction && ((NpcFaction)npc).getFaction().equals(((NpcFaction)this.npc).getFaction()) && npc.getNpcType().toLowerCase().endsWith("leader");
  }

}
