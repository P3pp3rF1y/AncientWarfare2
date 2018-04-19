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

package net.shadowmage.ancientwarfare.vehicle.upgrades;

import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;

public class VehicleUpgradeAim extends VehicleUpgradeBase {

	public VehicleUpgradeAim() {
		super("vehicle_upgrade_aim");
	}

	@Override
	public void applyVehicleEffects(VehicleBase vehicle) {
		float adj = 1 - vehicle.currentAccuracy;
		vehicle.currentAccuracy += adj * .5f;
		if (vehicle.currentAccuracy > 1) {
			vehicle.currentAccuracy = 1;
		}
	}

}
