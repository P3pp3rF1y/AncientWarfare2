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

/**
 * Cached static axis variable to use for interaction with AABBs (x-axis)
 */
private static Axis aabbAxis1 = new Axis(1,0,0);

/**
 * Cached static axis variable to use for interaction with AABBs (z-axis)
 */
private static Axis aabbAxis2 = new Axis(0,0,1);

/**
 * W,L,H values for the OBB
 */
public final float width, height, length;

/**
 * cached halfWidth, halfLength values for the OBB
 */
public final float halfWidth, halfLength;

/**
 * cached values for max width/length extents, used to set an input AABB to cover the extents of this OBB
 */
private float widthExtent, lengthExtent;

/**
 * lower corners of an entity-origin OBB
 */
private Vec3[] corners = new Vec3[4];//upper corners would be the same thing, with y=height...so too boring to implement

/**
 * lower corners of world-origin OBB
 */
private Vec3[] cornerPos = new Vec3[4];//actual world-position corners for the BB;

/**
 * world-origin center of the OBB.  Updated when updateForPosition() is called.
 */
private double x, y, z;

/**
 * cached axis vectors for this OBB, updated when yaw/rotation changes
 */
private Axis axis1, axis2;

/**
 * last calcd yaw value.  Used to determine if new yaw calculations are needed
 */
private float yaw = 0;

private boolean debug = false;

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
  cornerPos[0] = copyVec(corners[0]);
  cornerPos[1] = copyVec(corners[1]);
  cornerPos[2] = copyVec(corners[2]);
  cornerPos[3] = copyVec(corners[3]);
  axis1 = new Axis(1,0,0);
  axis2 = new Axis(0,0,1);
  updateForRotation(0);
  updateAxis();
  }

/**
 * Update the world-position for this OBB.<br>
 * Must be called whenever the entities position is updated<br>
 * Internally it updates the cached world-position corner vectors to reflect the new input entity center position.
 * @param x
 * @param y
 * @param z
 */
public final void updateForPosition(double x, double y, double z)
  {
  this.x = x;
  this.y = y;
  this.z = z;
  for(int i = 0; i < 4; i++)
    {
    updateCornerVector(i);
    }
  }

public void setDebug(boolean val){debug = val;}

/**
 * updates the input world-position corner to be the origin-corner + last known position
 * @param index
 */
private void updateCornerVector(int index)
  {
  cornerPos[index].xCoord = corners[index].xCoord + x;
  cornerPos[index].yCoord = corners[index].yCoord + y;
  cornerPos[index].zCoord = corners[index].zCoord + z;
  }

/**
 * Vector helper function to set one vector identical to another
 * @param input
 * @param toSet
 */
private void setVector(Vec3 input, Vec3 toSet)
  {
  toSet.xCoord = input.xCoord;
  toSet.yCoord = input.yCoord;
  toSet.zCoord = input.zCoord;
  }

/**
 * Vector helper function to copy a vector
 * @param in
 * @return
 */
private Vec3 copyVec(Vec3 in)
  {
  return Vec3.createVectorHelper(in.xCoord, in.yCoord, in.zCoord);
  }

/**
 * Update this OBB for the input yaw rotation.<br>
 * Subsequently calls updateForPosition(currentPos) to update world-corner positions;
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
  updateForPosition(x, y, z);
  }

private void updateAxis()
  {
  axis1.axisX = corners[1].xCoord - corners[0].xCoord;
  axis1.axisZ = corners[1].zCoord - corners[0].zCoord;
  axis1.normalize();
  axis2.axisX = corners[2].xCoord - corners[1].xCoord;
  axis2.axisZ = corners[2].zCoord - corners[1].zCoord;
  axis2.normalize();
  }

/**
 * does not check y-coordinates. 2-d only (as there is no rotation for pitch, y-tests are a simple line overlap test)
 * @param bb
 * @return
 */
public final Vec3 getMinCollisionVector(OBB bb)
  {
  return getMinCollisionVector(bb.cornerPos, bb.axis1, bb.axis2);
  }

public final Vec3 getMinCollisionVector(AxisAlignedBB bb)
  {
  if(bb.minY > cornerPos[0].yCoord + height || bb.maxY < cornerPos[0].yCoord){return null;}//quickly check Y-intersection prior to other tests
  Vec3 c1 = Vec3.createVectorHelper(bb.minX, 0, bb.minZ);
  Vec3 c2 = Vec3.createVectorHelper(bb.maxX, 0, bb.minZ);
  Vec3 c3 = Vec3.createVectorHelper(bb.maxX, 0, bb.maxZ);
  Vec3 c4 = Vec3.createVectorHelper(bb.minX, 0, bb.maxZ);
  return getMinCollisionVector(new Vec3[]{c1, c2, c3, c4}, aabbAxis1, aabbAxis2);
  }

