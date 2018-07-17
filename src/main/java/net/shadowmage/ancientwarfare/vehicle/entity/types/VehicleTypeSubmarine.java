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
import net.shadowmage.ancientwarfare.vehicle.registry.ArmorRegistry;
import net.shadowmage.ancientwarfare.vehicle.registry.UpgradeRegistry;

public class VehicleTypeSubmarine extends VehicleType {
	public VehicleTypeSubmarine(int typeNum) {
		super(typeNum);
		this.configName = "submarine";
		this.vehicleMaterial = VehicleMaterial.materialIron;
		this.materialCount = 5;
		this.movementType = VehicleMovementType.WATER2;

		this.maxMissileWeight = 15.f;

		this.validAmmoTypes.add(AmmoRegistry.ammoTorpedo10);
		this.validAmmoTypes.add(AmmoRegistry.ammoTorpedo15);
		this.validAmmoTypes.add(AmmoRegistry.ammoTorpedo30);
		this.validAmmoTypes.add(AmmoRegistry.ammoTorpedo45);

		this.ammoBySoldierRank.put(0, AmmoRegistry.ammoBallistaBolt);
		this.ammoBySoldierRank.put(1, AmmoRegistry.ammoBallistaBolt);
		this.ammoBySoldierRank.put(2, AmmoRegistry.ammoBallistaBoltFlame);

		this.validUpgrades.add(UpgradeRegistry.speedUpgrade);
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
		this.riderVerticalOffset = -0.05f;
		this.riderSits = true;

		this.mountable = true;
		this.drivable = true;
		this.combatEngine = true;
		this.pilotableBySoldiers = false;

		this.pitchAdjustable = false;
		this.powerAdjustable = false;
		this.yawAdjustable = false;
		this.turretRotationMax = 0.f;//adjust based on mobile/stand fixed (0), stand fixed(90'), or mobile or stand turret (360)
		this.displayName = "item.vehicleSpawner.24";
		this.displayTooltip.add("item.vehicleSpawner.tooltip.torsion");
		this.displayTooltip.add("item.vehicleSpawner.tooltip.submarine");
		this.displayTooltip.add("item.vehicleSpawner.tooltip.noturret");

		this.enabled = false;
	}

	@Override
	public ResourceLocation getTextureForMaterialLevel(int level) {
		switch (level) {
			case 0:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/submarine_1.png");
			case 1:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/submarine_2.png");
			case 2:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/submarine_3.png");
			case 3:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/submarine_4.png");
			case 4:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/submarine_5.png");
			default:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/submarine_1.png");
		}
	}

	@Override
	public VehicleFiringVarsHelper getFiringVarsHelper(VehicleBase veh) {
		return new SubmarineVarsHelper(veh);
	}

	public class SubmarineVarsHelper extends VehicleFiringVarsHelper {
		private SubmarineVarsHelper(VehicleBase vehicle) {
			super(vehicle);
		}

		@Override
		public NBTTagCompound serializeNBT() {
			return new NBTTagCompound();
		}

		@Override
		public void deserializeNBT(NBTTagCompound tag) {
			//noop
		}

		@Override
		public void onFiringUpdate() {
			vehicle.firingHelper.startLaunching();
		}

		@Override
		public void onReloadUpdate() {
			//noop
		}

		@Override
		public void onLaunchingUpdate() {
			if (!vehicle.world.isRemote && vehicle.ammoHelper.getCurrentAmmoCount() > 0) {
				vehicle.playSound(SoundEvents.ENTITY_FIREWORK_LAUNCH, 1.0F, 0.5F);
			}
			vehicle.firingHelper.spawnMissile(0, 0, 0);
			vehicle.firingHelper.setFinishedLaunching();
		}

		@Override
		public void onReloadingFinished() {
			//noop
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
