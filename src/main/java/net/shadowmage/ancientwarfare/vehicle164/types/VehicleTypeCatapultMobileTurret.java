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
		this.canAdjustYaw = true;
		this.isDrivable = true;
		this.shouldRiderSit = true;
		this.moveRiderWithTurret = true;
		this.turretRotationMax = 180.f;
		this.validUpgrades.add(VehicleUpgradeRegistry.speedUpgrade);
		this.addNeededResearch(0, ResearchGoal.vehicleMobility1);
		this.addNeededResearch(1, ResearchGoal.vehicleMobility2);
		this.addNeededResearch(2, ResearchGoal.vehicleMobility3);
		this.addNeededResearch(3, ResearchGoal.vehicleMobility4);
		this.addNeededResearch(4, ResearchGoal.vehicleMobility5);
		this.addNeededResearch(0, ResearchGoal.vehicleTurrets1);
		this.addNeededResearch(1, ResearchGoal.vehicleTurrets2);
		this.addNeededResearch(2, ResearchGoal.vehicleTurrets3);
		this.addNeededResearch(3, ResearchGoal.vehicleTurrets4);
		this.addNeededResearch(4, ResearchGoal.vehicleTurrets5);
		this.additionalMaterials.add(new ItemStackWrapperCrafting(ItemLoader.mobilityUnit, 1, false, false));
		this.additionalMaterials.add(new ItemStackWrapperCrafting(ItemLoader.turretComponents, 1, false, false));
	}

	@Override
	public String getTextureForMaterialLevel(int level) {
		switch (level) {
			case 0:
				return Config.texturePath + "models/catapultMobileTurret1.png";
			case 1:
				return Config.texturePath + "models/catapultMobileTurret2.png";
			case 2:
				return Config.texturePath + "models/catapultMobileTurret3.png";
			case 3:
				return Config.texturePath + "models/catapultMobileTurret4.png";
			case 4:
				return Config.texturePath + "models/catapultMobileTurret5.png";
			default:
				return Config.texturePath + "models/catapultMobileTurret1.png";
		}
	}
}
