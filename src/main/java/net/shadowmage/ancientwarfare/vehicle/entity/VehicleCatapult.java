package net.shadowmage.ancientwarfare.vehicle.entity;

import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.vehicle.entity.movement.VehicleInputHandlerCatapult;

public class VehicleCatapult extends VehicleTurreted {

    public VehicleCatapult(World world) {
        super(world);
        moveHandler = new VehicleInputHandlerCatapult(this);
    }

}
