package net.shadowmage.ancientwarfare.vehicle.entity;

import net.minecraft.entity.Entity;
import net.shadowmage.ancientwarfare.core.entity.AWEntityRegistry;
import net.shadowmage.ancientwarfare.core.entity.AWEntityRegistry.EntityDeclaration;
import net.shadowmage.ancientwarfare.vehicle.AncientWarfareVehicles;
import net.shadowmage.ancientwarfare.vehicle.missiles.MissileBase;

public class AWVehicleEntityLoader {

    private static int nextID = 0;

    public static void load() {
        EntityDeclaration reg = new VehiculeDeclaration(VehicleBase.class, AWEntityRegistry.VEHICLE_TEST);
        AWEntityRegistry.registerEntity(reg);

        reg = new VehiculeDeclaration(MissileBase.class, AWEntityRegistry.MISSILE_TEST);
        AWEntityRegistry.registerEntity(reg);
    }

    private static class VehiculeDeclaration extends EntityDeclaration {

        public VehiculeDeclaration(Class<? extends Entity> entityClass, String entityName) {
            super(entityClass, entityName, nextID++, AncientWarfareVehicles.modID);
        }

        @Override
        public Object mod() {
            return AncientWarfareVehicles.instance;
        }

        @Override
        public int trackingRange() {
            return 120;
        }

        @Override
        public int updateFrequency() {
            return 3;
        }

        @Override
        public boolean sendsVelocityUpdates() {
            return true;
        }
    }
}
