package net.shadowmage.ancientwarfare.vehicle.config;

import net.shadowmage.ancientwarfare.core.config.ModConfiguration;

public class AWVehicleStatics extends ModConfiguration {

	public static final String KEY_VEHICLE_FORWARD = "keybind.vehicle.forward";
	public static final String KEY_VEHICLE_REVERSE = "keybind.vehicle.reverse";
	public static final String KEY_VEHICLE_LEFT = "keybind.vehicle.left";
	public static final String KEY_VEHICLE_RIGHT = "keybind.vehicle.right";
	public static final String KEY_VEHICLE_FIRE = "keybind.vehicle.fire";
	public static final String KEY_VEHICLE_ASCEND = "keybind.vehicle.ascend";
	public static final String KEY_VEHICLE_DESCEND = "keybind.vehicle.descend";

	public AWVehicleStatics(String mod) {
		super(mod);
	}

	//TODO add configuration for these values (probably annotations)
	public static boolean oversizeAmmoEnabled = true;
	public static boolean soldiersUseAmmo = false;
	public static boolean adjustMissilesForAccuracy = false;
	public static boolean vehiclesTearUpGrass = true;
	public static boolean blockDestruction = true;
	public static boolean blockFires = true;
	public static boolean allowFriendlyFire = false;
	public static boolean fireBlockBreakEvents = true;

	@Override
	public void initializeCategories() {

	}

	@Override
	public void initializeValues() {
	}

}
