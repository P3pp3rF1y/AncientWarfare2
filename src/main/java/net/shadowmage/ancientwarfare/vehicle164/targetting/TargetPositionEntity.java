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

package shadowmage.ancient_warfare.common.targeting;

import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import java.lang.ref.WeakReference;

public class TargetPositionEntity extends TargetPosition {
	WeakReference<Entity> target;

	public TargetPositionEntity(Entity ent, TargetType type) {
		super(type);
		this.target = new WeakReference<Entity>(ent);
	}

	@Override
	public Entity getEntity(World world) {
		return this.target.get();
	}

	@Override
	public boolean isEntityEntry() {
		return true;
	}

	@Override
	public int floorX() {
		return MathHelper.floor_float(posX());
	}

	@Override
	public int floorY() {
		return (int) (target.get() != null ? (float) target.get().posY : Integer.MAX_VALUE);
	}

	@Override
	public int floorZ() {
		return MathHelper.floor_float(posZ());
	}

	@Override
	public float posX() {
		return target.get() != null ? (float) target.get().posX : Float.MAX_VALUE;
	}

	@Override
	public float posY() {
		return target.get() != null ? (float) target.get().posY + target.get().height * 0.65f : Float.MAX_VALUE;
	}

	@Override
	public float posZ() {
		return target.get() != null ? (float) target.get().posZ : Float.MAX_VALUE;
	}

	@Override
	public String toString() {
		return "entity target: " + posX() + "," + posY() + "," + posZ() + " :: " + getTargetType();
	}

}
