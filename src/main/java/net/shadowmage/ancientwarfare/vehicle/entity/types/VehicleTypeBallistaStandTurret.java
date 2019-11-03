package net.shadowmage.ancientwarfare.vehicle.entity.types;

import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;

public class VehicleTypeBallistaStandTurret extends VehicleTypeBallista {
	public VehicleTypeBallistaStandTurret(int typeNum) {
		super(typeNum);
		configName = "ballista_stand_turret";
		baseMissileVelocityMax = 42.f;//stand versions should have higher velocity, as should fixed version--i.e. mobile turret should have the worst of all versions
		width = 1.2f;
		height = 1.4f;

		armorBaySize = 4;
		upgradeBaySize = 4;

		turretVerticalOffset = 18.f * 0.0625f;
		riderForwardsOffset = -1.8f;
		riderVerticalOffset = 0.35f;
		riderSits = false;
		drivable = true;//adjust based on isMobile or not
		baseForwardSpeed = 0.f;
		baseStrafeSpeed = 1f;
		riderMovesWithTurret = true;
		yawAdjustable = true;//adjust based on hasTurret or not
		turretRotationMax = 45.f;
		displayName = "item.vehicleSpawner.5";
		displayTooltip.add("item.vehicleSpawner.tooltip.torsion");
		displayTooltip.add("item.vehicleSpawner.tooltip.fixed");
		displayTooltip.add("item.vehicleSpawner.tooltip.midturret");
	}

	@Override
	public ResourceLocation getTextureForMaterialLevel(int level) {
		switch (level) {
			case 0:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/ballista_stand_1.png");
			case 1:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/ballista_stand_2.png");
			case 2:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/ballista_stand_3.png");
			case 3:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/ballista_stand_4.png");
			case 4:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/ballista_stand_5.png");
			default:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/ballista_stand_1.png");
		}
	}

}
