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
import net.shadowmage.ancientwarfare.vehicle.init.AWVehicleSounds;
import net.shadowmage.ancientwarfare.vehicle.registry.AmmoRegistry;
import net.shadowmage.ancientwarfare.vehicle.registry.ArmorRegistry;
import net.shadowmage.ancientwarfare.vehicle.registry.UpgradeRegistry;

public class VehicleTypeCatapult extends VehicleType {

	/**
	 * @param typeNum
	 */
	public VehicleTypeCatapult(int typeNum) {
		super(typeNum);
		configName = "catapult_base";
		vehicleMaterial = VehicleMaterial.materialWood;
		materialCount = 5;
		maxMissileWeight = 10.f;

		validAmmoTypes.add(AmmoRegistry.ammoStoneShot10);
		validAmmoTypes.add(AmmoRegistry.ammoStoneShot15);
		validAmmoTypes.add(AmmoRegistry.ammoFireShot10);
		validAmmoTypes.add(AmmoRegistry.ammoFireShot15);
		validAmmoTypes.add(AmmoRegistry.ammoPebbleShot10);
		validAmmoTypes.add(AmmoRegistry.ammoPebbleShot15);
		validAmmoTypes.add(AmmoRegistry.ammoClusterShot10);
		validAmmoTypes.add(AmmoRegistry.ammoClusterShot15);
		validAmmoTypes.add(AmmoRegistry.ammoExplosive10);
		validAmmoTypes.add(AmmoRegistry.ammoExplosive15);
		validAmmoTypes.add(AmmoRegistry.ammoHE10);
		validAmmoTypes.add(AmmoRegistry.ammoHE15);
		validAmmoTypes.add(AmmoRegistry.ammoNapalm10);
		validAmmoTypes.add(AmmoRegistry.ammoNapalm15);

		validAmmoTypes.add(AmmoRegistry.ammoArrow);
		validAmmoTypes.add(AmmoRegistry.ammoArrowFlame);
		validAmmoTypes.add(AmmoRegistry.ammoArrowIron);
		validAmmoTypes.add(AmmoRegistry.ammoArrowIronFlame);

		if (AWVehicleStatics.oversizeAmmoEnabled) {
			validAmmoTypes.add(AmmoRegistry.ammoStoneShot30);
			validAmmoTypes.add(AmmoRegistry.ammoStoneShot45);
			validAmmoTypes.add(AmmoRegistry.ammoFireShot30);
			validAmmoTypes.add(AmmoRegistry.ammoFireShot45);
			validAmmoTypes.add(AmmoRegistry.ammoPebbleShot30);
			validAmmoTypes.add(AmmoRegistry.ammoPebbleShot45);
			validAmmoTypes.add(AmmoRegistry.ammoClusterShot30);
			validAmmoTypes.add(AmmoRegistry.ammoClusterShot45);
			validAmmoTypes.add(AmmoRegistry.ammoExplosive30);
			validAmmoTypes.add(AmmoRegistry.ammoExplosive45);
			validAmmoTypes.add(AmmoRegistry.ammoHE30);
			validAmmoTypes.add(AmmoRegistry.ammoHE45);

		}

		ammoBySoldierRank.put(0, AmmoRegistry.ammoStoneShot10);
		ammoBySoldierRank.put(1, AmmoRegistry.ammoStoneShot10);
		ammoBySoldierRank.put(2, AmmoRegistry.ammoStoneShot10);

		validArmors.add(ArmorRegistry.armorStone);
		validArmors.add(ArmorRegistry.armorObsidian);
		validArmors.add(ArmorRegistry.armorIron);

		mountable = true;
		combatEngine = true;
		powerAdjustable = true;
		pitchAdjustable = false;
		accuracy = 0.95f;
		baseStrafeSpeed = 2.f;
		baseForwardSpeed = 6.f * 0.05f;
		basePitchMax = 20;
		basePitchMin = 20;

		validUpgrades.add(UpgradeRegistry.aimUpgrade);
		validUpgrades.add(UpgradeRegistry.pitchDownUpgrade);
		validUpgrades.add(UpgradeRegistry.pitchUpUpgrade);
		validUpgrades.add(UpgradeRegistry.powerUpgrade);
		validUpgrades.add(UpgradeRegistry.reloadUpgrade);

		width = 2;
		height = 2;
		baseMissileVelocityMax = 32.f;
		missileVerticalOffset = 0;
		missileForwardsOffset = -2.0f;
		riderForwardsOffset = 1.2f;
		riderVerticalOffset = 0.55f;
		displayName = "Catapult";
		storageBaySize = 0;
		armorBaySize = 3;
		upgradeBaySize = 3;
		yawAdjustable = false;
		drivable = false;
		riderSits = true;
		riderMovesWithTurret = false;
	}

	@Override
	public void playFiringSound(VehicleBase vehicleBase) {
		vehicleBase.playSound(AWVehicleSounds.CATAPULT_LAUNCH, 6, 1);
	}

	@Override
	public void playReloadSound(VehicleBase vehicleBase) {
		vehicleBase.playSound(AWVehicleSounds.CATAPULT_RELOAD, 1, 1);
	}

	@Override
	public VehicleFiringVarsHelper getFiringVarsHelper(VehicleBase veh) {
		return new CatapultVarHelper(veh);
	}
}
