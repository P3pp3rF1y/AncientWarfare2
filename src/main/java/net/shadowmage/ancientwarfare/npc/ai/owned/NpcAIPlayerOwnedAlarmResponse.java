package net.shadowmage.ancientwarfare.npc.ai.owned;

import net.shadowmage.ancientwarfare.npc.ai.NpcAI;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;
import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;

public class NpcAIPlayerOwnedAlarmResponse extends NpcAI<NpcPlayerOwned> {

	public NpcAIPlayerOwnedAlarmResponse(NpcPlayerOwned npc) {
		super(npc);
		setMutexBits(ATTACK + MOVE);
	}

	@Override
	public boolean shouldExecute() {
		return super.shouldExecute() && npc.getUpkeepPoint().isPresent() && npc.getUpkeepDimensionId() == npc.world.provider.getDimension() && npc.isAlarmed;
	}

	@Override
	public void startExecuting() {
		npc.addAITask(TASK_ALARM);
	}

	@Override
	public void updateTask() {
		npc.getUpkeepPoint().ifPresent(pos -> {
			double dist = npc.getDistanceSq(pos.getX() + 0.5d, pos.getY(), pos.getZ() + 0.5d);
			if (dist > AWNPCStatics.npcActionRange * AWNPCStatics.npcActionRange) {
				npc.addAITask(TASK_MOVE);
				moveToPosition(pos, dist);
			} else {
				npc.removeAITask(TASK_MOVE);
			}
		});
	}

	@Override
	public void resetTask() {
		moveRetryDelay = 0;
		npc.removeAITask(TASK_ALARM + TASK_MOVE);
	}
}
