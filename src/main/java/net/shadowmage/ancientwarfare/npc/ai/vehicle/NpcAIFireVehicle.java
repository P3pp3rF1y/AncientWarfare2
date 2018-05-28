package net.shadowmage.ancientwarfare.npc.ai.vehicle;

import net.shadowmage.ancientwarfare.npc.ai.NpcAI;
import net.shadowmage.ancientwarfare.npc.entity.vehicle.NpcSiegeEngineer;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;

public class NpcAIFireVehicle extends NpcAI<NpcSiegeEngineer> {
	private int actionTick = 0;

	public NpcAIFireVehicle(NpcSiegeEngineer npc) {
		super(npc);
	}

	@Override
	@SuppressWarnings("squid:S3655")
	public boolean shouldExecute() {
		//noinspection ConstantConditions
		return npc.getAttackTarget() != null && npc.isRidingVehicle() && npc.getVehicle().get().firingHelper.isAimedAt(npc.getAttackTarget())
				&& npc.getVehicle().get().firingHelper.isReadyToFire();
	}

	@Override
	@SuppressWarnings("squid:S3655")
	public void updateTask() {
		if (actionTick <= 0) {
			//noinspection ConstantConditions
			VehicleBase vehicle = npc.getVehicle().get();
			vehicle.firingHelper.handleFireUpdate();
			actionTick = (vehicle.currentReloadTicks + 20);
		} else {
			actionTick--;
		}
	}
}
