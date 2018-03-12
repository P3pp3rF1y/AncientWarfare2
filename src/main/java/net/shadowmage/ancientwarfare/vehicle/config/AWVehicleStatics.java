package net.shadowmage.ancientwarfare.vehicle.config;

import net.shadowmage.ancientwarfare.core.config.ModConfiguration;

public class AWVehicleStatics extends ModConfiguration {

	public static final String KEY_VEHICLE_FORWARD = "keybind.vehicle.forward";
	public static final String KEY_VEHICLE_REVERSE = "keybind.vehicle.reverse";
	public static final String KEY_VEHICLE_LEFT = "keybind.vehicle.left";
	public static final String KEY_VEHICLE_RIGHT = "keybind.vehicle.right";
	public static final String KEY_VEHICLE_FIRE = "keybind.vehicle.fire";
	public static final String KEY_VEHICLE_ASCEND_AIM_UP = "keybind.vehicle.ascend.aim.up";
	public static final String KEY_VEHICLE_DESCEND_AIM_DOWN = "keybind.vehicle.descend.aim.down";
	public static final String KEY_VEHICLE_AMMO_PREV = "keybind.vehicle.ammo.prev";
	public static final String KEY_VEHICLE_AMMO_NEXT = "keybind.vehicle.ammo.next";
	public static final String KEY_VEHICLE_TURRET_LEFT = "keybind.vehicle.turret.left";
	public static final String KEY_VEHICLE_TURRET_RIGHT = "keybind.vehicle.turret.right";
	public static final String KEY_VEHICLE_MOUSE_AIM = "keybind.vehicle.mouse.aim";
	public static final String KEY_VEHICLE_AMMO_SELECT = "keybind.vehicle.ammo.select";

	public AWVehicleStatics(String mod) {
		super(mod);
	}

	//TODO add configuration for these values (probably annotations) - descriptions in 1.6.4 AW
	public static boolean oversizeAmmoEnabled = true;
	public static boolean soldiersUseAmmo = false;
	public static boolean adjustMissilesForAccuracy = false;
	public static boolean vehiclesTearUpGrass = true;
	public static boolean blockDestruction = true;
	public static boolean blockFires = true;
	public static boolean allowFriendlyFire = false;
	public static boolean fireBlockBreakEvents = true;
	public static boolean useVehicleSetupTime = true;

	public static boolean renderVehiclesInFirstPerson = true;
	public static boolean renderVehicleNameplates = true;

	@Override
	public void initializeCategories() {

	}

	@Override
	public void initializeValues() {
	}

}
