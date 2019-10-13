package net.shadowmage.ancientwarfare.vehicle.render.vehicle;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.helpers.VehicleFiringVarsHelper;
import net.shadowmage.ancientwarfare.vehicle.model.ModelTrebuchetStandFixed;

public class RenderTrebuchetLarge extends RenderVehicleBase {

	ModelTrebuchetStandFixed model = new ModelTrebuchetStandFixed();

	public RenderTrebuchetLarge(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void renderVehicle(VehicleBase vehicle, double x, double y, double z, float yaw, float tick) {
		GlStateManager.enableRescaleNormal();
		GlStateManager.scale(2.5f, 2.5f, 2.5f);
		VehicleFiringVarsHelper var = vehicle.firingVarsHelper;
		model.setArmRotations(var.getVar1() + tick * var.getVar2(), var.getVar3() + tick * var.getVar4());
		model.render(vehicle, 0, 0, 0, 0, 0, 0.0625f);
		GlStateManager.disableRescaleNormal();
	}

}
