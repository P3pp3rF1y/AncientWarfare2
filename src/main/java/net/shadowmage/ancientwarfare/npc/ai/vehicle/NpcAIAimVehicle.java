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
	public boolean shouldExecute() {
		return npc.getAttackTarget() != null && npc.getVehicle().isPresent() && !npc.getVehicle().get().firingHelper.isAimedAt(npc.getAttackTarget());
	}

	@Override
	public boolean shouldContinueExecuting() {
		return super.shouldContinueExecuting();
	}

	@Override
	public void updateTask() {
		//noinspection ConstantConditions
		VehicleBase vehicle = npc.getVehicle().get();

		EntityLivingBase target = npc.getAttackTarget();

		if (turnVehicleIfYawDifferenceGreat(vehicle, target)) {
			return;
		}
		vehicle.moveHelper.setStrafeInput((byte) 0);
		vehicle.moveHelper.setForwardInput((byte) 0);
		vehicle.firingHelper.handleSoldierTargetInput(target.posX, target.posY, target.posZ);
	}

	private boolean turnVehicleIfYawDifferenceGreat(VehicleBase vehicle, EntityLivingBase target) {
		float yaw = vehicle.firingHelper.getAimYaw(target);

		//if turret cannot rotate fully around, or if it can but yaw diff is great, turn towards target
		if (vehicle.vehicleType.getBaseTurretRotationAmount() < 180 || Trig.getAngleDiff(vehicle.localTurretRotation, yaw) > 120) {
			if (!Trig.isAngleBetween(vehicle.rotationYaw + yaw, vehicle.localTurretRotationHome - vehicle.currentTurretRotationMax - 1.5f,
					vehicle.localTurretRotationHome + vehicle.currentTurretRotationMax + 1.5f)) {
				if (yaw < 0) {
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
}
