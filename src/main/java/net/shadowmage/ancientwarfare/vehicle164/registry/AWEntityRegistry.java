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

package shadowmage.ancient_warfare.common.registry;

import cpw.mods.fml.common.registry.EntityRegistry;
import net.minecraft.entity.Entity;
import shadowmage.ancient_warfare.common.AWCore;

public class AWEntityRegistry {

	static int nextIDNumber = 0;

	public static int getNextEntityID() {
		int id = nextIDNumber;
		nextIDNumber++;
		return id;
	}

	public static void registerEntity(Class<? extends Entity> clz, String name, int range, int freq, boolean velocity) {
		EntityRegistry.registerModEntity(clz, name, getNextEntityID(), AWCore.instance, range, freq, velocity);
	}

}
