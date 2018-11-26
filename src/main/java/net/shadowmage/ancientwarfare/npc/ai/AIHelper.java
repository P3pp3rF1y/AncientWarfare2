package net.shadowmage.ancientwarfare.npc.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public final class AIHelper {
	private AIHelper() {
	}

	public static boolean isTarget(NpcBase npc, EntityLivingBase target, boolean checkSight) {
		return !(target == npc || !target.isEntityAlive() || !npc.canTarget(target)
				|| target instanceof EntityPlayer && ((EntityPlayer) target).capabilities.disableDamage
				|| checkSight && !npc.getEntitySenses().canSee(target));
	}
}
