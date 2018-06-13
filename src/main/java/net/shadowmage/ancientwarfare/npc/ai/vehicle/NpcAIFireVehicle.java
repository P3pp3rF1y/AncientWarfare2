package net.shadowmage.ancientwarfare.npc.ai.vehicle;

import net.minecraft.util.math.Vec3d;
import net.shadowmage.ancientwarfare.npc.ai.NpcAI;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.entity.vehicle.ITarget;
import net.shadowmage.ancientwarfare.npc.entity.vehicle.IVehicleUser;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;

public class NpcAIFireVehicle<T extends NpcBase & IVehicleUser> extends NpcAI<T> {
	private int actionTick = 0;

	public NpcAIFireVehicle(T npc) {
		super(npc);
	}

	@Override
	public boolean shouldExecute() {
		return npc.getTarget().isPresent() && npc.canContinueRidingVehicle() && npc.isRidingVehicle() && isInRange() && canFire();
	}

	@SuppressWarnings("squid:S3655")
	private boolean canFire() {
		//noinspection ConstantConditions
		return npc.getVehicle().get().firingHelper.isAimedAt(npc.getTarget().get())
				&& npc.getVehicle().get().firingHelper.isReadyToFire();
	}

	@SuppressWarnings("squid:S3655")
	private boolean isInRange() {
		//noinspection ConstantConditions
		VehicleBase vehicle = npc.getVehicle().get();
		//noinspection ConstantConditions
		ITarget target = npc.getTarget().get();
		return vehicle.getEffectiveRange((float) (target.getY() - vehicle.posY)) >= vehicle.getMissileOffset().add(vehicle.getPositionVector())
				.distanceTo(new Vec3d(target.getX(), target.getY(), target.getZ()));
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
