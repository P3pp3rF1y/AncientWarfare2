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
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.shadowmage.ancientwarfare.vehicle.entity.IVehicleType;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.entity.types.VehicleType;
import net.shadowmage.ancientwarfare.vehicle.registry.RenderRegistry;
import org.lwjgl.opengl.GL11;
import shadowmage.ancient_warfare.client.model.ModelVehicleBase;
import shadowmage.ancient_warfare.common.config.Settings;

import java.text.DecimalFormat;

public class RenderVehicleHelper extends Render implements IItemRenderer {

	private RenderVehicleHelper() {
	}

	private static RenderVehicleHelper INSTANCE;
	private static Minecraft mc = Minecraft.getMinecraft();

	public static RenderVehicleHelper instance() {
		if (INSTANCE == null) {
			INSTANCE = new RenderVehicleHelper();
		}
		return INSTANCE;
	}

	@Override
	public void doRender(Entity var1, double x, double y, double z, float yaw, float tick) {
		boolean useAlpha = false;
		if (!Settings.renderVehiclesInFirstPerson && var1.getControllingPassenger() == mc.thePlayer && mc.gameSettings.thirdPersonView == 0) {
			useAlpha = true;
			GL11.glColor4f(1.f, 1.f, 1.f, 0.2f);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		}
		VehicleBase vehicle = (VehicleBase) var1;
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		GL11.glRotatef(yaw, 0, 1, 0);
		GL11.glScalef(-1, -1, 1);
		if (vehicle.hitAnimationTicks > 0) {
			float percent = ((float) vehicle.hitAnimationTicks / 20.f);
			GL11.glColor4f(1.f, 1.f - percent, 1.f - percent, 1.f);
		}
		AWTextureManager.bindTexture(vehicle.getTexture());
		RenderVehicleBase render = RenderRegistry.instance().getRenderForVehicle(vehicle.vehicleType.getGlobalVehicleType());
		render.renderVehicle(vehicle, x, y, z, yaw, tick);
		AWRenderHelper.instance().setTeamRenderColor(vehicle.teamNum);
		render.renderVehicleFlag();
		GL11.glColor4f(1.f, 1.f, 1.f, 1.f);
		GL11.glPopMatrix();
		if (useAlpha) {
			GL11.glDisable(GL11.GL_BLEND);
		}
		/**
		 * dont' render nameplate for the vehicle that thePlayer is on
		 */
		if (Settings.getRenderVehicleNameplates() && vehicle.getControllingPassenger() != mc.thePlayer) {
			renderNamePlate(vehicle, x, y, z, yaw, tick);
		}
	}

	private DecimalFormat formatter1d = new DecimalFormat("#.#");

	private void renderNamePlate(VehicleBase vehicle, double par3, double par5, double par7, float yaw, float tick) {
		double var10 = vehicle.getDistanceSqToEntity(this.renderManager.livingPlayer);
		int par9 = 64;
		String par2Str = vehicle.vehicleType.getLocalizedName() + " " + formatter1d.format(vehicle.getHealth()) + "/" + formatter1d.format(vehicle.baseHealth);
		if (var10 <= (double) (par9 * par9)) {
			FontRenderer var12 = this.getFontRendererFromRenderManager();
			float var13 = 1.6F;
			float var14 = 0.016666668F * var13;
			float namePlateHeight = vehicle.height + 0.75f;
			GL11.glPushMatrix();
			GL11.glTranslatef((float) par3 + 0.0F, (float) par5 + namePlateHeight, (float) par7);
			GL11.glNormal3f(0.0F, 1.0F, 0.0F);
			GL11.glRotatef(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
			GL11.glScalef(-var14, -var14, var14);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDepthMask(false);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			Tessellator var15 = Tessellator.instance;
			byte var16 = 0;
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			var15.startDrawingQuads();
			int var17 = var12.getStringWidth(par2Str) / 2;
			var15.setColorRGBA_F(0.0F, 0.0F, 0.0F, 0.25F);
			var15.addVertex((double) (-var17 - 1), (double) (-1 + var16), 0.0D);
			var15.addVertex((double) (-var17 - 1), (double) (8 + var16), 0.0D);
			var15.addVertex((double) (var17 + 1), (double) (8 + var16), 0.0D);
			var15.addVertex((double) (var17 + 1), (double) (-1 + var16), 0.0D);
			var15.draw();
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			var12.drawString(par2Str, -var12.getStringWidth(par2Str) / 2, var16, 553648127);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glDepthMask(true);
			var12.drawString(par2Str, -var12.getStringWidth(par2Str) / 2, var16, -1);
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glPopMatrix();
		}
	}

	public static void renderVehicleModel(int typeNum, int level) {
		IVehicleType type = VehicleType.getVehicleType(typeNum);
		ModelVehicleBase model = RenderRegistry.instance().getVehicleModel(typeNum);
		if (type != null && model != null) {
			GL11.glPushMatrix();
			GL11.glScalef(-1, -1, 1);
			AWTextureManager.bindTexture(type.getTextureForMaterialLevel(level));
			model.render(null, 0, 0, 0, 0, 0, 0.0625f);
			model.renderFlag();
			GL11.glPopMatrix();
		}
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return Settings.useVehicleInventoryModels;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return Settings.useVehicleInventoryModels;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		GL11.glPushMatrix();
		if (type == ItemRenderType.EQUIPPED || type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
			GL11.glTranslatef(0.5f, 1.0f, 0.5f);
		} else if (type == ItemRenderType.ENTITY) {
			GL11.glTranslatef(0.f, .5f, 0.f);
		}
		GL11.glScalef(0.35f, 0.35f, 0.35f);
		GL11.glTranslatef(0, -1.f, 0);
		if (type != ItemRenderType.EQUIPPED) {
			GL11.glRotatef(180.f, 0, 1, 0);
		}
		renderVehicleModel(item.getItemDamage(), ItemVehicleSpawner.getVehicleLevelForStack(item));
		GL11.glPopMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return AWTextureManager.getResource(((VehicleBase) entity).getTexture());
	}

}
