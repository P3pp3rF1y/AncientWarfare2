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
		return npc.isRidingVehicle() && !npc.canContinueRidingVehicle();
	}

	@Override
	@SuppressWarnings("squid:S3655")
	public void startExecuting() {
		npc.dismountRidingEntity();
		//noinspection ConstantConditions
		npc.getVehicle().get().moveHelper.stopMotion(); //stop motion in case it started moving based on some other AI task (like AIM one)
		npc.resetVehicle();
	}
}
