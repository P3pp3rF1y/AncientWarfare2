package net.shadowmage.ancientwarfare.npc.ai.faction;

import net.minecraft.entity.ai.EntityAIRestrictSun;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFaction;

public class NpcAIFactionRestrictSun extends EntityAIRestrictSun {
	private NpcFaction npc;

	public NpcAIFactionRestrictSun(NpcFaction npc) {
		super(npc);
		this.npc = npc;
	}

	@Override
	public boolean shouldExecute() {
		return npc.burnsInSun() && super.shouldExecute();
	}
}
