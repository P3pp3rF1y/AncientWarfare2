package net.shadowmage.ancientwarfare.npc.ai;

import net.minecraft.entity.Entity;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class NpcAIFollowPlayer extends NpcAI<NpcBase> {
	private static final double ATTACK_IGNORE_DISTANCE = 4.d * 4.d;
	private static final double FOLLOW_STOP_DISTANCE = 4.d * 4.d;

	private Entity target;

	public NpcAIFollowPlayer(NpcBase npc) {
		super(npc);
		setMutexBits(MOVE);
	}

	@Override
	public boolean shouldExecute() {
		if (!super.shouldExecute()) {
			return false;
		}
		target = npc.getFollowingEntity();
		if (target == null) {
			return false;
		}
		if (npc.getAttackTarget() != null) {
			return npc.getDistanceSq(target) >= ATTACK_IGNORE_DISTANCE || npc.getDistanceSq(npc.getAttackTarget()) >= ATTACK_IGNORE_DISTANCE;
		}
		return true;
	}

	@Override
	public void startExecuting() {
		moveRetryDelay = 0;
		npc.addAITask(TASK_FOLLOW);
	}

	@Override
	public void resetTask() {
		target = null;
		moveRetryDelay = 0;
		npc.removeAITask(TASK_FOLLOW + TASK_MOVE);
	}

	@Override
	public void updateTask() {
		npc.getLookHelper().setLookPositionWithEntity(target, 10.0F, (float) npc.getVerticalFaceSpeed());
		double distance = npc.getDistanceSq(target);
		if (distance > FOLLOW_STOP_DISTANCE) {
			npc.addAITask(TASK_MOVE);
			moveToEntity(target, distance);
		} else {
			npc.removeAITask(TASK_MOVE);
			npc.getNavigator().clearPath();
		}
	}
}
