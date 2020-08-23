package net.shadowmage.ancientwarfare.npc.ai.vehicle;

import com.google.common.base.Predicate;
import net.shadowmage.ancientwarfare.npc.ai.NpcAI;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.entity.vehicle.IVehicleUser;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;

import java.util.Comparator;
import java.util.List;

public class NpcAIFindVehicle<T extends NpcBase & IVehicleUser> extends NpcAI<T> {
	private static final double SEARCH_DISTANCE = 30D;
	@SuppressWarnings({"Guava", "java:S4738"}) // need to use Guava Predicate because of vanilla getEntitiesWithinAABB uses it
	private static final Predicate<VehicleBase> SELECTOR = v -> v != null && v.isDrivable() && v.getPassengers().isEmpty();

	public NpcAIFindVehicle(T npc) {
		super(npc);
	}

	@Override
	public boolean shouldExecute() {
		return super.shouldExecute() && !npc.getVehicle().isPresent() && npc.canContinueRidingVehicle() && (!npc.isRiding() || npc.getRidingEntity() instanceof VehicleBase);
	}

	@Override
	public void updateTask() {
		if (npc.isRiding()) {
			//noinspection ConstantConditions
			npc.setVehicle((VehicleBase) npc.getRidingEntity());
			return;
		}

		List<VehicleBase> vehicles = npc.world.getEntitiesWithinAABB(VehicleBase.class, npc.getEntityBoundingBox().grow(SEARCH_DISTANCE), SELECTOR);
		vehicles.stream().filter(v -> !v.isBeingRidden() && v.vehicleType.canSoldiersPilot()).min(Comparator.comparing(v -> v.getDistanceSq(npc))).ifPresent(v -> npc.setVehicle(v));
	}
}
