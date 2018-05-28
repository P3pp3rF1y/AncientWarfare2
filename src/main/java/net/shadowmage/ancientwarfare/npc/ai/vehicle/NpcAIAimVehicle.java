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

		//check to see if yaw to target is within the range reachable by just turret rotation

		//noinspection ConstantConditions
		float yaw = Trig.getYawTowardsTarget(vehicle.posX, vehicle.posZ, target.posX, target.posZ, vehicle.rotationYaw);

		if (turnTurret(vehicle, target, yaw)) {
			return;
		}
		vehicle.moveHelper.setStrafeInput((byte) 0);
		vehicle.moveHelper.setForwardInput((byte) 0);
		vehicle.firingHelper.handleSoldierTargetInput(target.posX, target.posY, target.posZ);

		if (yaw <= 2 && vehicle.vehicleType.getBaseTurretRotationAmount() <= 0) {
			vehicle.rotationYaw = vehicle.rotationYaw + yaw;
			vehicle.moveHelper.stopMotion();
		}
	}

	private boolean turnTurret(VehicleBase vehicle, EntityLivingBase target, float yaw) {
		if (vehicle.vehicleType.getBaseTurretRotationAmount() < 180 || Math
				.abs(yaw) > 120)//if turret cannot rotate fully around, or if it can but yaw diff is great, turn towards target
		{
			if (!Trig.isAngleBetween(vehicle.rotationYaw + yaw, vehicle.localTurretRotationHome - vehicle.currentTurretRotationMax - 1.5f,
					vehicle.localTurretRotationHome + vehicle.currentTurretRotationMax + 1.5f))//expand the bounds a bit
			{
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
