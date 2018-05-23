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

package net.shadowmage.ancientwarfare.vehicle.entity.types;

public class VehicleTypeCannonStandFixed extends VehicleTypeCannon {

	/**
	 * @param typeNum
	 */
	public VehicleTypeCannonStandFixed(int typeNum) {
		super(typeNum);
		this.configName = "cannon_stand";
		this.drivable = true;
		this.displayName = "item.vehicleSpawner.9";
		this.displayTooltip.add("item.vehicleSpawner.tooltip.gunpowder");
		this.displayTooltip.add("item.vehicleSpawner.tooltip.fixed");
		this.displayTooltip.add("item.vehicleSpawner.tooltip.noturret");
		this.width = 1.2f;
		this.height = 1.4f;
		this.baseStrafeSpeed = 0.5f;
		this.baseForwardSpeed = 0.f;
		this.riderSits = false;
		this.riderVerticalOffset = 0.35f;
		this.riderForwardsOffset = -1.5f;
		this.armorBaySize = 4;
		this.upgradeBaySize = 4;
		this.ammoBaySize = 6;
	}
}
