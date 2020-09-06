package net.shadowmage.ancientwarfare.vehicle.render.vehicle;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;

import javax.annotation.Nullable;

public abstract class RenderVehicleBase extends Render<VehicleBase> {

	protected RenderVehicleBase(RenderManager renderManager) {
		super(renderManager);
	}

	@Nullable
	@Override
	protected ResourceLocation getEntityTexture(VehicleBase entity) {
		return entity.getTexture();
	}

	public abstract void renderVehicle(VehicleBase entity, double x, double y, double z, float entityYaw, float partialTicks);
}
