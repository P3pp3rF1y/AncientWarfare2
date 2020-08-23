package net.shadowmage.ancientwarfare.npc.ai;

import net.minecraft.util.math.BlockPos;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class NpcAIMoveHome extends NpcAI<NpcBase> {
	private static final int TICKER_MAX = 10;

	private final float dayRange;
	private final float nightRange;
	private final float dayLeash;
	private final float nightLeash;

	private int ticker = 0;
	private boolean goneHome = false;

	public NpcAIMoveHome(NpcBase npc, float dayRange, float nightRange, float dayLeash, float nightLeash) {
		super(npc);
		setMutexBits(MOVE + ATTACK);
		this.dayRange = dayRange;
		this.nightRange = nightRange;
		this.dayLeash = dayLeash;
		this.nightLeash = nightLeash;
	}

	@Override
	public boolean shouldExecute() {
		if (!super.shouldExecute() || !npc.hasHome()) {
			return false;
		}
		BlockPos cc = npc.getHomePosition();
		float distSq = (float) npc.getDistanceSq(cc.getX() + 0.5d, cc.getY(), cc.getZ() + 0.5d);
		return npc.shouldBeAtHome() || exceedsRange(distSq);
	}

	protected boolean exceedsRange(float distSq) {
		float range = getRange() * getRange();
		return distSq > range;
	}

	protected float getLeashRange() {
		return !npc.shouldSleep() ? dayLeash : nightLeash;
	}

	protected float getRange() {
		return !npc.shouldSleep() ? dayRange : nightRange;
	}

	@Override
	public void startExecuting() {
		npc.addAITask(TASK_GO_HOME);
		updateTasks();
		goneHome = false;
	}

	@Override
	public void updateTask() {
		ticker++;
		if (ticker >= TICKER_MAX && npc.isSleeping()) {
			if (npc.isBedCacheValid()) {
				npc.setPositionToBed();
			} else {
				npc.wakeUp();
			}
			return;
		}
		BlockPos cc = npc.getHomePosition();
		double dist = npc.getDistanceSq(cc.getX() + 0.5d, cc.getY(), cc.getZ() + 0.5d);
		double leash = getLeashRange() * getLeashRange();
		if ((dist > leash) && (!goneHome) && (!npc.isSleeping())) {
			npc.addAITask(TASK_MOVE);
			moveToPosition(cc, dist);
		} else {
			stopMovingOrSleepAtHome();
		}
		if (ticker >= TICKER_MAX) {
			updateTasks();
			ticker = 0;
		}
	}

	private void stopMovingOrSleepAtHome() {
		npc.removeAITask(TASK_MOVE);
		goneHome = true;
		if (npc.getOwner().getName().isEmpty()) {
			stopMovement();
		} else {
			if ((ticker >= TICKER_MAX) && (npc.shouldSleep())) {
				layInBed();
			}
		}
	}

	private void layInBed() {
		BlockPos pos = npc.findBed();
		if (pos != null) {
			double dist = npc.getDistanceSq(pos);
			if (dist > AWNPCStatics.npcActionRange * AWNPCStatics.npcActionRange) {
				forceMoveToPosition(pos, dist);
			} else {
				if (npc.lieDown(pos)) {
					npc.setPositionToBed();
					stopMovement();
				}
			}
		}
	}

	private void updateTasks() {
		if (npc.shouldSleep()) {
			npc.addAITask(TASK_SLEEP);
		} else {
			npc.removeAITask(TASK_SLEEP);
		}
		if (!npc.worksInRain() && npc.world.isRaining()) {
			npc.addAITask(TASK_RAIN);
		} else {
			npc.removeAITask(TASK_RAIN);
		}
	}

	private void stopMovement() {
		npc.removeAITask(TASK_MOVE);
		npc.getNavigator().clearPath();
	}

	@Override
	public void resetTask() {
		stopMovement();
		npc.removeAITask(TASK_GO_HOME + TASK_RAIN + TASK_SLEEP);
		if (npc.isSleeping()) {
			npc.wakeUp();
		}
	}
}
