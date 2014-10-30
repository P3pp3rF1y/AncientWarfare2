package net.shadowmage.ancientwarfare.npc.ai.faction;

import net.shadowmage.ancientwarfare.npc.ai.owned.NpcAIPlayerOwnedFindCommander;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFaction;

public class NpcAIFactionFindCommander extends NpcAIPlayerOwnedFindCommander
{

public NpcAIFactionFindCommander(NpcBase npc)
  {
  super(npc);
  }

@Override
protected boolean isCommander(NpcBase npc)
  {
  return npc instanceof NpcFaction && ((NpcFaction)npc).getFaction().equals(((NpcFaction)this.npc).getFaction()) && npc.getNpcType().toLowerCase().endsWith("leader");
  }

}
