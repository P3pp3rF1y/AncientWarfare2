package net.shadowmage.ancientwarfare.vehicle.entity.types;

import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;

public class VehicleTypeCatapultStandTurret extends VehicleTypeCatapult {
	public VehicleTypeCatapultStandTurret(int typeNum) {
		super(typeNum);
		configName = "catapult_stand_turret";
		width = 2.7f;
		height = 2.f;
		baseMissileVelocityMax = 32.f;
		turretVerticalOffset = 13 * 0.0625f;
		turretVerticalOffset = 0.4f;
		riderForwardsOffset = 1.2f;
		riderVerticalOffset = 0.55f;
		displayName = "item.vehicleSpawner.1";
		displayTooltip.add("item.vehicleSpawner.tooltip.torsion");
		displayTooltip.add("item.vehicleSpawner.tooltip.fixed");
		displayTooltip.add("item.vehicleSpawner.tooltip.midturret");
		storageBaySize = 0;
		armorBaySize = 4;
		upgradeBaySize = 4;
		yawAdjustable = true;
		baseForwardSpeed = 0.f;
		baseStrafeSpeed = .75f;
		turretRotationMax = 45.f;
		drivable = true;
		riderSits = true;
		riderMovesWithTurret = true;
	}

	@Override
	public ResourceLocation getTextureForMaterialLevel(int level) {
		switch (level) {
			case 0:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/catapult_stand_turret_1.png");
			case 1:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/catapult_stand_turret_2.png");
			case 2:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/catapult_stand_turret_3.png");
			case 3:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/catapult_stand_turret_4.png");
			case 4:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/catapult_stand_turret_5.png");
			default:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/catapult_stand_turret_1.png");
		}
	}
}
