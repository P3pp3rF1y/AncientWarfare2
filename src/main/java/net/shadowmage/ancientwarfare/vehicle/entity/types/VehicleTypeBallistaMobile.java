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
import net.shadowmage.ancientwarfare.vehicle.registry.VehicleUpgradeRegistry;

public class VehicleTypeBallistaMobile extends VehicleTypeBallista {

	/**
	 * @param typeNum
	 */
	public VehicleTypeBallistaMobile(int typeNum) {
		super(typeNum);
		this.configName = "ballista_mobile";
		this.baseMissileVelocityMax = 42.f;//stand versions should have higher velocity, as should fixed version--i.e. mobile turret should have the worst of all versions
		this.width = 2.7f;
		this.height = 1.4f;

		this.armorBaySize = 3;
		this.upgradeBaySize = 3;

		this.baseStrafeSpeed = 1.7f;
		this.baseForwardSpeed = 4.2f * 0.05f;
		this.turretForwardsOffset = 1.f;
		this.turretVerticalOffset = 1.2f;
		this.riderForwardsOffset = -1.2f;
		this.riderVerticalOffset = 0.7f;
		this.riderSits = true;

		this.drivable = true;//adjust based on isMobile or not
		this.yawAdjustable = false;//adjust based on hasTurret or not
		this.turretRotationMax = 0.f;//adjust based on mobile/stand fixed (0), stand fixed(90'), or mobile or stand turret (360)
		this.displayName = "item.vehicleSpawner.6";
		this.displayTooltip.add("item.vehicleSpawner.tooltip.torsion");
		this.displayTooltip.add("item.vehicleSpawner.tooltip.mobile");
		this.displayTooltip.add("item.vehicleSpawner.tooltip.noturret");

		this.validUpgrades.add(VehicleUpgradeRegistry.speedUpgrade);
/* TODO vehicle recipe
		this.addNeededResearch(0, ResearchGoal.vehicleMobility1);
		this.addNeededResearch(1, ResearchGoal.vehicleMobility2);
		this.addNeededResearch(2, ResearchGoal.vehicleMobility3);
		this.addNeededResearch(3, ResearchGoal.vehicleMobility4);
		this.addNeededResearch(4, ResearchGoal.vehicleMobility5);
		this.additionalMaterials.add(new ItemStackWrapperCrafting(ItemLoader.mobilityUnit, 1, false, false));
*/
	}

	@Override
	public ResourceLocation getTextureForMaterialLevel(int level) {
		switch (level) {
			case 0:
				return new ResourceLocation(AncientWarfareCore.modID, "model/vehicle/ballistaMobile1");
			case 1:
				return new ResourceLocation(AncientWarfareCore.modID, "model/vehicle/ballistaMobile2");
			case 2:
				return new ResourceLocation(AncientWarfareCore.modID, "model/vehicle/ballistaMobile3");
			case 3:
				return new ResourceLocation(AncientWarfareCore.modID, "model/vehicle/ballistaMobile4");
			case 4:
				return new ResourceLocation(AncientWarfareCore.modID, "model/vehicle/ballistaMobile5");
			default:
				return new ResourceLocation(AncientWarfareCore.modID, "model/vehicle/ballistaMobile1");
		}
	}

}
