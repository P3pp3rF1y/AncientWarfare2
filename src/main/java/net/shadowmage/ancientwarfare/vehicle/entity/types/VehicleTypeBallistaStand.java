package net.shadowmage.ancientwarfare.vehicle.entity.types;

import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;

public class VehicleTypeBallistaStand extends VehicleTypeBallista {
	public VehicleTypeBallistaStand(int typeNum) {
		super(typeNum);
		this.configName = "ballista_stand";
		this.baseMissileVelocityMax = 45.f;//stand versions should have higher velocity, as should fixed version--i.e. mobile turret should have the worst of all versions
		this.width = 1.2f;
		this.height = 1.4f;

		this.armorBaySize = 4;
		this.upgradeBaySize = 4;

		//20 units vertical, 0 forwards
		this.turretVerticalOffset = 18.f * 0.0625f;
		this.riderForwardsOffset = -1.8f;
		this.riderVerticalOffset = 0.35f;
		this.riderSits = false;
		this.drivable = true;//adjust based on isMobile or not
		this.baseForwardSpeed = 0.f;
		this.baseStrafeSpeed = .5f;
		this.yawAdjustable = false;//adjust based on hasTurret or not
		this.turretRotationMax = 0.f;//adjust based on mobile/stand fixed (0), stand fixed(90'), or mobile or stand turret (360)
		this.displayName = "item.vehicleSpawner.4";
		this.displayTooltip.add("item.vehicleSpawner.tooltip.torsion");
		this.displayTooltip.add("item.vehicleSpawner.tooltip.fixed");
		this.displayTooltip.add("item.vehicleSpawner.tooltip.noturret");
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
