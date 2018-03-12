package net.shadowmage.ancientwarfare.vehicle.proxy;

import net.shadowmage.ancientwarfare.vehicle.input.VehicleInputHandler;

public class ClientProxy extends CommonProxy {
	@Override
	public void init() {
		VehicleInputHandler.initKeyBindings();
	}
}
