package net.shadowmage.ancientwarfare.vehicle.proxy;

import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.vehicle.gui.GuiIds;
import net.shadowmage.ancientwarfare.vehicle.gui.GuiVehicleAmmoSelection;
import net.shadowmage.ancientwarfare.vehicle.gui.GuiVehicleInventory;
import net.shadowmage.ancientwarfare.vehicle.gui.GuiVehicleStats;
import net.shadowmage.ancientwarfare.vehicle.input.VehicleInputHandler;

public class ClientProxy extends CommonProxy {

	@Override
	public void preInit() {
		NetworkHandler.registerGui(GuiIds.AMMO_SELECTION, GuiVehicleAmmoSelection.class);
		NetworkHandler.registerGui(GuiIds.VEHICLE_INVENTORY, GuiVehicleInventory.class);
		NetworkHandler.registerGui(GuiIds.VEHICLE_STATS, GuiVehicleStats.class);
	}

	@Override
	public void init() {
		VehicleInputHandler.initKeyBindings();
	}
}
