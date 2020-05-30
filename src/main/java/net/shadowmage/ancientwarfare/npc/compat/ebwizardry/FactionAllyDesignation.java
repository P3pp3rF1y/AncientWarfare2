package net.shadowmage.ancientwarfare.npc.compat.ebwizardry;

import electroblob.wizardry.util.IElementalDamage;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFaction;

public final class FactionAllyDesignation {

	// Prevents any magic damage to friendly NPCs
	@SubscribeEvent
	public static void onLivingAttackEvent(LivingAttackEvent event) {
		if (event.getSource() instanceof IElementalDamage && event.getSource().getTrueSource() instanceof NpcFaction) {
			if (event.getEntity() instanceof NpcFaction) {
				NpcFaction caster = (NpcFaction) event.getSource().getTrueSource();
				NpcFaction target = (NpcFaction) event.getEntity();
				if (target.getFaction().equals(caster.getFaction())) {
					event.setCanceled(true);
				}
			}
		}
	}
}
