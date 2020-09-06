package net.shadowmage.ancientwarfare.npc.ai.faction;

import net.shadowmage.ancientwarfare.npc.ai.NpcAIMedicBase;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFaction;
import net.shadowmage.ancientwarfare.npc.entity.faction.attributes.AdditionalAttributes;

public class NpcAIFactionPriest extends NpcAIMedicBase<NpcFaction> {

	public NpcAIFactionPriest(NpcFaction npc) {
		super(npc);
	}

	@Override
	protected boolean isProperSubtype() {
		return true;
	}

	@Override
	protected float getAmountToHealEachTry() {
		return npc.getAdditionalAttributeValue(AdditionalAttributes.HEAL_PER_TRY).orElse(0f);
	}
}
