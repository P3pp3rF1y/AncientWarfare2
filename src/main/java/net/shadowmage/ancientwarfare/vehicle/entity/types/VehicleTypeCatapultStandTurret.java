package net.shadowmage.ancientwarfare.vehicle.entity.types;

import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;

public class VehicleTypeCatapultStandTurret extends VehicleTypeCatapult {
	public VehicleTypeCatapultStandTurret(int typeNum) {
		super(typeNum);
		this.configName = "catapult_stand_turret";
		this.width = 2.7f;
		this.height = 2.f;
		this.baseMissileVelocityMax = 32.f;
		this.turretVerticalOffset = 13 * 0.0625f;
		this.turretVerticalOffset = 0.4f;
		this.riderForwardsOffset = 1.2f;
		this.riderVerticalOffset = 0.55f;
		this.displayName = "item.vehicleSpawner.1";
		this.displayTooltip.add("item.vehicleSpawner.tooltip.torsion");
		this.displayTooltip.add("item.vehicleSpawner.tooltip.fixed");
		this.displayTooltip.add("item.vehicleSpawner.tooltip.midturret");
		this.storageBaySize = 0;
		this.armorBaySize = 4;
		this.upgradeBaySize = 4;
		this.yawAdjustable = true;
		this.baseForwardSpeed = 0.f;
		this.baseStrafeSpeed = .5f;
		this.turretRotationMax = 45.f;
		this.drivable = true;
		this.riderSits = true;
		this.riderMovesWithTurret = true;
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
