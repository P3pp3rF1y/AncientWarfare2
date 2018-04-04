package net.shadowmage.ancientwarfare.vehicle.input;

import net.minecraft.client.Minecraft;
import net.shadowmage.ancientwarfare.core.input.InputHandler;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;

import java.util.function.Consumer;

public class VehicleCallback implements InputHandler.IInputCallback {
	private final Consumer<VehicleBase> callback;

	public VehicleCallback(Consumer<VehicleBase> callback) {
		this.callback = callback;
	}

	@Override
	public void onKeyPressed() {
		Minecraft mc = Minecraft.getMinecraft();
		VehicleBase vehicle = (VehicleBase) mc.player.getRidingEntity();
		callback.accept(vehicle);
	}
}
