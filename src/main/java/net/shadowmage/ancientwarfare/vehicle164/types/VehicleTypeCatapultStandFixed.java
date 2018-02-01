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

package shadowmage.ancient_warfare.common.vehicles.types;

import shadowmage.ancient_warfare.common.config.Config;

public class VehicleTypeCatapultStandFixed extends VehicleTypeCatapult {
	/**
	 * @param typeNum
	 */
	public VehicleTypeCatapultStandFixed(int typeNum) {
		super(typeNum);
		this.configName = "catapult_stand";
		this.width = 2;
		this.height = 1.7f;
		this.baseMissileVelocityMax = 37.f;
		this.turretVerticalOffset = 3 * 0.0625f;
		this.riderForwardsOffset = 1.2f;
		this.riderVerticalOffset = 0.0f;
		this.displayName = "item.vehicleSpawner.0";
		this.displayTooltip.add("item.vehicleSpawner.tooltip.torsion");
		this.displayTooltip.add("item.vehicleSpawner.tooltip.fixed");
		this.displayTooltip.add("item.vehicleSpawner.tooltip.noturret");
		this.storageBaySize = 0;
		this.armorBaySize = 4;
		this.upgradeBaySize = 4;
		this.canAdjustYaw = false;
		this.isDrivable = true;
		this.baseForwardSpeed = 0.f;
		this.baseStrafeSpeed = .5f;
		this.shouldRiderSit = true;
		this.moveRiderWithTurret = false;
	}

	@Override
	public String getTextureForMaterialLevel(int level) {
		switch (level) {
			case 0:
				return Config.texturePath + "models/catapultStandFixed1.png";
			case 1:
				return Config.texturePath + "models/catapultStandFixed2.png";
			case 2:
				return Config.texturePath + "models/catapultStandFixed3.png";
			case 3:
				return Config.texturePath + "models/catapultStandFixed4.png";
			case 4:
				return Config.texturePath + "models/catapultStandFixed5.png";
			default:
				return Config.texturePath + "models/catapultStandFixed1.png";
		}
	}

}
