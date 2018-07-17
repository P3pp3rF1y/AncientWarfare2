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
import net.shadowmage.ancientwarfare.vehicle.registry.UpgradeRegistry;

public class VehicleTypeCannon extends VehicleType {
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

		this.validUpgrades.add(UpgradeRegistry.pitchDownUpgrade);
		this.validUpgrades.add(UpgradeRegistry.pitchUpUpgrade);
		this.validUpgrades.add(UpgradeRegistry.pitchExtUpgrade);
		this.validUpgrades.add(UpgradeRegistry.powerUpgrade);
		this.validUpgrades.add(UpgradeRegistry.reloadUpgrade);
		this.validUpgrades.add(UpgradeRegistry.aimUpgrade);

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
	}

	@Override
	public ResourceLocation getTextureForMaterialLevel(int level) {
		switch (level) {
			case 0:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/cannon_1.png");
			case 1:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/cannon_2.png");
			case 2:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/cannon_3.png");
			case 3:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/cannon_4.png");
			case 4:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/cannon_5.png");
			default:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/cannon_1.png");
		}
	}

	@Override
	public VehicleFiringVarsHelper getFiringVarsHelper(VehicleBase veh) {
		return new CannonVarHelper(veh);
	}

	public class CannonVarHelper extends VehicleFiringVarsHelper {

		private int firingTicks = 0;

		private CannonVarHelper(VehicleBase vehicle) {
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
			//noop
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
			//noop
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
