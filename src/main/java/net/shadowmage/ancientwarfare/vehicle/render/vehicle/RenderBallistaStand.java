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
import net.shadowmage.ancientwarfare.vehicle.model.ModelBallistaStand;

public class RenderBallistaStand extends RenderVehicleBase {

	ModelBallistaStand model = new ModelBallistaStand();

	public RenderBallistaStand(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void renderVehicle(VehicleBase veh, double x, double y, double z, float yaw, float tick) {
		VehicleFiringVarsHelper var = veh.firingVarsHelper;
		model.setTurretRotation(yaw - veh.localTurretRotation - tick * veh.currentTurretYawSpeed, -veh.localTurretPitch + tick * veh.currentTurretPitchSpeed);
		model.setCrankRotations(var.getVar1() + (tick * var.getVar2()));
		model.setBowAndStringRotation(var.getVar3() + tick * var.getVar4(), var.getVar5() + tick * var.getVar6());
		model.setTriggerAngle(var.getVar7());
		float wheelAngle = veh.wheelRotation + (tick * (veh.wheelRotation - veh.wheelRotationPrev));
		model.render(veh, 0, 0, 0, 0, 0, 0.0625f);
	}

}
