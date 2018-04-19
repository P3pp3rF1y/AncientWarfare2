package net.shadowmage.ancientwarfare.npc.ai;

import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.entity.NpcCombat;

public class NpcAIDistressResponse extends NpcAI<NpcBase> {

	private NpcBase target;
	private double followStopDistance = 2.d * 2.d;

	public NpcAIDistressResponse(NpcBase npc) {
		super(npc);
		this.setMutexBits(MOVE + ATTACK);
	}

	/*
	 * Returns whether the EntityAIBase should begin execution.
	 */
	@Override
	public boolean shouldExecute() {
		if (this.npc instanceof NpcCombat) {
			target = ((NpcCombat) npc).getDistressedTarget();
			if (target != null) {
				if (target.isEntityAlive())
					return true;
				else
					((NpcCombat) npc).clearDistress();
			}
		}
		return false;
	}

	/*
	 * Execute a one shot task or start executing a continuous task
	 */
	@Override
	public void startExecuting() {
		moveRetryDelay = 0;
		this.npc.addAITask(TASK_FOLLOW + TASK_ATTACK);
	}

	/*
	 * Resets the task
	 */
	@Override
	public void resetTask() {
		this.target = null;
		moveRetryDelay = 0;
		this.npc.removeAITask(TASK_FOLLOW + TASK_MOVE + TASK_ATTACK);
		if (npc instanceof NpcCombat)
			((NpcCombat) npc).clearDistress();
	}

	/*
	 * Updates the task
	 */
	@Override
	public void updateTask() {
		if (target.isDead) {
			// mission failed!
			resetTask();
			return;
		}
		this.npc.getLookHelper().setLookPositionWithEntity(this.target, 10.0F, (float) this.npc.getVerticalFaceSpeed());
		double distance = npc.getDistanceSqToEntity(target);
		if (distance > followStopDistance) {
			this.npc.addAITask(TASK_MOVE + TASK_ATTACK);
			moveToEntity(target, distance);
		} else // we've reached the distressed NPC, no need to follow them anymore
			resetTask();
	}

}
