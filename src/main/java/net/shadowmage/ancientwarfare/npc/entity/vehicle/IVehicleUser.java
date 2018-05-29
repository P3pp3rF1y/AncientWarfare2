package net.shadowmage.ancientwarfare.npc.entity.vehicle;

import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;

import java.util.Optional;

public interface IVehicleUser {
	Optional<VehicleBase> getVehicle();

	void setVehicle(VehicleBase vehicle);

	void resetVehicle();

	boolean isRidingVehicle();

	boolean canContinueRidingVehicle();
}
