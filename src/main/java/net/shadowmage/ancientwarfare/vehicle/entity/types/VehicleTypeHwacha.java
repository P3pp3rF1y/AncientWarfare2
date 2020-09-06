package net.shadowmage.ancientwarfare.vehicle.entity.types;

import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.util.Trig;
import net.shadowmage.ancientwarfare.vehicle.config.AWVehicleStatics;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.entity.materials.VehicleMaterial;
import net.shadowmage.ancientwarfare.vehicle.helpers.VehicleFiringVarsHelper;
import net.shadowmage.ancientwarfare.vehicle.registry.AmmoRegistry;
import net.shadowmage.ancientwarfare.vehicle.registry.ArmorRegistry;
import net.shadowmage.ancientwarfare.vehicle.registry.UpgradeRegistry;

public class VehicleTypeHwacha extends VehicleType {
	public VehicleTypeHwacha(int typeNum) {
		super(typeNum);
		this.configName = "hwacha";
		this.vehicleMaterial = VehicleMaterial.materialWood;
		this.materialCount = 4;
		this.width = 1.5f;
		this.height = 1.8f;

		this.maxMissileWeight = 1.f;

		baseHealth = AWVehicleStatics.vehicleStats.vehicleHwachaHealth;

		this.validAmmoTypes.add(AmmoRegistry.ammoRocket);
		this.validAmmoTypes.add(AmmoRegistry.ammoHwachaRocketFlame);
		this.validAmmoTypes.add(AmmoRegistry.ammoHwachaRocketExplosive);
		this.validAmmoTypes.add(AmmoRegistry.ammoHwachaRocketAirburst);

		this.ammoBySoldierRank.put(0, AmmoRegistry.ammoRocket);
		this.ammoBySoldierRank.put(1, AmmoRegistry.ammoRocket);
		this.ammoBySoldierRank.put(2, AmmoRegistry.ammoRocket);

		this.validArmors.add(ArmorRegistry.armorStone);
		this.validArmors.add(ArmorRegistry.armorIron);
		this.validArmors.add(ArmorRegistry.armorObsidian);

		this.validUpgrades.add(UpgradeRegistry.aimUpgrade);
		this.validUpgrades.add(UpgradeRegistry.pitchDownUpgrade);
		this.validUpgrades.add(UpgradeRegistry.pitchUpUpgrade);
		this.validUpgrades.add(UpgradeRegistry.powerUpgrade);
		this.validUpgrades.add(UpgradeRegistry.reloadUpgrade);
		this.validUpgrades.add(UpgradeRegistry.speedUpgrade);

		this.mountable = true;
		this.drivable = true;
		this.riderSits = false;
		this.riderMovesWithTurret = false;
		this.combatEngine = true;
		this.powerAdjustable = true;
		this.pitchAdjustable = false;
		this.yawAdjustable = false;
		this.accuracy = 0.75f;
		this.baseStrafeSpeed = 1.f;
		this.baseForwardSpeed = 3.5f * 0.05f;
		this.basePitchMax = 39;
		this.basePitchMin = 39;
		this.turretRotationMax = 0.f;

		this.width = 2;
		this.height = 2;
		this.baseMissileVelocityMax = 42.f;

		this.turretVerticalOffset = 8 * 0.0625f;
		this.missileForwardsOffset = -0.9375f - 0.0625f;

		this.riderForwardsOffset = -1.4f;
		this.riderVerticalOffset = 0.35f;
		this.displayName = "item.vehicleSpawner.12";
		this.displayTooltip.add("item.vehicleSpawner.tooltip.gunpowder");
		this.displayTooltip.add("item.vehicleSpawner.tooltip.mobile");
		this.displayTooltip.add("item.vehicleSpawner.tooltip.noturret");
		this.displayTooltip.add("item.vehicleSpawner.tooltip.barrage");
		this.storageBaySize = 0;
		this.armorBaySize = 3;
		this.ammoBaySize = 6;
		this.upgradeBaySize = 3;
	}

	@Override
	public ResourceLocation getTextureForMaterialLevel(int level) {
		switch (level) {
			case 0:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/hwacha_1.png");
			case 1:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/hwacha_2.png");
			case 2:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/hwacha_3.png");
			case 3:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/hwacha_4.png");
			case 4:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/hwacha_5.png");
			default:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/hwacha_1.png");
		}
	}

	@Override
	public VehicleFiringVarsHelper getFiringVarsHelper(VehicleBase veh) {
		return new HwachaFiringVarsHelper(veh);
	}

	public class HwachaFiringVarsHelper extends VehicleFiringVarsHelper {
		private int missileFired = 0;
		private int delayTick = 0;

		private HwachaFiringVarsHelper(VehicleBase vehicle) {
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
			delayTick++;
			if (delayTick >= 5) {
				delayTick = 0;
				calcMissileOffset(missileFired);
				if (!vehicle.world.isRemote && vehicle.ammoHelper.getCurrentAmmoCount() > 0) {
					vehicle.playSound(SoundEvents.ENTITY_FIREWORK_LAUNCH, 1.0F, 0.5F);
				}
				vehicle.firingHelper.spawnMissile(missileOffsetX, missileOffsetY, missileOffsetZ);
				this.missileFired++;
				if (missileFired >= 36) {
					vehicle.firingHelper.setFinishedLaunching();
				}
			}
		}

		private float missileOffsetX;
		private float missileOffsetY;
		private float missileOffsetZ;

		private void calcMissileOffset(int missileNum) {
			int currentRow = missileNum / 9;
			int currentCol = missileNum % 9;
			float targetX = ((float) currentCol) * 0.0625f * 2.f;
			targetX -= 8.f * 0.0625f;
			float targetY = ((float) currentRow) * 0.0625f * 2.f;
			float targetZ = 0.f;
			float targetAngle = 0.f + vehicle.rotationYaw;
			float len = MathHelper.sqrt(targetX * targetX + targetZ * targetZ);
			missileOffsetX = Trig.cosDegrees(targetAngle) * len;
			if (targetX < 0) {
				missileOffsetX *= -1;
			}
			missileOffsetZ = -Trig.sinDegrees(targetAngle) * len;
			missileOffsetY = Trig.cosDegrees(vehicle.localTurretPitch) * targetY;
			missileOffsetZ += Trig.sinDegrees(vehicle.localTurretPitch) * targetY;
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
