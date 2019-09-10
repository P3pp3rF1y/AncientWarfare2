package net.shadowmage.ancientwarfare.vehicle.entity.types;

public class VehicleTypeCannonStandFixed extends VehicleTypeCannon {

	public VehicleTypeCannonStandFixed(int typeNum) {
		super(typeNum);
		configName = "cannon_stand";
		drivable = true;
		displayName = "item.vehicleSpawner.9";
		displayTooltip.add("item.vehicleSpawner.tooltip.gunpowder");
		displayTooltip.add("item.vehicleSpawner.tooltip.fixed");
		displayTooltip.add("item.vehicleSpawner.tooltip.noturret");
		width = 1.2f;
		height = 1.4f;
		baseStrafeSpeed = 0.75f;
		baseForwardSpeed = 0.f;
		riderSits = false;
		riderVerticalOffset = 0.35f;
		riderForwardsOffset = -1.5f;
		armorBaySize = 4;
		upgradeBaySize = 4;
		ammoBaySize = 6;
	}
}
