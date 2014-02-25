/**
   Copyright 2012 John Cummens (aka Shadowmage, Shadowmage4513)
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
package shadowmage.ancient_framework.common.utils;

import java.util.HashSet;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class RayTraceUtils
{

public static MovingObjectPosition tracePathWithYawPitch(World world, float x, float y, float z, float yaw, float pitch, float range, float borderSize, HashSet<Entity> excluded)
  {
  float tx = x + (Trig.sinDegrees(yaw+180) * range * Trig.cosDegrees(pitch));
  float ty = (-Trig.sinDegrees(pitch) * range) + y;
  float tz = z + (Trig.cosDegrees(yaw) * range * Trig.cosDegrees(pitch));
  return tracePath(world, x, y, z, tx, ty, tz, borderSize, excluded);
  }

public static MovingObjectPosition getPlayerTarget(EntityPlayer player, float range, float border)
  {
  HashSet<Entity> excluded = new HashSet<Entity>();
  excluded.add(player);
  if(player.ridingEntity!=null)
    {
    excluded.add(player.ridingEntity);
    }
  return tracePathWithYawPitch(player.worldObj, (float)player.posX, (float)player.posY + (player.worldObj.isRemote ? 0.f : 1.62f), (float)player.posZ, player.rotationYaw, player.rotationPitch, range, border, excluded);  
  }

/**
 * 
 * @param world
 * @param x startX
 * @param y startY
 * @param z startZ
 * @param tx endX
 * @param ty endY
 * @param tz endZ
 * @param borderSize extra area to examine around line for entities
 * @param excluded any excluded entities (the player, etc)
 * @return a MovingObjectPosition of either the block hit (no entity hit), the entity hit (hit an entity), or null for nothing hit
 */
public static MovingObjectPosition tracePath(World world, float x, float y, float z, float tx, float ty, float tz, float borderSize, HashSet<Entity> excluded)
  {
  Vec3 startVec = Vec3.fakePool.getVecFromPool(x, y, z);
  Vec3 endVec = Vec3.fakePool.getVecFromPool(tx, ty, tz);
  float minX = x < tx ? x : tx;
  float minY = y < ty ? y : ty;
  float minZ = z < tz ? z : tz;
  float maxX = x > tx ? x : tx;
  float maxY = y > ty ? y : ty; 
  float maxZ = z > tz ? z : tz;
  AxisAlignedBB bb = AxisAlignedBB.getAABBPool().getAABB(minX, minY, minZ, maxX, maxY, maxZ).expand(borderSize, borderSize, borderSize);
  List<Entity> allEntities = world.getEntitiesWithinAABBExcludingEntity(null, bb);  
  MovingObjectPosition blockHit = world.clip(startVec, endVec);
  startVec = Vec3.fakePool.getVecFromPool(x, y, z);
  endVec = Vec3.fakePool.getVecFromPool(tx, ty, tz);
  float maxDistance = (float) endVec.distanceTo(startVec);
  if(blockHit!=null)
    {
    maxDistance = (float) blockHit.hitVec.distanceTo(startVec);
    }  
  Entity closestHitEntity = null;
  float closestHit = Float.POSITIVE_INFINITY;
  float currentHit = 0.f;
  AxisAlignedBB entityBb;// = ent.getBoundingBox();
  MovingObjectPosition intercept;
  for(Entity ent : allEntities)
    {    
    if(ent.canBeCollidedWith() && !excluded.contains(ent))
      {
      float entBorder =  ent.getCollisionBorderSize();
      entityBb = ent.boundingBox;
      if(entityBb!=null)
        {
        entityBb = entityBb.expand(entBorder, entBorder, entBorder);
        intercept = entityBb.calculateIntercept(startVec, endVec);
        if(intercept!=null)
          {
          currentHit = (float) intercept.hitVec.distanceTo(startVec);
          if(currentHit <= maxDistance && (currentHit < closestHit || currentHit==0))
            {            
            closestHit = currentHit;
            closestHitEntity = ent;
            }
          } 
        }
      }
    }  
  if(closestHitEntity!=null)
    {
    blockHit = new MovingObjectPosition(closestHitEntity);
    }
  return blockHit;
  }

}
