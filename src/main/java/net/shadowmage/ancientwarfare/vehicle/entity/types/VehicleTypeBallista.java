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

import net.shadowmage.ancientwarfare.vehicle.VehicleVarHelpers.BallistaVarHelper;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.entity.materials.VehicleMaterial;
import net.shadowmage.ancientwarfare.vehicle.helpers.VehicleFiringVarsHelper;
import net.shadowmage.ancientwarfare.vehicle.registry.AmmoRegistry;
import net.shadowmage.ancientwarfare.vehicle.registry.ArmorRegistry;
import net.shadowmage.ancientwarfare.vehicle.registry.UpgradeRegistry;

public abstract class VehicleTypeBallista extends VehicleType {

	/**
	 * @param typeNum
	 */
	public VehicleTypeBallista(int typeNum) {
		super(typeNum);
		this.configName = "ballista_base";

		this.vehicleMaterial = VehicleMaterial.materialWood;
		this.materialCount = 5;

		this.maxMissileWeight = 2.f;

		this.validAmmoTypes.add(AmmoRegistry.ammoBallistaBolt);
		this.validAmmoTypes.add(AmmoRegistry.ammoBallistaBoltFlame);
		this.validAmmoTypes.add(AmmoRegistry.ammoBallistaBoltExplosive);
		this.validAmmoTypes.add(AmmoRegistry.ammoBallistaBoltIron);

		this.ammoBySoldierRank.put(0, AmmoRegistry.ammoBallistaBolt);
		this.ammoBySoldierRank.put(1, AmmoRegistry.ammoBallistaBolt);
		this.ammoBySoldierRank.put(2, AmmoRegistry.ammoBallistaBoltFlame);

		this.validUpgrades.add(UpgradeRegistry.pitchDownUpgrade);
		this.validUpgrades.add(UpgradeRegistry.pitchUpUpgrade);
		this.validUpgrades.add(UpgradeRegistry.pitchExtUpgrade);
		this.validUpgrades.add(UpgradeRegistry.powerUpgrade);
		this.validUpgrades.add(UpgradeRegistry.reloadUpgrade);
		this.validUpgrades.add(UpgradeRegistry.aimUpgrade);

		this.validArmors.add(ArmorRegistry.armorStone);
		this.validArmors.add(ArmorRegistry.armorObsidian);
		this.validArmors.add(ArmorRegistry.armorIron);

		this.storageBaySize = 0;
		this.accuracy = 0.98f;
		this.baseStrafeSpeed = 1.5f;
		this.baseForwardSpeed = 4.f * 0.05f;
		this.basePitchMax = 15;
		this.basePitchMin = -15;
		this.mountable = true;
		this.combatEngine = true;
		this.pitchAdjustable = true;
		this.powerAdjustable = false;

		/**
		 * default values that should be overriden by ballista types...
		 */
		this.baseMissileVelocityMax = 42.f;//stand versions should have higher velocity, as should fixed version--i.e. mobile turret should have the worst of all versions
		this.width = 2;
		this.height = 2;

		this.armorBaySize = 3;
		this.upgradeBaySize = 3;
		this.ammoBaySize = 6;

		this.drivable = false;//adjust based on isMobile or not
		this.yawAdjustable = false;//adjust based on hasTurret or not
		this.turretRotationMax = 360.f;//adjust based on mobile fixed (0), stand fixed(90'), or mobile or stand turret (360)
/* TODO vehicle recipe
		this.addNeededResearchForMaterials();
		this.addNeededResearch(0, ResearchGoal.vehicleTorsion1);
		this.addNeededResearch(1, ResearchGoal.vehicleTorsion2);
		this.addNeededResearch(2, ResearchGoal.vehicleTorsion3);
		this.addNeededResearch(3, ResearchGoal.vehicleTorsion4);
		this.addNeededResearch(4, ResearchGoal.vehicleTorsion5);

		this.additionalMaterials.add(new ItemStackWrapperCrafting(Item.silk, 8, false, false));
		this.additionalMaterials.add(new ItemStackWrapperCrafting(ItemLoader.torsionUnit, 2, false, false));
		this.additionalMaterials.add(new ItemStackWrapperCrafting(ItemLoader.equipmentBay, 1, false, false));
*/
	}

	@Override
	public VehicleFiringVarsHelper getFiringVarsHelper(VehicleBase veh) {
		return new BallistaVarHelper(veh);
	}

}
