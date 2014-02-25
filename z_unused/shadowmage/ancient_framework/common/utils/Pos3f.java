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

import java.text.DecimalFormat;

import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class Pos3f
{
public float x;
public float y;
public float z;

public Pos3f()
  {
  
  }

public Pos3f(Pos3f in)
  {
  this.x = in.x;
  this.y = in.y;
  this.z = in.z;
  }

public Pos3f(float x, float y, float z)
  {
  this.x = x;
  this.y = y;
  this.z = z;
  }

public Pos3f(double x, double y, double z)
  {
  this.x = (float)x;
  this.y = (float)y;
  this.z = (float)z;
  }

public Pos3f(Vec3 vec)
  {
  this.x = (float)vec.xCoord;
  this.y = (float)vec.yCoord;
  this.z = (float)vec.zCoord;  
  }

public void setup(float x, float y, float z)
  {
  this.x = x;
  this.y = y;
  this.z = z;
  }

public void setup(double x, double y, double z)
  {
  this.setup((float)x,(float) y,(float) z);
  }

public boolean equals(Pos3f in)
  {
  if(this.x == in.x && this.y == in.y && this.z == in.z)
    {
    return true;
    }
  return false;
  }

public static Pos3f getOffset(Pos3f pos1, Pos3f pos2)
  {
  return new Pos3f(pos2.x - pos1.x, pos2.y - pos1.y, pos2.z - pos1.z);
  }

public Pos3f copy()
  {
  return new Pos3f(this.x, this.y, this.z);
  }

public int floorX()
  {
  return MathHelper.floor_float(this.x);
  }

public int floorY()
  {
  return MathHelper.floor_float(this.y);  
  }

public int floorZ()
  {
  return MathHelper.floor_float(this.z);
  }

/**
 * returns a NEW Pos3f
 * @param add
 * @return
 */
public Pos3f add(Pos3f add)
  {  
  return new Pos3f(this.x + add.x, this.y + add.y, this.z + add.z);
  }

/**
 * returns a NEW Pos3f
 * @param add
 * @return
 */
public Pos3f add(float x, float y, float z)
  {  
  return new Pos3f(this.x + x, this.y + y, this.z + z);
  }

/**
 * alters THIS Pos3f
 * @param add
 */
public void addToThis(Pos3f add)
  {
  this.x += add.x;
  this.y += add.y;
  this.z += add.z;
  }


/**
 * normalizes a motion vector to a 0-1 range for all axis.  Normalizes from the largest vector, percentage wise, to the others.
 * 
 * @param vec
 * @return altered vector<same vector>
 */
public static Pos3f normalizeVector(Pos3f vec)
  {
  float x = vec.x;
  float y = vec.y;
  float z = vec.z;
  float highest = x;
  if(y>highest)
    {
    highest = y;
    }
  if(z>highest)
    {
    highest = z;
    }  
  vec.x = x / highest;
  vec.y = y / highest;
  vec.z = z / highest;
  return vec;  
  }

/**
 * normalizes a motion vector to a 0-1 range for all axis.  Normalizes from the largest vector, percentage wise, to the others.
 * 
 * @param vec to normalize
 * @return altered vector<same vector>
 */
public static Vec3 normalizeVector(Vec3 vec)
  {
  float x = (float) vec.xCoord;
  float y = (float) vec.yCoord;
  float z = (float) vec.zCoord;
  float high1 = x >= y ? x : y;
  float high2 = z >= high1 ? z : high1;
  float highest = high1 >= high2? high1 : high2;
  vec.xCoord = x / highest;
  vec.yCoord = y / highest;
  vec.zCoord = z / highest;
  return vec;  
  }

@Override
public String toString()
  {
  DecimalFormat formatter = new DecimalFormat("#.##");
  String sX = formatter.format(x);
  String sY = formatter.format(y);
  String sZ = formatter.format(z);    
  return "X: "+sX+" Y: "+sY+" Z: "+sZ;
  }


}
