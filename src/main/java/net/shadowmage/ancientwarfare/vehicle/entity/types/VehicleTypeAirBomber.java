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
import net.shadowmage.ancientwarfare.vehicle.registry.UpgradeRegistry;

public class VehicleTypeAirBomber extends VehicleType {
	public VehicleTypeAirBomber(int typeNum) {
		super(typeNum);
		this.configName = "aircraft_bomber";
		this.vehicleMaterial = VehicleMaterial.materialWood;
		this.materialCount = 4;
		this.movementType = VehicleMovementType.AIR1;
		this.maxMissileWeight = 20.f;
		this.accuracy = 0.7f;

		this.validAmmoTypes.add(AmmoRegistry.ammoStoneShot10);
		this.validAmmoTypes.add(AmmoRegistry.ammoStoneShot15);
		this.validAmmoTypes.add(AmmoRegistry.ammoStoneShot30);
		this.validAmmoTypes.add(AmmoRegistry.ammoFireShot10);
		this.validAmmoTypes.add(AmmoRegistry.ammoFireShot15);
		this.validAmmoTypes.add(AmmoRegistry.ammoFireShot30);
		this.validAmmoTypes.add(AmmoRegistry.ammoExplosive10);
		this.validAmmoTypes.add(AmmoRegistry.ammoExplosive15);
		this.validAmmoTypes.add(AmmoRegistry.ammoExplosive30);
		this.validAmmoTypes.add(AmmoRegistry.ammoHE10);
		this.validAmmoTypes.add(AmmoRegistry.ammoHE15);
		this.validAmmoTypes.add(AmmoRegistry.ammoHE30);
		this.validAmmoTypes.add(AmmoRegistry.ammoNapalm10);
		this.validAmmoTypes.add(AmmoRegistry.ammoNapalm15);

		this.validAmmoTypes.add(AmmoRegistry.ammoBallistaBolt);
		this.validAmmoTypes.add(AmmoRegistry.ammoBallistaBoltFlame);
		this.validAmmoTypes.add(AmmoRegistry.ammoBallistaBoltExplosive);
		this.validAmmoTypes.add(AmmoRegistry.ammoBallistaBoltIron);

		this.ammoBySoldierRank.put(0, AmmoRegistry.ammoBallistaBolt);
		this.ammoBySoldierRank.put(1, AmmoRegistry.ammoBallistaBolt);
		this.ammoBySoldierRank.put(2, AmmoRegistry.ammoBallistaBoltFlame);

		this.validUpgrades.add(UpgradeRegistry.speedUpgrade);
		this.validUpgrades.add(UpgradeRegistry.reloadUpgrade);

		this.armorBaySize = 0;
		this.upgradeBaySize = 6;
		this.ammoBaySize = 2;
		this.storageBaySize = 0;

		this.baseHealth = 50;

		this.width = 2.7f;
		this.height = 1.4f;

		this.baseStrafeSpeed = 2.f;
		this.baseForwardSpeed = 20f * 0.05f;

		this.turretForwardsOffset = 0.f;
		this.missileVerticalOffset = 0.65f;
		this.basePitchMax = -90;
		this.basePitchMin = -90;
		this.baseMissileVelocityMax = 5.f;

		this.riderForwardsOffset = -0.0625f * 7;
		this.riderVerticalOffset = 0.0625f * 12 - 0.15f;
		this.riderSits = true;

		this.mountable = true;
		this.drivable = true;//adjust based on isMobile or not
		this.combatEngine = true;
		this.pilotableBySoldiers = false;

		this.pitchAdjustable = true;
		this.powerAdjustable = false;
		this.yawAdjustable = false;
		this.turretRotationMax = 0.f;//adjust based on mobile/stand fixed (0), stand fixed(90'), or mobile or stand turret (360)
		this.displayName = "item.vehicleSpawner.21";
		this.displayTooltip.add("item.vehicleSpawner.tooltip.gunpowder");
		this.displayTooltip.add("item.vehicleSpawner.tooltip.air");
		this.displayTooltip.add("item.vehicleSpawner.tooltip.noturret");

		this.enabled = false;
	}

	@Override
	public ResourceLocation getTextureForMaterialLevel(int level) {
		switch (level) {
			case 0:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/airplane_1.png");
			case 1:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/airplane_2.png");
			case 2:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/airplane_3.png");
			case 3:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/airplane_4.png");
			case 4:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/airplane_5.png");
			default:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/airplane_1.png");
		}
	}

	@Override
	public VehicleFiringVarsHelper getFiringVarsHelper(VehicleBase veh) {
		return new AircraftVarsHelper(veh);
	}

	public class AircraftVarsHelper extends VehicleFiringVarsHelper {

		private int missileFired = 0;
		private int maxMissiles = 0;
		private int delayTick = 0;

		private AircraftVarsHelper(VehicleBase vehicle) {
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
			this.maxMissiles = vehicle.firingHelper.getMissileLaunchCount();
			vehicle.firingHelper.startLaunching();
		}

		@Override
		public void onReloadUpdate() {
			//noop
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
