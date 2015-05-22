package net.shadowmage.ancientwarfare.npc.ai.faction;

import net.shadowmage.ancientwarfare.npc.ai.owned.NpcAIPlayerOwnedCommander;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFaction;

import java.util.Locale;

public class NpcAIFactionCommander extends NpcAIPlayerOwnedCommander {

    public NpcAIFactionCommander(NpcFaction npc) {
        super(npc);
    }

    @Override
    protected boolean isCommander(NpcBase npc) {
        return canBeCommanded(npc) && npc.getNpcType().toLowerCase(Locale.ENGLISH).endsWith("leader");
    }

    @Override
    protected boolean canBeCommanded(NpcBase npc){
        return npc instanceof NpcFaction && ((NpcFaction) npc).getFaction().equals(((NpcFaction) this.npc).getFaction());
    }
}
