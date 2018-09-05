package net.shadowmage.ancientwarfare.npc.compat;

import net.shadowmage.ancientwarfare.core.compat.ICompat;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;
import net.shadowmage.ancientwarfare.npc.event.EventHandler;

public class EpicSiegeCompat implements ICompat {
	@Override
	public String getModId() {
		return "epicsiegemod";
	}

	private Class hostileAI;

	@Override
	public void init() {
		try {
			hostileAI = Class.forName("funwayguy.epicsiegemod.ai.ESM_EntityAINearestAttackableTarget");
			EventHandler.INSTANCE.registerAdditionalHostileAICheck(e -> hostileAI.isInstance(e));
		}
		catch (ClassNotFoundException e) {
			AncientWarfareNPC.LOG.error("Unable to find ESM_EntityAINearestAttackableTarget ai class", e);
		}
	}
}
