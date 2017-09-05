package net.shadowmage.ancientwarfare.vehicle.entity.movement;

import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;

public abstract class VehicleInputHandler {

    VehicleBase vehicle;

    public VehicleInputHandler(VehicleBase vehicle) {
        this.vehicle = vehicle;
    }

    /*
     * @param inputStates indices are the ordinals of VehicleInputKeys
     */
    public abstract void updateVehicleMotion(boolean[] inputStates);

    protected void handleTurretUpdateServer(int data) {

    }

    protected void handleTurretUpdateClient(int data) {

    }

    public void onTurretDataReceived(int data) {
        if (vehicle.worldObj.isRemote) {
            handleTurretUpdateClient(data);
        } else {
            handleTurretUpdateServer(data);
        }
    }

}
