package net.shadowmage.ancientwarfare.vehicle.collision;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
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
  }

/**
 * Update this OBB for the input yaw rotation
 * @param yaw
 */
public void updateForRotation(float yaw)
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
  }

public boolean collides(Vec3[] inCorners)
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
public boolean collides(AxisAlignedBB bb)
  {  
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
public boolean collides(OBB bb)
  {
  return collides(bb.corners);
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

}
