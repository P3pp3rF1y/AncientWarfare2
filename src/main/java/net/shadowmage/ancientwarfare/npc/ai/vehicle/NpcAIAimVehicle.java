package net.shadowmage.ancientwarfare.npc.ai.vehicle;

import net.shadowmage.ancientwarfare.core.util.Trig;
import net.shadowmage.ancientwarfare.npc.ai.NpcAI;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.entity.vehicle.ITarget;
import net.shadowmage.ancientwarfare.npc.entity.vehicle.IVehicleUser;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;

public class NpcAIAimVehicle<T extends NpcBase & IVehicleUser> extends NpcAI<T> {
	public NpcAIAimVehicle(T npc) {
		super(npc);
	}

	@Override
	@SuppressWarnings("squid:S3655")
	public boolean shouldExecute() {
		//noinspection ConstantConditions
		return npc.getTarget().isPresent() && npc.canContinueRidingVehicle() && npc.isRidingVehicle() && !npc.getVehicle().get().firingHelper
				.isAimedAt(npc.getTarget().get());
	}

	@Override
	@SuppressWarnings("squid:S3655")
	public void updateTask() {
		//noinspection ConstantConditions
		VehicleBase vehicle = npc.getVehicle().get();

		//noinspection ConstantConditions
		ITarget target = npc.getTarget().get();

		//noinspection ConstantConditions
		if (turnVehicleIfYawDifferenceGreat(vehicle, target)) {
			return;
		}
		vehicle.moveHelper.setStrafeInput((byte) 0);
		vehicle.moveHelper.setForwardInput((byte) 0);
		vehicle.firingHelper.handleSoldierTargetInput(target.getX(), target.getY(), target.getZ());
	}

	@SuppressWarnings("squid:S1066")
	private boolean turnVehicleIfYawDifferenceGreat(VehicleBase vehicle, ITarget target) {
		float yawDiff = Trig.getAngleDiffSigned(vehicle.rotationYaw, vehicle.firingHelper.getAimYaw(target));

		if (!vehicle.vehicleType.canAdjustYaw() && Math.abs(yawDiff) < 2) {
			//if there's a minor difference in the rotation just set the rotation to it instead of continues steps to one side and back
			vehicle.rotationYaw += yawDiff;
			vehicle.moveHelper.stopMotion();
		} else if (vehicle.vehicleType.getBaseTurretRotationAmount() < 180 || Math.abs(yawDiff) > 120) {
			//if turret cannot rotate fully around, or if it can but yaw diff is great, turn towards target
			if (!Trig.isAngleBetween(vehicle.rotationYaw + yawDiff, vehicle.localTurretRotationHome - getMaxRotDifference(vehicle),
					vehicle.localTurretRotationHome + getMaxRotDifference(vehicle))) {
				if (yawDiff < 0) {
					vehicle.moveHelper.setStrafeInput((byte) 1); //left
				} else {
					vehicle.moveHelper.setStrafeInput((byte) -1); //right
				}
				vehicle.moveHelper.setForwardInput((byte) 0);
				vehicle.firingHelper.handleSoldierTargetInput(target.getX(), target.getY(), target.getZ());
				return true;
			}
		}
		return false;
	}

	private float getMaxRotDifference(VehicleBase vehicle) {
		return Math.min(vehicle.currentTurretRotationMax + 1.5f, 180f);
	}

	@Override
	public void resetTask() {
		npc.getVehicle().ifPresent(v -> v.moveHelper.setStrafeInput((byte) 0));
	}
}
