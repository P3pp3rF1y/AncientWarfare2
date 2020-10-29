package net.shadowmage.ancientwarfare.npc.ai.vehicle;

import net.minecraft.entity.Entity;
import net.shadowmage.ancientwarfare.npc.ai.NpcAI;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.entity.vehicle.IVehicleUser;

public class NpcAIMountVehicle<T extends NpcBase & IVehicleUser> extends NpcAI<T> {
	private static final double MOUNT_REACH = 1.0D;

	public NpcAIMountVehicle(T npc) {
		super(npc);
	}

	@Override
	public boolean shouldExecute() {
		return super.shouldExecute() && !npc.isRiding() && npc.canContinueRidingVehicle() && !npc.getVehicle().map(Entity::isBeingRidden).orElse(false);
	}

	@Override
	public void updateTask() {
		npc.getVehicle().ifPresent(vehicle -> {
			double distance = npc.getDistanceSq(vehicle.getPosition());

			if (npc.getEntityBoundingBox().grow(MOUNT_REACH).intersects(vehicle.getEntityBoundingBox())) {
				npc.startRiding(vehicle);
			} else {
				moveToPosition(vehicle.getPosition(), distance);
				npc.addAITask(TASK_MOVE);
			}
		});
	}

	@Override
	public void resetTask() {
		super.resetTask();
		npc.resetVehicle();
		npc.removeAITask(TASK_MOVE);
	}
}
