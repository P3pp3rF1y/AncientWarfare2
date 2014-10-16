package net.shadowmage.ancientwarfare.vehicle.input;

import net.shadowmage.ancientwarfare.vehicle.config.AWVehicleStatics;

public enum VehicleInputKey
{
FORWARD(AWVehicleStatics.KEY_VEHICLE_FORWARD),
REVERSE(AWVehicleStatics.KEY_VEHICLE_REVERSE),
LEFT(AWVehicleStatics.KEY_VEHICLE_FORWARD),
RIGHT(AWVehicleStatics.KEY_VEHICLE_FORWARD),
ASCEND(AWVehicleStatics.KEY_VEHICLE_FORWARD),
DESCEND(AWVehicleStatics.KEY_VEHICLE_FORWARD),
FIRE(AWVehicleStatics.KEY_VEHICLE_FORWARD);
String name;
VehicleInputKey(String name){this.name=name;}
public String getName(){return name;}
}
