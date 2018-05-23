package net.shadowmage.ancientwarfare.npc.ai.vehicle;

import com.google.common.base.Predicate;
import net.shadowmage.ancientwarfare.npc.ai.NpcAI;
import net.shadowmage.ancientwarfare.npc.entity.vehicle.NpcSiegeEngineer;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;

import java.util.Comparator;
import java.util.List;

public class NpcAIFindVehicle extends NpcAI<NpcSiegeEngineer> {
	private static final double SEARCH_DISTANCE = 30D;
	private Predicate<VehicleBase> selector = v -> v != null && v.isDrivable() && v.getPassengers().isEmpty();

	public NpcAIFindVehicle(NpcSiegeEngineer npc) {
		super(npc);
	}

	@Override
	public boolean shouldExecute() {
		return !npc.isRiding() && !npc.getVehicle().isPresent();
	}

	@Override
	public void updateTask() {
		List<VehicleBase> vehicles = npc.world.getEntitiesWithinAABB(VehicleBase.class, npc.getEntityBoundingBox().grow(SEARCH_DISTANCE), selector);
		vehicles.stream().filter(v -> !v.isBeingRidden()).sorted(Comparator.comparing(v -> v.getDistanceSqToEntity(npc))).findFirst().ifPresent(v -> npc.setVehicle(v));
	}
}
