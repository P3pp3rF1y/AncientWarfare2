package net.shadowmage.ancientwarfare.vehicle.entity.types;

import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;

public class VehicleTypeCatapultStandFixed extends VehicleTypeCatapult {
	public VehicleTypeCatapultStandFixed(int typeNum) {
		super(typeNum);
		this.configName = "catapult_stand";
		this.width = 2;
		this.height = 1.7f;
		this.baseMissileVelocityMax = 37.f;
		this.turretVerticalOffset = 3 * 0.0625f;
		this.riderForwardsOffset = 1.2f;
		this.riderVerticalOffset = 0.0f;
		this.displayName = "item.vehicleSpawner.0";
		this.displayTooltip.add("item.vehicleSpawner.tooltip.torsion");
		this.displayTooltip.add("item.vehicleSpawner.tooltip.fixed");
		this.displayTooltip.add("item.vehicleSpawner.tooltip.noturret");
		this.storageBaySize = 0;
		this.armorBaySize = 4;
		this.upgradeBaySize = 4;
		this.yawAdjustable = false;
		this.drivable = true;
		this.baseForwardSpeed = 0.f;
		this.baseStrafeSpeed = .5f;
		this.riderSits = true;
		this.riderMovesWithTurret = false;
	}

	@Override
	public ResourceLocation getTextureForMaterialLevel(int level) {
		switch (level) {
			case 0:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/catapult_stand_fixed_1.png");
			case 1:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/catapult_stand_fixed_2.png");
			case 2:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/catapult_stand_fixed_3.png");
			case 3:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/catapult_stand_fixed_4.png");
			case 4:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/catapult_stand_fixed_5.png");
			default:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/catapult_stand_fixed_1.png");
		}
	}

}
