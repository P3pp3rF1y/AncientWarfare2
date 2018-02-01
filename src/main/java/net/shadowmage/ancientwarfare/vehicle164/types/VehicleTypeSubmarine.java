/**
 * Copyright 2012-2013 John Cummens (aka Shadowmage, Shadowmage4513)
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
import net.minecraft.nbt.NBTTagCompound;
import shadowmage.ancient_warfare.common.config.Config;
import shadowmage.ancient_warfare.common.item.ItemLoader;
import shadowmage.ancient_warfare.common.registry.ArmorRegistry;
import shadowmage.ancient_warfare.common.registry.VehicleUpgradeRegistry;
import shadowmage.ancient_warfare.common.research.ResearchGoal;
import shadowmage.ancient_warfare.common.utils.ItemStackWrapperCrafting;
import shadowmage.ancient_warfare.common.vehicles.VehicleBase;
import shadowmage.ancient_warfare.common.vehicles.VehicleMovementType;
import shadowmage.ancient_warfare.common.vehicles.helpers.VehicleFiringVarsHelper;
import shadowmage.ancient_warfare.common.vehicles.materials.VehicleMaterial;
import shadowmage.ancient_warfare.common.vehicles.missiles.Ammo;

public class VehicleTypeSubmarine extends VehicleType {

	/**
	 * @param typeNum
	 */
	public VehicleTypeSubmarine(int typeNum) {
		super(typeNum);
		this.configName = "submarine";
		this.vehicleMaterial = VehicleMaterial.materialIron;
		this.materialCount = 5;
		this.movementType = VehicleMovementType.WATER2;

		this.maxMissileWeight = 15.f;

		this.validAmmoTypes.add(Ammo.ammoTorpedo10);
		this.validAmmoTypes.add(Ammo.ammoTorpedo15);
		this.validAmmoTypes.add(Ammo.ammoTorpedo30);
		this.validAmmoTypes.add(Ammo.ammoTorpedo45);

		this.ammoBySoldierRank.put(0, Ammo.ammoBallistaBolt);
		this.ammoBySoldierRank.put(1, Ammo.ammoBallistaBolt);
		this.ammoBySoldierRank.put(2, Ammo.ammoBallistaBoltFlame);

		this.validUpgrades.add(VehicleUpgradeRegistry.speedUpgrade);
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

		this.addNeededResearch(0, ResearchGoal.vehicleMobility3);
		this.addNeededResearch(1, ResearchGoal.vehicleMobility3);
		this.addNeededResearch(2, ResearchGoal.vehicleMobility4);
		this.addNeededResearch(3, ResearchGoal.vehicleMobility4);
		this.addNeededResearch(4, ResearchGoal.vehicleMobility5);

		this.addNeededResearch(0, ResearchGoal.upgradeMechanics3);
		this.addNeededResearch(1, ResearchGoal.upgradeMechanics3);
		this.addNeededResearch(2, ResearchGoal.upgradeMechanics4);
		this.addNeededResearch(3, ResearchGoal.upgradeMechanics4);
		this.addNeededResearch(4, ResearchGoal.upgradeMechanics5);

		this.additionalMaterials.add(new ItemStackWrapperCrafting(Item.silk, 8, false, false));
		this.additionalMaterials.add(new ItemStackWrapperCrafting(ItemLoader.torsionUnit, 2, false, false));
		this.additionalMaterials.add(new ItemStackWrapperCrafting(ItemLoader.equipmentBay, 1, false, false));
		this.additionalMaterials.add(new ItemStackWrapperCrafting(ItemLoader.mobilityUnit, 1, false, false));
		this.additionalMaterials.add(new ItemStackWrapperCrafting(Block.cactus, 2, false, false));

		this.width = 2.7f;
		this.height = 1.4f;

		this.baseStrafeSpeed = 1.2f;
		this.baseForwardSpeed = 3.7f * 0.05f;

		this.turretForwardsOffset = 2.45f;
		this.turretVerticalOffset = 0.325f;
		this.accuracy = 0.98f;
		this.basePitchMax = 0;
		this.basePitchMin = 0;
		this.baseMissileVelocityMax = 42.f;//stand versions should have higher velocity, as should fixed version--i.e. mobile turret should have the worst of all versions

		this.riderForwardsOffset = 0.75f;
		this.riderVerticalOffset = 0.1f;
		this.shouldRiderSit = true;

		this.isMountable = true;
		this.isDrivable = true;
		this.isCombatEngine = true;
		this.canSoldiersPilot = false;

		this.canAdjustPitch = false;
		this.canAdjustPower = false;
		this.canAdjustYaw = false;
		this.turretRotationMax = 0.f;//adjust based on mobile/stand fixed (0), stand fixed(90'), or mobile or stand turret (360)
		this.displayName = "item.vehicleSpawner.24";
		this.displayTooltip.add("item.vehicleSpawner.tooltip.torsion");
		this.displayTooltip.add("item.vehicleSpawner.tooltip.submarine");
		this.displayTooltip.add("item.vehicleSpawner.tooltip.noturret");

		this.enabled = false;
	}

	@Override
	public String getTextureForMaterialLevel(int level) {
		switch (level) {
			case 0:
				return Config.texturePath + "models/submarine1.png";
			case 1:
				return Config.texturePath + "models/submarine2.png";
			case 2:
				return Config.texturePath + "models/submarine3.png";
			case 3:
				return Config.texturePath + "models/submarine4.png";
			case 4:
				return Config.texturePath + "models/submarine5.png";
			default:
				return Config.texturePath + "models/submarine1.png";
		}
	}

	@Override
	public VehicleFiringVarsHelper getFiringVarsHelper(VehicleBase veh) {
		return new SubmarineVarsHelper(veh);
	}

	public class SubmarineVarsHelper extends VehicleFiringVarsHelper {
		/**
		 * @param vehicle
		 */
		public SubmarineVarsHelper(VehicleBase vehicle) {
			super(vehicle);
		}

		@Override
		public NBTTagCompound getNBTTag() {
			return new NBTTagCompound();
		}

		@Override
		public void readFromNBT(NBTTagCompound tag) {

		}

		@Override
		public void onFiringUpdate() {
			vehicle.firingHelper.startLaunching();
		}

		@Override
		public void onReloadUpdate() {

		}

		@Override
		public void onLaunchingUpdate() {
			if (!vehicle.worldObj.isRemote && vehicle.ammoHelper.getCurrentAmmoCount() > 0) {
				vehicle.worldObj.playSoundAtEntity(vehicle, "fireworks.launch", 1.0F, 0.5F);
			}
			vehicle.firingHelper.spawnMissile(0, 0, 0);
			vehicle.firingHelper.setFinishedLaunching();
		}

		@Override
		public void onReloadingFinished() {

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
