package net.shadowmage.ancientwarfare.npc.ai.vehicle;

import net.minecraft.entity.EntityLivingBase;
import net.shadowmage.ancientwarfare.core.util.Trig;
import net.shadowmage.ancientwarfare.npc.ai.NpcAI;
import net.shadowmage.ancientwarfare.npc.entity.vehicle.NpcSiegeEngineer;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;

public class NpcAIAimVehicle extends NpcAI<NpcSiegeEngineer> {
	public NpcAIAimVehicle(NpcSiegeEngineer npc) {
		super(npc);
	}

	@Override
	@SuppressWarnings("squid:S3655")
	public boolean shouldExecute() {
		//noinspection ConstantConditions
		return npc.getAttackTarget() != null && npc.isRidingVehicle() && !npc.getVehicle().get().firingHelper.isAimedAt(npc.getAttackTarget());
	}

	@Override
	@SuppressWarnings("squid:S3655")
	public void updateTask() {
		//noinspection ConstantConditions
		VehicleBase vehicle = npc.getVehicle().get();

		EntityLivingBase target = npc.getAttackTarget();

		//noinspection ConstantConditions
		if (turnVehicleIfYawDifferenceGreat(vehicle, target)) {
			return;
		}
		vehicle.moveHelper.setStrafeInput((byte) 0);
		vehicle.moveHelper.setForwardInput((byte) 0);
		vehicle.firingHelper.handleSoldierTargetInput(target.posX, target.posY + target.getEyeHeight(), target.posZ);
	}

	private boolean turnVehicleIfYawDifferenceGreat(VehicleBase vehicle, EntityLivingBase target) {
		float yawDiff = Trig.getAngleDiffSigned(vehicle.rotationYaw, vehicle.firingHelper.getAimYaw(target));

		if (!vehicle.vehicleType.canAdjustYaw() && yawDiff < 2) {
			//if there's a minor difference in the rotation just set the rotation to it instead of continues steps to one side and back
			vehicle.rotationYaw += yawDiff;
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
				vehicle.firingHelper.handleSoldierTargetInput(target.posX, target.posY, target.posZ);
				return true;
			}
		}
		return false;
	}

	private float getMaxRotDifference(VehicleBase vehicle) {
		return Math.min(vehicle.currentTurretRotationMax + 1.5f, 180f);
	}
}
