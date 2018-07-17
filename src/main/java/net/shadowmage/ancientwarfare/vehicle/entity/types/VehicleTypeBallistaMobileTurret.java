package net.shadowmage.ancientwarfare.vehicle.entity.types;

import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.vehicle.registry.UpgradeRegistry;

public class VehicleTypeBallistaMobileTurret extends VehicleTypeBallista {
	public VehicleTypeBallistaMobileTurret(int typeNum) {
		super(typeNum);
		this.configName = "ballista_mobile_turret";
		this.baseMissileVelocityMax = 37.f;//stand versions should have higher velocity, as should fixed version--i.e. mobile turret should have the worst of all versions
		this.width = 2.7f;
		this.height = 1.4f;

		this.armorBaySize = 3;
		this.upgradeBaySize = 3;
		this.ammoBaySize = 6;

		this.turretForwardsOffset = 1.f;
		this.turretVerticalOffset = 1.2f;
		this.riderForwardsOffset = -1.2f;
		this.riderVerticalOffset = 0.55f;
		this.riderSits = true;

		this.drivable = true;//adjust based on isMobile or not
		this.yawAdjustable = true;//adjust based on hasTurret or not
		this.turretRotationMax = 360.f;//adjust based on mobile fixed (0), stand fixed(90'), or mobile or stand turret (360)
		this.displayName = "item.vehicleSpawner.7";
		this.displayTooltip.add("item.vehicleSpawner.tooltip.torsion");
		this.displayTooltip.add("item.vehicleSpawner.tooltip.mobile");
		this.displayTooltip.add("item.vehicleSpawner.tooltip.fullturret");

		this.validUpgrades.add(UpgradeRegistry.speedUpgrade);
	}

	@Override
	public ResourceLocation getTextureForMaterialLevel(int level) {
		switch (level) {
			case 0:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/ballista_mobile_1.png");
			case 1:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/ballista_mobile_2.png");
			case 2:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/ballista_mobile_3.png");
			case 3:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/ballista_mobile_4.png");
			case 4:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/ballista_mobile_5.png");
			default:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/ballista_mobile_1.png");
		}
	}

}
