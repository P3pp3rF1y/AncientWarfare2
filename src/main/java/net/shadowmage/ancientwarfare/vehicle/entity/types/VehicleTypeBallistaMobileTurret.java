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

public class VehicleTypeBallistaMobileTurret extends VehicleTypeBallista {

	/**
	 * @param typeNum
	 */
	public VehicleTypeBallistaMobileTurret(int typeNum) {
		super(typeNum);
		this.configName = "ballista_mobile_turret";
		this.baseMissileVelocityMax = 37.f;//stand versions should have higher velocity, as should fixed version--i.e. mobile turret should have the worst of all versions
		this.width = 2.7f;
		this.height = 1.4f;

		this.armorBaySize = 3;
		this.upgradeBaySize = 3;
		this.ammoBaySize = 6;

		this.turretForwardsOffset = 1.f;
		this.turretVerticalOffset = 1.2f;
		this.riderForwardsOffset = -1.2f;
		this.riderVerticalOffset = 0.7f;
		this.riderSits = true;

		this.drivable = true;//adjust based on isMobile or not
		this.yawAdjustable = true;//adjust based on hasTurret or not
		this.turretRotationMax = 360.f;//adjust based on mobile fixed (0), stand fixed(90'), or mobile or stand turret (360)
		this.displayName = "item.vehicleSpawner.7";
		this.displayTooltip.add("item.vehicleSpawner.tooltip.torsion");
		this.displayTooltip.add("item.vehicleSpawner.tooltip.mobile");
		this.displayTooltip.add("item.vehicleSpawner.tooltip.fullturret");

		this.validUpgrades.add(UpgradeRegistry.speedUpgrade);
/* TODO vehicle recipe
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
*/
	}

	@Override
	public ResourceLocation getTextureForMaterialLevel(int level) {
		switch (level) {
			case 0:
				return new ResourceLocation(AncientWarfareCore.modID, "textures/model/vehicle/ballista_mobile_1.png");
			case 1:
				return new ResourceLocation(AncientWarfareCore.modID, "textures/model/vehicle/ballista_mobile_2.png");
			case 2:
				return new ResourceLocation(AncientWarfareCore.modID, "textures/model/vehicle/ballista_mobile_3.png");
			case 3:
				return new ResourceLocation(AncientWarfareCore.modID, "textures/model/vehicle/ballista_mobile_4.png");
			case 4:
				return new ResourceLocation(AncientWarfareCore.modID, "textures/model/vehicle/ballista_mobile_5.png");
			default:
				return new ResourceLocation(AncientWarfareCore.modID, "textures/model/vehicle/ballista_mobile_1.png");
		}
	}

}
