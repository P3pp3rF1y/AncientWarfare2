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
import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleMovementType;
import net.shadowmage.ancientwarfare.vehicle.entity.materials.VehicleMaterial;
import net.shadowmage.ancientwarfare.vehicle.helpers.VehicleFiringVarsHelper;
import net.shadowmage.ancientwarfare.vehicle.registry.AmmoRegistry;
import net.shadowmage.ancientwarfare.vehicle.registry.VehicleUpgradeRegistry;

public class VehicleTypeAirInterceptor extends VehicleType {

	/**
	 * @param typeNum
	 */
	public VehicleTypeAirInterceptor(int typeNum) {
		super(typeNum);
		this.configName = "aircraft_interceptor";
		this.vehicleMaterial = VehicleMaterial.materialWood;
		this.materialCount = 4;
		this.movementType = VehicleMovementType.AIR1;
		this.maxMissileWeight = 10.f;

		this.validAmmoTypes.add(AmmoRegistry.ammoPebbleShot10);

		this.validAmmoTypes.add(AmmoRegistry.ammoArrow);
		this.validAmmoTypes.add(AmmoRegistry.ammoArrowFlame);
		this.validAmmoTypes.add(AmmoRegistry.ammoArrowIron);
		this.validAmmoTypes.add(AmmoRegistry.ammoArrowIronFlame);

		this.validAmmoTypes.add(AmmoRegistry.ammoRocket);
		this.validAmmoTypes.add(AmmoRegistry.ammoHwachaRocketFlame);
		this.validAmmoTypes.add(AmmoRegistry.ammoHwachaRocketExplosive);
		this.validAmmoTypes.add(AmmoRegistry.ammoHwachaRocketAirburst);

		this.ammoBySoldierRank.put(0, AmmoRegistry.ammoArrow);
		this.ammoBySoldierRank.put(1, AmmoRegistry.ammoArrowFlame);
		this.ammoBySoldierRank.put(2, AmmoRegistry.ammoArrowIronFlame);

		this.validUpgrades.add(VehicleUpgradeRegistry.speedUpgrade);
		this.validUpgrades.add(VehicleUpgradeRegistry.reloadUpgrade);
		this.validUpgrades.add(VehicleUpgradeRegistry.powerUpgrade);
		this.validUpgrades.add(VehicleUpgradeRegistry.aimUpgrade);

		this.armorBaySize = 0;
		this.upgradeBaySize = 6;
		this.ammoBaySize = 2;
		this.storageBaySize = 0;

/* TODO vehicle recipe
		this.addNeededResearchForMaterials();

		this.addNeededResearch(0, ResearchGoal.vehicleMobility5);
		this.addNeededResearch(1, ResearchGoal.vehicleMobility5);
		this.addNeededResearch(2, ResearchGoal.vehicleMobility5);
		this.addNeededResearch(3, ResearchGoal.vehicleMobility5);
		this.addNeededResearch(4, ResearchGoal.vehicleMobility5);

		this.addNeededResearch(0, ResearchGoal.vehicleGunpowderWeapons5);
		this.addNeededResearch(1, ResearchGoal.vehicleGunpowderWeapons5);
		this.addNeededResearch(2, ResearchGoal.vehicleGunpowderWeapons5);
		this.addNeededResearch(3, ResearchGoal.vehicleGunpowderWeapons5);
		this.addNeededResearch(4, ResearchGoal.vehicleGunpowderWeapons5);

		this.addNeededResearch(0, ResearchGoal.upgradeMechanics5);
		this.addNeededResearch(1, ResearchGoal.upgradeMechanics5);
		this.addNeededResearch(2, ResearchGoal.upgradeMechanics5);
		this.addNeededResearch(3, ResearchGoal.upgradeMechanics5);
		this.addNeededResearch(4, ResearchGoal.upgradeMechanics5);

		this.additionalMaterials.add(new ItemStackWrapperCrafting(Item.silk, 8, false, false));
		this.additionalMaterials.add(new ItemStackWrapperCrafting(ItemLoader.powderCase, 2, false, false));
		this.additionalMaterials.add(new ItemStackWrapperCrafting(ItemLoader.equipmentBay, 1, false, false));
		this.additionalMaterials.add(new ItemStackWrapperCrafting(ItemLoader.mobilityUnit, 2, false, false));
		this.additionalMaterials.add(new ItemStackWrapperCrafting(Block.cloth, 10, true, false));
*/

		this.baseHealth = 50;

		this.width = 2.7f;
		this.height = 1.4f;

		this.baseStrafeSpeed = 2.f;
		this.baseForwardSpeed = 25f * 0.05f;

		this.turretForwardsOffset = 0.f;
		this.turretVerticalOffset = 0.f;
		this.missileVerticalOffset = 0.25f;
		this.accuracy = 0.94f;
		this.basePitchMax = 0;
		this.basePitchMin = 0;
		this.baseMissileVelocityMax = 50.f;

		this.riderForwardsOffset = -0.0625f * 7;
		this.riderVerticalOffset = 0.0625f * 12;
		this.riderSits = true;

		this.mountable = true;
		this.drivable = true;//adjust based on isMobile or not
		this.combatEngine = true;
		this.pilotableBySoldiers = false;

		this.pitchAdjustable = true;
		this.powerAdjustable = false;
		this.yawAdjustable = false;
		this.turretRotationMax = 0.f;//adjust based on mobile/stand fixed (0), stand fixed(90'), or mobile or stand turret (360)
		this.displayName = "item.vehicleSpawner.22";
		this.displayTooltip.add("item.vehicleSpawner.tooltip.gunpowder");
		this.displayTooltip.add("item.vehicleSpawner.tooltip.air");
		this.displayTooltip.add("item.vehicleSpawner.tooltip.noturret");

		this.enabled = false;
	}

