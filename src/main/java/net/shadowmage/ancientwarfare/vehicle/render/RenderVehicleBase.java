/**
 * Copyright 2012 John Cummens (aka Shadowmage, Shadowmage4513)
 * This software is distributed under the terms of the GNU General Public License.
 * Please see COPYING for precise license information.
 * <p>
 * This file is part of Ancient Warfare.
 * <p>
 * Ancient Warfare is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * Ancient Warfare is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with Ancient Warfare.  If not, see <http://www.gnu.org/licenses/>.
 */

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
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.text.DecimalFormat;

public abstract class RenderVehicleBase extends Render<VehicleBase> {

	protected RenderVehicleBase(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(VehicleBase vehicle, double x, double y, double z, float renderYaw, float partialTicks) {
		boolean useAlpha = false;
		if (!AWVehicleStatics.renderVehiclesInFirstPerson && vehicle.getControllingPassenger() == Minecraft.getMinecraft().player && Minecraft
				.getMinecraft().gameSettings.thirdPersonView == 0) {
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
		renderVehicle(vehicle, x, y, z, renderYaw, partialTicks);
		//TODO add code to change to team color - similar to RenderNpcBase code for it
		renderVehicleFlag();
		GlStateManager.color(1.f, 1.f, 1.f, 1.f);
		GlStateManager.popMatrix();
		if (useAlpha) {
			GlStateManager.disableBlend();
		}
		/**
		 * dont' render nameplate for the vehicle that thePlayer is on
		 */
		if (AWVehicleStatics.renderVehicleNameplates && vehicle.getControllingPassenger() != Minecraft.getMinecraft().player) {
			renderNamePlate(vehicle, x, y, z, renderYaw, partialTicks);
		}

	}

	private DecimalFormat formatter1d = new DecimalFormat("#.#");

	private void renderNamePlate(VehicleBase vehicle, double x, double y, double z, float yaw, float tick) {
		double var10 = vehicle.getDistanceSqToEntity(this.renderManager.renderViewEntity);
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

	protected abstract void renderVehicle(VehicleBase entity, double x, double y, double z, float entityYaw, float partialTicks);

	@Nullable
	@Override
	protected ResourceLocation getEntityTexture(VehicleBase entity) {
		return entity.getTexture();
	}

	public abstract void renderVehicleFlag();
}
