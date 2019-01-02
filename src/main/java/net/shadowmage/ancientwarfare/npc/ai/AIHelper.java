package net.shadowmage.ancientwarfare.npc.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

import javax.annotation.Nullable;

public final class AIHelper {
	private AIHelper() {
	}

	public static boolean isTarget(NpcBase npc, @Nullable EntityLivingBase target, boolean checkSight) {
		return target != null && !(target == npc || !target.isEntityAlive() || !npc.canTarget(target)
				|| target instanceof EntityPlayer && ((EntityPlayer) target).capabilities.disableDamage
				|| checkSight && !npc.getEntitySenses().canSee(target));
	}

	public static boolean isWithinFollowRange(EntityLiving entity, EntityLivingBase target) {
		return entity.getDistance(target) <= entity.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getAttributeValue();
	}
}
