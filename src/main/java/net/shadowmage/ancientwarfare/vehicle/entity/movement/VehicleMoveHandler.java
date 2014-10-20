package net.shadowmage.ancientwarfare.vehicle.entity.movement;

import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;

public abstract class VehicleMoveHandler
{

VehicleBase vehicle;

public VehicleMoveHandler(VehicleBase vehicle)
  {
  this.vehicle = vehicle;
  }

/**
 * 
 * @param inputStates indices are the ordinals of VehicleInputKeys
 */
public abstract void updateVehicleMotion(boolean[] inputStates);

}
