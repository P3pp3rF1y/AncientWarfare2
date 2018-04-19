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

import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.vehicle.registry.UpgradeRegistry;

public class VehicleTypeCatapultMobileTurret extends VehicleTypeCatapult {

	/**
	 * @param typeNum
	 */
	public VehicleTypeCatapultMobileTurret(int typeNum) {
		super(typeNum);
		this.configName = "catapult_mobile_turret";
		this.width = 2.7f;
		this.height = 2;
		this.baseStrafeSpeed = 1.5f;
		this.baseForwardSpeed = 4.0f * 0.05f;
		this.baseMissileVelocityMax = 30.f;
		this.turretVerticalOffset = 15 * 0.0625f;
		this.riderForwardsOffset = 0.8f;
		this.riderVerticalOffset = 0.8f;
		this.displayName = "item.vehicleSpawner.3";
		this.displayTooltip.add("item.vehicleSpawner.tooltip.torsion");
		this.displayTooltip.add("item.vehicleSpawner.tooltip.mobile");
		this.displayTooltip.add("item.vehicleSpawner.tooltip.fullturret");
		this.storageBaySize = 0;
		this.armorBaySize = 3;
		this.upgradeBaySize = 3;
		this.yawAdjustable = true;
		this.drivable = true;
		this.riderSits = true;
		this.riderMovesWithTurret = true;
		this.turretRotationMax = 180.f;
		this.validUpgrades.add(UpgradeRegistry.speedUpgrade);
	}

	@Override
	public ResourceLocation getTextureForMaterialLevel(int level) {
		switch (level) {
			case 0:
				return new ResourceLocation(AncientWarfareCore.modID, "textures/model/vehicle/catapult_mobile_turret_1.png");
			case 1:
				return new ResourceLocation(AncientWarfareCore.modID, "textures/model/vehicle/catapult_mobile_turret_2.png");
			case 2:
				return new ResourceLocation(AncientWarfareCore.modID, "textures/model/vehicle/catapult_mobile_turret_3.png");
			case 3:
				return new ResourceLocation(AncientWarfareCore.modID, "textures/model/vehicle/catapult_mobile_turret_4.png");
			case 4:
				return new ResourceLocation(AncientWarfareCore.modID, "textures/model/vehicle/catapult_mobile_turret_5.png");
			default:
				return new ResourceLocation(AncientWarfareCore.modID, "textures/model/vehicle/catapult_mobile_turret_1.png");
		}
	}
}
