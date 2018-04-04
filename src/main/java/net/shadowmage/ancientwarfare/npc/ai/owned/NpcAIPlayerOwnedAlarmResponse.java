package net.shadowmage.ancientwarfare.npc.ai.owned;

import net.minecraft.util.math.BlockPos;
import net.shadowmage.ancientwarfare.npc.ai.NpcAI;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;
import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;

public class NpcAIPlayerOwnedAlarmResponse extends NpcAI<NpcPlayerOwned> {

	public NpcAIPlayerOwnedAlarmResponse(NpcPlayerOwned npc) {
		super(npc);
		this.setMutexBits(ATTACK + MOVE);
	}

	@Override
	public boolean shouldExecute() {
		if (!npc.getIsAIEnabled()) {
			return false;
		}
		return npc.getUpkeepPoint() != null && npc.getUpkeepDimensionId() == npc.world.provider.getDimension() && npc.isAlarmed;
	}

	@Override
	public boolean shouldContinueExecuting() {
		if (!npc.getIsAIEnabled()) {
			return false;
		}
		return npc.getUpkeepPoint() != null && npc.getUpkeepDimensionId() == npc.world.provider.getDimension() && npc.isAlarmed;
	}

	/*
	 * Execute a one shot task or start executing a continuous task
	 */
	@Override
	public void startExecuting() {
		npc.addAITask(TASK_ALARM);
	}

	/*
	 * Updates the task
	 */
	@Override
	public void updateTask() {
		BlockPos pos = npc.getUpkeepPoint();
		if (pos == null) {
			return;
		}
		double dist = npc.getDistanceSq(pos.getX() + 0.5d, pos.getY(), pos.getZ() + 0.5d);
		if (dist > AWNPCStatics.npcActionRange * AWNPCStatics.npcActionRange) {
			npc.addAITask(TASK_MOVE);
			moveToPosition(pos, dist);
		} else {
			npc.removeAITask(TASK_MOVE);
		}
	}

	/*
	 * Resets the task
	 */
	@Override
	public void resetTask() {
		moveRetryDelay = 0;
		npc.removeAITask(TASK_ALARM + TASK_MOVE);
	}
}
