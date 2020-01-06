package net.shadowmage.ancientwarfare.vehicle.entity.types;

import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.vehicle.VehicleVarHelpers.CatapultVarHelper;
import net.shadowmage.ancientwarfare.vehicle.config.AWVehicleStatics;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleMovementType;
import net.shadowmage.ancientwarfare.vehicle.entity.materials.VehicleMaterial;
import net.shadowmage.ancientwarfare.vehicle.helpers.VehicleFiringVarsHelper;
import net.shadowmage.ancientwarfare.vehicle.init.AWVehicleSounds;
import net.shadowmage.ancientwarfare.vehicle.registry.AmmoRegistry;
import net.shadowmage.ancientwarfare.vehicle.registry.ArmorRegistry;
import net.shadowmage.ancientwarfare.vehicle.registry.UpgradeRegistry;

public class VehicleTypeBoatCatapult extends VehicleType {
	public VehicleTypeBoatCatapult(int typeNum) {
		super(typeNum);
		configName = "boat_catapult";
		vehicleMaterial = VehicleMaterial.materialWood;
		materialCount = 5;
		movementType = VehicleMovementType.WATER;
		validAmmoTypes.add(AmmoRegistry.ammoStoneShot10);
		validAmmoTypes.add(AmmoRegistry.ammoStoneShot15);
		validAmmoTypes.add(AmmoRegistry.ammoFireShot10);
		validAmmoTypes.add(AmmoRegistry.ammoFireShot15);
		validAmmoTypes.add(AmmoRegistry.ammoPebbleShot10);
		validAmmoTypes.add(AmmoRegistry.ammoPebbleShot15);
		validAmmoTypes.add(AmmoRegistry.ammoClusterShot10);
		validAmmoTypes.add(AmmoRegistry.ammoClusterShot15);
		validAmmoTypes.add(AmmoRegistry.ammoExplosive10);
		validAmmoTypes.add(AmmoRegistry.ammoExplosive15);
		validAmmoTypes.add(AmmoRegistry.ammoHE10);
		validAmmoTypes.add(AmmoRegistry.ammoHE15);
		validAmmoTypes.add(AmmoRegistry.ammoNapalm10);
		validAmmoTypes.add(AmmoRegistry.ammoNapalm15);

		validAmmoTypes.add(AmmoRegistry.ammoArrow);
		validAmmoTypes.add(AmmoRegistry.ammoArrowFlame);
		validAmmoTypes.add(AmmoRegistry.ammoArrowIron);
		validAmmoTypes.add(AmmoRegistry.ammoArrowIronFlame);

		if (AWVehicleStatics.oversizeAmmoEnabled) {
			validAmmoTypes.add(AmmoRegistry.ammoStoneShot30);
			validAmmoTypes.add(AmmoRegistry.ammoStoneShot45);
			validAmmoTypes.add(AmmoRegistry.ammoFireShot30);
			validAmmoTypes.add(AmmoRegistry.ammoFireShot45);
			validAmmoTypes.add(AmmoRegistry.ammoPebbleShot30);
			validAmmoTypes.add(AmmoRegistry.ammoPebbleShot45);
			validAmmoTypes.add(AmmoRegistry.ammoClusterShot30);
			validAmmoTypes.add(AmmoRegistry.ammoClusterShot45);
			validAmmoTypes.add(AmmoRegistry.ammoExplosive30);
			validAmmoTypes.add(AmmoRegistry.ammoExplosive45);
			validAmmoTypes.add(AmmoRegistry.ammoHE30);
			validAmmoTypes.add(AmmoRegistry.ammoHE45);

		}

		ammoBySoldierRank.put(0, AmmoRegistry.ammoStoneShot10);
		ammoBySoldierRank.put(1, AmmoRegistry.ammoStoneShot10);
		ammoBySoldierRank.put(2, AmmoRegistry.ammoStoneShot10);

		validArmors.add(ArmorRegistry.armorStone);
		validArmors.add(ArmorRegistry.armorObsidian);
		validArmors.add(ArmorRegistry.armorIron);

		validUpgrades.add(UpgradeRegistry.aimUpgrade);
		validUpgrades.add(UpgradeRegistry.pitchDownUpgrade);
		validUpgrades.add(UpgradeRegistry.pitchUpUpgrade);
		validUpgrades.add(UpgradeRegistry.powerUpgrade);
		validUpgrades.add(UpgradeRegistry.speedUpgrade);
		validUpgrades.add(UpgradeRegistry.aimUpgrade);
		validUpgrades.add(UpgradeRegistry.reloadUpgrade);

		storageBaySize = 0;
		armorBaySize = 3;
		upgradeBaySize = 3;

		drivable = true;
		riderSits = true;
		riderMovesWithTurret = false;
		mountable = true;

		combatEngine = true;
		powerAdjustable = true;
		pitchAdjustable = false;
		yawAdjustable = false;

		width = 2.7f;
		height = 1.4f;

		baseStrafeSpeed = 2.f;
		baseForwardSpeed = 6.2f * 0.05f;

		accuracy = 0.95f;

		basePitchMax = 20;
		basePitchMin = 20;
		baseMissileVelocityMax = 32.f;
		maxMissileWeight = 10.f;

		missileVerticalOffset = 0;

		missileForwardsOffset = -37 * 0.0625f;
		turretVerticalOffset = 18 * 0.0625f;

		riderForwardsOffset = 1.10f;
		riderVerticalOffset = 0.55f;

		displayName = "item.vehicleSpawner.19";
		displayTooltip.add("item.vehicleSpawner.tooltip.torsion");
		displayTooltip.add("item.vehicleSpawner.tooltip.boat");
		displayTooltip.add("item.vehicleSpawner.tooltip.noturret");
	}

	@Override
	public ResourceLocation getTextureForMaterialLevel(int level) {
		switch (level) {
			case 0:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/boat_catapult_1.png");
			case 1:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/boat_catapult_2.png");
			case 2:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/boat_catapult_3.png");
			case 3:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/boat_catapult_4.png");
			case 4:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/boat_catapult_5.png");
			default:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/boat_catapult_1.png");
		}
	}

	@Override
	public VehicleFiringVarsHelper getFiringVarsHelper(VehicleBase veh) {
		return new CatapultVarHelper(veh);
	}

	@Override
	public void playFiringSound(VehicleBase vehicleBase) {
		vehicleBase.playSound(AWVehicleSounds.CATAPULT_LAUNCH, 6, 1);
	}

	@Override
	public void playReloadSound(VehicleBase vehicleBase) {
		vehicleBase.playSound(AWVehicleSounds.CATAPULT_RELOAD, 1, 1);
	}
}
