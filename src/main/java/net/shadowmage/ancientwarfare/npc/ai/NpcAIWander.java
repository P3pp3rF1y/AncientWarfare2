package net.shadowmage.ancientwarfare.npc.ai;

import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.Vec3d;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class NpcAIWander extends NpcAI<NpcBase> {

	private Vec3d vec3;

	public NpcAIWander(NpcBase npc) {
		this(npc, 0.625);
	}

	public NpcAIWander(NpcBase npc, double par2) {
		super(npc);
		this.moveSpeed = par2;
		this.setMutexBits(MOVE);
	}

	@Override
	public boolean shouldExecute() {
		if (!npc.getIsAIEnabled() || npc.shouldBeAtHome() || npc.getRNG().nextInt(120) != 0 || npc.hasPath()) {
			return false;
		} else {
			vec3 = RandomPositionGenerator.findRandomTarget(npc, MIN_RANGE, 7);
			return vec3 != null;
		}
	}

	@Override
	public boolean shouldContinueExecuting() {
		if (!npc.getIsAIEnabled() || npc.shouldBeAtHome()) {
			return false;
		}
		return !npc.getNavigator().noPath();
	}

	@Override
	public void startExecuting() {
		npc.addAITask(NpcAI.TASK_WANDER + NpcAI.TASK_MOVE);
		setPath(vec3.x, vec3.y, vec3.z);
	}

	@Override
	public void resetTask() {
		npc.removeAITask(NpcAI.TASK_WANDER + NpcAI.TASK_MOVE);
	}

}
