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
import net.shadowmage.ancientwarfare.vehicle.VehicleVarHelpers.BallistaVarHelper;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleMovementType;
import net.shadowmage.ancientwarfare.vehicle.entity.materials.VehicleMaterial;
import net.shadowmage.ancientwarfare.vehicle.helpers.VehicleFiringVarsHelper;
import net.shadowmage.ancientwarfare.vehicle.registry.AmmoRegistry;
import net.shadowmage.ancientwarfare.vehicle.registry.ArmorRegistry;
import net.shadowmage.ancientwarfare.vehicle.registry.UpgradeRegistry;

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

		this.validAmmoTypes.add(AmmoRegistry.ammoBallistaBolt);
		this.validAmmoTypes.add(AmmoRegistry.ammoBallistaBoltFlame);
		this.validAmmoTypes.add(AmmoRegistry.ammoBallistaBoltExplosive);
		this.validAmmoTypes.add(AmmoRegistry.ammoBallistaBoltIron);

		this.ammoBySoldierRank.put(0, AmmoRegistry.ammoBallistaBolt);
		this.ammoBySoldierRank.put(1, AmmoRegistry.ammoBallistaBolt);
		this.ammoBySoldierRank.put(2, AmmoRegistry.ammoBallistaBoltFlame);

		this.validUpgrades.add(UpgradeRegistry.speedUpgrade);
		this.validUpgrades.add(UpgradeRegistry.pitchDownUpgrade);
		this.validUpgrades.add(UpgradeRegistry.pitchUpUpgrade);
		this.validUpgrades.add(UpgradeRegistry.pitchExtUpgrade);
		this.validUpgrades.add(UpgradeRegistry.powerUpgrade);
		this.validUpgrades.add(UpgradeRegistry.reloadUpgrade);
		this.validUpgrades.add(UpgradeRegistry.aimUpgrade);

		this.validArmors.add(ArmorRegistry.armorStone);
		this.validArmors.add(ArmorRegistry.armorObsidian);
		this.validArmors.add(ArmorRegistry.armorIron);

		this.armorBaySize = 3;
		this.upgradeBaySize = 3;
		this.ammoBaySize = 6;
		this.storageBaySize = 0;

/* TODO vehicle recipe
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
*/

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
		this.riderSits = true;

		this.mountable = true;
		this.drivable = true;//adjust based on isMobile or not
		this.combatEngine = true;

		this.pitchAdjustable = true;
		this.powerAdjustable = false;
		this.yawAdjustable = true;
		this.turretRotationMax = 360.f;//adjust based on mobile/stand fixed (0), stand fixed(90'), or mobile or stand turret (360)
		this.displayName = "item.vehicleSpawner.18";
		this.displayTooltip.add("item.vehicleSpawner.tooltip.torsion");
		this.displayTooltip.add("item.vehicleSpawner.tooltip.boat");
		this.displayTooltip.add("item.vehicleSpawner.tooltip.fullturret");

	}

	@Override
	public ResourceLocation getTextureForMaterialLevel(int level) {
		switch (level) {
			case 0:
				return new ResourceLocation(AncientWarfareCore.modID, "model/vehicle/boatBallista1");
			case 1:
				return new ResourceLocation(AncientWarfareCore.modID, "model/vehicle/boatBallista2");
			case 2:
				return new ResourceLocation(AncientWarfareCore.modID, "model/vehicle/boatBallista3");
			case 3:
				return new ResourceLocation(AncientWarfareCore.modID, "model/vehicle/boatBallista4");
			case 4:
				return new ResourceLocation(AncientWarfareCore.modID, "model/vehicle/boatBallista5");
			default:
				return new ResourceLocation(AncientWarfareCore.modID, "model/vehicle/boatBallista1");
		}
	}

	@Override
	public VehicleFiringVarsHelper getFiringVarsHelper(VehicleBase veh) {
		return new BallistaVarHelper(veh);
	}

}
