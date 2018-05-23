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

import net.shadowmage.ancientwarfare.vehicle.registry.UpgradeRegistry;

public class VehicleTypeTrebuchetMobileFixed extends VehicleTypeTrebuchet {

	/**
	 * @param typeNum
	 */
	public VehicleTypeTrebuchetMobileFixed(int typeNum) {
		super(typeNum);
		this.configName = "trebuchet_mobile";
		this.displayName = "item.vehicleSpawner.15";
		this.displayTooltip.add("item.vehicleSpawner.tooltip.weight");
		this.displayTooltip.add("item.vehicleSpawner.tooltip.mobile");
		this.displayTooltip.add("item.vehicleSpawner.tooltip.noturret");
		this.width = 2.7f;
		this.height = 2.7f;
		this.baseForwardSpeed = 3.7f * 0.05f;
		this.baseStrafeSpeed = 1.0f;
		this.riderSits = true;
		this.riderForwardsOffset = 1.275f;
		this.riderVerticalOffset = 0.65f;
		this.turretVerticalOffset = (34.f + 67.5f + 24.0f + 12.f) * 0.0625f;
		this.validUpgrades.add(UpgradeRegistry.speedUpgrade);
	}
}
