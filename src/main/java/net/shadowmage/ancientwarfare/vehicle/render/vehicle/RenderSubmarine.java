package net.shadowmage.ancientwarfare.vehicle.render.vehicle;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.model.ModelSubmarine;
import org.lwjgl.opengl.GL11;

public class RenderSubmarine extends RenderVehicleBase {

	ModelSubmarine model = new ModelSubmarine();

	public RenderSubmarine(RenderManager renderManager) {
		super(renderManager);
	}

	/**
	 *
	 */
	@Override
	public void renderVehicle(VehicleBase veh, double x, double y, double z, float yaw, float tick) {
		float wheelAngle = veh.wheelRotation + (tick * (veh.wheelRotation - veh.wheelRotationPrev));
		model.setWheelRotations(wheelAngle, wheelAngle, wheelAngle, wheelAngle);
		model.render(veh, 0, 0, 0, 0, 0, 0.0625f);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.depthMask(false);
		//TODO fix to bind correct texture here
		//bindTexture(Config.texturePath + "models/submarine_screen.png");
		model.render(veh, 0, 0, 0, 0, 0, 0.0625f);
		GlStateManager.depthMask(true);
		GlStateManager.disableBlend();
		bindTexture(veh.getTexture());
	}

}
