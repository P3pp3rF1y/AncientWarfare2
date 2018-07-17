package net.shadowmage.ancientwarfare.vehicle.entity.types;

import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.vehicle.registry.UpgradeRegistry;

public class VehicleTypeCatapultMobileTurret extends VehicleTypeCatapult {
	public VehicleTypeCatapultMobileTurret(int typeNum) {
		super(typeNum);
		this.configName = "catapult_mobile_turret";
		this.width = 2.7f;
		this.height = 2;
		this.baseStrafeSpeed = 1.5f;
		this.baseForwardSpeed = 4.0f * 0.05f;
		this.baseMissileVelocityMax = 30.f;
		this.turretVerticalOffset = 15 * 0.0625f;
		this.riderForwardsOffset = 0.8f;
		this.riderVerticalOffset = 0.65f;
		this.displayName = "item.vehicleSpawner.3";
		this.displayTooltip.add("item.vehicleSpawner.tooltip.torsion");
		this.displayTooltip.add("item.vehicleSpawner.tooltip.mobile");
		this.displayTooltip.add("item.vehicleSpawner.tooltip.fullturret");
		this.storageBaySize = 0;
		this.armorBaySize = 3;
		this.upgradeBaySize = 3;
		this.yawAdjustable = true;
		this.drivable = true;
		this.riderSits = true;
		this.riderMovesWithTurret = true;
		this.turretRotationMax = 180.f;
		this.validUpgrades.add(UpgradeRegistry.speedUpgrade);
	}

	@Override
	public ResourceLocation getTextureForMaterialLevel(int level) {
		switch (level) {
			case 0:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/catapult_mobile_turret_1.png");
			case 1:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/catapult_mobile_turret_2.png");
			case 2:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/catapult_mobile_turret_3.png");
			case 3:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/catapult_mobile_turret_4.png");
			case 4:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/catapult_mobile_turret_5.png");
			default:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/catapult_mobile_turret_1.png");
		}
	}
}
