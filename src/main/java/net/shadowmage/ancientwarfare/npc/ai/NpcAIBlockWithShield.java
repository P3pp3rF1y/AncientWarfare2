package net.shadowmage.ancientwarfare.npc.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.util.EnumHand;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

import java.util.Optional;

public class NpcAIBlockWithShield extends NpcAI<NpcBase> {
	private EntityLivingBase target;
	private int blockingDuration = 40;
	private int maxBlockingDuration;
	private int cooldownTicks;
	private float attackReach = 2.5F;

	public NpcAIBlockWithShield(NpcBase npc, int maxBlockingDurationTicks, int cooldownTicks) {
		super(npc);
		maxBlockingDuration = maxBlockingDurationTicks;
		this.cooldownTicks = cooldownTicks;
	}

	@Override
	public boolean shouldExecute() {
		return npc.getIsAIEnabled() && hasShieldInOffhand() && npc.getShieldDisabledTick() <= 0 && getAttackTarget().isPresent() && getAttackTarget().map(EntityLivingBase::isEntityAlive).orElse(false) && isTargetInRange();
	}

	@Override
	public boolean shouldContinueExecuting() {
		return hasShieldInOffhand() && npc.getShieldDisabledTick() <= 0 && npc.getIsAIEnabled() && target != null && target.isEntityAlive() && getAttackTarget().map(t -> t.equals(target)).orElse(false) && isTargetInRange();
	}

	@Override
	public void startExecuting() {
		target = getAttackTarget().orElse(null);
		moveRetryDelay = 0;
		blockingDuration = maxBlockingDuration;
	}

	@Override
	public final void resetTask() {
		target = null;
		moveRetryDelay = 0;
		npc.removeAITask(TASK_MOVE + TASK_ATTACK);
		npc.setShieldBlocking(false);
		npc.stopActiveHand();
	}

	@Override
	public final void updateTask() {
		npc.getLookHelper().setLookPositionWithEntity(target, 30.f, 30.f);
		double distanceToEntity = npc.getDistanceSq(target.posX, target.getEntityBoundingBox().minY, target.posZ);
		if (blockingDuration > 0) {
			blockingDuration--;
		} else {
			stopBlocking();
		}
		if (shouldCloseOnTarget(distanceToEntity)) {
			npc.addAITask(TASK_MOVE);
			moveToEntity(target, distanceToEntity);
			if (isTargetAimingWithBow()) {
				if (blockingDuration > 0) {
					startBlocking();
				}
			}
		} else if (blockingDuration > 0) {
			startBlocking();
		}
	}

	private void startBlocking() {
		npc.setActiveHand(EnumHand.OFF_HAND);
		npc.setShieldBlocking(true);
		moveSpeed = 0.8;
	}

	private void stopBlocking() {
		npc.stopActiveHand();
		npc.setShieldDisabledTick(cooldownTicks);
		moveSpeed = 1.0;
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

	protected boolean shouldCloseOnTarget(double distanceToEntity) {
		double attackDistance = (npc.width / 2D) + (getTarget().width / 2D) + attackReach;
		return (distanceToEntity > (attackDistance * attackDistance)) || !npc.getEntitySenses().canSee(getTarget());
	}

	public final EntityLivingBase getTarget() {
		return target;
	}

	private boolean hasShieldInOffhand() {
		return npc.getHeldItemOffhand().getItem().isShield(npc.getHeldItemOffhand(), npc);
	}

	private boolean isTargetAimingWithBow() {
		return (npc.isBow(target.getHeldItemMainhand().getItem()) && target.isHandActive() && target.getActiveHand() == EnumHand.MAIN_HAND) ||
				(npc.isBow(target.getHeldItemOffhand().getItem()) && target.isHandActive() && target.getActiveHand() == EnumHand.OFF_HAND);
	}
}
