package net.shadowmage.ancientwarfare.npc.ai.faction;

import net.shadowmage.ancientwarfare.npc.ai.NpcAI;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class NpcAIFactionArcherStayAtHome extends NpcAI<NpcBase> {
	public NpcAIFactionArcherStayAtHome(NpcBase npc) {
		super(npc);
		setMutexBits(ATTACK + MOVE);
	}

	@Override
	public boolean shouldExecute() {
		return super.shouldExecute() && (npc.getAttackTarget() == null || npc.getAttackTarget().isDead && npc.hasHome()) && !isAtHome();
	}

	private boolean isAtHome() {
		return npc.getDistanceSqFromHome() <= MIN_RANGE;
	}

	@Override
	public void startExecuting() {
		npc.addAITask(TASK_MOVE);
	}

	@Override
	public void updateTask() {
		moveToPosition(npc.getHomePosition(), npc.getDistanceSqFromHome());
	}

	@Override
	public void resetTask() {
		npc.removeAITask(TASK_MOVE);
	}
}
