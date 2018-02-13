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

package net.shadowmage.ancientwarfare.vehicle.entity;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;

/**
 * used by missiles to communicate hit information to vastly different classes
 * (soldiers and vehicles)
 * soldiers and vehicles use this for stat tracking purposes
 * effectively server-side only.
 *
 * @author Shadowmage
 */
public interface IMissileHitCallback {
	/**
	 * callback for when a fired missile impacts a position
	 *
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 */
	void onMissileImpact(World world, double x, double y, double z);

	/**
	 * callback for when a fired missile impacts an entity
	 *
	 * @param world
	 * @param entity
	 */
	void onMissileImpactEntity(World world, Entity entity);
}
