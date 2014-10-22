package net.shadowmage.ancientwarfare.vehicle.collision;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.util.Trig;

/**
 * Entity Origin-local OBB for entity collision detection between other OBB and vanilla minecraft AABBs<br>
 * OBB keeps vectors in local space (relative to entity).<br>
 * Need to call updateForRotation(float) whenever rotation changes for the entity.  This will update the internal
 * coordinates of the OBB.<br>
 * After that the entities vanilla AABB will need to be updated by calling setAABBToOBBExtents(AxisAlignedBB), passing in the entities bounding box,
 * this will set the extents of the bounding box to encompass the entire OBB.
 * @author Shadowmage
 */
public final class OBB
{

private static Axis aabbAxis1 = new Axis(1,0,0);
private static Axis aabbAxis2 = new Axis(0,0,1);

/**
 * W,L,H values for the OBB
 */
public final float width, height, length;
public final float halfWidth, halfLength;

/**
 * cached values for max width/length extents, used to set an input AABB to cover the extents of this OBB
 */
private float widthExtent, lengthExtent;

/**
 * corners of a entity-origin OBB
 */
private Vec3[] corners = new Vec3[4];//upper corners would be the same thing, with y=height...so too boring to implement
private Axis axis1, axis2;

private float yaw = 0;
/**
 * private vectors used during some calculations to reduce object churn
 */
private Vec3 scratch = Vec3.createVectorHelper(0, 0, 0);

public OBB(float width, float height, float length)
  {
  this.width = width;
  this.height = height;
  this.length = length;
  this.halfWidth = width / 2.f;
  this.halfLength = length / 2.f;  
  corners[0] = Vec3.createVectorHelper(-halfWidth, 0, -halfLength);//front left
  corners[1] = Vec3.createVectorHelper(halfWidth, 0, -halfLength);//front right
  corners[2] = Vec3.createVectorHelper(halfWidth, 0, halfLength);//rear right
  corners[3] = Vec3.createVectorHelper(-halfWidth, 0, halfLength);//rear left
  axis1 = new Axis(0,0,0);
  axis2 = new Axis(0,0,0);
  updateForRotation(0);
  updateAxis();  
  }

/**
 * return the scalar-position point for a given corner projected onto an axis (combines projectPoint and projectScalar)
 * @param p
 * @param a
 * @return
 */
private double projectOntoAxis(Vec3 p, Vec3 a)
  {
  return projectScalar(projectPoint(p, a, scratch), a);
  }

/**
 * projects a point onto the given axis.<br>
 * the returned vector is the input point projected onto the input axis.
 * @param p
 * @param a
 * @param out the vector to store the result in.  must not be null
 * @return
 */
private Vec3 projectPoint(Vec3 p, Vec3 a, Vec3 out)
  {    
  double supper = p.xCoord * a.xCoord + p.zCoord * a.zCoord;
  double slower = a.xCoord * a.xCoord + a.zCoord * a.zCoord;
  double scalar = supper/slower;
  out.xCoord = scalar*a.xCoord;
  out.zCoord = scalar*a.zCoord;
  return out;
  }

/**
 * returns a scalar value that denotes a points position on an axis, relative to other values returned from this method<br>
 * overall the return value is meaningless aside from a method to determine ordering on a line.
 * @param p
 * @param a
 * @return
 */
private double projectScalar(Vec3 p, Vec3 a)
  {
  return p.xCoord * a.xCoord + p.zCoord * a.zCoord; 
  }

private void updateAxis()
  {
  axis1.x = corners[1].xCoord - corners[0].xCoord;
  axis1.z = corners[1].zCoord - corners[0].zCoord;
  axis1.normalize();
  axis2.x = corners[2].xCoord - corners[1].xCoord;
  axis2.z = corners[2].zCoord - corners[1].zCoord;
  axis2.normalize();
  }

/**
 * Update this OBB for the input yaw rotation
 * @param yaw
 */
public final void updateForRotation(float yaw)
  {
  if(yaw==this.yaw){return;}//do not recalc if yaw has not changed
  this.yaw = yaw;
  float yawRad = Trig.TORADIANS * yaw;
  float cos = MathHelper.cos(yawRad);
  float sin = MathHelper.sin(yawRad);
   
  float x1 = -halfWidth;
  float z1 = -halfLength;
  float x2 = halfWidth;
  float z2 = -halfLength; 
  
  float tx1 = x1 * cos - z1 * sin;
  float tz1 = x1 * sin + z1 * cos;
  float tx2 = x2 * cos - z2 * sin;
  float tz2 = x2 * sin + z2 * cos;

  widthExtent = Math.max(Math.abs(tx1), Math.abs(tx2));
  lengthExtent = Math.max(Math.abs(tz1), Math.abs(tz2));
  
  //front-left corner
  corners[0].xCoord = tx1;
  corners[0].zCoord = tz1;
 
  //front-right corner
  corners[1].xCoord = tx2;
  corners[1].zCoord = tz2;
  
  //rear-right corner
  corners[2].xCoord = -corners[0].xCoord;
  corners[2].zCoord = -corners[0].zCoord;
  
  //rear-left corner
  corners[3].xCoord = -corners[1].xCoord;
  corners[3].zCoord = -corners[1].zCoord;
  updateAxis();
  }

public final boolean collides(Vec3[] inCorners)
  {
  /**
   * basic SAT test for OBBs.
   */  
  
  /**
   * Scratch values, for use in calculations
   */
  double s1, s2, s3, s4, minA, maxA, minB, maxB;
  
  /**
   * Axis vectors, determined by the input bounding boxes
   */
  //x-axis for this OBB
  Vec3 axis1 = Vec3.createVectorHelper(corners[1].xCoord - corners[0].xCoord , 0, corners[1].zCoord - corners[0].zCoord);
  //z-axis for this OBB  
  Vec3 axis2 = Vec3.createVectorHelper(corners[2].xCoord - corners[1].xCoord , 0, corners[2].zCoord - corners[1].zCoord);
    
  
  //x-axis for input BB
  Vec3 axis3 = Vec3.createVectorHelper(inCorners[1].xCoord - inCorners[0].xCoord, 0, inCorners[1].zCoord - inCorners[0].zCoord);
  //z-axis for input BB
  Vec3 axis4 = Vec3.createVectorHelper(inCorners[2].xCoord - inCorners[1].xCoord, 0, inCorners[2].zCoord - inCorners[1].zCoord);

  /**
   * Axis 1
   */
  s1 = projectOntoAxis(corners[0], axis1);
  s2 = projectOntoAxis(corners[1], axis1);
  s3 = projectOntoAxis(corners[2], axis1);
  s4 = projectOntoAxis(corners[3], axis1);
  minA = Math.min(Math.min(s1, s2), Math.min(s3, s4));
  maxA = Math.max(Math.max(s1, s2), Math.max(s3, s4));

  s1 = projectOntoAxis(inCorners[0], axis1);
  s2 = projectOntoAxis(inCorners[1], axis1);
  s3 = projectOntoAxis(inCorners[2], axis1);
  s4 = projectOntoAxis(inCorners[3], axis1);
  minB = Math.min(Math.min(s1, s2), Math.min(s3, s4));
  maxB = Math.max(Math.max(s1, s2), Math.max(s3, s4));
  if(maxB < minA || minB > maxA)//no overlap
    {
    return false;
    }

  /**
   * Axis 2
   */
  s1 = projectOntoAxis(corners[0], axis2);
  s2 = projectOntoAxis(corners[1], axis2);
  s3 = projectOntoAxis(corners[2], axis2);
  s4 = projectOntoAxis(corners[3], axis2);
  minA = Math.min(Math.min(s1, s2), Math.min(s3, s4));
  maxA = Math.max(Math.max(s1, s2), Math.max(s3, s4));

  s1 = projectOntoAxis(inCorners[0], axis2);
  s2 = projectOntoAxis(inCorners[1], axis2);
  s3 = projectOntoAxis(inCorners[2], axis2);
  s4 = projectOntoAxis(inCorners[3], axis2);
  minB = Math.min(Math.min(s1, s2), Math.min(s3, s4));
  maxB = Math.max(Math.max(s1, s2), Math.max(s3, s4));
  if(maxB < minA || minB > maxA)//no overlap
    {
    return false;
    }

  /**
   * Axis 3
   */
  s1 = projectOntoAxis(corners[0], axis3);
  s2 = projectOntoAxis(corners[1], axis3);
  s3 = projectOntoAxis(corners[2], axis3);
  s4 = projectOntoAxis(corners[3], axis3);
  minA = Math.min(Math.min(s1, s2), Math.min(s3, s4));
  maxA = Math.max(Math.max(s1, s2), Math.max(s3, s4));

  s1 = projectOntoAxis(inCorners[0], axis3);
  s2 = projectOntoAxis(inCorners[1], axis3);
  s3 = projectOntoAxis(inCorners[2], axis3);
  s4 = projectOntoAxis(inCorners[3], axis3);
  minB = Math.min(Math.min(s1, s2), Math.min(s3, s4));
  maxB = Math.max(Math.max(s1, s2), Math.max(s3, s4));
  if(maxB < minA || minB > maxA)//no overlap
    {
    return false;
    }

  /**
   * Axis 3
   */
  s1 = projectOntoAxis(corners[0], axis4);
  s2 = projectOntoAxis(corners[1], axis4);
  s3 = projectOntoAxis(corners[2], axis4);
  s4 = projectOntoAxis(corners[3], axis4);
  minA = Math.min(Math.min(s1, s2), Math.min(s3, s4));
  maxA = Math.max(Math.max(s1, s2), Math.max(s3, s4));

  s1 = projectOntoAxis(inCorners[0], axis4);
  s2 = projectOntoAxis(inCorners[1], axis4);
  s3 = projectOntoAxis(inCorners[2], axis4);
  s4 = projectOntoAxis(inCorners[3], axis4);
  minB = Math.min(Math.min(s1, s2), Math.min(s3, s4));
  maxB = Math.max(Math.max(s1, s2), Math.max(s3, s4));
  if(maxB < minA || minB > maxA)//no overlap
    {
    return false;
    }
  return true;
  }

/**
 * return true if the input AABB collides with this OBB
 * @param bb
 * @return
 */
public final boolean collides(AxisAlignedBB bb)
  {  
  if(bb.minY>corners[0].yCoord+height || bb.maxY<corners[0].yCoord){return false;}//quickly check Y-intersection prior to other tests
  Vec3 c1 = Vec3.createVectorHelper(bb.minX, 0, bb.minZ);
  Vec3 c2 = Vec3.createVectorHelper(bb.maxX, 0, bb.minZ);
  Vec3 c3 = Vec3.createVectorHelper(bb.maxX, 0, bb.maxZ);
  Vec3 c4 = Vec3.createVectorHelper(bb.minX, 0, bb.maxZ);
  return collides(new Vec3[]{c1, c2, c3, c4});
  }

/**
 * return true if the input OBB collides with this OBB
 * @param bb
 * @return
 */
public final boolean collides(OBB bb)
  {
  return collides(bb.corners);
  }

public final Vec3 getMinCollisionVector(OBB bb)
  {
  return getMinCollisionVector(bb.corners, bb.axis1, bb.axis2);
  }

public final Vec3 getMinCollisionVector(AxisAlignedBB bb)
  {
  if(bb.minY>corners[0].yCoord + height || bb.maxY < corners[0].yCoord){return null;}//quickly check Y-intersection prior to other tests
  Vec3 c1 = Vec3.createVectorHelper(bb.minX, 0, bb.minZ);
  Vec3 c2 = Vec3.createVectorHelper(bb.maxX, 0, bb.minZ);
  Vec3 c3 = Vec3.createVectorHelper(bb.maxX, 0, bb.maxZ);
  Vec3 c4 = Vec3.createVectorHelper(bb.minX, 0, bb.maxZ);
  return getMinCollisionVector(new Vec3[]{c1, c2, c3, c4}, aabbAxis1, aabbAxis2);
  }

public final Vec3 getMinCollisionVector(Vec3[] inCorners, Axis axis3, Axis axis4)
  {
  double minOverlap = Double.MAX_VALUE;
  double overlap = 0;  
  Axis overlapAxis = null;
  
  Projection p1 = axis1.projectShape(corners);
  Projection p2 = axis1.projectShape(inCorners);
  overlap = p1.getOverlap(p2);
  AWLog.logDebug("calced overlap for axis1: "+overlap);
  if(overlap==0)
    {
    return null;
    }//no collision on that axis
  else if (Math.abs(overlap)<Math.abs(minOverlap))
    {
    minOverlap = overlap;
    overlapAxis = axis1;
    }
  
  p1 = axis2.projectShape(corners);
  p2 = axis2.projectShape(inCorners);
  overlap = p1.getOverlap(p2);
  AWLog.logDebug("calced overlap for axis2: "+overlap);
  if(overlap==0){return null;}//no collision on that axis
  else if (Math.abs(overlap)<Math.abs(minOverlap))
    {
    minOverlap = overlap;
    overlapAxis = axis2;
    }
  
  p1 = axis3.projectShape(corners);
  p2 = axis3.projectShape(inCorners);
  overlap = p1.getOverlap(p2);
  AWLog.logDebug("calced overlap for axis3: "+overlap);
  if(overlap==0){return null;}//no collision on that axis
  else if (Math.abs(overlap)<Math.abs(minOverlap))
    {
    minOverlap = overlap;
    overlapAxis = axis3;
    }
  
  p1 = axis4.projectShape(corners);
  p2 = axis4.projectShape(inCorners);
  overlap = p1.getOverlap(p2);
  AWLog.logDebug("calced overlap for axis4: "+overlap);
  if(overlap==0){return null;}//no collision on that axis
  else if (Math.abs(overlap)<Math.abs(minOverlap))
    {
    minOverlap = overlap;
    overlapAxis = axis4;
    }
   
  return Vec3.createVectorHelper(overlapAxis.x*minOverlap, 0, overlapAxis.z*minOverlap);
  }

/**
 * Return a corner of the OBB for the given index
 * @param index valid values are 0-3
 * @return the currently calculated corner position for the given index
 */
public Vec3 getCorner(int index){return corners[index];}

/**
 * Sets the input bb to encompass the extents of the OBB<br>
 * the altered AABB is in local-coordinates, and will need to be offset by the entities posX, posY, posZ to be a world-coordinate AABB
 * @param bb the AABB to be set
 */
public final void setAABBToOBBExtents(AxisAlignedBB bb)
  {
  bb.setBounds(-widthExtent, 0, -lengthExtent, widthExtent, height, lengthExtent);
  }

private static class Axis
{

double x, y, z;

public Axis(double x, double y, double z)
  {
  this.x = x;
  this.y = y;
  this.z = z;
  normalize();
  }

public final void normalize()
  {
  double sq = x*x+y*y+z*z;
  if(sq>0.0000001d)
    {
    sq = Math.sqrt(sq);
    x/=sq;
    y/=sq;
    z/=sq;
    }  
  }

public final double dot(Vec3 vec)
  {
  return x * vec.xCoord + y * vec.yCoord + z * vec.zCoord;
  }

@Override
public String toString()
  {  
  return String.format("Axis: %.2f, %.2f, %.2f", x, y, z);
  }

private Projection projectShape(Vec3[] corners)
  {
  double min = Double.MAX_VALUE;
  double max = Double.MIN_VALUE;
  double d;
  for(Vec3 p : corners)
    {    
    d = dot(p);
    if(d<min){min=d;}
    if(d>max){max=d;}
    }
  AWLog.logDebug("returning projection min/max: "+min+"::"+max+" for axis: "+this);
  return new Projection(min, max);
  }

}

private static class Projection
{

double min, max;

public Projection(double min, double max)
  {
  this.min=min;
  this.max=max;
  }

public double getOverlap(Projection p)
  {
  if(p.max<min){return 0;}//input ends before this starts
  if(p.min>max){return 0;}//this ends before input starts
  
  //TODO...no clue if the rest of this is right
  //kind of seems to be
  double d1 = max - p.min;
  double d2 = p.min - max;
  return Math.abs(d1) < Math.abs(d2)? d1 : d2;
  }

@Override
public String toString()
  {
  return String.format("Proj: %.2f |<->| %.2f", min, max);
  }
}



}
