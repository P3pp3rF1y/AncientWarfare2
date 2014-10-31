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

import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

/**
 * Static Math utilities class<br>
 * @author Shadowmage
 */
public class Trig
{
public static final float PI = 3.141592653589793f;
public static final float TORADIANS = PI / 180.f;
public static final float TODEGREES = 180.f / PI;
public static final float GRAVITY = 9.81f;
public static final double gravityTick =  GRAVITY *0.05D * 0.05D;

private Trig(){}//static utility class, no public facing constructor

public static int getPower(int num, int exp)
  {
  return Double.valueOf(Math.floor(Math.pow(num, exp))).intValue();
  }

public static float toRadians(float degrees)
  {
  return degrees * TORADIANS;
  }

public static float toDegrees(float radians)
  {
  return radians * TODEGREES;
  }

public static float cosDegrees(float degrees)
  {   
  return MathHelper.cos(degrees * TORADIANS);
  }

public static float sinDegrees(float degrees)
  {  
  return MathHelper.sin(degrees * TORADIANS);
  }

public static float cos(float radians)
  {
  return MathHelper.cos(radians);
  }

public static float sin(float radians)
  {
  return MathHelper.sin(radians);
  }

public static boolean getLineIntersection(Vec3 p0, Vec3 p1, Vec3 p2, Vec3 p3, Vec3 out)
  {
  double s1_x, s1_z, s2_x, s2_z;
  s1_x = p1.xCoord - p0.xCoord;
  s1_z = p1.zCoord - p0.zCoord;
  s2_x = p3.xCoord - p2.xCoord; 
  s2_z = p3.zCoord - p2.zCoord;

  double s, t;
  s = (-s1_z * (p0.xCoord - p2.xCoord) + s1_x * (p0.zCoord - p2.zCoord)) / (-s2_x * s1_z + s1_x * s2_z);
  t = ( s2_x * (p0.zCoord - p2.zCoord) - s2_z * (p0.xCoord - p2.xCoord)) / (-s2_x * s1_z + s1_x * s2_z);

  if (s >= 0 && s <= 1 && t >= 0 && t <= 1)
    {
    if(out!=null)
      {
      out.xCoord = p0.xCoord + (t * s1_x);
      out.zCoord = p0.zCoord + (t * s1_z);
      }
    return true;
    }
  return false;
  }

public static boolean getLineIntersection2(Vec3 p0, Vec3 p1, Vec3 p2, Vec3 p3, Vec3 out)
  {
  double s02_x, s02_y, s10_x, s10_y, s32_x, s32_y, s_numer, t_numer, denom, t;
  s10_x = p1.xCoord - p0.xCoord;
  s10_y = p1.zCoord - p0.zCoord;
  s32_x = p3.xCoord - p2.xCoord;
  s32_y = p3.zCoord - p2.zCoord;

  denom = s10_x * s32_y - s32_x * s10_y;
  if (denom == 0){return false;} // Collinear
  boolean denomPositive = denom > 0;

  s02_x = p0.xCoord - p2.xCoord;
  s02_y = p0.zCoord - p2.zCoord;
  s_numer = s10_x * s02_y - s10_y * s02_x;
  if ((s_numer < 0) == denomPositive){return false;}     

  t_numer = s32_x * s02_y - s32_y * s02_x;
  if ((t_numer < 0) == denomPositive){return false;}

  if (((s_numer > denom) == denomPositive) || ((t_numer > denom) == denomPositive)){return false;}
  // Collision detected
  t = t_numer / denom;
  if(out!=null)
    {
    out.xCoord = p0.xCoord + (t * s10_x);
    out.zCoord = p0.zCoord + (t * s10_y);
    }
  return true;
  }

public static double getOverlap(double minA, double maxA, double minB, double maxB)
  {
  if(minA > maxB || maxA < minB)
    {
    return 0;
    }
  else if (minA < minB)
    {
    return minB - maxA;
    }
  else 
    {
    return maxB - minA;
    }
  }

public static double wrapTo360(double in)
  {
  while(in>=360)
    {
    in-=360;
    }
  while(in<0)
    {
    in+=360;
    }
  return in;
  }

public static float wrapTo360(float in)
  {
  while(in>=360.f)
    {
    in-=360.f;
    }
  while(in<0)
    {
    in+=360.f;
    }
  return in;  
  }

public static int getAbsDiff(int a, int b)
  {
  if(a<b)
    {
    return b-a;
    }
  return a-b;
  }

public static float getAbsDiff(float a, float b)
  {
  if(a<b)
    {
    return b-a;
    }
  return a-b;
  }

public static double getAbsDiff(double a, double b)
  {
  if(a<b)
    {
    return b-a;
    }
  return a-b;
  }

/**
 * tests if _test_ is >=min && <=max
 * @param test
 * @param min
 * @param max
 * @return
 */
public static boolean isBetween(int test, int a, int b)
  {
  int min = a < b? a : b;
  int max = a < b? b : a;
  return test >= min && test <=max;
  }

/**
 * is the angle between min and max (inclusive e.g. test <= max && test>=min) 
 * @param test
 * @param min
 * @param max
 * @return
 */
public static boolean isAngleBetween(float test, float min, float max)
  {
  test = Trig.wrapTo360(test);
  min = Trig.wrapTo360(min);
  max = Trig.wrapTo360(max);
  if(min > max)
    {
    return test>=min || test <= max;
    }  
  if(test >= min && test <= max)
    {
    return true;
    }      
  return false;
  }

/**
 * returns the sqrt velocity of the input vectors (asuming base zero)
 * @param x
 * @param y
 * @param z
 * @return
 */
public static float getVelocity(float x, float y, float z)
  {
  return MathHelper.sqrt_float(x*x + y*y + z*z);
  }

public static float getVelocity(double x, double y, double z)
  {
  return Trig.getVelocity((float)x,(float)y,(float)z);
  }

public static float getDistance(double x, double y, double z, double x1, double y1, double z1)
  {
  return Math.abs(getVelocity(x1-x, y1-y, z1-z));
  }

/**
 * get velocity of a 2d vector
 * @param x
 * @param z
 * @return
 */
public static float getVelocity(double x, double z)
  {
  return MathHelper.sqrt_float((float)(x*x + z*z));
  }

public static int getDifference(int a, int b)
  {
  return Math.abs(a-b);
  }

public static int getMin(int a, int b)
  {
  return a < b ? a : b;
  }

public static int getMax(int a, int b)
  {
  return a < b ? b : a;
  }

public static float getAngle(float x, float y)
  {
  return toDegrees((float) Math.atan2(y, x));
  }

public static float getYawTowards(double x, double z, double tx, double tz)
  {
  float xAO = (float) (tx - x);  
  float zAO = (float) (tz - z);
  float yaw = Trig.toDegrees((float) Math.atan2(zAO, xAO));
  yaw = -yaw;
  while(yaw<-180.f)
    {
    yaw+=360.f;
    }
  while (yaw>=180.f)
    {
    yaw-=360.f;
    }
  return yaw;
  }

/**
 * get relative yaw change direction towards target from input yaw
 * @param entityFrom
 * @param x
 * @param oY
 * @param z
 * @return
 */
public static float getYawTowardsTarget(double xStart, double zStart, double x, double z, float originYaw)
  {
  float xAO = (float) (xStart - x);  
  float zAO = (float) (zStart - z);
  float yaw = Trig.toDegrees((float) Math.atan2(xAO, zAO));
  float vehYaw = originYaw;
  while(vehYaw < 0.f)
    {
    vehYaw +=360;
    }
  while(vehYaw >= 360.f)
    {
    vehYaw-=360;
    }
  float yawDiff = yaw - vehYaw;
  while(yawDiff<-180.f)
    {
    yawDiff +=360.f;
    }
  while(yawDiff>=180.f)
    {
    yawDiff-=360.f;
    }
  return yawDiff;
  }

public static float getYawDifference(float yaw, float dest)
  {
  float diff = Trig.getAbsDiff(yaw, dest);
  while(diff < -180)
    {
    diff+=360.f;
    }
  while(diff>=180)
    {
    diff-=360.f;
    }
  return diff;
  }

public static byte getTurnDirection(float yaw, float dest)
  {
  float diff = Trig.getAbsDiff(yaw, dest);
  while(diff<-180)
    {
    diff+=360.f;
    }
  while(diff>=180)
    {
    diff-=360.f;
    }
  return (byte) (diff < 0 ? -1 : 1);
  }

public static double min(double... vals)
  {
  double min = vals[0];
  for(int i = 1; i< vals.length; i++)
    {
    if(vals[i]<min){min=vals[i];}
    }
  return min;
  }

public static double max(double... vals)
  {
  double max = vals[0];
  for(int i = 1; i< vals.length; i++)
    {
    if(vals[i]>max){max=vals[i];}
    }
  return max;
  }

public static float min(float... vals)
  {
  float min = Float.MAX_VALUE;
  for(float val : vals)
    {
    if(val < min){min = val;}
    }
  return min;
  }

public static float max(float... vals)
  {
  float max = Float.MIN_VALUE;
  for(float val : vals)
    {
    if(val > max){max = val;}
    }
  return max;  
  }

}
