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

import net.shadowmage.ancientwarfare.vehicle.registry.VehicleUpgradeRegistry;
import shadowmage.ancient_warfare.common.config.Config;
import shadowmage.ancient_warfare.common.item.ItemLoader;
import shadowmage.ancient_warfare.common.research.ResearchGoal;
import shadowmage.ancient_warfare.common.utils.ItemStackWrapperCrafting;

public class VehicleTypeCatapultMobileFixed extends VehicleTypeCatapult {

	/**
	 * @param typeNum
	 */
	public VehicleTypeCatapultMobileFixed(int typeNum) {
		super(typeNum);
		this.configName = "catapult_mobile";
		this.width = 2.7f;
		this.height = 2;
		this.baseStrafeSpeed = 1.7f;
		this.baseForwardSpeed = 4.2f * 0.05f;
		this.baseMissileVelocityMax = 32.f;
		this.turretVerticalOffset = 0.9375f;
		this.riderForwardsOffset = 1.2f;
		this.riderVerticalOffset = 0.7f;
		this.displayName = "item.vehicleSpawner.2";
		this.displayTooltip.add("item.vehicleSpawner.tooltip.torsion");
		this.displayTooltip.add("item.vehicleSpawner.tooltip.mobile");
		this.displayTooltip.add("item.vehicleSpawner.tooltip.noturret");
		this.storageBaySize = 0;
		this.armorBaySize = 3;
		this.upgradeBaySize = 3;
		this.canAdjustYaw = false;
		this.isDrivable = true;
		this.shouldRiderSit = true;
		this.moveRiderWithTurret = false;
		this.validUpgrades.add(VehicleUpgradeRegistry.speedUpgrade);
		this.addNeededResearch(0, ResearchGoal.vehicleMobility1);
		this.addNeededResearch(1, ResearchGoal.vehicleMobility2);
		this.addNeededResearch(2, ResearchGoal.vehicleMobility3);
		this.addNeededResearch(3, ResearchGoal.vehicleMobility4);
		this.addNeededResearch(4, ResearchGoal.vehicleMobility5);
		this.additionalMaterials.add(new ItemStackWrapperCrafting(ItemLoader.mobilityUnit, 1, false, false));
	}

	@Override
	public String getTextureForMaterialLevel(int level) {
		switch (level) {
			case 0:
				return Config.texturePath + "models/catapultMobileFixed1.png";
			case 1:
				return Config.texturePath + "models/catapultMobileFixed2.png";
			case 2:
				return Config.texturePath + "models/catapultMobileFixed3.png";
			case 3:
				return Config.texturePath + "models/catapultMobileFixed4.png";
			case 4:
				return Config.texturePath + "models/catapultMobileFixed5.png";
			default:
				return Config.texturePath + "models/catapultMobileFixed1.png";
		}
	}
}
