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
import net.shadowmage.ancientwarfare.vehicle.registry.AmmoRegistry;
import net.shadowmage.ancientwarfare.vehicle.registry.ArmorRegistry;
import net.shadowmage.ancientwarfare.vehicle.registry.UpgradeRegistry;

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
		this.validAmmoTypes.add(AmmoRegistry.ammoStoneShot10);
		this.validAmmoTypes.add(AmmoRegistry.ammoStoneShot15);
		this.validAmmoTypes.add(AmmoRegistry.ammoFireShot10);
		this.validAmmoTypes.add(AmmoRegistry.ammoFireShot15);
		this.validAmmoTypes.add(AmmoRegistry.ammoPebbleShot10);
		this.validAmmoTypes.add(AmmoRegistry.ammoPebbleShot15);
		this.validAmmoTypes.add(AmmoRegistry.ammoClusterShot10);
		this.validAmmoTypes.add(AmmoRegistry.ammoClusterShot15);
		this.validAmmoTypes.add(AmmoRegistry.ammoExplosive10);
		this.validAmmoTypes.add(AmmoRegistry.ammoExplosive15);
		this.validAmmoTypes.add(AmmoRegistry.ammoHE10);
		this.validAmmoTypes.add(AmmoRegistry.ammoHE15);
		this.validAmmoTypes.add(AmmoRegistry.ammoNapalm10);
		this.validAmmoTypes.add(AmmoRegistry.ammoNapalm15);

		this.validAmmoTypes.add(AmmoRegistry.ammoArrow);
		this.validAmmoTypes.add(AmmoRegistry.ammoArrowFlame);
		this.validAmmoTypes.add(AmmoRegistry.ammoArrowIron);
		this.validAmmoTypes.add(AmmoRegistry.ammoArrowIronFlame);

		if (AWVehicleStatics.oversizeAmmoEnabled) {
			this.validAmmoTypes.add(AmmoRegistry.ammoStoneShot30);
			this.validAmmoTypes.add(AmmoRegistry.ammoStoneShot45);
			this.validAmmoTypes.add(AmmoRegistry.ammoFireShot30);
			this.validAmmoTypes.add(AmmoRegistry.ammoFireShot45);
			this.validAmmoTypes.add(AmmoRegistry.ammoPebbleShot30);
			this.validAmmoTypes.add(AmmoRegistry.ammoPebbleShot45);
			this.validAmmoTypes.add(AmmoRegistry.ammoClusterShot30);
			this.validAmmoTypes.add(AmmoRegistry.ammoClusterShot45);
			this.validAmmoTypes.add(AmmoRegistry.ammoExplosive30);
			this.validAmmoTypes.add(AmmoRegistry.ammoExplosive45);
			this.validAmmoTypes.add(AmmoRegistry.ammoHE30);
			this.validAmmoTypes.add(AmmoRegistry.ammoHE45);

		}

		this.ammoBySoldierRank.put(0, AmmoRegistry.ammoStoneShot10);
		this.ammoBySoldierRank.put(1, AmmoRegistry.ammoStoneShot10);
		this.ammoBySoldierRank.put(2, AmmoRegistry.ammoStoneShot10);

		this.validArmors.add(ArmorRegistry.armorStone);
		this.validArmors.add(ArmorRegistry.armorObsidian);
		this.validArmors.add(ArmorRegistry.armorIron);

		this.validUpgrades.add(UpgradeRegistry.aimUpgrade);
		this.validUpgrades.add(UpgradeRegistry.pitchDownUpgrade);
		this.validUpgrades.add(UpgradeRegistry.pitchUpUpgrade);
		this.validUpgrades.add(UpgradeRegistry.powerUpgrade);
		this.validUpgrades.add(UpgradeRegistry.speedUpgrade);
		this.validUpgrades.add(UpgradeRegistry.aimUpgrade);
		this.validUpgrades.add(UpgradeRegistry.reloadUpgrade);

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
	}

	@Override
	public ResourceLocation getTextureForMaterialLevel(int level) {
		switch (level) {
			case 0:
				return new ResourceLocation(AncientWarfareCore.modID, "textures/model/vehicle/boat_catapult_1.png");
			case 1:
				return new ResourceLocation(AncientWarfareCore.modID, "textures/model/vehicle/boat_catapult_2.png");
			case 2:
				return new ResourceLocation(AncientWarfareCore.modID, "textures/model/vehicle/boat_catapult_3.png");
			case 3:
				return new ResourceLocation(AncientWarfareCore.modID, "textures/model/vehicle/boat_catapult_4.png");
			case 4:
				return new ResourceLocation(AncientWarfareCore.modID, "textures/model/vehicle/boat_catapult_5.png");
			default:
				return new ResourceLocation(AncientWarfareCore.modID, "textures/model/vehicle/boat_catapult_1.png");
		}
	}

	@Override
	public VehicleFiringVarsHelper getFiringVarsHelper(VehicleBase veh) {
		return new CatapultVarHelper(veh);
	}

}
