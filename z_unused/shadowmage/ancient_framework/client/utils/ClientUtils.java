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
package shadowmage.ancient_framework.client.utils;

import java.util.Iterator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public class ClientUtils
{

/**
 * 
 */
public ClientUtils()
  {
  // TODO Auto-generated constructor stub
  }

public static MovingObjectPosition getPlayerLookTargetClient(EntityPlayer player, float range, Entity excludedEntity)
  {  
  /**
   * Vec3 positionVector = this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX, this.posY, this.posZ);
    Vec3 moveVector = this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
    MovingObjectPosition hitPosition = this.worldObj.rayTraceBlocks_do_do(positionVector, moveVector, false, true);
    positionVector = this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX, this.posY, this.posZ);
    moveVector = this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
   */
  Vec3 playerPos = player.getPosition(0);
  Vec3 lookVector = player.getLook(0);
  Vec3 endVector = playerPos.addVector(lookVector.xCoord * range, lookVector.yCoord * range, lookVector.zCoord * range);
  MovingObjectPosition blockHit = player.worldObj.clip(playerPos, endVector);
  
  /**
   * reseat vectors, as they get messed with in the rayTrace...
   */
  playerPos = player.getPosition(0);
  lookVector = player.getLook(0);
  
  float var9 = 1.f;
  
  float closestFound = 0.f;
  if(blockHit!=null)
    {
    closestFound = (float) blockHit.hitVec.distanceTo(playerPos);
    }
  Minecraft mc = Minecraft.getMinecraft();
  List possibleHitEntities = mc.theWorld.getEntitiesWithinAABBExcludingEntity(mc.renderViewEntity, mc.renderViewEntity.boundingBox.addCoord(lookVector.xCoord * range, lookVector.yCoord * range, lookVector.zCoord * range).expand((double)var9, (double)var9, (double)var9));
  Iterator<Entity> it = possibleHitEntities.iterator();
  Entity hitEntity = null;
  Entity currentExaminingEntity = null;
  while(it.hasNext())
    {
    currentExaminingEntity = it.next();
    if(currentExaminingEntity == excludedEntity)
      {
      continue;
      }
    if(currentExaminingEntity.canBeCollidedWith())
      {
      float borderSize = currentExaminingEntity.getCollisionBorderSize();
      AxisAlignedBB entBB = currentExaminingEntity.boundingBox.expand((double)borderSize, (double)borderSize, (double)borderSize);
      MovingObjectPosition var17 = entBB.calculateIntercept(playerPos, endVector);

      if (entBB.isVecInside(playerPos))
        {
        if (0.0D < closestFound || closestFound == 0.0D)
          {
          hitEntity = currentExaminingEntity;
          closestFound = 0.0f;
          }
        }
      else if (var17 != null)
        {
        double var18 = playerPos.distanceTo(var17.hitVec);

        if (var18 < closestFound || closestFound == 0.0D)
          {
          hitEntity = currentExaminingEntity;
          closestFound = (float) var18;
          }
        }
      }   
    }
  if(hitEntity!=null)
    {
//    Config.logDebug("entity hit!!");
    blockHit = new MovingObjectPosition(hitEntity);
    blockHit.hitVec.yCoord += hitEntity.height * 0.65f;
    }
  return blockHit;
  }

}
