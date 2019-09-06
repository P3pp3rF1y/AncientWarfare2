package net.shadowmage.ancientwarfare.vehicle.network;

import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;

public class PacketPackCommand extends PacketVehicleBase {
	public PacketPackCommand() {}

	public PacketPackCommand(VehicleBase vehicle) {
		super(vehicle);
	}

	@Override
	public void execute() {
		vehicle.packVehicle();
	}
}
