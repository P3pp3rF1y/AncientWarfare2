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
	public void startExecuting() {
		npc.dismountRidingEntity();
		npc.resetVehicle();
	}
}
