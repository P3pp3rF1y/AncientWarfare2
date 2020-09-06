package net.shadowmage.ancientwarfare.vehicle.entity.types;

public class VehicleTypeCannonStandTurret extends VehicleTypeCannon {

	public VehicleTypeCannonStandTurret(int typeNum) {
		super(typeNum);
		configName = "cannon_stand_turret";
		displayName = "item.vehicleSpawner.10";
		displayTooltip.add("item.vehicleSpawner.tooltip.gunpowder");
		displayTooltip.add("item.vehicleSpawner.tooltip.fixed");
		displayTooltip.add("item.vehicleSpawner.tooltip.midturret");
		width = 1.2f;
		height = 1.6f;
		turretRotationMax = 45;
		riderMovesWithTurret = true;
		yawAdjustable = true;
		riderSits = true;
		baseStrafeSpeed = 0.75f;
		baseForwardSpeed = 0.f;
		baseMissileVelocityMax = 40.f;
		turretVerticalOffset = 13.5f * 0.0625f;
		riderVerticalOffset = 0.55f;
		riderForwardsOffset = -1.25f;
	}

}
