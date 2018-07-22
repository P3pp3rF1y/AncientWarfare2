package net.shadowmage.ancientwarfare.vehicle.entity.types;

import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;

public class VehicleTypeTrebuchetStandTurret extends VehicleTypeTrebuchet {
	public VehicleTypeTrebuchetStandTurret(int typeNum) {
		super(typeNum);
		this.configName = "trebuchet_stand_turret";
		this.displayName = "item.vehicleSpawner.14";
		this.displayTooltip.add("item.vehicleSpawner.tooltip.weight");
		this.displayTooltip.add("item.vehicleSpawner.tooltip.fixed");
		this.displayTooltip.add("item.vehicleSpawner.tooltip.midturret");
		this.width = 2.7f;
		this.height = 2.7f;
		this.yawAdjustable = true;
		this.riderMovesWithTurret = true;
		this.riderSits = true;
		this.turretRotationMax = 45;
		this.riderForwardsOffset = 1.275f;
		this.riderVerticalOffset = 0.55f;
		this.turretVerticalOffset = (34.f + 67.5f + 24.0f + 9.5f) * 0.0625f;
	}

	@Override
	public ResourceLocation getTextureForMaterialLevel(int level) {
		switch (level) {
			case 0:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/trebuchet_mobile_1.png");
			case 1:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/trebuchet_mobile_2.png");
			case 2:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/trebuchet_mobile_3.png");
			case 3:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/trebuchet_mobile_4.png");
			case 4:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/trebuchet_mobile_5.png");
			default:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/trebuchet_mobile_1.png");
		}
	}
}
