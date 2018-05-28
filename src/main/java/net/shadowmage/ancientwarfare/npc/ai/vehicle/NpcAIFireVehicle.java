package net.shadowmage.ancientwarfare.npc.ai.vehicle;

import net.shadowmage.ancientwarfare.npc.ai.NpcAI;
import net.shadowmage.ancientwarfare.npc.entity.vehicle.NpcSiegeEngineer;

public class NpcAIFireVehicle extends NpcAI<NpcSiegeEngineer> {
	private int actionTick = 0;

	public NpcAIFireVehicle(NpcSiegeEngineer npc) {
		super(npc);
	}

	@Override
	@SuppressWarnings("squid:S3655")
	public boolean shouldExecute() {
		//noinspection ConstantConditions
		return npc.getAttackTarget() != null && npc.isRidingVehicle() && npc.getVehicle().get().firingHelper.isAimedAt(npc.getAttackTarget()); // figure out a call that would tell if vehicle is aimed at target
	}

	@Override
	public void updateTask() {
/*
		if (vehicle.firingHelper.isAtTarget()) {
			if (actionTick <= 0) {
				vehicle.firingHelper.handleFireUpdate();
				this.actionTick = (vehicle.currentReloadTicks + 20);
				vehicle.moveHelper.setForwardInput((byte) 0);
				vehicle.moveHelper.setStrafeInput((byte) 0);
			}
		} else if (vehicle.firingHelper.isNearTarget()) { //TODO is this really needed ? Or is this the only thing that's needed to aim?
			if (actionTick <= 0) {
				vehicle.localTurretPitch = vehicle.localTurretDestPitch;
				vehicle.localTurretRotation = vehicle.localTurretDestRot;
				vehicle.sendCompleteTurretPacket();
				vehicle.firingHelper.handleFireUpdate();
				actionTick = (vehicle.currentReloadTicks + 20);
				vehicle.moveHelper.setForwardInput((byte) 0);
				vehicle.moveHelper.setStrafeInput((byte) 0);
			}
		} else//delay a bit to line up to target
		{
			actionTick = 1;
		}
*/
	}
}
