package net.shadowmage.ancientwarfare.vehicle.entity.types;

import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;

public class VehicleTypeBallistaStand extends VehicleTypeBallista {
	public VehicleTypeBallistaStand(int typeNum) {
		super(typeNum);
		configName = "ballista_stand";
		baseMissileVelocityMax = 45.f;//stand versions should have higher velocity, as should fixed version--i.e. mobile turret should have the worst of all versions
		width = 1.2f;
		height = 1.4f;

		armorBaySize = 4;
		upgradeBaySize = 4;

		//20 units vertical, 0 forwards
		turretVerticalOffset = 18.f * 0.0625f;
		riderForwardsOffset = -1.8f;
		riderVerticalOffset = 0.35f;
		riderSits = false;
		drivable = true;//adjust based on isMobile or not
		baseForwardSpeed = 0.f;
		baseStrafeSpeed = 1f;
		yawAdjustable = false;//adjust based on hasTurret or not
		turretRotationMax = 0.f;//adjust based on mobile/stand fixed (0), stand fixed(90'), or mobile or stand turret (360)
		displayName = "item.vehicleSpawner.4";
		displayTooltip.add("item.vehicleSpawner.tooltip.torsion");
		displayTooltip.add("item.vehicleSpawner.tooltip.fixed");
		displayTooltip.add("item.vehicleSpawner.tooltip.noturret");
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
