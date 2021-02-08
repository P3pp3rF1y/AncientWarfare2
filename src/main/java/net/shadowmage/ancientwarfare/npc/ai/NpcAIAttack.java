package net.shadowmage.ancientwarfare.npc.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

import java.util.Optional;

public abstract class NpcAIAttack<T extends NpcBase> extends NpcAI<T> {
	private EntityLivingBase target;
	private int attackDelay = 0;

	public NpcAIAttack(T npc) {
		super(npc);
	}

	@Override
	public boolean shouldExecute() {
		return super.shouldExecute() && hasTargetInRange();
	}

	private boolean hasTargetInRange() {
		if (target == null) {
			return getAttackTarget().isPresent() && getAttackTarget().map(EntityLivingBase::isEntityAlive).orElse(false) && isTargetInRange();
		}
		return target.isEntityAlive() && getAttackTarget().map(t -> t.equals(target)).orElse(false) && isTargetInRange();
	}

	private boolean isTargetInRange() {
		return getAttackTarget().map(t -> npc.getDistance(t) < getAdjustedTargetDistance()).orElse(false);
	}

	private Optional<EntityLivingBase> getAttackTarget() {
		return npc.getAttackTarget() != null ? Optional.of(npc.getAttackTarget()) : Optional.ofNullable(npc.getRevengeTarget());
	}

	@Override
	public final void startExecuting() {
		target = getAttackTarget().orElse(null);
		moveRetryDelay = 0;
		npc.addAITask(TASK_ATTACK);
		npc.setSwingingArms(true);
		attackDelay = 0;
	}

	@Override
	public final void resetTask() {
		target = null;
		moveRetryDelay = 0;
		npc.removeAITask(TASK_MOVE + TASK_ATTACK);
		npc.setSwingingArms(false);
	}

	protected int getCoolDown() {
		return attackDelay;
	}

	@Override
	public final void updateTask() {
		npc.getLookHelper().setLookPositionWithEntity(target, 30.f, 30.f);
		double distanceToEntitySq = npc.getDistanceSq(target.posX, target.getEntityBoundingBox().minY, target.posZ);
		if (shouldCloseOnTarget(distanceToEntitySq)) {
			npc.addAITask(TASK_MOVE);
			moveToEntity(target, distanceToEntitySq);
		} else {
			npc.getNavigator().clearPath();
			attackDelay--;
			doAttack(distanceToEntitySq);
		}
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
}
