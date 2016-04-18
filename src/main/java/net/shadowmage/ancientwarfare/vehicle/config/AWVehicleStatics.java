/**
 Copyright 2012-2013 John Cummens (aka Shadowmage, Shadowmage4513)
 This software is distributed under the terms of the GNU General Public License.
 Please see COPYING for precise license information.

 This file is part of Ancient Warfare.

 Ancient Warfare is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Ancient Warfare is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Ancient Warfare.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.shadowmage.ancientwarfare.vehicle.config;

import net.shadowmage.ancientwarfare.core.config.ModConfiguration;

public class AWVehicleStatics extends ModConfiguration {

    public static final String KEY_VEHICLE_FORWARD = "keybind.vehicle.forward";
    public static final String KEY_VEHICLE_REVERSE = "keybind.vehicle.reverse";
    public static final String KEY_VEHICLE_LEFT = "keybind.vehicle.left";
    public static final String KEY_VEHICLE_RIGHT = "keybind.vehicle.right";
    public static final String KEY_VEHICLE_FIRE = "keybind.vehicle.fire";
    public static final String KEY_VEHICLE_ASCEND = "keybind.vehicle.ascend";
    public static final String KEY_VEHICLE_DESCEND = "keybind.vehicle.descend";

    public AWVehicleStatics(String mod) {
        super(mod);
    }

    @Override
    public void initializeCategories() {

    }

    @Override
    public void initializeValues() {
    }

}
