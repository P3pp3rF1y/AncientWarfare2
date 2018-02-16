package net.shadowmage.ancientwarfare.vehicle.refactoring.input;

import net.shadowmage.ancientwarfare.vehicle.config.AWVehicleStatics;

public enum VehicleInputKey {
    FORWARD(AWVehicleStatics.KEY_VEHICLE_FORWARD),
    REVERSE(AWVehicleStatics.KEY_VEHICLE_REVERSE),
    LEFT(AWVehicleStatics.KEY_VEHICLE_LEFT),
    RIGHT(AWVehicleStatics.KEY_VEHICLE_RIGHT),
    ASCEND(AWVehicleStatics.KEY_VEHICLE_ASCEND),
    DESCEND(AWVehicleStatics.KEY_VEHICLE_DESCEND),
    FIRE(AWVehicleStatics.KEY_VEHICLE_FIRE);
    String name;

    VehicleInputKey(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
