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

import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.vehicle.config.AWVehicleStatics;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.entity.materials.VehicleMaterial;
import net.shadowmage.ancientwarfare.vehicle.helpers.VehicleFiringVarsHelper;
import net.shadowmage.ancientwarfare.vehicle.registry.AmmoRegistry;
import net.shadowmage.ancientwarfare.vehicle.registry.ArmorRegistry;
import net.shadowmage.ancientwarfare.vehicle.registry.VehicleUpgradeRegistry;

public class VehicleTypeCannon extends VehicleType {

	/**
	 * @param typeNum
	 */
	public VehicleTypeCannon(int typeNum) {
		super(typeNum);
		this.configName = "cannon_base";
		this.vehicleMaterial = VehicleMaterial.materialIron;
		this.materialCount = 5;
		this.maxMissileWeight = 10.f;

		this.validAmmoTypes.add(AmmoRegistry.ammoIronShot5);
		this.validAmmoTypes.add(AmmoRegistry.ammoIronShot10);
		this.validAmmoTypes.add(AmmoRegistry.ammoGrapeShot5);
		this.validAmmoTypes.add(AmmoRegistry.ammoGrapeShot10);
		this.validAmmoTypes.add(AmmoRegistry.ammoCanisterShot5);
		this.validAmmoTypes.add(AmmoRegistry.ammoCanisterShot10);

		if (AWVehicleStatics.oversizeAmmoEnabled) {
			this.validAmmoTypes.add(AmmoRegistry.ammoIronShot15);
			this.validAmmoTypes.add(AmmoRegistry.ammoIronShot25);
			this.validAmmoTypes.add(AmmoRegistry.ammoGrapeShot15);
			this.validAmmoTypes.add(AmmoRegistry.ammoGrapeShot25);
			this.validAmmoTypes.add(AmmoRegistry.ammoCanisterShot15);
			this.validAmmoTypes.add(AmmoRegistry.ammoCanisterShot25);
		}

		this.ammoBySoldierRank.put(0, AmmoRegistry.ammoIronShot5);
		this.ammoBySoldierRank.put(1, AmmoRegistry.ammoIronShot5);
		this.ammoBySoldierRank.put(2, AmmoRegistry.ammoIronShot5);

		this.validUpgrades.add(VehicleUpgradeRegistry.pitchDownUpgrade);
		this.validUpgrades.add(VehicleUpgradeRegistry.pitchUpUpgrade);
		this.validUpgrades.add(VehicleUpgradeRegistry.pitchExtUpgrade);
		this.validUpgrades.add(VehicleUpgradeRegistry.powerUpgrade);
		this.validUpgrades.add(VehicleUpgradeRegistry.reloadUpgrade);
		this.validUpgrades.add(VehicleUpgradeRegistry.aimUpgrade);

		this.validArmors.add(ArmorRegistry.armorStone);
		this.validArmors.add(ArmorRegistry.armorIron);
		this.validArmors.add(ArmorRegistry.armorObsidian);

		this.turretVerticalOffset = 11.5f * 0.0625f;
		this.storageBaySize = 0;
		this.accuracy = 0.98f;
		this.drivable = true;
		this.baseForwardSpeed = 0.f;
		this.baseStrafeSpeed = .5f;
		this.basePitchMax = 15;
		this.basePitchMin = -15;
		this.mountable = true;
		this.combatEngine = true;
		this.pitchAdjustable = true;
		this.powerAdjustable = false;
		this.yawAdjustable = false;

		this.baseMissileVelocityMax = 42.f;
		this.width = 2;
		this.height = 2;

		this.armorBaySize = 3;
		this.upgradeBaySize = 3;
		this.ammoBaySize = 6;
/* TODO vehicle recipe
		this.addNeededResearchForMaterials();
		this.addNeededResearch(0, ResearchGoal.vehicleGunpowderWeapons1);
		this.addNeededResearch(1, ResearchGoal.vehicleGunpowderWeapons2);
		this.addNeededResearch(2, ResearchGoal.vehicleGunpowderWeapons3);
		this.addNeededResearch(3, ResearchGoal.vehicleGunpowderWeapons4);
		this.addNeededResearch(4, ResearchGoal.vehicleGunpowderWeapons5);
		this.additionalMaterials.add(new ItemStackWrapperCrafting(ItemLoader.powderCase, 2, false, false));
		this.additionalMaterials.add(new ItemStackWrapperCrafting(ItemLoader.equipmentBay, 1, false, false));
*/
	}

	@Override
	public ResourceLocation getTextureForMaterialLevel(int level) {
		switch (level) {
			case 0:
				return new ResourceLocation(AncientWarfareCore.modID, "model/vehicle/cannon1");
			case 1:
				return new ResourceLocation(AncientWarfareCore.modID, "model/vehicle/cannon2");
			case 2:
				return new ResourceLocation(AncientWarfareCore.modID, "model/vehicle/cannon3");
			case 3:
				return new ResourceLocation(AncientWarfareCore.modID, "model/vehicle/cannon4");
			case 4:
				return new ResourceLocation(AncientWarfareCore.modID, "model/vehicle/cannon5");
			default:
				return new ResourceLocation(AncientWarfareCore.modID, "model/vehicle/cannon1");
		}
	}

	@Override
	public VehicleFiringVarsHelper getFiringVarsHelper(VehicleBase veh) {
		return new CannonVarHelper(veh);
	}

	public class CannonVarHelper extends VehicleFiringVarsHelper {

		int firingTicks = 0;

		/**
		 * @param vehicle
		 */
		public CannonVarHelper(VehicleBase vehicle) {
			super(vehicle);
		}

		@Override
		public NBTTagCompound serializeNBT() {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setInteger("fT", firingTicks);
			return tag;
		}

		@Override
		public void deserializeNBT(NBTTagCompound tag) {

		}

		@Override
		public void onFiringUpdate() {
			if (firingTicks == 0 && !vehicle.world.isRemote) {
				vehicle.playSound(SoundEvents.ENTITY_FIREWORK_LAUNCH, 0.50F, .25F);
				vehicle.playSound(SoundEvents.ENTITY_TNT_PRIMED, 1.0F, 0.5F);
			}
			firingTicks++;
			if (vehicle.world.isRemote) {
				//TODO offset
				vehicle.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, vehicle.posX, vehicle.posY + 1.2d, vehicle.posZ, 0.0D, 0.05D, 0.0D);
			}
			if (firingTicks > 10) {
				if (!vehicle.world.isRemote) {
					vehicle.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE, 1.f, 1.f);
				}
				this.vehicle.firingHelper.startLaunching();
				firingTicks = 0;
			}
		}

		@Override
		public void onReloadUpdate() {

		}

		@Override
		public void onLaunchingUpdate() {
			vehicle.firingHelper.spawnMissile(0, 0, 0);
			//TODO play explosion sound
			//TODO spawn particles for explosion in direction of missile flight @ end of barrel (translate/offset)
			vehicle.firingHelper.setFinishedLaunching();
		}

		@Override
		public void onReloadingFinished() {
			firingTicks = 0;
		}

		@Override
		public float getVar1() {
			return 0;
		}

		@Override
		public float getVar2() {
			return 0;
		}

		@Override
		public float getVar3() {
			return 0;
		}

		@Override
		public float getVar4() {
			return 0;
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
