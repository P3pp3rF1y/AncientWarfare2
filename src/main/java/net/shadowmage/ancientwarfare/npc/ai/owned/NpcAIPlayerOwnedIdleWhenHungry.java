package net.shadowmage.ancientwarfare.npc.ai.owned;

import net.shadowmage.ancientwarfare.npc.ai.NpcAI;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class NpcAIPlayerOwnedIdleWhenHungry extends NpcAI<NpcBase> {

	int moveTimer = 0;

	public NpcAIPlayerOwnedIdleWhenHungry(NpcBase npc) {
		super(npc);
		setMutexBits(MOVE + ATTACK + HUNGRY);
	}

	@Override
	public boolean shouldExecute() {
		return super.shouldExecute() && npc.getAttackTarget() == null && npc.requiresUpkeep() && npc.getFoodRemaining() == 0;
	}

	/*
	 * Execute a one shot task or start executing a continuous task
	 */
	@Override
	public void startExecuting() {
		npc.addAITask(TASK_IDLE_HUNGRY);
		moveTimer = 0;
		if (npc.hasHome()) {
			returnHome();
		}
	}

	/*
	 * Resets the task
	 */
	@Override
	public void resetTask() {
		npc.removeAITask(TASK_IDLE_HUNGRY);
	}

	/*
	 * Updates the task
	 */
	@Override
	public void updateTask() {
		if (npc.hasHome()) {
			moveTimer--;
			if (moveTimer <= 0) {
				returnHome();
				moveTimer = 10;
			}
		}
	}

}
