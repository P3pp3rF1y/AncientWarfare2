package net.shadowmage.ancientwarfare.vehicle.entity.types;

import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.vehicle.VehicleVarHelpers.BallistaVarHelper;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleMovementType;
import net.shadowmage.ancientwarfare.vehicle.entity.materials.VehicleMaterial;
import net.shadowmage.ancientwarfare.vehicle.helpers.VehicleFiringVarsHelper;
import net.shadowmage.ancientwarfare.vehicle.init.AWVehicleSounds;
import net.shadowmage.ancientwarfare.vehicle.registry.AmmoRegistry;
import net.shadowmage.ancientwarfare.vehicle.registry.ArmorRegistry;
import net.shadowmage.ancientwarfare.vehicle.registry.UpgradeRegistry;

public class VehicleTypeBoatBallista extends VehicleType {
	public VehicleTypeBoatBallista(int typeNum) {
		super(typeNum);
		configName = "boat_ballista";
		vehicleMaterial = VehicleMaterial.materialWood;
		materialCount = 5;
		movementType = VehicleMovementType.WATER;

		maxMissileWeight = 2.f;

		validAmmoTypes.add(AmmoRegistry.ammoBallistaBolt);
		validAmmoTypes.add(AmmoRegistry.ammoBallistaBoltFlame);
		validAmmoTypes.add(AmmoRegistry.ammoBallistaBoltExplosive);
		validAmmoTypes.add(AmmoRegistry.ammoBallistaBoltIron);

		ammoBySoldierRank.put(0, AmmoRegistry.ammoBallistaBolt);
		ammoBySoldierRank.put(1, AmmoRegistry.ammoBallistaBolt);
		ammoBySoldierRank.put(2, AmmoRegistry.ammoBallistaBoltFlame);

		validUpgrades.add(UpgradeRegistry.speedUpgrade);
		validUpgrades.add(UpgradeRegistry.pitchDownUpgrade);
		validUpgrades.add(UpgradeRegistry.pitchUpUpgrade);
		validUpgrades.add(UpgradeRegistry.pitchExtUpgrade);
		validUpgrades.add(UpgradeRegistry.powerUpgrade);
		validUpgrades.add(UpgradeRegistry.reloadUpgrade);
		validUpgrades.add(UpgradeRegistry.aimUpgrade);

		validArmors.add(ArmorRegistry.armorStone);
		validArmors.add(ArmorRegistry.armorObsidian);
		validArmors.add(ArmorRegistry.armorIron);

		armorBaySize = 3;
		upgradeBaySize = 3;
		ammoBaySize = 6;
		storageBaySize = 0;

		width = 2.7f;
		height = 1.4f;

		baseStrafeSpeed = 2.f;
		baseForwardSpeed = 6.2f * 0.05f;

		turretForwardsOffset = 23 * 0.0625f;
		turretVerticalOffset = 1.325f;
		accuracy = 0.98f;
		basePitchMax = 15;
		basePitchMin = -15;
		baseMissileVelocityMax = 42.f;//stand versions should have higher velocity, as should fixed version--i.e. mobile turret should have the worst of all versions

		riderForwardsOffset = -1.0f;
		riderVerticalOffset = 0.55f;
		riderSits = true;

		mountable = true;
		drivable = true;//adjust based on isMobile or not
		combatEngine = true;

		pitchAdjustable = true;
		powerAdjustable = false;
		yawAdjustable = true;
		turretRotationMax = 360.f;//adjust based on mobile/stand fixed (0), stand fixed(90'), or mobile or stand turret (360)
		displayName = "item.vehicleSpawner.18";
		displayTooltip.add("item.vehicleSpawner.tooltip.torsion");
		displayTooltip.add("item.vehicleSpawner.tooltip.boat");
		displayTooltip.add("item.vehicleSpawner.tooltip.fullturret");
	}

	@Override
	public ResourceLocation getTextureForMaterialLevel(int level) {
		switch (level) {
			case 0:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/boat_ballista_1.png");
			case 1:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/boat_ballista_2.png");
			case 2:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/boat_ballista_3.png");
			case 3:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/boat_ballista_4.png");
			case 4:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/boat_ballista_5.png");
			default:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/boat_ballista_1.png");
		}
	}

	@Override
	public VehicleFiringVarsHelper getFiringVarsHelper(VehicleBase veh) {
		return new BallistaVarHelper(veh);
	}

	@Override
	public void playReloadSound(VehicleBase vehicleBase) {
		vehicleBase.playSound(AWVehicleSounds.BALLISTA_RELOAD, 1, 1);
	}

	@Override
	public void playFiringSound(VehicleBase vehicleBase) {
		vehicleBase.playSound(AWVehicleSounds.BALLISTA_LAUNCH, 6, 1);
	}
}
