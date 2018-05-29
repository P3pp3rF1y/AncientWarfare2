package net.shadowmage.ancientwarfare.npc.ai.vehicle;

import net.shadowmage.ancientwarfare.npc.ai.NpcAI;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.entity.vehicle.IVehicleUser;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;

public class NpcAIFireVehicle<T extends NpcBase & IVehicleUser> extends NpcAI<T> {
	private int actionTick = 0;

	public NpcAIFireVehicle(T npc) {
		super(npc);
	}

	@Override
	public boolean shouldExecute() {
		return npc.getAttackTarget() != null && npc.canContinueRidingVehicle() && npc.isRidingVehicle() && canFire();
	}

	@SuppressWarnings("squid:S3655")
	private boolean canFire() {
		//noinspection ConstantConditions
		return npc.getVehicle().get().firingHelper.isAimedAt(npc.getAttackTarget())
				&& npc.getVehicle().get().firingHelper.isReadyToFire();
	}

	@Override
	@SuppressWarnings("squid:S3655")
	public void updateTask() {
		if (actionTick <= 0) {
			//noinspection ConstantConditions
			VehicleBase vehicle = npc.getVehicle().get();
			vehicle.firingHelper.handleFireUpdate();
			actionTick = 20;
		} else {
			actionTick--;
		}
	}
}