private Vec3 getMinCollisionVector(Vec3[] inCorners, Axis axis3, Axis axis4)
  {
  double minOverlap = Double.MAX_VALUE;
  double overlap = 0;  
  Axis overlapAxis = null;
  
  Projection p1 = axis1.projectShape(cornerPos);
  Projection p2 = axis1.projectShape(inCorners);
  if(!p1.doesOverlap(p2)){return null;}//no collision on that axis
  overlap = p1.getOverlap(p2);
  if(debug){AWLog.logDebug("overlap axis: "+axis1+" :: "+overlap+" p: "+p1+" :: "+p2);}
  if(Math.abs(overlap)<Math.abs(minOverlap))
    {
    minOverlap = overlap;
    overlapAxis = axis1;
    }
  
  p1 = axis2.projectShape(cornerPos);
  p2 = axis2.projectShape(inCorners);
  if(!p1.doesOverlap(p2)){return null;}//no collision on that axis
  overlap = p1.getOverlap(p2);
  if(debug){AWLog.logDebug("overlap axis: "+axis2+" :: "+overlap+" p: "+p1+" :: "+p2);}
  if(Math.abs(overlap)<Math.abs(minOverlap))
    {
    minOverlap = overlap;
    overlapAxis = axis2;
    }
  
  p1 = axis3.projectShape(cornerPos);
  p2 = axis3.projectShape(inCorners);
  if(!p1.doesOverlap(p2)){return null;}//no collision on that axis
  overlap = p1.getOverlap(p2);
  if(debug){AWLog.logDebug("overlap axis: "+axis3+" :: "+overlap+" p: "+p1+" :: "+p2);}
  if(Math.abs(overlap)<Math.abs(minOverlap))
    {
    minOverlap = overlap;
    overlapAxis = axis3;
    }
  
  p1 = axis4.projectShape(cornerPos);
  p2 = axis4.projectShape(inCorners);
  if(!p1.doesOverlap(p2)){return null;}//no collision on that axis
  overlap = p1.getOverlap(p2);
  if(debug){AWLog.logDebug("overlap axis: "+axis4+" :: "+overlap+" p: "+p1+" :: "+p2);}
  if(Math.abs(overlap)<Math.abs(minOverlap))
    {
    minOverlap = overlap;
    overlapAxis = axis4;
    }
  if(debug){AWLog.logDebug("final overlap: "+overlapAxis+" :: "+minOverlap);}
  return Vec3.createVectorHelper(overlapAxis.axisX * minOverlap, 0, overlapAxis.axisZ * minOverlap);
  }

/**
 * Return a corner of the OBB for the given index<br>
 * 0=front left<br>
 * 1=front right<br>
 * 2=rear right<br>
 * 3=rear left<br>
 * @param index valid values are 0-3
 * @return the currently calculated corner position for the given index, vec is in world coordinates
 */
public Vec3 getCorner(int index){return cornerPos[index];}

/**
 * Sets the input bb to encompass the extents of the OBB<br>
 * @param bb the AABB to be set
 */
public final void setAABBToOBBExtents(AxisAlignedBB bb)
  {
  bb.setBounds(-widthExtent, 0, -lengthExtent, widthExtent, height, lengthExtent);
  bb.offset(x, y, z);
  }

public static final class Axis
{

public double axisX, axisY, axisZ;

public Axis(double x, double y, double z)
  {
  this.axisX = x;
  this.axisY = y;
  this.axisZ = z;
  normalize();
  }

public final void normalize()
  {
  double sq = axisX*axisX+axisY*axisY+axisZ*axisZ;
  if(Math.abs(sq)>0.0000001d)
    {
    sq = Math.sqrt(sq);
    axisX /= sq;
    axisY /= sq;
    axisZ /= sq;
    }  
  }

public final double dot(Vec3 vec)
  {
  return axisX * vec.xCoord + axisY * vec.yCoord + axisZ * vec.zCoord;
  }

@Override
public String toString()
  {  
  return String.format("Axis: %.2f, %.2f, %.2f", axisX, axisY, axisZ);
  }

private Projection projectShape(Vec3[] corners)
  {
  double min = Double.MAX_VALUE;
  double max = Double.MIN_VALUE;
  double d;
  for(Vec3 p : corners)
    {    
    d = dot(p);
    if(d < min){min=d;}
    if(d > max){max=d;}
    }
  return new Projection(min, max);
  }

}

public static final class Projection
{

public double min, max;

public Projection(double min, double max)
  {
  this.min=min;
  this.max=max;
  }

public boolean doesOverlap(Projection p)
  {
  if(p.max<min){return false;}
  if(p.min>max){return false;}
  return true;
  }

public double getOverlap(Projection p)
  {
  if(min>p.max){return 0;}
  if(max<p.min){return 0;}
  
  if(max==p.max && min==p.min){return max-min;}//complete overlap of same size...weird...no way to know which direction is correct....
  else if(max>=p.min){return p.min-max;}
  else if(min<=p.max){return p.max-min;}
  return 0;
  }

@Override
public String toString()
  {
  return String.format("Proj: %.2f |<->| %.2f", min, max);
  }
}

}
