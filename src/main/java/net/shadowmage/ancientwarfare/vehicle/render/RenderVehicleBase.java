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

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;

public abstract class RenderVehicleBase extends Render {

	@Override
	public void doRender(Entity var1, double var2, double var4, double var6, float var8, float var9) {

	}

	public abstract void renderVehicle(VehicleBase vehicle, double x, double y, double z, float yaw, float tick);

	public abstract void renderVehicleFlag();

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return AWTextureManager.getResource(((VehicleBase) entity).getTexture());
	}
}
