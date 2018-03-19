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

public class VehicleTypeTrebuchetStandTurret extends VehicleTypeTrebuchet {

	/**
	 * @param typeNum
	 */
	public VehicleTypeTrebuchetStandTurret(int typeNum) {
		super(typeNum);
		this.configName = "trebuchet_stand_turret";
		this.displayName = "item.vehicleSpawner.14";
		this.displayTooltip.add("item.vehicleSpawner.tooltip.weight");
		this.displayTooltip.add("item.vehicleSpawner.tooltip.fixed");
		this.displayTooltip.add("item.vehicleSpawner.tooltip.midturret");
		this.width = 2.7f;
		this.height = 2.7f;
		this.yawAdjustable = true;
		this.riderMovesWithTurret = true;
		this.riderSits = true;
		this.turretRotationMax = 45;
		this.riderForwardsOffset = 1.275f;
		this.riderVerticalOffset = 0.7f;
		this.turretVerticalOffset = (34.f + 67.5f + 24.0f + 9.5f) * 0.0625f;
/* TODO vehicle recipes
		this.addNeededResearch(0, ResearchGoal.vehicleTurrets1);
		this.addNeededResearch(1, ResearchGoal.vehicleTurrets2);
		this.addNeededResearch(2, ResearchGoal.vehicleTurrets3);
		this.addNeededResearch(3, ResearchGoal.vehicleTurrets4);
		this.addNeededResearch(4, ResearchGoal.vehicleTurrets5);
		this.additionalMaterials.add(new ItemStackWrapperCrafting(ItemLoader.turretComponents, 1, false, false));
*/
	}

	@Override
	public ResourceLocation getTextureForMaterialLevel(int level) {
		switch (level) {
			case 0:
				return new ResourceLocation(AncientWarfareCore.modID, "textures/model/vehicle/trebuchet_mobile_1.png");
			case 1:
				return new ResourceLocation(AncientWarfareCore.modID, "textures/model/vehicle/trebuchet_mobile_2.png");
			case 2:
				return new ResourceLocation(AncientWarfareCore.modID, "textures/model/vehicle/trebuchet_mobile_3.png");
			case 3:
				return new ResourceLocation(AncientWarfareCore.modID, "textures/model/vehicle/trebuchet_mobile_4.png");
			case 4:
				return new ResourceLocation(AncientWarfareCore.modID, "textures/model/vehicle/trebuchet_mobile_5.png");
			default:
				return new ResourceLocation(AncientWarfareCore.modID, "textures/model/vehicle/trebuchet_mobile_1.png");
		}
	}
}
