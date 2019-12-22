package net.shadowmage.ancientwarfare.vehicle.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.vehicle.config.AWVehicleStatics;
import net.shadowmage.ancientwarfare.vehicle.entity.IVehicleType;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.registry.VehicleRegistry;
import net.shadowmage.ancientwarfare.vehicle.render.vehicle.RenderBallistaMobile;
import net.shadowmage.ancientwarfare.vehicle.render.vehicle.RenderBallistaStand;
import net.shadowmage.ancientwarfare.vehicle.render.vehicle.RenderBatteringRam;
import net.shadowmage.ancientwarfare.vehicle.render.vehicle.RenderBoatBallista;
import net.shadowmage.ancientwarfare.vehicle.render.vehicle.RenderBoatCatapult;
import net.shadowmage.ancientwarfare.vehicle.render.vehicle.RenderBoatTransport;
import net.shadowmage.ancientwarfare.vehicle.render.vehicle.RenderCannonMobileFixed;
import net.shadowmage.ancientwarfare.vehicle.render.vehicle.RenderCannonStandFixed;
import net.shadowmage.ancientwarfare.vehicle.render.vehicle.RenderCannonStandTurret;
import net.shadowmage.ancientwarfare.vehicle.render.vehicle.RenderCatapultMobileFixed;
import net.shadowmage.ancientwarfare.vehicle.render.vehicle.RenderCatapultMobileTurret;
import net.shadowmage.ancientwarfare.vehicle.render.vehicle.RenderCatapultStandFixed;
import net.shadowmage.ancientwarfare.vehicle.render.vehicle.RenderCatapultStandTurret;
import net.shadowmage.ancientwarfare.vehicle.render.vehicle.RenderChestCart;
import net.shadowmage.ancientwarfare.vehicle.render.vehicle.RenderHwacha;
import net.shadowmage.ancientwarfare.vehicle.render.vehicle.RenderTrebuchetLarge;
import net.shadowmage.ancientwarfare.vehicle.render.vehicle.RenderTrebuchetMobileFixed;
import net.shadowmage.ancientwarfare.vehicle.render.vehicle.RenderTrebuchetStandFixed;
import net.shadowmage.ancientwarfare.vehicle.render.vehicle.RenderTrebuchetStandTurret;
import net.shadowmage.ancientwarfare.vehicle.render.vehicle.RenderVehicleBase;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.HashMap;

public class RenderVehicle extends Render<VehicleBase> {

	private HashMap<IVehicleType, RenderVehicleBase> vehicleRenders = new HashMap<>();

	public RenderVehicle(RenderManager renderManager) {
		super(renderManager);

		vehicleRenders.put(VehicleRegistry.CATAPULT_STAND_FIXED, new RenderCatapultStandFixed(renderManager));
		vehicleRenders.put(VehicleRegistry.CATAPULT_STAND_TURRET, new RenderCatapultStandTurret(renderManager));
		vehicleRenders.put(VehicleRegistry.CATAPULT_MOBILE_FIXED, new RenderCatapultMobileFixed(renderManager));
		vehicleRenders.put(VehicleRegistry.CATAPULT_MOBILE_TURRET, new RenderCatapultMobileTurret(renderManager));
		vehicleRenders.put(VehicleRegistry.BALLISTA_STAND_FIXED, new RenderBallistaStand(renderManager));
		vehicleRenders.put(VehicleRegistry.BALLISTA_STAND_TURRET, new RenderBallistaStand(renderManager));
		vehicleRenders.put(VehicleRegistry.BALLISTA_MOBILE_FIXED, new RenderBallistaMobile(renderManager));
		vehicleRenders.put(VehicleRegistry.BALLISTA_MOBILE_TURRET, new RenderBallistaMobile(renderManager));
		vehicleRenders.put(VehicleRegistry.BATTERING_RAM, new RenderBatteringRam(renderManager));
		vehicleRenders.put(VehicleRegistry.CANNON_STAND_FIXED, new RenderCannonStandFixed(renderManager));
		vehicleRenders.put(VehicleRegistry.CANNON_STAND_TURRET, new RenderCannonStandTurret(renderManager));
		vehicleRenders.put(VehicleRegistry.CANNON_MOBILE_FIXED, new RenderCannonMobileFixed(renderManager));
		vehicleRenders.put(VehicleRegistry.HWACHA, new RenderHwacha(renderManager));
		vehicleRenders.put(VehicleRegistry.TREBUCHET_STAND_FIXED, new RenderTrebuchetStandFixed(renderManager));
		vehicleRenders.put(VehicleRegistry.TREBUCHET_STAND_TURRET, new RenderTrebuchetStandTurret(renderManager));
		vehicleRenders.put(VehicleRegistry.TREBUCHET_MOBILE_FIXED, new RenderTrebuchetMobileFixed(renderManager));
		vehicleRenders.put(VehicleRegistry.TREBUCHET_LARGE, new RenderTrebuchetLarge(renderManager));
		vehicleRenders.put(VehicleRegistry.CHEST_CART, new RenderChestCart(renderManager));
		vehicleRenders.put(VehicleRegistry.BOAT_BALLISTA, new RenderBoatBallista(renderManager));
		vehicleRenders.put(VehicleRegistry.BOAT_CATAPULT, new RenderBoatCatapult(renderManager));
		vehicleRenders.put(VehicleRegistry.BOAT_TRANSPORT, new RenderBoatTransport(renderManager));
	}

