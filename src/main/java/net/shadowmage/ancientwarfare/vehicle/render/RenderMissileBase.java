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
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import shadowmage.ancient_warfare.common.vehicles.missiles.IAmmoType;
import shadowmage.ancient_warfare.common.vehicles.missiles.MissileBase;

public abstract class RenderMissileBase extends Render {
	static Minecraft mc = Minecraft.getMinecraft();

	@Override
	public void doRender(Entity var1, double var2, double var4, double var6, float var8, float var9) {
		GL11.glPushMatrix();
		MissileBase missile = (MissileBase) var1;
		GL11.glTranslated(var2, var4, var6);
		GL11.glRotatef(var8 - 90, 0, 1, 0);
		GL11.glRotatef(var1.rotationPitch - 90, 1, 0, 0);
		GL11.glScaled(-1, -1, 1);
		float scale = missile.ammoType.getRenderScale();
		GL11.glScalef(scale, scale, scale);
		AWTextureManager.bindTexture(missile.getTexture());
		renderMissile(missile, missile.ammoType, var2, var4, var6, var8, var9);
		GL11.glPopMatrix();
	}

	public abstract void renderMissile(MissileBase missile, IAmmoType ammo, double x, double y, double z, float yaw, float tick);

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return AWTextureManager.getResource(((MissileBase) entity).getTexture());
	}
}
