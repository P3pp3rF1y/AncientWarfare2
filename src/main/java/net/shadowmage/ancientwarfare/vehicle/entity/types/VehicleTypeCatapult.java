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

import net.shadowmage.ancientwarfare.vehicle.VehicleVarHelpers.CatapultVarHelper;
import net.shadowmage.ancientwarfare.vehicle.config.AWVehicleStatics;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.entity.materials.VehicleMaterial;
import net.shadowmage.ancientwarfare.vehicle.helpers.VehicleFiringVarsHelper;
import net.shadowmage.ancientwarfare.vehicle.missiles.ItemAmmo;
import net.shadowmage.ancientwarfare.vehicle.registry.ArmorRegistry;
import net.shadowmage.ancientwarfare.vehicle.registry.VehicleUpgradeRegistry;

public class VehicleTypeCatapult extends VehicleType {

	/**
	 * @param typeNum
	 */
	public VehicleTypeCatapult(int typeNum) {
		super(typeNum);
		this.configName = "catapult_base";
		this.vehicleMaterial = VehicleMaterial.materialWood;
		this.materialCount = 5;
		this.maxMissileWeight = 10.f;

		this.validAmmoTypes.add(ItemAmmo.ammoStoneShot10);
		this.validAmmoTypes.add(ItemAmmo.ammoStoneShot15);
		this.validAmmoTypes.add(ItemAmmo.ammoFireShot10);
		this.validAmmoTypes.add(ItemAmmo.ammoFireShot15);
		this.validAmmoTypes.add(ItemAmmo.ammoPebbleShot10);
		this.validAmmoTypes.add(ItemAmmo.ammoPebbleShot15);
		this.validAmmoTypes.add(ItemAmmo.ammoClusterShot10);
		this.validAmmoTypes.add(ItemAmmo.ammoClusterShot15);
		this.validAmmoTypes.add(ItemAmmo.ammoExplosive10);
		this.validAmmoTypes.add(ItemAmmo.ammoExplosive15);
		this.validAmmoTypes.add(ItemAmmo.ammoHE10);
		this.validAmmoTypes.add(ItemAmmo.ammoHE15);
		this.validAmmoTypes.add(ItemAmmo.ammoNapalm10);
		this.validAmmoTypes.add(ItemAmmo.ammoNapalm15);

		this.validAmmoTypes.add(ItemAmmo.ammoArrow);
		this.validAmmoTypes.add(ItemAmmo.ammoArrowFlame);
		this.validAmmoTypes.add(ItemAmmo.ammoArrowIron);
		this.validAmmoTypes.add(ItemAmmo.ammoArrowIronFlame);

		if (AWVehicleStatics.oversizeAmmoEnabled) {
			this.validAmmoTypes.add(ItemAmmo.ammoStoneShot30);
			this.validAmmoTypes.add(ItemAmmo.ammoStoneShot45);
			this.validAmmoTypes.add(ItemAmmo.ammoFireShot30);
			this.validAmmoTypes.add(ItemAmmo.ammoFireShot45);
			this.validAmmoTypes.add(ItemAmmo.ammoPebbleShot30);
			this.validAmmoTypes.add(ItemAmmo.ammoPebbleShot45);
			this.validAmmoTypes.add(ItemAmmo.ammoClusterShot30);
			this.validAmmoTypes.add(ItemAmmo.ammoClusterShot45);
			this.validAmmoTypes.add(ItemAmmo.ammoExplosive30);
			this.validAmmoTypes.add(ItemAmmo.ammoExplosive45);
			this.validAmmoTypes.add(ItemAmmo.ammoHE30);
			this.validAmmoTypes.add(ItemAmmo.ammoHE45);

		}

		this.ammoBySoldierRank.put(0, ItemAmmo.ammoStoneShot10);
		this.ammoBySoldierRank.put(1, ItemAmmo.ammoStoneShot10);
		this.ammoBySoldierRank.put(2, ItemAmmo.ammoStoneShot10);

		this.validArmors.add(ArmorRegistry.armorStone);
		this.validArmors.add(ArmorRegistry.armorObsidian);
		this.validArmors.add(ArmorRegistry.armorIron);

		this.mountable = true;
		this.combatEngine = true;
		this.powerAdjustable = true;
		this.pitchAdjustable = false;
		this.accuracy = 0.95f;
		this.baseStrafeSpeed = 2.f;
		this.baseForwardSpeed = 6.f * 0.05f;
		this.basePitchMax = 20;
		this.basePitchMin = 20;

		this.validUpgrades.add(VehicleUpgradeRegistry.aimUpgrade);
		this.validUpgrades.add(VehicleUpgradeRegistry.pitchDownUpgrade);
		this.validUpgrades.add(VehicleUpgradeRegistry.pitchUpUpgrade);
		this.validUpgrades.add(VehicleUpgradeRegistry.powerUpgrade);
		this.validUpgrades.add(VehicleUpgradeRegistry.reloadUpgrade);

		this.width = 2;
		this.height = 2;
		this.baseMissileVelocityMax = 32.f;
		this.missileVerticalOffset = 0;
		this.missileForwardsOffset = -2.0f;
		this.riderForwardsOffset = 1.2f;
		this.riderVerticalOffset = 0.7f;
		this.displayName = "Catapult";
		this.storageBaySize = 0;
		this.armorBaySize = 3;
		this.upgradeBaySize = 3;
		this.yawAdjustable = false;
		this.drivable = false;
		this.riderSits = true;
		this.riderMovesWithTurret = false;
/* TODO vehicle recipe
		this.addNeededResearchForMaterials();
		this.addNeededResearch(0, ResearchGoal.vehicleTorsion1);
		this.addNeededResearch(1, ResearchGoal.vehicleTorsion2);
		this.addNeededResearch(2, ResearchGoal.vehicleTorsion3);
		this.addNeededResearch(3, ResearchGoal.vehicleTorsion4);
		this.addNeededResearch(4, ResearchGoal.vehicleTorsion5);
		this.additionalMaterials.add(new ItemStackWrapperCrafting(ItemLoader.torsionUnit, 3, false, false));
		this.additionalMaterials.add(new ItemStackWrapperCrafting(ItemLoader.equipmentBay, 1, false, false));
*/
	}

	@Override
	public VehicleFiringVarsHelper getFiringVarsHelper(VehicleBase veh) {
		return new CatapultVarHelper(veh);
	}

}
