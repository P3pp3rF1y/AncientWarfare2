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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.vehicle.VehicleVarHelpers.BatteringRamVarHelper;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.entity.materials.VehicleMaterial;
import net.shadowmage.ancientwarfare.vehicle.helpers.VehicleFiringVarsHelper;
import net.shadowmage.ancientwarfare.vehicle.registry.ArmorRegistry;
import net.shadowmage.ancientwarfare.vehicle.registry.UpgradeRegistry;

public class VehicleTypeBatteringRam extends VehicleType {

	/**
	 * @param typeNum
	 */
	public VehicleTypeBatteringRam(int typeNum) {
		super(typeNum);
		this.configName = "battering_ram";
		this.vehicleMaterial = VehicleMaterial.materialWood;
		this.validArmors.add(ArmorRegistry.armorStone);
		this.validArmors.add(ArmorRegistry.armorIron);
		this.validArmors.add(ArmorRegistry.armorObsidian);

		this.validUpgrades.add(UpgradeRegistry.speedUpgrade);
		this.validUpgrades.add(UpgradeRegistry.reloadUpgrade);

		this.width = 2.7f;
		this.height = 2.1f;
		this.mountable = true;
		this.drivable = true;
		this.combatEngine = true;
		this.powerAdjustable = false;
		this.pitchAdjustable = false;
		this.yawAdjustable = false;
		this.riderSits = true;
		this.riderMovesWithTurret = false;
		this.accuracy = 0.99f;
		this.baseStrafeSpeed = 1.f;
		this.baseForwardSpeed = 4.5f * 0.05f;
		this.basePitchMax = 0.f;
		this.basePitchMin = 0.f;
		this.turretRotationMax = 0.f;
		this.turretForwardsOffset = 2.6f;
		this.turretVerticalOffset = 1.8f;
		this.turretHorizontalOffset = -.3f;
		this.minAttackDistance = 1.f;
		this.riderForwardsOffset = 0.f;
		this.riderVerticalOffset = 0.65f;
		this.riderHorizontalOffset = 0.325f;
		this.displayName = "item.vehicleSpawner.8";
		this.displayTooltip.add("item.vehicleSpawner.tooltip.weight");
		this.displayTooltip.add("item.vehicleSpawner.tooltip.mobile");
		this.displayTooltip.add("item.vehicleSpawner.tooltip.noturret");
		this.displayTooltip.add("item.vehicleSpawner.tooltip.ram");
		this.storageBaySize = 0;
		this.armorBaySize = 6;
		this.upgradeBaySize = 3;
/* TODO vehicle recipe
		this.addNeededResearchForMaterials();
		this.addNeededResearch(0, ResearchGoal.vehicleCounterweights1);
		this.addNeededResearch(1, ResearchGoal.vehicleCounterweights2);
		this.addNeededResearch(2, ResearchGoal.vehicleCounterweights3);
		this.addNeededResearch(3, ResearchGoal.vehicleCounterweights4);
		this.addNeededResearch(4, ResearchGoal.vehicleCounterweights5);
		this.addNeededResearch(0, ResearchGoal.vehicleMobility1);
		this.addNeededResearch(1, ResearchGoal.vehicleMobility2);
		this.addNeededResearch(2, ResearchGoal.vehicleMobility3);
		this.addNeededResearch(3, ResearchGoal.vehicleMobility4);
		this.addNeededResearch(4, ResearchGoal.vehicleMobility5);
		this.additionalMaterials.add(new ItemStackWrapperCrafting(ItemLoader.mobilityUnit, 1, false, false));
		this.additionalMaterials.add(new ItemStackWrapperCrafting(Item.silk, 8, false, false));
		this.additionalMaterials.add(new ItemStackWrapperCrafting(ItemLoader.counterWeightUnit, 2, false, false));
		this.additionalMaterials.add(new ItemStackWrapperCrafting(ItemLoader.equipmentBay, 1, false, false));
*/
	}

	@Override
	public VehicleFiringVarsHelper getFiringVarsHelper(VehicleBase veh) {
		return new BatteringRamVarHelper(veh);
	}

	@Override
	public ResourceLocation getTextureForMaterialLevel(int level) {
		switch (level) {
			case 0:
				return new ResourceLocation(AncientWarfareCore.modID, "textures/model/vehicle/battering_ram_1.png");
			case 1:
				return new ResourceLocation(AncientWarfareCore.modID, "textures/model/vehicle/battering_ram_2.png");
			case 2:
				return new ResourceLocation(AncientWarfareCore.modID, "textures/model/vehicle/battering_ram_3.png");
			case 3:
				return new ResourceLocation(AncientWarfareCore.modID, "textures/model/vehicle/battering_ram_4.png");
			case 4:
				return new ResourceLocation(AncientWarfareCore.modID, "textures/model/vehicle/battering_ram_5.png");
			default:
				return new ResourceLocation(AncientWarfareCore.modID, "textures/model/vehicle/battering_ram_1.png");
		}
	}

	public static BlockPos[] getEffectedPositions(VehicleBase vehicle) {
		double x1 = vehicle.posX;
		double y1 = vehicle.posY;
		double z1 = vehicle.posZ;

		BlockPos[] positions = new BlockPos[7];
		Vec3d offset = vehicle.getMissileOffset();
		float bx = (float) (vehicle.posX + offset.x);
		float by = (float) (vehicle.posY + offset.y);
		float bz = (float) (vehicle.posZ + offset.z);
		BlockPos blockHit = new BlockPos(bx, by, bz);

		positions[0] = blockHit;
		positions[1] = new BlockPos(blockHit.getX() - 1, blockHit.getY(), blockHit.getZ());
		positions[2] = new BlockPos(blockHit.getX(), blockHit.getY() - 1, blockHit.getZ());
		positions[3] = new BlockPos(blockHit.getX(), blockHit.getY(), blockHit.getZ() - 1);
		positions[4] = new BlockPos(blockHit.getX() + 1, blockHit.getY(), blockHit.getZ());
		positions[5] = new BlockPos(blockHit.getX(), blockHit.getY() + 1, blockHit.getZ());
		positions[6] = new BlockPos(blockHit.getX(), blockHit.getY(), blockHit.getZ() + 1);
		return positions;
	}
}
