package net.shadowmage.ancientwarfare.npc.ai;

import net.minecraft.entity.EntityLivingBase;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public abstract class NpcAIAttack<T extends NpcBase> extends NpcAI<T> {
	private EntityLivingBase target;
	private int attackDelay = 35;

	public NpcAIAttack(T npc) {
		super(npc);
	}

	@Override
	public final boolean shouldExecute() {
		return npc.getIsAIEnabled() && npc.getAttackTarget() != null && npc.getAttackTarget().isEntityAlive();
	}

	@Override
	public final boolean shouldContinueExecuting() {
		return npc.getIsAIEnabled() && target != null && target.isEntityAlive() && target.equals(npc.getAttackTarget());
	}

	@Override
	public final void startExecuting() {
		target = npc.getAttackTarget();
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
	}

	@Override
	public final void updateTask() {
		npc.getLookHelper().setLookPositionWithEntity(target, 30.f, 30.f);
		double distanceToEntity = this.npc.getDistanceSq(target.posX, target.getEntityBoundingBox().minY, target.posZ);
		if (shouldCloseOnTarget(distanceToEntity)) {
			npc.addAITask(TASK_MOVE);
			moveToEntity(target, distanceToEntity);
		} else {
			attackDelay--;
			doAttack(distanceToEntity);
		}
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
