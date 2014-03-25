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
package net.shadowmage.ancientwarfare.core.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.world.BlockEvent;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.config.Statics;

public class BlockTools
{

/**
 * returns a direction right of the input
 * @param dir
 * @return
 */
public static int turnRight(int dir)
  {
  return (dir +1) %4;
  }

/**
 * returns a direction to the left of the input
 * @param dir
 * @return
 */
public static int turnLeft(int dir)
  {
  return (dir+3)%4;
  }

/**
 * returns a direction opposite of the input on the horizontal axis
 * @param dir
 * @return
 */
public static int turnAround(int dir)
  {
  return (dir+2)%4;
  }

/**
 * rotate a float X offset (-1<=x<=1) within a block
 */
public static float rotateFloatX(float x, float z, int turns)
  {
  float x1, z1;
  x1 = x;
  z1 = z;
  for(int i = 0; i <turns; i++)
    {
    z = x1;
    x = 1.f-z1;
    x1 = x;
    z1 = z;
    }
  return x;
  }

public static float rotateFloatZ(float x, float z, int turns)
  {
  float x1, z1;
  x1 = x;
  z1 = z;
  for(int i = 0; i <turns; i++)
    {
    z = x1;
    x = 1.f-z1;
    x1 = x;
    z1 = z;
    }
  return z;
  }

public static int getCardinalFromSide(ForgeDirection theSide)
  {
  int side;
  switch(theSide.ordinal())
  {
  case 2://n
  side = 2;
  break;
  
  case 3://s
  side = 0;
  break;
  
  case 4://w
  side = 3;
  break;
  
  case 5://e
  side = 1;
  break;
  
  default:
  side = -1;
  break;  
  }    
  return side;
  }

public static ForgeDirection getLeft(ForgeDirection a)
  {
  switch(a)
  {
  case SOUTH:
  return ForgeDirection.EAST;
  case WEST:
  return ForgeDirection.SOUTH;
  case NORTH:
  return ForgeDirection.WEST;
  case EAST:
  return ForgeDirection.NORTH;
  case DOWN:
    break;
  case UNKNOWN:
    break;
  case UP:
    break;
  default:
    break;
  }
  return a;
  }

public static ForgeDirection getRight(ForgeDirection a)
  {
  switch(a)
  {
  case SOUTH:
  return ForgeDirection.WEST;
  case WEST:
  return ForgeDirection.NORTH;
  case NORTH:
  return ForgeDirection.EAST;
  case EAST:
  return ForgeDirection.SOUTH;
    case DOWN:
      break;
    case UNKNOWN:
      break;
    case UP:
      break;
    default:
      break;
  }
  return a;
  }

public static ForgeDirection rotateRight(ForgeDirection a, int rot)
  {
  for(int i = 0; i< rot; i++)
    {
    a = getRight(a);
    }
  return a;
  }

public static ForgeDirection getOpposite(ForgeDirection a)
  {
  return a.getOpposite();
  }

public static ForgeDirection getForgeDirectionFromCardinal(int side)
  {
  switch(side)
  {
  case 2://n
  return ForgeDirection.NORTH;
  
  case 0://s
  return ForgeDirection.SOUTH;
  
  case 3://w
  return ForgeDirection.EAST;
  
  case 1://e
  return ForgeDirection.WEST;
  
  default:
  return ForgeDirection.UNKNOWN;
  }    
  }

public static BlockPosition offsetForSide(BlockPosition pos, int sideHit)
  {
  switch (sideHit)
    {
    case 0:
    pos.y--;
    break;
    case 1:
    pos.y++;
    break;
    case 2:
    pos.z--;
    break;
    case 3:
    pos.z++;
    break;
    case 4:
    pos.x--;
    break;
    case 5:
    pos.x++;
    }  
  return pos;    
  }

/**
 * 
 * @param start original as-scanned orientation
 * @param destination the rotation destination (2 for normalize to template, other #s for template->build)
 * @return the number of right-turns necessary to arrive at the new normalized facing
 */
public static int getRotationAmount(int start, int destination)
  {
  if(start==destination)
    {
    return 0;
    }
  int turn = destination-start;
  if(turn<0)
    {
    turn += 4;
    }  
  return turn;
  }

public static int getRotationAmt(int facing)
  {
  if(facing==2)
    {
    return 0;
    }
  else if(facing==3)
    {
    return 1;
    }
  else if(facing==0)
    {
    return 2;
    }
  else if(facing==1)
    {
    return 3;
    }
  return 0;
  }

public static BlockPosition getNorthRotatedPosition(int x, int y, int z, int rotation, int xSize, int zSize)
  {
  if(rotation==0)//south, invert x,z
    {
    return new BlockPosition(xSize - x - 1 , y, zSize - z - 1 );
    }
  if(rotation==1)//west
    {
    return new BlockPosition(xSize - z - 1, y, x);
    }
  if(rotation==2)//north, no change
    {
    return new BlockPosition(x,y,z);
    }
  if(rotation==3)//east
    {
    return new BlockPosition(z, y, zSize - x - 1);
    }
  return null;
  }

public static BlockPosition getAverageOf(BlockPosition ... positions)
  {  
  float x = 0;
  float y = 0;
  float z = 0;
  int count = 0;
  for(BlockPosition pos : positions)
    {
    x+=pos.x;
    y+=pos.y;
    z+=pos.z;
    count++;
    }
  if(count>0)
    {
    x /= count;
    y /= count;
    z /= count;      
    }
  return new BlockPosition(x,y,z);
  }

/**
 * will return null if nothing is in range
 * @param player
 * @param world
 * @param offset
 * @return
 */
public static BlockPosition getBlockClickedOn(EntityPlayer player, World world, boolean offset)
  {
  float scaleFactor = 1.0F;
  float rotPitch = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * scaleFactor;
  float rotYaw = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * scaleFactor;
  double testX = player.prevPosX + (player.posX - player.prevPosX) * scaleFactor;
  double testY = player.prevPosY + (player.posY - player.prevPosY) * scaleFactor + 1.62D - player.yOffset;
  double testZ = player.prevPosZ + (player.posZ - player.prevPosZ) * scaleFactor;
  Vec3 testVector = Vec3.createVectorHelper(testX, testY, testZ);
  float var14 = MathHelper.cos(-rotYaw * 0.017453292F - (float)Math.PI);
  float var15 = MathHelper.sin(-rotYaw * 0.017453292F - (float)Math.PI);
  float var16 = -MathHelper.cos(-rotPitch * 0.017453292F);
  float vectorY = MathHelper.sin(-rotPitch * 0.017453292F);
  float vectorX = var15 * var16;
  float vectorZ = var14 * var16;
  double reachLength = 5.0D;
  Vec3 testVectorFar = testVector.addVector(vectorX * reachLength, vectorY * reachLength, vectorZ * reachLength);
  MovingObjectPosition testHitPosition = world.rayTraceBlocks(testVector, testVectorFar, true);

  /**
   * if nothing was hit, return null
   */
  if (testHitPosition == null)
    {
    return null;
    }

  Vec3 var25 = player.getLook(scaleFactor);
  float var27 = 1.0F;
  List entitiesPossiblyHitByVector = world.getEntitiesWithinAABBExcludingEntity(player, player.boundingBox.addCoord(var25.xCoord * reachLength, var25.yCoord * reachLength, var25.zCoord * reachLength).expand(var27, var27, var27));
  Iterator entityIterator = entitiesPossiblyHitByVector.iterator();
  while (entityIterator.hasNext())
    {
    Entity testEntity = (Entity)entityIterator.next();
    if (testEntity.canBeCollidedWith())
      {
      float bbExpansionSize = testEntity.getCollisionBorderSize();
      AxisAlignedBB entityBB = testEntity.boundingBox.expand(bbExpansionSize, bbExpansionSize, bbExpansionSize);
      /**
       * if an entity is hit, return its position
       */
      if (entityBB.isVecInside(testVector))
        {
        return new BlockPosition(testEntity.posX, testEntity.posY, testEntity.posZ);         
        }
      }
    }
  /**
   * if no entity was hit, return the position impacted.
   */
  int var42 = testHitPosition.blockX;
  int var43 = testHitPosition.blockY;
  int var44 = testHitPosition.blockZ;
  /**
   * if should offset for side hit (block clicked IN)
   */
  if(offset)
    {
    switch (testHitPosition.sideHit)
      {
      case 0:
      --var43;
      break;
      case 1:
      ++var43;
      break;
      case 2:
      --var44;
      break;
      case 3:
      ++var44;
      break;
      case 4:
      --var42;
      break;
      case 5:
      ++var42;
      }
    }      
  return new BlockPosition(var42, var43, var44); 
  }

public static BlockPosition rotateAroundOrigin(BlockPosition pos, int turns)
  {
  for(int i = 0; i < turns; i++)
    {
    rotateAroundOrigin(pos);
    }
  return pos;
  }

public static BlockPosition rotateAroundOrigin(BlockPosition pos)
  {
  int x = pos.x;
  int z = pos.z;
  boolean xNeg = x<0;
  boolean zNeg = z<0;
  if(!xNeg && !zNeg)//first quadrant
    {
    pos.x = -z;
    pos.z = x;
    }
  else if(xNeg && !zNeg)//second quadrant
    {
    pos.x = -z;
    pos.z = x;
    }
  else if(xNeg && zNeg)
    {
    pos.x = -z;
    pos.z = -x;
    }
  else//!xNeg && zNeg
    {
    pos.x = -z;
    pos.z = x;
    }  
  return pos;
  }

/**
 * checks to see if TEST lies somewhere in the cube bounded by pos1 and pos2
 * @param test
 * @param pos1
 * @param pos2
 * @return true if it does
 */
public static boolean isPositionWithinBounds(BlockPosition test, BlockPosition pos1, BlockPosition pos2)
  {
  int minX;
  int maxX;
  int minY;
  int maxY;
  int minZ;
  int maxZ;
  if(pos1.x < pos2.x)
    {
    minX = pos1.x;
    maxX = pos2.x;
    }
  else
    {
    minX = pos2.x;
    maxX = pos1.x;
    }
  if(pos1.y < pos2.y)
    {
    minY = pos1.y;
    maxY = pos2.y;
    }
  else
    {
    minY = pos2.y;
    maxY = pos1.y;
    }
  if(pos1.z < pos2.z)
    {
    minZ = pos1.z;
    maxZ = pos2.z;
    }
  else
    {
    minZ = pos2.z;
    maxZ = pos1.z;
    }
  
  if(test.x >= minX && test.x <=maxX)
    {
    if(test.y >= minY && test.y <=maxY)
      {
      if(test.z >= minZ && test.z <=maxZ)
        {
        return true;
        }
      }
    }
  return false;
  }

public static BlockPosition getMin(BlockPosition pos1, BlockPosition pos2)
  {
  BlockPosition pos = new BlockPosition(Trig.getMin(pos1.x, pos2.x), Trig.getMin(pos1.y, pos2.y), Trig.getMin(pos1.z, pos2.z));
  return pos;
  }

public static BlockPosition getMax(BlockPosition pos1, BlockPosition pos2)
  {
  BlockPosition pos = new BlockPosition(Trig.getMax(pos1.x, pos2.x), Trig.getMax(pos1.y, pos2.y), Trig.getMax(pos1.z, pos2.z));
  return pos;
  }

public static int getPlayerFacingFromYaw(float rotation)
  {
  double yaw = (double)rotation;
  while(yaw < 0.d)
    {
    yaw+=360.d;
    }
  while(yaw >=360.d)
    {
    yaw-=360.d;
    }
  double adjYaw = yaw +45;
  adjYaw *=4;//multiply by four
  adjYaw /= 360.d;
  int facing = (MathHelper.floor_double(adjYaw)) % 4;//round down, mod 4 for a 0-3 range
  return facing;
  }

//facing south greatest X, lowest Z
//facing west greatest X, greatest Z
//facing north. FL corner is lowest X, greatest Z
//facing east FL corner is lowest X, lowest Z

///**
// * return this actual position of target relative to min
// * target is a raw vector, not an actual position, relative to the building template(facing 2) and facing input
// * @param min
// * @param target
// * @param face
// * @param size the size of the area
// * @return
// */
//public static BlockPosition getTranslatedPosition(BlockPosition min, BlockPosition target, int face, BlockPosition size)
//  {
//  BlockPosition hit = new BlockPosition(min);
//  hit.y = min.y + target.y;
//  if(face==0)
//    {
//    hit.x = min.x-target.x;
//    hit.z = min.z+(size.z-target.z)-1;
//    }
//  if(face==1)
//    {
//    hit.x = min.x-(size.z-target.z)+1;
//    hit.z = min.z-target.x;
//    }
//  if(face==2)
//    {
//    hit.x = min.x+target.x;
//    hit.z = min.z-(size.z-target.z)+1;
//    }
//  if(face==3)
//    {
//    hit.x = min.x+(size.z-target.z)-1;
//    hit.z = min.z+target.x;
//    }
//  return hit;
//  }

///**
// * return a normalized buildKey for a raw scanned structure, given the input bounding, given key, and given facing
// * normalizes to a playerFacing of 2 (north)
// * @param face
// * @param pos1
// * @param pos2
// * @param key
// * @return
// */
//public static BlockPosition offsetBuildKey(int face, BlockPosition pos1, BlockPosition pos2, BlockPosition key)
//  {
//  //facing south greatest X, lowest Z
//  //facing west greatest X, greatest Z
//  //facing north. FL corner is lowest X, greatest Z
//  //facing east FL corner is lowest X, lowest Z
//  BlockPosition min = BlockTools.getMin(pos1, pos2);
//  BlockPosition max = BlockTools.getMax(pos1, pos2);
//  BlockPosition realKey = new BlockPosition(0,0,0);
//  realKey.y = key.y - min.y;
//  if(face==0)
//    {
//    realKey.x = max.x - key.x;
//    realKey.z = min.z - key.z;
//    }
//  if(face==2)
//    {
//    realKey.x = key.x - min.x;
//    realKey.z = key.z - max.z;
//    }
//  if(face==1)
//    {
//    realKey.x = max.z - key.z;
//    realKey.z = key.x - max.x;
//    }
//  if(face==3)
//    {
//    realKey.x = key.z - min.z;
//    realKey.z = min.x - key.x;
//    }
//  return realKey;
//  }

public static BlockPosition rotateInArea(BlockPosition pos, int xSize, int zSize, int turns)
  {
  int xSize1 = xSize;
  int zSize1 = zSize;  
  int x = pos.x;
  int z = pos.z;
  if(x>=xSize)
    {
    x = 0;
    }
  if(z>=zSize)
    {
    z = 0;
    }
  int x1 = x;
  int z1 = z;  
  for(int i = 0; i < turns; i++)
    {
    x =  zSize - 1 - z1;
    z =  x1;
    x1 = x;
    z1 = z;
    xSize = zSize1;
    zSize = xSize1;
    xSize1 = xSize;
    zSize1 = zSize;
    }
  pos.x = x;
  pos.z = z;
  return pos;
  }

public static void breakBlockAndDrop(World world, int x, int y, int z, int fortune)
  {   
  List<ItemStack> drops = breakBlock(world, x, y, z, fortune);
  for(ItemStack stack : drops)
    {
    float f = 0.7F;
    double d0 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
    double d1 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
    double d2 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
    EntityItem entityitem = new EntityItem(world, (double)x + d0, (double)y + d1, (double)z + d2, stack);
    entityitem.delayBeforeCanPickup = 10;
    world.spawnEntityInWorld(entityitem);
    }
  }

public static List<ItemStack> breakBlock(World world, int x, int y, int z, int fortune)
  {
  return breakBlock(world, "AncientWarfare", x, y, z, fortune);
  }

public static List<ItemStack> breakBlock(World world, String playerName, int x, int y, int z, int fortune)
  {
  if(world.isRemote)
    {
    return Collections.emptyList();
    }
  int meta = world.getBlockMetadata(x, y, z);  
  Block block = world.getBlock(x, y, z);
  if( block==null  || block.getBlockHardness(world, x, y, z) <0 )
    {
    return Collections.emptyList();
    }
  boolean dropBlock = true;
  if(Statics.fireBlockBreakEvents)
    {
    BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(x, y, z, world, block, meta, AncientWarfareCore.instance.proxy.getFakePlayer((WorldServer)world, playerName));
    MinecraftForge.EVENT_BUS.post(event);
    if(event.isCanceled())
      {
      dropBlock = false;
      }
    }  
  if(dropBlock)
    {
    ArrayList<ItemStack> drops = block.getDrops(world, x,y,z, world.getBlockMetadata(x,y,z), fortune);       
    world.setBlockToAir(x,y,z);
    return drops;
    }  
  return Collections.emptyList();
  }

}
