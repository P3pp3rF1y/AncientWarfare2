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

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.vehicle.config.AWVehicleStatics;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.entity.materials.VehicleMaterial;
import net.shadowmage.ancientwarfare.vehicle.helpers.VehicleFiringVarsHelper;
import net.shadowmage.ancientwarfare.vehicle.registry.AmmoRegistry;
import net.shadowmage.ancientwarfare.vehicle.registry.ArmorRegistry;
import net.shadowmage.ancientwarfare.vehicle.registry.UpgradeRegistry;

public class VehicleTypeTrebuchet extends VehicleType {
	/**
	 * @param typeNum
	 */
	public VehicleTypeTrebuchet(int typeNum) {
		super(typeNum);
		this.configName = "trebuchet_base";
		this.vehicleMaterial = VehicleMaterial.materialWood;
		this.materialCount = 8;
		this.maxMissileWeight = 10.f;

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
		this.validArmors.add(ArmorRegistry.armorIron);
		this.validArmors.add(ArmorRegistry.armorObsidian);

		this.validUpgrades.add(UpgradeRegistry.pitchDownUpgrade);
		this.validUpgrades.add(UpgradeRegistry.pitchUpUpgrade);
		this.validUpgrades.add(UpgradeRegistry.powerUpgrade);
		this.validUpgrades.add(UpgradeRegistry.reloadUpgrade);
		this.validUpgrades.add(UpgradeRegistry.aimUpgrade);

		this.displayName = "Trebuchet";
		//  this.displayTooltip.add("Trebuchet Firing Mechanism");
		//  this.displayTooltip.add("Fixed-Emplacement");
		//  this.displayTooltip.add("No Turret");
		this.pitchAdjustable = false;
		this.powerAdjustable = true;
		this.yawAdjustable = false;
		this.mountable = true;
		this.combatEngine = true;
		this.drivable = true;
		this.riderSits = false;
		this.riderMovesWithTurret = false;
		this.baseForwardSpeed = 0.f;
		this.baseStrafeSpeed = 0.5f;
		this.ammoBaySize = 6;
		this.armorBaySize = 3;
		this.upgradeBaySize = 3;
		this.storageBaySize = 0;
		this.accuracy = 0.85f;

		this.basePitchMax = 70.f;
		this.basePitchMin = 70.f;
		this.turretVerticalOffset = (34.f + 67.5f + 24.0f) * 0.0625f;
		this.baseMissileVelocityMax = 40.f;
/* TODO vehicle recipe
		this.addNeededResearchForMaterials();
		this.addNeededResearch(0, ResearchGoal.vehicleCounterweights1);
		this.addNeededResearch(1, ResearchGoal.vehicleCounterweights2);
		this.addNeededResearch(2, ResearchGoal.vehicleCounterweights3);
		this.addNeededResearch(3, ResearchGoal.vehicleCounterweights4);
		this.addNeededResearch(4, ResearchGoal.vehicleCounterweights5);
		this.additionalMaterials.add(new ItemStackWrapperCrafting(Item.silk, 8, false, false));
		this.additionalMaterials.add(new ItemStackWrapperCrafting(Item.leather, 4, false, false));
		this.additionalMaterials.add(new ItemStackWrapperCrafting(ItemLoader.counterWeightUnit, 4, false, false));
		this.additionalMaterials.add(new ItemStackWrapperCrafting(ItemLoader.equipmentBay, 1, false, false));
*/
	}

	@Override
	public ResourceLocation getTextureForMaterialLevel(int level) {
		switch (level) {
			case 0:
				return new ResourceLocation(AncientWarfareCore.modID, "model/vehicle/trebuchet1");
			case 1:
				return new ResourceLocation(AncientWarfareCore.modID, "model/vehicle/trebuchet2");
			case 2:
				return new ResourceLocation(AncientWarfareCore.modID, "model/vehicle/trebuchet3");
			case 3:
				return new ResourceLocation(AncientWarfareCore.modID, "model/vehicle/trebuchet4");
			case 4:
				return new ResourceLocation(AncientWarfareCore.modID, "model/vehicle/trebuchet5");
			default:
				return new ResourceLocation(AncientWarfareCore.modID, "model/vehicle/trebuchet1");
		}
	}

	@Override
	public VehicleFiringVarsHelper getFiringVarsHelper(VehicleBase veh) {
		return new TrebuchetVarHelper(veh);
	}

	public class TrebuchetVarHelper extends VehicleFiringVarsHelper {

		float armAngle = -27.f;
		float armSpeed = 0.f;
		float stringAngle = -64.f;
		float stringSpeed = 0.f;

		/**
		 * @param vehicle
		 */
		public TrebuchetVarHelper(VehicleBase vehicle) {
			super(vehicle);
		}

		@Override
		public NBTTagCompound serializeNBT() {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setFloat("aA", this.armAngle);
			tag.setFloat("aS", armSpeed);
			tag.setFloat("sA", stringAngle);
			tag.setFloat("sS", stringSpeed);
			return tag;
		}

		@Override
		public void deserializeNBT(NBTTagCompound tag) {
			this.armAngle = tag.getFloat("aA");
			this.armSpeed = tag.getFloat("aS");
			this.stringAngle = tag.getFloat("sA");
			this.stringSpeed = tag.getFloat("sS");
		}

		@Override
		public void onFiringUpdate() {
			float increment = (90.f + 27.f) / 20.f;
			armAngle += increment;
			armSpeed = increment;
			stringAngle += 1.3162316f * increment;
			stringSpeed = 1.3162316f * increment;
			if (armAngle >= 90) {
				armSpeed = 0;
				armAngle = 90.f;
				stringAngle = 90.f;
				stringSpeed = 0.f;
				vehicle.firingHelper.startLaunching();
			}
		}

		@Override
		public void onReloadUpdate() {
			float increment = (90.f + 27.f) / (float) vehicle.currentReloadTicks;
			if (armAngle > -27) {
				armAngle -= increment;
				armSpeed = -increment;
				stringAngle -= 1.3162316f * increment;
				stringSpeed = -1.3162316f * increment;
			} else {
				armAngle = -27;
				armSpeed = 0;
				stringAngle = -64.f;
				stringSpeed = 0.f;
			}
		}

		@Override
		public void onLaunchingUpdate() {
			vehicle.firingHelper.spawnMissilesByWeightCount(0, 0, 0);
			vehicle.firingHelper.setFinishedLaunching();
		}

		@Override
		public void onReloadingFinished() {
			armAngle = -27;
			armSpeed = 0;
			stringAngle = -64.f;
			stringSpeed = 0.f;
		}

		@Override
		public float getVar1() {
			return armAngle;
		}

		@Override
		public float getVar2() {
			return armSpeed;
		}

		@Override
		public float getVar3() {
			return stringAngle;
		}

		@Override
		public float getVar4() {
			return stringSpeed;
		}

		@Override
		public float getVar5() {
			return 0;
		}

		@Override
		public float getVar6() {
			return 0;
		}

		@Override
		public float getVar7() {
			return 0;
		}

		@Override
		public float getVar8() {
			return 0;
		}
	}

}
