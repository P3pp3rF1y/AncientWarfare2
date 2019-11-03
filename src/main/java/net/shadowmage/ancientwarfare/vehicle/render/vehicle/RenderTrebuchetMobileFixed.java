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
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.helpers.VehicleFiringVarsHelper;
import net.shadowmage.ancientwarfare.vehicle.model.ModelTrebuchetMobileFixed;

public class RenderTrebuchetMobileFixed extends RenderVehicleBase {

	ModelTrebuchetMobileFixed model = new ModelTrebuchetMobileFixed();

	public RenderTrebuchetMobileFixed(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void renderVehicle(VehicleBase vehicle, double x, double y, double z, float yaw, float tick) {
		VehicleFiringVarsHelper var = vehicle.firingVarsHelper;
		float wheelAngle = vehicle.wheelRotation + (tick * (vehicle.wheelRotation - vehicle.wheelRotationPrev));
		model.setWheelRotations(wheelAngle, wheelAngle, wheelAngle, wheelAngle);
		model.setArmRotations(var.getVar1() + tick * var.getVar2(), var.getVar3() + tick * var.getVar4());
		model.render(vehicle, 0, 0, 0, 0, 0, 0.0625f);
	}

}
