package net.shadowmage.ancientwarfare.npc.ai.owned;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.npc.ai.NpcAI;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;
import net.shadowmage.ancientwarfare.npc.entity.NpcWorker;

import java.util.Optional;

public class NpcAIPlayerOwnedWorkRandom extends NpcAI<NpcWorker> {
	private int ticksAtSite = 0;

	public NpcAIPlayerOwnedWorkRandom(NpcWorker npc) {
		super(npc);
		setMutexBits(ATTACK + MOVE);
	}

	@Override
	public boolean shouldExecute() {
		if (!super.shouldExecute()) {
			return false;
		}
		if (npc.getFoodRemaining() <= 0 || npc.shouldBeAtHome()) {
			return false;
		}
		return npc.ordersStack.isEmpty() && npc.autoWorkTarget != null;
	}

	@Override
	public void startExecuting() {
		npc.addAITask(TASK_WORK);
		ticksAtSite = 0;
	}

	@Override
	public void updateTask() {
		BlockPos pos = npc.autoWorkTarget;
		double dist = npc.getDistanceSq(pos);
		if (dist > npc.getWorkRangeSq()) {
			npc.addAITask(TASK_MOVE);
			ticksAtSite = 0;
			moveToPosition(pos, dist);
		} else {
			npc.getNavigator().clearPath();
			npc.removeAITask(TASK_MOVE);
			workAtSite();
		}
	}

	@Override
	public void resetTask() {
		npc.removeAITask(TASK_WORK + TASK_MOVE);
	}

	private void workAtSite() {
		ticksAtSite++;
		if (npc.ticksExisted % 10 == 0) {
			npc.swingArm(EnumHand.MAIN_HAND);
		}
		if (ticksAtSite >= AWNPCStatics.npcWorkTicks) {
			ticksAtSite = 0;
			Optional<IWorkSite> te = WorldTools.getTile(npc.world, npc.autoWorkTarget, IWorkSite.class);
			if (te.isPresent()) {
				IWorkSite site = te.get();
				if (npc.canWorkAt(site.getWorkType()) && site.hasWork()) {
					npc.addExperience(AWNPCStatics.npcXpFromWork);
					site.addEnergyFromWorker(npc);
					return;
				}
			}
			npc.autoWorkTarget = null;
		}
	}

	public void readFromNBT(NBTTagCompound tag) {
		ticksAtSite = tag.getInteger("ticksAtSite");
	}

	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag.setInteger("ticksAtSite", ticksAtSite);
		return tag;
	}

}
