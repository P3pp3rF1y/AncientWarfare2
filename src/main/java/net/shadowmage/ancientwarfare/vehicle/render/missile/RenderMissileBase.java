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

package net.shadowmage.ancientwarfare.vehicle.render.missile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.vehicle.missiles.IAmmo;
import net.shadowmage.ancientwarfare.vehicle.missiles.MissileBase;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;

public abstract class RenderMissileBase extends Render<MissileBase> {
	static Minecraft mc = Minecraft.getMinecraft();

	protected RenderMissileBase(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(MissileBase missile, double x, double y, double z, float entityYaw, float partialTicks) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.rotate(entityYaw - 90, 0, 1, 0);
		GlStateManager.rotate(missile.rotationPitch - 90, 1, 0, 0);
		GL11.glScaled(-1, -1, 1);
		float scale = missile.ammoType.getRenderScale();
		GlStateManager.scale(scale, scale, scale);

		bindTexture(missile.getTexture());
		renderMissile(missile, missile.ammoType, x, y, z, entityYaw, partialTicks);
		GlStateManager.popMatrix();
	}

	public abstract void renderMissile(MissileBase missile, IAmmo ammo, double x, double y, double z, float yaw, float tick);

	@Nullable
	@Override
	protected ResourceLocation getEntityTexture(MissileBase entity) {
		return entity.getTexture();
	}
}
