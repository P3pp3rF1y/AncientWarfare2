package net.shadowmage.ancientwarfare.vehicle.refactoring.entity.movement;

import net.minecraft.entity.MoverType;
import net.minecraft.util.math.Vec3d;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.util.Trig;
import net.shadowmage.ancientwarfare.vehicle.refactoring.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.refactoring.input.VehicleInputKey;

public class VehicleInputHandlerCatapult extends VehicleInputHandler {

    static int maxFiringDelay = 20;
    static int maxReloadTime = 20;
    int firingDelay = 0;//countdown for fire pressed until missile launched
    int reloadDelay = 0;//countdown for post-fired

    public VehicleInputHandlerCatapult(VehicleBase vehicle) {
        super(vehicle);
    }

    @Override
    public void updateVehicleMotion(boolean[] inputStates) {
        float rotation = 0;
        double forward = 0;
        if (inputStates[VehicleInputKey.FORWARD.ordinal()]) {
            forward -= 0.25d;
        }
        if (inputStates[VehicleInputKey.REVERSE.ordinal()]) {
            forward += 0.25d;
        }
        if (inputStates[VehicleInputKey.LEFT.ordinal()]) {
            rotation += 1.f;
        }
        if (inputStates[VehicleInputKey.RIGHT.ordinal()]) {
            rotation -= 1.f;
        }
        /*
         * first move the vehicle forward along its current move vector
         */
        Vec3d forwardAxis = vehicle.getLookVec();

        double mx = forwardAxis.x * forward;
        double mz = forwardAxis.z * forward;

        if (vehicle.onGround) {
            vehicle.motionY = -Trig.gravityTick;
        } else {
            vehicle.motionY -= Trig.gravityTick;
        }
        double my = vehicle.motionY;

        vehicle.move(MoverType.SELF, mx, my, mz);
        /*
         * then rotate the vehicle towards its new orientation
         */
        if (rotation != 0) {
            vehicle.moveHelper.rotateVehicle(rotation);
        }
        updateFiringStatus(inputStates[VehicleInputKey.FIRE.ordinal()]);
    }

    protected void updateFiringStatus(boolean fire) {
        if (firingDelay > 0)//firing was initiated
        {
            firingDelay--;
            if (firingDelay == 0)//firing is complete
            {
                launchMissile();
                firingDelay = 0;
                reloadDelay = maxReloadTime;
            }
        }
        if (reloadDelay > 0) {
            reloadDelay--;
        }
        if (fire && firingDelay <= 0 && reloadDelay <= 0)//was not firing or reloading, but fire was pressed
        {
            AWLog.logDebug("Initiating firing sequence...");
            firingDelay = maxFiringDelay;
        }
    }

    protected void launchMissile() {
        AWLog.logDebug("Should launch missile = true");
    }

}
