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

public class VehicleTypeBallistaStand extends VehicleTypeBallista {
	/**
	 * @param typeNum
	 */
	public VehicleTypeBallistaStand(int typeNum) {
		super(typeNum);
		this.configName = "ballista_stand";
		this.baseMissileVelocityMax = 45.f;//stand versions should have higher velocity, as should fixed version--i.e. mobile turret should have the worst of all versions
		this.width = 1.2f;
		this.height = 1.4f;

		this.armorBaySize = 4;
		this.upgradeBaySize = 4;

		//20 units vertical, 0 forwards
		this.turretVerticalOffset = 18.f * 0.0625f;
		this.riderForwardsOffset = -1.8f;
		this.riderVerticalOffset = 0.5f;
		this.shouldRiderSit = false;
		this.isDrivable = true;//adjust based on isMobile or not
		this.baseForwardSpeed = 0.f;
		this.baseStrafeSpeed = .5f;
		this.canAdjustYaw = false;//adjust based on hasTurret or not
		this.turretRotationMax = 0.f;//adjust based on mobile/stand fixed (0), stand fixed(90'), or mobile or stand turret (360)
		this.displayName = "item.vehicleSpawner.4";
		this.displayTooltip.add("item.vehicleSpawner.tooltip.torsion");
		this.displayTooltip.add("item.vehicleSpawner.tooltip.fixed");
		this.displayTooltip.add("item.vehicleSpawner.tooltip.noturret");
	}

	@Override
	public String getTextureForMaterialLevel(int level) {
		switch (level) {
			case 0:
				return Config.texturePath + "models/ballistaStand1.png";
			case 1:
				return Config.texturePath + "models/ballistaStand2.png";
			case 2:
				return Config.texturePath + "models/ballistaStand3.png";
			case 3:
				return Config.texturePath + "models/ballistaStand4.png";
			case 4:
				return Config.texturePath + "models/ballistaStand5.png";
			default:
				return Config.texturePath + "models/ballistaStand1.png";
		}
	}
}
