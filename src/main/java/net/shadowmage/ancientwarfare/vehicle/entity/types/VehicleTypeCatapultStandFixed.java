package net.shadowmage.ancientwarfare.vehicle.entity.types;

import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;

public class VehicleTypeCatapultStandFixed extends VehicleTypeCatapult {
	public VehicleTypeCatapultStandFixed(int typeNum) {
		super(typeNum);
		configName = "catapult_stand";
		width = 2;
		height = 1.7f;
		baseMissileVelocityMax = 37.f;
		turretVerticalOffset = 3 * 0.0625f;
		riderForwardsOffset = 1.2f;
		riderVerticalOffset = 0.0f;
		displayName = "item.vehicleSpawner.0";
		displayTooltip.add("item.vehicleSpawner.tooltip.torsion");
		displayTooltip.add("item.vehicleSpawner.tooltip.fixed");
		displayTooltip.add("item.vehicleSpawner.tooltip.noturret");
		storageBaySize = 0;
		armorBaySize = 4;
		upgradeBaySize = 4;
		yawAdjustable = false;
		drivable = true;
		baseForwardSpeed = 0.f;
		baseStrafeSpeed = .75f;
		riderSits = true;
		riderMovesWithTurret = false;
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
