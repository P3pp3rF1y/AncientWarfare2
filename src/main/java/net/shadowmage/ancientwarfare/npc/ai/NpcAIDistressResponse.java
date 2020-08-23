package net.shadowmage.ancientwarfare.npc.ai;

import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.entity.NpcCombat;

public class NpcAIDistressResponse extends NpcAI<NpcBase> {
	private NpcBase target = null;
	private static final double FOLLOW_STOP_DISTANCE = 2.d * 2.d;

	public NpcAIDistressResponse(NpcBase npc) {
		super(npc);
		setMutexBits(MOVE + ATTACK);
	}

	@Override
	public boolean shouldExecute() {
		if (!super.shouldExecute()) {
			return false;
		}
		if (npc instanceof NpcCombat) {
			target = ((NpcCombat) npc).getDistressedTarget();
			if (target != null) {
				if (target.isEntityAlive()) {
					return true;
				} else {
					((NpcCombat) npc).clearDistress();
				}
			}
		}
		return false;
	}

	@Override
	public void startExecuting() {
		moveRetryDelay = 0;
		npc.addAITask(TASK_FOLLOW + TASK_ATTACK);
	}

	@Override
	public void resetTask() {
		target = null;
		moveRetryDelay = 0;
		npc.removeAITask(TASK_FOLLOW + TASK_MOVE + TASK_ATTACK);
		if (npc instanceof NpcCombat) {
			((NpcCombat) npc).clearDistress();
		}
	}

	@Override
	public void updateTask() {
		if (target.isDead) {
			// mission failed!
			resetTask();
			return;
		}
		npc.getLookHelper().setLookPositionWithEntity(target, 10.0F, (float) npc.getVerticalFaceSpeed());
		double distance = npc.getDistanceSq(target);
		if (distance > FOLLOW_STOP_DISTANCE) {
			npc.addAITask(TASK_MOVE + TASK_ATTACK);
			moveToEntity(target, distance);
		} else {
			// we've reached the distressed NPC, no need to follow them anymore
			resetTask();
		}
	}

}
