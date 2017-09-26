package net.shadowmage.ancientwarfare.vehicle.entity;

import net.minecraft.entity.Entity;
import net.shadowmage.ancientwarfare.core.entity.AWEntityRegistry;
import net.shadowmage.ancientwarfare.core.entity.AWEntityRegistry.EntityDeclaration;
import net.shadowmage.ancientwarfare.vehicle.AncientWarfareVehicles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AWVehicleEntityLoader {

    private static int nextID = 0;

    private static List<String> vehicleTypes = new ArrayList<>();
    private static HashMap<String, String> regNameToIcon = new HashMap<>();

    public static void load() {
        EntityDeclaration reg = new VehiculeDeclaration(VehicleTurreted.class, AWEntityRegistry.VEHICLE_TEST);
        registerVehicleEntity(reg, "fooicon");

        reg = new VehiculeDeclaration(VehicleCatapult.class, AWEntityRegistry.VEHICLE_CATAPULT);
        registerVehicleEntity(reg, "fooicon");

        reg = new VehiculeDeclaration(MissileBase.class, AWEntityRegistry.MISSILE_TEST);
        AWEntityRegistry.registerEntity(reg);
    }

    private static void registerVehicleEntity(EntityDeclaration reg, String icon) {
        if (!vehicleTypes.contains(reg.name())) {
            vehicleTypes.add(reg.name());
        }
        AWEntityRegistry.registerEntity(reg);
        regNameToIcon.put(reg.name(), icon);
    }

    public static List<String> getVehicleTypes() {
        return vehicleTypes;
    }

    public static String getIcon(String vehicleType) {
        return regNameToIcon.get(vehicleType);
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
