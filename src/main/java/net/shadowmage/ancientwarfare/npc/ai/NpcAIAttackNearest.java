package net.shadowmage.ancientwarfare.npc.ai;

import com.google.common.base.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.util.math.AxisAlignedBB;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

import javax.annotation.Nullable;

public class NpcAIAttackNearest extends EntityAINearestAttackableTarget<EntityLivingBase> {
	@SuppressWarnings({"java:S4738", "Guava"})
	public NpcAIAttackNearest(NpcBase npc, @Nullable final Predicate<Entity> targetSelector) {
		super(npc, EntityLivingBase.class, 0, true, false, targetSelector);
	}

	@Override
	public boolean shouldExecute() {
		boolean ret = super.shouldExecute();
		if (!ret && taskOwner.getAttackTarget() != null && taskOwner.getAttackTarget().isDead) {
			taskOwner.setAttackTarget(null);
		}
		return ret;
	}

	@Override
	protected boolean isSuitableTarget(@Nullable EntityLivingBase target, boolean unused) {
		return AIHelper.isTarget((NpcBase) taskOwner, target, shouldCheckSight);
	}

	@Override
	protected AxisAlignedBB getTargetableArea(double targetDistance) {
		return taskOwner.getEntityBoundingBox().grow(targetDistance, targetDistance, targetDistance);
	}
}
