package net.shadowmage.ancientwarfare.vehicle.entity.types;

import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.vehicle.VehicleVarHelpers.DummyVehicleHelper;
import net.shadowmage.ancientwarfare.vehicle.config.AWVehicleStatics;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleMovementType;
import net.shadowmage.ancientwarfare.vehicle.entity.materials.VehicleMaterial;
import net.shadowmage.ancientwarfare.vehicle.helpers.VehicleFiringVarsHelper;
import net.shadowmage.ancientwarfare.vehicle.registry.ArmorRegistry;
import net.shadowmage.ancientwarfare.vehicle.registry.UpgradeRegistry;

public class VehicleTypeBoatTransport extends VehicleType {
	public VehicleTypeBoatTransport(int typeNum) {
		super(typeNum);
		this.configName = "boat_transport";
		baseHealth = AWVehicleStatics.vehicleStats.vehicleChestBoatHealth;
		this.vehicleMaterial = VehicleMaterial.materialWood;
		this.movementType = VehicleMovementType.WATER;
		this.materialCount = 5;
		this.validArmors.add(ArmorRegistry.armorStone);
		this.validArmors.add(ArmorRegistry.armorObsidian);
		this.validArmors.add(ArmorRegistry.armorIron);
		this.width = 2.7f;
		this.height = 1.4f;
		this.mountable = true;
		this.drivable = true;
		this.combatEngine = false;
		this.riderSits = true;
		this.riderForwardsOffset = 1.4f;
		this.riderVerticalOffset = 0.55f;
		this.baseStrafeSpeed = 2.f;
		this.baseForwardSpeed = 6.2f * 0.05f;
		this.ammoBaySize = 0;
		this.upgradeBaySize = 6;
		this.armorBaySize = 6;
		this.storageBaySize = 54 * 4;
		this.displayName = "item.vehicleSpawner.20";
		this.displayTooltip.add("item.vehicleSpawner.tooltip.noweapon");
		this.displayTooltip.add("item.vehicleSpawner.tooltip.boat");
		this.displayTooltip.add("item.vehicleSpawner.tooltip.noturret");
		this.displayTooltip.add("item.vehicleSpawner.tooltip.storage");
		this.validUpgrades.add(UpgradeRegistry.speedUpgrade);
	}

	@Override
	public ResourceLocation getTextureForMaterialLevel(int level) {
		switch (level) {
			case 0:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/boat_transport_1.png");
			case 1:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/boat_transport_2.png");
			case 2:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/boat_transport_3.png");
			case 3:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/boat_transport_4.png");
			case 4:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/boat_transport_5.png");
			default:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/boat_transport_1.png");
		}
	}

	@Override
	public VehicleFiringVarsHelper getFiringVarsHelper(VehicleBase veh) {
		return new DummyVehicleHelper(veh);
	}

}
