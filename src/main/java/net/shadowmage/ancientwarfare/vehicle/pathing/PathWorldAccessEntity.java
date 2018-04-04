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

package net.shadowmage.ancientwarfare.vehicle.pathing;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;

/**
 * a wrapper for a world-obj that will do additional validation of nodes to see if the entity can walk
 * on the node, and fit in the area (used more for vehicles than soldiers)
 *
 * @author Shadowmage
 */
public class PathWorldAccessEntity extends PathWorldAccess {

	//TODO look into completely removing this class

	World world;//so there is no casting necessary to access world functions (getcolliding bb/etc)
	Entity entity;

	/**
	 * @param world
	 */
	public PathWorldAccessEntity(World world, Entity entity) {
		super(world);
		world = world;
		this.entity = entity;
	}

	//@Override
	//protected boolean checkColidingEntities(int x, int y, int z)
	//  {
	//  return !this.world.checkNoEntityCollision(AxisAlignedBB.getAABBPool().getAABB(x, y, z, x+1, y+1, z+1), this.entity);
	//  }

	//@Override
	//public boolean isWalkable(int x, int y, int z)
	//  {
	////  return super.isWalkable(x, y, z);
	//  if(this.entity.width>1.f)
	//    {
	//    //check blocks in the x/z +/- 1/2 width
	//    int size = MathHelper.ceiling_double_int(entity.width/2);
	////    int size = 1;
	//    for(int dx = x-size; dx<= x+size; dx++)
	//      {
	//      for(int dz = z-size; dz<= z+size; dz++)
	//        {
	//        if(!super.isWalkable(dx, y, dz))
	//          {
	//          if(super.isWalkable(dx, y+1, dz) || super.isWalkable(dx, y-1, dz))
	//            {
	//            continue;
	////            return true;
	//            }
	//          return false;
	//          }
	//        }
	//      }
	//    return true;
	//    }
	//  else
	//    {
	//    return super.isWalkable(x, y, z);
	//    }
	//  }

	@Override
	public boolean isRemote() {
		return this.world.isRemote;
	}
}
