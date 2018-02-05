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

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.missiles.Ammo;
import net.shadowmage.ancientwarfare.vehicle.registry.ArmorRegistry;
import net.shadowmage.ancientwarfare.vehicle.registry.VehicleUpgradeRegistry;
import shadowmage.ancient_warfare.common.config.Config;
import shadowmage.ancient_warfare.common.item.ItemLoader;
import shadowmage.ancient_warfare.common.research.ResearchGoal;
import shadowmage.ancient_warfare.common.utils.ItemStackWrapperCrafting;
import shadowmage.ancient_warfare.common.vehicles.VehicleMovementType;
import shadowmage.ancient_warfare.common.vehicles.VehicleVarHelpers.BallistaVarHelper;
import shadowmage.ancient_warfare.common.vehicles.helpers.VehicleFiringVarsHelper;
import shadowmage.ancient_warfare.common.vehicles.materials.VehicleMaterial;

public class VehicleTypeBoatBallista extends VehicleType {

	/**
	 * @param typeNum
	 */
	public VehicleTypeBoatBallista(int typeNum) {
		super(typeNum);
		this.configName = "boat_ballista";
		this.vehicleMaterial = VehicleMaterial.materialWood;
		this.materialCount = 5;
		this.movementType = VehicleMovementType.WATER;

		this.maxMissileWeight = 2.f;

		this.validAmmoTypes.add(Ammo.ammoBallistaBolt);
		this.validAmmoTypes.add(Ammo.ammoBallistaBoltFlame);
		this.validAmmoTypes.add(Ammo.ammoBallistaBoltExplosive);
		this.validAmmoTypes.add(Ammo.ammoBallistaBoltIron);

		this.ammoBySoldierRank.put(0, Ammo.ammoBallistaBolt);
		this.ammoBySoldierRank.put(1, Ammo.ammoBallistaBolt);
		this.ammoBySoldierRank.put(2, Ammo.ammoBallistaBoltFlame);

		this.validUpgrades.add(VehicleUpgradeRegistry.speedUpgrade);
		this.validUpgrades.add(VehicleUpgradeRegistry.pitchDownUpgrade);
		this.validUpgrades.add(VehicleUpgradeRegistry.pitchUpUpgrade);
		this.validUpgrades.add(VehicleUpgradeRegistry.pitchExtUpgrade);
		this.validUpgrades.add(VehicleUpgradeRegistry.powerUpgrade);
		this.validUpgrades.add(VehicleUpgradeRegistry.reloadUpgrade);
		this.validUpgrades.add(VehicleUpgradeRegistry.aimUpgrade);

		this.validArmors.add(ArmorRegistry.armorStone);
		this.validArmors.add(ArmorRegistry.armorObsidian);
		this.validArmors.add(ArmorRegistry.armorIron);

		this.armorBaySize = 3;
		this.upgradeBaySize = 3;
		this.ammoBaySize = 6;
		this.storageBaySize = 0;

		this.addNeededResearchForMaterials();
		this.addNeededResearch(0, ResearchGoal.vehicleTorsion1);
		this.addNeededResearch(1, ResearchGoal.vehicleTorsion2);
		this.addNeededResearch(2, ResearchGoal.vehicleTorsion3);
		this.addNeededResearch(3, ResearchGoal.vehicleTorsion4);
		this.addNeededResearch(4, ResearchGoal.vehicleTorsion5);

		this.addNeededResearch(0, ResearchGoal.vehicleMobility1);
		this.addNeededResearch(1, ResearchGoal.vehicleMobility2);
		this.addNeededResearch(2, ResearchGoal.vehicleMobility3);
		this.addNeededResearch(3, ResearchGoal.vehicleMobility4);
		this.addNeededResearch(4, ResearchGoal.vehicleMobility5);

		this.addNeededResearch(0, ResearchGoal.upgradeMechanics1);
		this.addNeededResearch(1, ResearchGoal.upgradeMechanics2);
		this.addNeededResearch(2, ResearchGoal.upgradeMechanics3);
		this.addNeededResearch(3, ResearchGoal.upgradeMechanics4);
		this.addNeededResearch(4, ResearchGoal.upgradeMechanics5);

		this.additionalMaterials.add(new ItemStackWrapperCrafting(Item.silk, 8, false, false));
		this.additionalMaterials.add(new ItemStackWrapperCrafting(ItemLoader.torsionUnit, 2, false, false));
		this.additionalMaterials.add(new ItemStackWrapperCrafting(ItemLoader.equipmentBay, 1, false, false));
		this.additionalMaterials.add(new ItemStackWrapperCrafting(ItemLoader.mobilityUnit, 1, false, false));
		this.additionalMaterials.add(new ItemStackWrapperCrafting(Block.cactus, 2, false, false));

		this.width = 2.7f;
		this.height = 1.4f;

		this.baseStrafeSpeed = 2.f;
		this.baseForwardSpeed = 6.2f * 0.05f;

		this.turretForwardsOffset = 23 * 0.0625f;
		this.turretVerticalOffset = 1.325f;
		this.accuracy = 0.98f;
		this.basePitchMax = 15;
		this.basePitchMin = -15;
		this.baseMissileVelocityMax = 42.f;//stand versions should have higher velocity, as should fixed version--i.e. mobile turret should have the worst of all versions

		this.riderForwardsOffset = -1.0f;
		this.riderVerticalOffset = 0.7f;
		this.shouldRiderSit = true;

		this.isMountable = true;
		this.isDrivable = true;//adjust based on isMobile or not
		this.isCombatEngine = true;

		this.canAdjustPitch = true;
		this.canAdjustPower = false;
		this.canAdjustYaw = true;
		this.turretRotationMax = 360.f;//adjust based on mobile/stand fixed (0), stand fixed(90'), or mobile or stand turret (360)
		this.displayName = "item.vehicleSpawner.18";
		this.displayTooltip.add("item.vehicleSpawner.tooltip.torsion");
		this.displayTooltip.add("item.vehicleSpawner.tooltip.boat");
		this.displayTooltip.add("item.vehicleSpawner.tooltip.fullturret");

	}

	@Override
	public String getTextureForMaterialLevel(int level) {
		switch (level) {
			case 0:
				return Config.texturePath + "models/boatBallista1.png";
			case 1:
				return Config.texturePath + "models/boatBallista2.png";
			case 2:
				return Config.texturePath + "models/boatBallista3.png";
			case 3:
				return Config.texturePath + "models/boatBallista4.png";
			case 4:
				return Config.texturePath + "models/boatBallista5.png";
			default:
				return Config.texturePath + "models/boatBallista1.png";
		}
	}

	@Override
	public VehicleFiringVarsHelper getFiringVarsHelper(VehicleBase veh) {
		return new BallistaVarHelper(veh);
	}

}
