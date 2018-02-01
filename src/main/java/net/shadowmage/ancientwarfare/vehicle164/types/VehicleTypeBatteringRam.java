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

import net.minecraft.item.Item;
import shadowmage.ancient_warfare.common.config.Config;
import shadowmage.ancient_warfare.common.item.ItemLoader;
import shadowmage.ancient_warfare.common.registry.ArmorRegistry;
import shadowmage.ancient_warfare.common.registry.VehicleUpgradeRegistry;
import shadowmage.ancient_warfare.common.research.ResearchGoal;
import shadowmage.ancient_warfare.common.utils.BlockPosition;
import shadowmage.ancient_warfare.common.utils.ItemStackWrapperCrafting;
import shadowmage.ancient_warfare.common.utils.Pos3f;
import shadowmage.ancient_warfare.common.vehicles.VehicleBase;
import shadowmage.ancient_warfare.common.vehicles.VehicleVarHelpers.BatteringRamVarHelper;
import shadowmage.ancient_warfare.common.vehicles.helpers.VehicleFiringVarsHelper;
import shadowmage.ancient_warfare.common.vehicles.materials.VehicleMaterial;

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

		this.validUpgrades.add(VehicleUpgradeRegistry.speedUpgrade);
		this.validUpgrades.add(VehicleUpgradeRegistry.reloadUpgrade);

		this.width = 2.7f;
		this.height = 2.1f;
		this.isMountable = true;
		this.isDrivable = true;
		this.isCombatEngine = true;
		this.canAdjustPower = false;
		this.canAdjustPitch = false;
		this.canAdjustYaw = false;
		this.shouldRiderSit = true;
		this.moveRiderWithTurret = false;
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
	}

	@Override
	public VehicleFiringVarsHelper getFiringVarsHelper(VehicleBase veh) {
		return new BatteringRamVarHelper(veh);
	}

	@Override
	public String getTextureForMaterialLevel(int level) {
		switch (level) {
			case 0:
				return Config.texturePath + "models/batteringRam1.png";
			case 1:
				return Config.texturePath + "models/batteringRam2.png";
			case 2:
				return Config.texturePath + "models/batteringRam3.png";
			case 3:
				return Config.texturePath + "models/batteringRam4.png";
			case 4:
				return Config.texturePath + "models/batteringRam5.png";
			default:
				return Config.texturePath + "models/batteringRam1.png";
		}
	}

	public static BlockPosition[] getEffectedPositions(VehicleBase vehicle) {
		double x1 = vehicle.posX;
		double y1 = vehicle.posY;
		double z1 = vehicle.posZ;

		BlockPosition[] positions = new BlockPosition[7];
		Pos3f offset = vehicle.getMissileOffset();
		double x2 = x1 + offset.x;
		double y2 = y1 + offset.y;
		double z2 = z1 + offset.z;
		float bx = (float) (vehicle.posX + offset.x);
		float by = (float) (vehicle.posY + offset.y);
		float bz = (float) (vehicle.posZ + offset.z);
		BlockPosition blockHit = new BlockPosition(bx, by, bz);

		positions[0] = blockHit;
		positions[1] = new BlockPosition(blockHit.x - 1, blockHit.y, blockHit.z);
		positions[2] = new BlockPosition(blockHit.x, blockHit.y - 1, blockHit.z);
		positions[3] = new BlockPosition(blockHit.x, blockHit.y, blockHit.z - 1);
		positions[4] = new BlockPosition(blockHit.x + 1, blockHit.y, blockHit.z);
		positions[5] = new BlockPosition(blockHit.x, blockHit.y + 1, blockHit.z);
		positions[6] = new BlockPosition(blockHit.x, blockHit.y, blockHit.z + 1);
		return positions;
	}
}
