package net.shadowmage.ancientwarfare.vehicle.network;

import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;

public class PacketFireUpdate extends PacketVehicleBase {
	public PacketFireUpdate() {}

	public PacketFireUpdate(VehicleBase vehicle) {
		super(vehicle);
	}

	@Override
	public void execute() {
		vehicle.firingHelper.handleFireUpdate();
	}
}
