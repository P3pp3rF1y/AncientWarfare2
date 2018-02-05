/**
 * Copyright 2012 John Cummens (aka Shadowmage, Shadowmage4513)
 * This software is distributed under the terms of the GNU General Public License.
 * Please see COPYING for precise license information.
 * <p>
 * This file is part of Ancient Warfare.
 * <p>
 * Ancient Warfare is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * Ancient Warfare is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with Ancient Warfare.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.shadowmage.ancientwarfare.vehicle.registry;

import net.shadowmage.ancientwarfare.vehicle.entity.IVehicleType;
import net.shadowmage.ancientwarfare.vehicle.missiles.IAmmoType;
import shadowmage.ancient_warfare.common.config.Config;
import shadowmage.ancient_warfare.common.item.ItemLoader;
import shadowmage.ancient_warfare.common.registry.entry.Description;
import shadowmage.ancient_warfare.common.vehicles.types.VehicleType;
import shadowmage.ancient_warfare.common.vehicles.types.VehicleTypeAirBomber;
import shadowmage.ancient_warfare.common.vehicles.types.VehicleTypeAirInterceptor;
import shadowmage.ancient_warfare.common.vehicles.types.VehicleTypeBallistaMobile;
import shadowmage.ancient_warfare.common.vehicles.types.VehicleTypeBallistaMobileTurret;
import shadowmage.ancient_warfare.common.vehicles.types.VehicleTypeBallistaStand;
import shadowmage.ancient_warfare.common.vehicles.types.VehicleTypeBallistaStandTurret;
import shadowmage.ancient_warfare.common.vehicles.types.VehicleTypeBatteringRam;
import shadowmage.ancient_warfare.common.vehicles.types.VehicleTypeBoatBallista;
import shadowmage.ancient_warfare.common.vehicles.types.VehicleTypeBoatCatapult;
import shadowmage.ancient_warfare.common.vehicles.types.VehicleTypeBoatTransport;
import shadowmage.ancient_warfare.common.vehicles.types.VehicleTypeCannonMobileFixed;
import shadowmage.ancient_warfare.common.vehicles.types.VehicleTypeCannonStandFixed;
import shadowmage.ancient_warfare.common.vehicles.types.VehicleTypeCannonStandTurret;
import shadowmage.ancient_warfare.common.vehicles.types.VehicleTypeCatapultMobileFixed;
import shadowmage.ancient_warfare.common.vehicles.types.VehicleTypeCatapultMobileTurret;
import shadowmage.ancient_warfare.common.vehicles.types.VehicleTypeCatapultStandFixed;
import shadowmage.ancient_warfare.common.vehicles.types.VehicleTypeCatapultStandTurret;
import shadowmage.ancient_warfare.common.vehicles.types.VehicleTypeChestCart;
import shadowmage.ancient_warfare.common.vehicles.types.VehicleTypeHelicopter;
import shadowmage.ancient_warfare.common.vehicles.types.VehicleTypeHwacha;
import shadowmage.ancient_warfare.common.vehicles.types.VehicleTypeSubmarine;
import shadowmage.ancient_warfare.common.vehicles.types.VehicleTypeTrebuchetLarge;
import shadowmage.ancient_warfare.common.vehicles.types.VehicleTypeTrebuchetMobileFixed;
import shadowmage.ancient_warfare.common.vehicles.types.VehicleTypeTrebuchetStandFixed;
import shadowmage.ancient_warfare.common.vehicles.types.VehicleTypeTrebuchetStandTurret;

import java.util.Iterator;

public class VehicleRegistry {

	public static final IVehicleType CATAPULT_STAND_FIXED = new VehicleTypeCatapultStandFixed(0);
	public static final IVehicleType CATAPULT_STAND_TURRET = new VehicleTypeCatapultStandTurret(1);
	public static final IVehicleType CATAPULT_MOBILE_FIXED = new VehicleTypeCatapultMobileFixed(2);
	public static final IVehicleType CATAPULT_MOBILE_TURRET = new VehicleTypeCatapultMobileTurret(3);

	public static final IVehicleType BALLISTA_STAND_FIXED = new VehicleTypeBallistaStand(4);
	public static final IVehicleType BALLISTA_STAND_TURRET = new VehicleTypeBallistaStandTurret(5);
	public static final IVehicleType BALLISTA_MOBILE_FIXED = new VehicleTypeBallistaMobile(6);
	public static final IVehicleType BALLISTA_MOBILE_TURRET = new VehicleTypeBallistaMobileTurret(7);

	public static final IVehicleType BATTERING_RAM = new VehicleTypeBatteringRam(8);

	public static final IVehicleType CANNON_STAND_FIXED = new VehicleTypeCannonStandFixed(9);
	public static final IVehicleType CANNON_STAND_TURRET = new VehicleTypeCannonStandTurret(10);
	public static final IVehicleType CANNON_MOBILE_FIXED = new VehicleTypeCannonMobileFixed(11);

	public static final IVehicleType HWACHA = new VehicleTypeHwacha(12);

	public static final IVehicleType TREBUCHET_STAND_FIXED = new VehicleTypeTrebuchetStandFixed(13);
	public static final IVehicleType TREBUCHET_STAND_TURRET = new VehicleTypeTrebuchetStandTurret(14);
	public static final IVehicleType TREBUCHET_MOBILE_FIXED = new VehicleTypeTrebuchetMobileFixed(15);
	public static final IVehicleType TREBUCHET_LARGE = new VehicleTypeTrebuchetLarge(16);

	public static final IVehicleType CHEST_CART = new VehicleTypeChestCart(17);

	public static final IVehicleType BOAT_BALLISTA = new VehicleTypeBoatBallista(18);
	public static final IVehicleType BOAT_CATAPULT = new VehicleTypeBoatCatapult(19);
	public static final IVehicleType BOAT_TRANSPORT = new VehicleTypeBoatTransport(20);

	public static final IVehicleType AIR_BOMBER = new VehicleTypeAirBomber(21);
	public static final IVehicleType AIR_FIGHTER = new VehicleTypeAirInterceptor(22);
	public static final IVehicleType AIR_HELICOPTER = new VehicleTypeHelicopter(23);

	public static final IVehicleType SUBMARINE_TEST = new VehicleTypeSubmarine(24);

	private VehicleRegistry() {
	}

	private static VehicleRegistry INSTANCE;

	public static VehicleRegistry instance() {
		if (INSTANCE == null) {
			INSTANCE = new VehicleRegistry();
		}
		return INSTANCE;
	}

	public void registerVehicles() {
		Description d = null;
		for (IVehicleType vehicle : VehicleType.vehicleTypes) {
			if (vehicle != null) {
				vehicle.setEnabled(
						Config.getConfig().get("e_vehicle_config", vehicle.getConfigName() + ".enabled", vehicle.isEnabled()).getBoolean(vehicle.isEnabled()));
				if (!vehicle.isEnabled()) {
					VehicleType.vehicleTypes[vehicle.getGlobalVehicleType()] = null;
					continue;
				}
				vehicle.setEnabledForCrafting(Config.getConfig().get("e_vehicle_config", vehicle.getConfigName() + ".craftable", true).getBoolean(true));
				vehicle.setEnabledForLoot(Config.getConfig().get("e_vehicle_config", vehicle.getConfigName() + ".add_to_chests", true).getBoolean(true));
				vehicle.setBaseAccuracy((float) Config.getConfig().get("e_vehicle_config", vehicle.getConfigName() + ".accuracy", vehicle.getBaseAccuracy())
						.getDouble(vehicle.getBaseAccuracy()));
				vehicle.setBaseForwardSpeed(
						(float) Config.getConfig().get("e_vehicle_config", vehicle.getConfigName() + ".forward_speed", vehicle.getBaseForwardSpeed())
								.getDouble(vehicle.getBaseForwardSpeed()));
				vehicle.setBaseHealth((float) Config.getConfig().get("e_vehicle_config", vehicle.getConfigName() + ".health", vehicle.getBaseHealth())
						.getDouble(vehicle.getBaseHealth()));
				vehicle.setBaseMissileVelocity(
						(float) Config.getConfig().get("e_vehicle_config", vehicle.getConfigName() + ".missile_speed", vehicle.getBaseMissileVelocityMax())
								.getDouble(vehicle.getBaseMissileVelocityMax()));
				vehicle.setBasePitchMax((float) Config.getConfig().get("e_vehicle_config", vehicle.getConfigName() + ".pitch_max", vehicle.getBasePitchMax())
						.getDouble(vehicle.getBasePitchMax()));
				vehicle.setBasePitchMin((float) Config.getConfig().get("e_vehicle_config", vehicle.getConfigName() + ".pitch_min", vehicle.getBasePitchMin())
						.getDouble(vehicle.getBasePitchMin()));
				vehicle.setBaseStrafeSpeed(
						(float) Config.getConfig().get("e_vehicle_config", vehicle.getConfigName() + ".strafe_speed", vehicle.getBaseStrafeSpeed())
								.getDouble(vehicle.getBaseStrafeSpeed()));
				vehicle.setBaseTurretRotationAmount(
						(float) Config.getConfig().get("e_vehicle_config", vehicle.getConfigName() + ".turret_rotation", vehicle.getBaseTurretRotationAmount())
								.getDouble(vehicle.getBaseTurretRotationAmount()));

				Iterator<IAmmoType> it = vehicle.getValidAmmoTypes().iterator();
				IAmmoType t;
				while (it.hasNext()) {
					t = it.next();
					if (!t.isEnabled()) {
						it.remove();
					}
				}

				d = ItemLoader.instance().addSubtypeInfoToItem(ItemLoader.vehicleSpawner, vehicle.getGlobalVehicleType(), vehicle.getDisplayName());
				for (String tip : vehicle.getDisplayTooltip()) {
					d.addTooltip(tip, vehicle.getGlobalVehicleType());
				}
				d.setIconTexture("ancientwarfare:testIcon5", vehicle.getGlobalVehicleType());
			}
		}
	}

}
