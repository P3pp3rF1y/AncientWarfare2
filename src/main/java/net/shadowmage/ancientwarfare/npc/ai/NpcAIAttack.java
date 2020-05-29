package net.shadowmage.ancientwarfare.npc.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.util.EnumHand;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

import java.util.Optional;

public abstract class NpcAIAttack<T extends NpcBase> extends NpcAI<T> {
	private EntityLivingBase target;
	private int attackDelay = 35;
	private int blockingDuration = 40;
	private int maxBlockDuration = 40;

	public NpcAIAttack(T npc) {
		super(npc);
	}

	@Override
	public boolean shouldExecute() {
		return npc.getIsAIEnabled() && getAttackTarget().isPresent() && getAttackTarget().map(EntityLivingBase::isEntityAlive).orElse(false) && isTargetInRange();
	}

	@Override
	public boolean shouldContinueExecuting() {
		return npc.getIsAIEnabled() && target != null && target.isEntityAlive() && getAttackTarget().map(t -> t.equals(target)).orElse(false) && isTargetInRange();
	}

	@Override
	public final void startExecuting() {
		target = getAttackTarget().orElse(null);
		moveRetryDelay = 0;
		npc.addAITask(TASK_ATTACK);
		npc.setSwingingArms(true);
	}

	@Override
	public final void resetTask() {
		target = null;
		moveRetryDelay = 0;
		npc.removeAITask(TASK_MOVE + TASK_ATTACK);
		npc.setSwingingArms(false);
		npc.stopActiveHand();
	}

	@Override
	public final void updateTask() {
		npc.getLookHelper().setLookPositionWithEntity(target, 30.f, 30.f);
		double distanceToEntity = npc.getDistanceSq(target.posX, target.getEntityBoundingBox().minY, target.posZ);
		if (shouldCloseOnTarget(distanceToEntity)) {
			npc.addAITask(TASK_MOVE);
			moveToEntity(target, distanceToEntity);
			if (hasShieldInOffhand() && blockingDuration > 0) {
				blockingDuration--;
				npc.setActiveHand(EnumHand.OFF_HAND);
				npc.setBlocking(true);
			} else {
				npc.setBlocking(false);
				npc.stopActiveHand();
			}
		} else {
			attackDelay--;
			doAttack(distanceToEntity);
			if (hasShieldInOffhand()) {
				npc.setActiveHand(EnumHand.OFF_HAND);
				npc.setBlocking(true);
				blockingDuration = maxBlockDuration;
			}
		}
	}

	private boolean isTargetInRange() {
		//noinspection ConstantConditions
		return getAttackTarget().map(t -> npc.getDistance(t) < getAdjustedTargetDistance()).orElse(false);
	}

	private Optional<EntityLivingBase> getAttackTarget() {
		return npc.getAttackTarget() != null ? Optional.of(npc.getAttackTarget()) : Optional.ofNullable(npc.getRevengeTarget());
	}

	private double getAdjustedTargetDistance() {
		if (npc.getFollowingEntity() != null) {
			return Math.max(getTargetDistance() / getDistanceToFollowingEntity(), 3D);
		}
		return Math.max(getTargetDistance() / (getHomeDistance() / 20), 3D);
	}

	private double getDistanceToFollowingEntity() {
		return Math.sqrt(npc.getDistanceSq(npc.getFollowingEntity().getPosition()));
	}

	private double getHomeDistance() {
		return Math.sqrt(npc.getDistanceSq(npc.getHomePosition()));
	}

	private double getTargetDistance() {
		IAttributeInstance iattributeinstance = npc.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE);
		//noinspection ConstantConditions
		return iattributeinstance == null ? 35.0D : iattributeinstance.getAttributeValue();
	}

	protected abstract boolean shouldCloseOnTarget(double distanceToEntity);

	protected abstract void doAttack(double distanceToEntity);

	public final EntityLivingBase getTarget() {
		return target;
	}

	public final void setAttackDelay(int value) {
		attackDelay = value;
	}

	public final int getAttackDelay() {
		return attackDelay;
	}

	public boolean hasShieldInOffhand() {
		return npc.getHeldItemOffhand().getItem().isShield(npc.getHeldItemOffhand(), npc);
	}
}
