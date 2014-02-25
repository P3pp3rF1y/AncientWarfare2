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

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;

public class BlockPosition
{

public int x;
public int y;
public int z;

public BlockPosition()
  {
  
  }

public BlockPosition(int x, int y, int z)
  {
  this.x = x;
  this.y = y;
  this.z = z;
  }

public BlockPosition(double x, double y, double z)
  {
  this.x = MathHelper.floor_double(x);
  this.y = MathHelper.floor_double(y);
  this.z = MathHelper.floor_double(z);
  }

public BlockPosition(NBTTagCompound tag)
  {
  read(tag);
  }

public BlockPosition(BlockPosition pos)
  {
  this.x = pos.x;
  this.y = pos.y;
  this.z = pos.z;
  }

public BlockPosition reassign(int x, int y, int z)
  {
  this.x = x;
  this.y = y;
  this.z = z;
  return this;
  }

public final BlockPosition read(NBTTagCompound tag)
  {
  this.x = tag.getInteger("x");
  this.y = tag.getInteger("y");
  this.z = tag.getInteger("z");
  return this;
  }

public void updateFromEntityPosition(Entity ent)
  {
  this.x = MathHelper.floor_double(ent.posX);
  this.y = MathHelper.floor_double(ent.posY);
  this.z = MathHelper.floor_double(ent.posZ);
  }

/**
 * return the distance of the CENTER of this block from the input position
 * @param x
 * @param y
 * @param z
 * @return
 */
public float getCenterDistanceFrom(double x, double y, double z)
  {
  return Trig.getDistance(x, y, z, this.x+0.5d, this.y, this.z+0.5d);
  }

public NBTTagCompound writeToNBT(NBTTagCompound tag)
  {
  tag.setInteger("x", x);
  tag.setInteger("y", y);
  tag.setInteger("z", z);
  return tag;
  }

/**
 * offsets THIS blockPosition by the passed in offset
 * @param offsetVector
 * @return
 */
public BlockPosition offsetBy(BlockPosition offsetVector)
  {
  this.x += offsetVector.x;
  this.y += offsetVector.y;
  this.z += offsetVector.z;
  return this;
  }

public void offset(int x, int y, int z)
  {
  this.x += x;
  this.y += y;
  this.z += z;
  }

/**
 * returns the DIFFERENCE between THIS and BASE
 * or the distance and directions needed to travel to
 * get from THIS block to BASE
 * i.e. if THIS x is 800, and base X is 1000, returns 
 * +200. if THIS x is 1000, and base X is 800, returns
 * -200
 * @param base the 
 * @return difference
 */
public BlockPosition getOffsetFrom(BlockPosition base)
  {
  BlockPosition diff = new BlockPosition();
  diff.x = base.x - this.x;
  diff.y = base.y - this.y;
  diff.z = base.z - this.z;  
  return diff;
  }

/**
 * moves the blocks position right by the input amount, relative to the input direction
 * @param facing
 * @param amt
 */
public void moveRight(int facing, int amt)
  {
  this.offsetForHorizontalDirection(BlockPosition.turnRight(facing), amt);
  }

/**
 * moves the blocks position backwards the input amount, relative to the input direction
 * @param facing
 * @param amt
 */
public void moveBack(int facing, int amt)
  {
  this.offsetForHorizontalDirection(BlockPosition.turnAround(facing), amt);
  }

/**
 * moves the blocks position left the input amount, relative to the input direction
 * @param facing
 * @param amt
 */
public void moveLeft(int facing, int amt)
  {
  this.offsetForHorizontalDirection(BlockPosition.turnLeft(facing), amt);
  }

/**
 * moves the blocks position forwards the input amount, relative to the input direction
 * @param facing
 * @param amt
 */
public void moveForward(int facing, int amt)
  {
  this.offsetForHorizontalDirection(facing, amt);
  }


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


//0=z+//south
//1=x-//west
//2=z-//north
//3=x+//east

//|| 0 = x- || 1 = x+ || 2 = z- || 3 = z+ ||
public void offsetForHorizontalDirection(int side)
  {
  if(side==0){this.z++;}
  else if(side==1){this.x--;}
  else if(side==2){this.z--;}
  else if(side==3){this.x++;}
  }

public void offsetForHorizontalDirection(int side, int amt)
  {
  if(side==0){this.z+=amt;}
  else if(side==1){this.x-=amt;}
  else if(side==2){this.z-=amt;}
  else if(side==3){this.x+=amt;}
  }

public boolean equals(BlockPosition pos)
  {
  return this.x == pos.x && this.y == pos.y && this.z== pos.z ? true : false;
  }

public BlockPosition copy()
  {
  return new BlockPosition(this.x, this.y, this.z);  
  }

public String toString()
  {
  return "X:"+this.x+" Y:"+this.y+" Z:"+this.z;
  }

public void offsetForMCSide(int side)
  {
  switch (side)
  {
  case 0:
  --y;
  break;
  case 1:
  ++y;
  break;
  case 2:
  --z;
  break;
  case 3:
  ++z;
  break;
  case 4:
  --x;
  break;
  case 5:
  ++x;
  }
  }

@Override
public int hashCode()
  {
  final int prime = 31;
  int result = 1;
  result = prime * result + x;
  result = prime * result + y;
  result = prime * result + z;
  return result;
  }

@Override
public boolean equals(Object obj)
  {
  if (this == obj)
    return true;
  if (obj == null)
    return false;
  if (getClass() != obj.getClass())
    return false;
  BlockPosition other = (BlockPosition) obj;
  if (x != other.x)
    return false;
  if (y != other.y)
    return false;
  if (z != other.z)
    return false;
  return true;
  }

}