	@Override
	public void doRender(VehicleBase vehicle, double x, double y, double z, float renderYaw, float partialTicks) {
		boolean useAlpha = false;
		if (!AWVehicleStatics.renderVehiclesInFirstPerson && vehicle.getControllingPassenger() == Minecraft.getMinecraft().player && Minecraft.getMinecraft().gameSettings.thirdPersonView == 0) {
			useAlpha = true;
			GlStateManager.color(1.f, 1.f, 1.f, 0.2f);
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		}
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.rotate(renderYaw, 0, 1, 0);
		GlStateManager.scale(-1, -1, 1);
		if (vehicle.hitAnimationTicks > 0) {
			float percent = ((float) vehicle.hitAnimationTicks / 20.f);
			GlStateManager.color(1.f, 1.f - percent, 1.f - percent, 1.f);
		}
		bindTexture(vehicle.getTexture());
		RenderVehicleBase render = vehicleRenders.get(vehicle.vehicleType);
		render.renderVehicle(vehicle, x, y, z, renderYaw, partialTicks);
		GlStateManager.color(1.f, 1.f, 1.f, 1.f);
		GlStateManager.popMatrix();
		if (useAlpha) {
			GlStateManager.disableBlend();
		}

		// dont' render nameplate for the vehicle that thePlayer is on
		if (isInWorld(vehicle) && AWVehicleStatics.renderVehicleNameplates && vehicle.getControllingPassenger() != Minecraft.getMinecraft().player) {
			renderNamePlate(vehicle, x, y, z);
		}

	}

	private boolean isInWorld(VehicleBase vehicle) {
		return vehicle.posY > 0;
	}

	private DecimalFormat formatter1d = new DecimalFormat("#.#");

	private void renderNamePlate(VehicleBase vehicle, double x, double y, double z) {
		double var10 = vehicle.getDistanceSq(this.renderManager.renderViewEntity);
		int par9 = 64;
		String par2Str = vehicle.vehicleType.getLocalizedName() + " " + formatter1d.format(vehicle.getHealth()) + "/" + formatter1d.format(vehicle.baseHealth);
		if (var10 <= (double) (par9 * par9)) {
			FontRenderer var12 = this.getFontRendererFromRenderManager();
			float var13 = 1.6F;
			float var14 = 0.016666668F * var13;
			float namePlateHeight = vehicle.height + 0.75f;
			GlStateManager.pushMatrix();
			GlStateManager.translate((float) x + 0.0F, (float) y + namePlateHeight, (float) z);
			GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
			GlStateManager.rotate(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate(this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
			GlStateManager.scale(-var14, -var14, var14);
			GlStateManager.disableLighting();
			GlStateManager.depthMask(false);
			GlStateManager.disableDepth();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			Tessellator tesselator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tesselator.getBuffer();
			byte var16 = 0;
			GlStateManager.disableTexture2D();
			bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
			int var17 = var12.getStringWidth(par2Str) / 2;
			bufferBuilder.pos((double) (-var17 - 1), (double) (-1 + var16), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
			bufferBuilder.pos((double) (-var17 - 1), (double) (8 + var16), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
			bufferBuilder.pos((double) (var17 + 1), (double) (8 + var16), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
			bufferBuilder.pos((double) (var17 + 1), (double) (-1 + var16), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
			tesselator.draw();
			GlStateManager.enableTexture2D();
			var12.drawString(par2Str, -var12.getStringWidth(par2Str) / 2, var16, 553648127);
			GlStateManager.enableDepth();
			GlStateManager.depthMask(true);
			var12.drawString(par2Str, -var12.getStringWidth(par2Str) / 2, var16, -1);
			GlStateManager.enableLighting();
			GlStateManager.disableBlend();
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

			GlStateManager.popMatrix();
		}
	}

	@Nullable
	@Override
	protected ResourceLocation getEntityTexture(VehicleBase entity) {
		return entity.getTexture();
	}

}
