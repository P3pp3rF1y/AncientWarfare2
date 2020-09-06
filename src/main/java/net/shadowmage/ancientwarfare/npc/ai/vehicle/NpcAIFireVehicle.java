package net.shadowmage.ancientwarfare.npc.ai.vehicle;

import net.minecraft.util.math.Vec3d;
import net.shadowmage.ancientwarfare.npc.ai.NpcAI;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.entity.vehicle.IVehicleUser;

public class NpcAIFireVehicle<T extends NpcBase & IVehicleUser> extends NpcAI<T> {
	private int actionTick = 0;

	public NpcAIFireVehicle(T npc) {
		super(npc);
	}

	@Override
	public boolean shouldExecute() {
		return super.shouldExecute() && npc.getTarget().isPresent() && npc.canContinueRidingVehicle() && npc.isRidingVehicle() && isInRange() && canFire();
	}

	private boolean canFire() {
		return npc.getVehicle().map(v -> npc.getTarget().map(t -> v.firingHelper.isAimedAt(t)
				&& v.firingHelper.isReadyToFire()).orElse(false)).orElse(false);
	}

	private boolean isInRange() {
		return npc.getVehicle().map(v -> npc.getTarget().map(t -> v.getEffectiveRange((float) (t.getY() - v.posY)) >= v.getMissileOffset().add(v.getPositionVector())
				.distanceTo(new Vec3d(t.getX(), t.getY(), t.getZ()))).orElse(false)).orElse(false);
	}

	@Override
	public void updateTask() {
		if (actionTick <= 0) {
			npc.getVehicle().ifPresent(v -> v.firingHelper.handleFireUpdate());
			actionTick = 20;
		} else {
			actionTick--;
		}
	}
}
