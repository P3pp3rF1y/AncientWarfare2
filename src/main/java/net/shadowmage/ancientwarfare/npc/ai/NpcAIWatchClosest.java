package net.shadowmage.ancientwarfare.npc.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class NpcAIWatchClosest extends EntityAIWatchClosest {
	public NpcAIWatchClosest(NpcBase npc, Class<? extends Entity> watchTargetClass, float maxDistance) {
		super(npc, watchTargetClass, maxDistance);
	}

	@Override
	public boolean shouldExecute() {
		//just a minor modification from vanilla that will always prefer to watch attacked target
		closestEntity = entity.getAttackTarget();
		return closestEntity != null || super.shouldExecute();
	}
}
