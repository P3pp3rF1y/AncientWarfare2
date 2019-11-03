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

package net.shadowmage.ancientwarfare.vehicle.render.vehicle;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.math.MathHelper;
import net.shadowmage.ancientwarfare.core.util.Trig;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.model.ModelChestCart;

public class RenderChestCart extends RenderVehicleBase {
	ModelChestCart model = new ModelChestCart();

	public RenderChestCart(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void renderVehicle(VehicleBase vehicle, double x, double y, double z, float yaw, float tick) {
		model.setHitchAngle(MathHelper.abs(Trig.getVelocity(vehicle.motionX, vehicle.motionY, vehicle.motionZ)) > 0.02f ? 0.f : 25.f);
		float wheelAngle = vehicle.wheelRotation + (tick * (vehicle.wheelRotation - vehicle.wheelRotationPrev));
		model.setWheelRotations(wheelAngle, wheelAngle, wheelAngle, wheelAngle);
		model.render(vehicle, 0, 0, 0, 0, 0, 0.0625f);
	}

}
