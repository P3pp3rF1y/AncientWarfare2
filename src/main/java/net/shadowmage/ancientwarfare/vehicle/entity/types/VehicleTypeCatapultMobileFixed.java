package net.shadowmage.ancientwarfare.vehicle.entity.types;

import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.vehicle.registry.UpgradeRegistry;

public class VehicleTypeCatapultMobileFixed extends VehicleTypeCatapult {
	public VehicleTypeCatapultMobileFixed(int typeNum) {
		super(typeNum);
		this.configName = "catapult_mobile";
		this.width = 2.7f;
		this.height = 2;
		this.baseStrafeSpeed = 1.7f;
		this.baseForwardSpeed = 4.2f * 0.05f;
		this.baseMissileVelocityMax = 32.f;
		this.turretVerticalOffset = 0.9375f;
		this.riderForwardsOffset = 1.2f;
		this.riderVerticalOffset = 0.55f;
		this.displayName = "item.vehicleSpawner.2";
		this.displayTooltip.add("item.vehicleSpawner.tooltip.torsion");
		this.displayTooltip.add("item.vehicleSpawner.tooltip.mobile");
		this.displayTooltip.add("item.vehicleSpawner.tooltip.noturret");
		this.storageBaySize = 0;
		this.armorBaySize = 3;
		this.upgradeBaySize = 3;
		this.yawAdjustable = false;
		this.drivable = true;
		this.riderSits = true;
		this.riderMovesWithTurret = false;
		this.validUpgrades.add(UpgradeRegistry.speedUpgrade);
	}

	@Override
	public ResourceLocation getTextureForMaterialLevel(int level) {
		switch (level) {
			case 0:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/catapult_mobile_fixed_1.png");
			case 1:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/catapult_mobile_fixed_2.png");
			case 2:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/catapult_mobile_fixed_3.png");
			case 3:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/catapult_mobile_fixed_4.png");
			case 4:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/catapult_mobile_fixed_5.png");
			default:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/catapult_mobile_fixed_1.png");
		}
	}
}