	@Override
	public ResourceLocation getTextureForMaterialLevel(int level) {
		switch (level) {
			case 0:
				return new ResourceLocation(AncientWarfareCore.modID, "model/vehicle/airplane1");
			case 1:
				return new ResourceLocation(AncientWarfareCore.modID, "model/vehicle/airplane2");
			case 2:
				return new ResourceLocation(AncientWarfareCore.modID, "model/vehicle/airplane3");
			case 3:
				return new ResourceLocation(AncientWarfareCore.modID, "model/vehicle/airplane4");
			case 4:
				return new ResourceLocation(AncientWarfareCore.modID, "model/vehicle/airplane5");
			default:
				return new ResourceLocation(AncientWarfareCore.modID, "model/vehicle/airplane1");
		}
	}

	@Override
	public VehicleFiringVarsHelper getFiringVarsHelper(VehicleBase veh) {
		return new AircraftVarsHelper(veh);
	}

	public class AircraftVarsHelper extends VehicleFiringVarsHelper {

		int missileFired = 0;
		int maxMissiles = 0;
		int delayTick = 0;

		/**
		 * @param vehicle
		 */
		public AircraftVarsHelper(VehicleBase vehicle) {
			super(vehicle);
		}

		@Override
		public NBTTagCompound serializeNBT() {
			return new NBTTagCompound();
		}

		@Override
		public void deserializeNBT(NBTTagCompound tag) {

		}

		@Override
		public void onFiringUpdate() {
			this.maxMissiles = vehicle.firingHelper.getMissileLaunchCount();
			vehicle.firingHelper.startLaunching();
		}

		@Override
		public void onReloadUpdate() {

		}

		@Override
		public void onLaunchingUpdate() {
			delayTick++;
			if (delayTick >= 2) {
				delayTick = 0;
				if (!vehicle.world.isRemote && vehicle.ammoHelper.getCurrentAmmoCount() > 0) {
					vehicle.playSound(SoundEvents.ENTITY_FIREWORK_LAUNCH, 1.0F, 0.5F);
				}
				vehicle.firingHelper.spawnMissile(0, 0, 0);
				this.missileFired++;
				if (missileFired >= maxMissiles) {
					vehicle.firingHelper.setFinishedLaunching();
				}
			}
		}

		@Override
		public void onReloadingFinished() {
			this.missileFired = 0;
			this.delayTick = 0;
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
