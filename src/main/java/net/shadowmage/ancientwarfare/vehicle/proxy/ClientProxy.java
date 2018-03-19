package net.shadowmage.ancientwarfare.vehicle.proxy;

import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.gui.GuiVehicleAmmoSelection;
import net.shadowmage.ancientwarfare.vehicle.gui.GuiVehicleInventory;
import net.shadowmage.ancientwarfare.vehicle.gui.GuiVehicleStats;
import net.shadowmage.ancientwarfare.vehicle.input.VehicleInputHandler;
import net.shadowmage.ancientwarfare.vehicle.missiles.MissileBase;
import net.shadowmage.ancientwarfare.vehicle.render.RenderMissile;
import net.shadowmage.ancientwarfare.vehicle.render.RenderVehicle;

public class ClientProxy extends CommonProxy {

	@Override
	public void preInit() {
		NetworkHandler.registerGui(NetworkHandler.GUI_VEHICLE_AMMO_SELECTION, GuiVehicleAmmoSelection.class);
		NetworkHandler.registerGui(NetworkHandler.GUI_VEHICLE_INVENTORY, GuiVehicleInventory.class);
		NetworkHandler.registerGui(NetworkHandler.GUI_VEHICLE_STATS, GuiVehicleStats.class);

		RenderingRegistry.registerEntityRenderingHandler(MissileBase.class, RenderMissile::new);
		RenderingRegistry.registerEntityRenderingHandler(VehicleBase.class, RenderVehicle::new);
	}

	@Override
	public void init() {
		VehicleInputHandler.initKeyBindings();
	}
}
