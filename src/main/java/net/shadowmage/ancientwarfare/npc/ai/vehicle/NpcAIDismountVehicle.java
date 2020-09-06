package net.shadowmage.ancientwarfare.npc.ai.vehicle;

import net.shadowmage.ancientwarfare.npc.ai.NpcAI;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.entity.vehicle.IVehicleUser;

public class NpcAIDismountVehicle<T extends NpcBase & IVehicleUser> extends NpcAI<T> {
	public NpcAIDismountVehicle(T npc) {
		super(npc);
	}

	@Override
	public boolean shouldExecute() {
		return super.shouldExecute() && npc.isRidingVehicle() && !npc.canContinueRidingVehicle();
	}

	@Override
	public void startExecuting() {
		npc.dismountRidingEntity();
		npc.getVehicle().ifPresent(v -> v.moveHelper.stopMotion()); //stop motion in case it started moving based on some other AI task (like AIM one)
		npc.resetVehicle();
	}
}
