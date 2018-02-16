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
import net.shadowmage.ancientwarfare.vehicle.VehicleVarHelpers.CatapultVarHelper;
import net.shadowmage.ancientwarfare.vehicle.config.AWVehicleStatics;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleMovementType;
import net.shadowmage.ancientwarfare.vehicle.entity.materials.VehicleMaterial;
import net.shadowmage.ancientwarfare.vehicle.helpers.VehicleFiringVarsHelper;
import net.shadowmage.ancientwarfare.vehicle.missiles.Ammo;
import net.shadowmage.ancientwarfare.vehicle.registry.ArmorRegistry;
import net.shadowmage.ancientwarfare.vehicle.registry.VehicleUpgradeRegistry;

public class VehicleTypeBoatCatapult extends VehicleType {

	/**
	 * @param typeNum
	 */
	public VehicleTypeBoatCatapult(int typeNum) {
		super(typeNum);
		this.configName = "boat_catapult";
		this.vehicleMaterial = VehicleMaterial.materialWood;
		this.materialCount = 5;
		this.movementType = VehicleMovementType.WATER;
		this.validAmmoTypes.add(Ammo.ammoStoneShot10);
		this.validAmmoTypes.add(Ammo.ammoStoneShot15);
		this.validAmmoTypes.add(Ammo.ammoFireShot10);
		this.validAmmoTypes.add(Ammo.ammoFireShot15);
		this.validAmmoTypes.add(Ammo.ammoPebbleShot10);
		this.validAmmoTypes.add(Ammo.ammoPebbleShot15);
		this.validAmmoTypes.add(Ammo.ammoClusterShot10);
		this.validAmmoTypes.add(Ammo.ammoClusterShot15);
		this.validAmmoTypes.add(Ammo.ammoExplosive10);
		this.validAmmoTypes.add(Ammo.ammoExplosive15);
		this.validAmmoTypes.add(Ammo.ammoHE10);
		this.validAmmoTypes.add(Ammo.ammoHE15);
		this.validAmmoTypes.add(Ammo.ammoNapalm10);
		this.validAmmoTypes.add(Ammo.ammoNapalm15);

		this.validAmmoTypes.add(Ammo.ammoArrow);
		this.validAmmoTypes.add(Ammo.ammoArrowFlame);
		this.validAmmoTypes.add(Ammo.ammoArrowIron);
		this.validAmmoTypes.add(Ammo.ammoArrowIronFlame);

		if (AWVehicleStatics.oversizeAmmoEnabled) {
			this.validAmmoTypes.add(Ammo.ammoStoneShot30);
			this.validAmmoTypes.add(Ammo.ammoStoneShot45);
			this.validAmmoTypes.add(Ammo.ammoFireShot30);
			this.validAmmoTypes.add(Ammo.ammoFireShot45);
			this.validAmmoTypes.add(Ammo.ammoPebbleShot30);
			this.validAmmoTypes.add(Ammo.ammoPebbleShot45);
			this.validAmmoTypes.add(Ammo.ammoClusterShot30);
			this.validAmmoTypes.add(Ammo.ammoClusterShot45);
			this.validAmmoTypes.add(Ammo.ammoExplosive30);
			this.validAmmoTypes.add(Ammo.ammoExplosive45);
			this.validAmmoTypes.add(Ammo.ammoHE30);
			this.validAmmoTypes.add(Ammo.ammoHE45);

		}

		this.ammoBySoldierRank.put(0, Ammo.ammoStoneShot10);
		this.ammoBySoldierRank.put(1, Ammo.ammoStoneShot10);
		this.ammoBySoldierRank.put(2, Ammo.ammoStoneShot10);

		this.validArmors.add(ArmorRegistry.armorStone);
		this.validArmors.add(ArmorRegistry.armorObsidian);
		this.validArmors.add(ArmorRegistry.armorIron);

		this.validUpgrades.add(VehicleUpgradeRegistry.aimUpgrade);
		this.validUpgrades.add(VehicleUpgradeRegistry.pitchDownUpgrade);
		this.validUpgrades.add(VehicleUpgradeRegistry.pitchUpUpgrade);
		this.validUpgrades.add(VehicleUpgradeRegistry.powerUpgrade);
		this.validUpgrades.add(VehicleUpgradeRegistry.speedUpgrade);
		this.validUpgrades.add(VehicleUpgradeRegistry.aimUpgrade);
		this.validUpgrades.add(VehicleUpgradeRegistry.reloadUpgrade);

		this.storageBaySize = 0;
		this.armorBaySize = 3;
		this.upgradeBaySize = 3;

		this.drivable = true;
		this.riderSits = true;
		this.riderMovesWithTurret = false;
		this.mountable = true;

		this.combatEngine = true;
		this.powerAdjustable = true;
		this.pitchAdjustable = false;
		this.yawAdjustable = false;

		this.width = 2.7f;
		this.height = 1.4f;

		this.baseStrafeSpeed = 2.f;
		this.baseForwardSpeed = 6.2f * 0.05f;

		this.accuracy = 0.95f;

		this.basePitchMax = 20;
		this.basePitchMin = 20;
		this.baseMissileVelocityMax = 32.f;
		this.maxMissileWeight = 10.f;

		this.missileVerticalOffset = 0;

		this.missileForwardsOffset = -37 * 0.0625f;
		this.turretVerticalOffset = 18 * 0.0625f;

		this.riderForwardsOffset = 1.10f;
		this.riderVerticalOffset = 0.7f;

		this.displayName = "item.vehicleSpawner.19";
		this.displayTooltip.add("item.vehicleSpawner.tooltip.torsion");
		this.displayTooltip.add("item.vehicleSpawner.tooltip.boat");
		this.displayTooltip.add("item.vehicleSpawner.tooltip.noturret");

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

		this.additionalMaterials.add(new ItemStackWrapperCrafting(ItemLoader.torsionUnit, 3, false, false));
		this.additionalMaterials.add(new ItemStackWrapperCrafting(ItemLoader.equipmentBay, 1, false, false));
		this.additionalMaterials.add(new ItemStackWrapperCrafting(ItemLoader.mobilityUnit, 1, false, false));
		this.additionalMaterials.add(new ItemStackWrapperCrafting(Block.cactus, 2, false, false));
*/
	}

	@Override
	public ResourceLocation getTextureForMaterialLevel(int level) {
		switch (level) {
			case 0:
				return new ResourceLocation(AncientWarfareCore.modID, "model/vehicle/boatCatapult1");
			case 1:
				return new ResourceLocation(AncientWarfareCore.modID, "model/vehicle/boatCatapult2");
			case 2:
				return new ResourceLocation(AncientWarfareCore.modID, "model/vehicle/boatCatapult3");
			case 3:
				return new ResourceLocation(AncientWarfareCore.modID, "model/vehicle/boatCatapult4");
			case 4:
				return new ResourceLocation(AncientWarfareCore.modID, "model/vehicle/boatCatapult5");
			default:
				return new ResourceLocation(AncientWarfareCore.modID, "model/vehicle/boatCatapult1");
		}
	}

	@Override
	public VehicleFiringVarsHelper getFiringVarsHelper(VehicleBase veh) {
		return new CatapultVarHelper(veh);
	}

}
