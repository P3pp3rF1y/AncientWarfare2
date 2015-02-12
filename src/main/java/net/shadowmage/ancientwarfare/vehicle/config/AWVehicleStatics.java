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

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;
import net.shadowmage.ancientwarfare.core.config.ModConfiguration;
import org.lwjgl.input.Keyboard;

public class AWVehicleStatics extends ModConfiguration {

    public static final String KEY_VEHICLE_FORWARD = "keybind.vehicle.forward";
    public static final String KEY_VEHICLE_REVERSE = "keybind.vehicle.reverse";
    public static final String KEY_VEHICLE_LEFT = "keybind.vehicle.left";
    public static final String KEY_VEHICLE_RIGHT = "keybind.vehicle.right";
    public static final String KEY_VEHICLE_FIRE = "keybind.vehicle.fire";
    public static final String KEY_VEHICLE_ASCEND = "keybind.vehicle.ascend";
    public static final String KEY_VEHICLE_DESCEND = "keybind.vehicle.descend";

    /**
     * shared settings:
     * NONE?
     */
    public static final String sharedSettings = "01_shared_settings";

    /**
     * server settings:
     * npc worker tick rate / ticks per work unit
     */
    public static final String serverSettinngs = "02_server_settings";

    /**
     * client settings:
     * --SET VIA PROXY / ClientOptions.INSTANCE
     */
    public static final String clientSettings = "03_client_settings";

    /**
     * movement keybinds
     */
    public static Property keybindForward, keybindReverse, keybindLeft, keybindRight;
    public static Property keybindFire;
    public static Property keybindAscend, keybindDescend;

    public AWVehicleStatics(Configuration config) {
        super(config);
    }

    @Override
    public void initializeCategories() {
        keybindForward = AncientWarfareCore.config.get(AWCoreStatics.keybinds, KEY_VEHICLE_FORWARD, Keyboard.KEY_W);
        keybindReverse = AncientWarfareCore.config.get(AWCoreStatics.keybinds, KEY_VEHICLE_REVERSE, Keyboard.KEY_S);
        keybindLeft = AncientWarfareCore.config.get(AWCoreStatics.keybinds, KEY_VEHICLE_LEFT, Keyboard.KEY_A);
        keybindRight = AncientWarfareCore.config.get(AWCoreStatics.keybinds, KEY_VEHICLE_RIGHT, Keyboard.KEY_D);
        keybindFire = AncientWarfareCore.config.get(AWCoreStatics.keybinds, KEY_VEHICLE_FIRE, Keyboard.KEY_SPACE);
        keybindAscend = AncientWarfareCore.config.get(AWCoreStatics.keybinds, KEY_VEHICLE_ASCEND, Keyboard.KEY_R);
        keybindDescend = AncientWarfareCore.config.get(AWCoreStatics.keybinds, KEY_VEHICLE_DESCEND, Keyboard.KEY_F);
    }

    @Override
    public void initializeValues() {
        this.config.save();
    }

}
