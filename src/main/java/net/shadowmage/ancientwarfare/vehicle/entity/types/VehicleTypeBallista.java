package net.shadowmage.ancientwarfare.vehicle.entity.types;

import net.shadowmage.ancientwarfare.vehicle.VehicleVarHelpers.BallistaVarHelper;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.entity.materials.VehicleMaterial;
import net.shadowmage.ancientwarfare.vehicle.helpers.VehicleFiringVarsHelper;
import net.shadowmage.ancientwarfare.vehicle.registry.AmmoRegistry;
import net.shadowmage.ancientwarfare.vehicle.registry.ArmorRegistry;
import net.shadowmage.ancientwarfare.vehicle.registry.UpgradeRegistry;

public abstract class VehicleTypeBallista extends VehicleType {

	public VehicleTypeBallista(int typeNum) {
		super(typeNum);
		configName = "ballista_base";

		vehicleMaterial = VehicleMaterial.materialWood;
		materialCount = 5;

		maxMissileWeight = 2.f;

		validAmmoTypes.add(AmmoRegistry.ammoBallistaBolt);
		validAmmoTypes.add(AmmoRegistry.ammoBallistaBoltFlame);
		validAmmoTypes.add(AmmoRegistry.ammoBallistaBoltExplosive);
		validAmmoTypes.add(AmmoRegistry.ammoBallistaBoltIron);

		ammoBySoldierRank.put(0, AmmoRegistry.ammoBallistaBolt);
		ammoBySoldierRank.put(1, AmmoRegistry.ammoBallistaBolt);
		ammoBySoldierRank.put(2, AmmoRegistry.ammoBallistaBoltFlame);

		validUpgrades.add(UpgradeRegistry.pitchDownUpgrade);
		validUpgrades.add(UpgradeRegistry.pitchUpUpgrade);
		validUpgrades.add(UpgradeRegistry.pitchExtUpgrade);
		validUpgrades.add(UpgradeRegistry.powerUpgrade);
		validUpgrades.add(UpgradeRegistry.reloadUpgrade);
		validUpgrades.add(UpgradeRegistry.aimUpgrade);

		validArmors.add(ArmorRegistry.armorStone);
		validArmors.add(ArmorRegistry.armorObsidian);
		validArmors.add(ArmorRegistry.armorIron);

		storageBaySize = 0;
		accuracy = 0.98f;
		baseStrafeSpeed = 2.25f;
		baseForwardSpeed = 4.f * 0.05f;
		basePitchMax = 15;
		basePitchMin = -15;
		mountable = true;
		combatEngine = true;
		pitchAdjustable = true;
		powerAdjustable = false;

		/*
		 * default values that should be overriden by ballista types...
		 */
		baseMissileVelocityMax = 42.f;//stand versions should have higher velocity, as should fixed version--i.e. mobile turret should have the worst of all versions
		width = 2;
		height = 2;

		armorBaySize = 3;
		upgradeBaySize = 3;
		ammoBaySize = 6;

		drivable = false;//adjust based on isMobile or not
		yawAdjustable = false;//adjust based on hasTurret or not
		turretRotationMax = 360.f;//adjust based on mobile fixed (0), stand fixed(90'), or mobile or stand turret (360)
	}

	@Override
	public VehicleFiringVarsHelper getFiringVarsHelper(VehicleBase veh) {
		return new BallistaVarHelper(veh);
	}

}
