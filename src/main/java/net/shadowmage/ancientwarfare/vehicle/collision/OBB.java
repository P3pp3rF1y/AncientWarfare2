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
 * W,L,H values for the OBB.  Must not change after being initially set.
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
private Vec3[] lowerCorners = new Vec3[4];//upper corners would be the same thing, with y=height...so too boring to implement

private float yaw = 0;

public OBB(float width, float height, float length)
  {
  this.width = width;
  this.height = height;
  this.length = length;
  this.halfWidth = width / 2.f;
  this.halfLength = length / 2.f;  
  lowerCorners[0] = Vec3.createVectorHelper(-halfWidth, 0, -halfLength);//front left
  lowerCorners[1] = Vec3.createVectorHelper(halfWidth, 0, -halfLength);//front right
  lowerCorners[2] = Vec3.createVectorHelper(halfWidth, 0, halfLength);//rear right
  lowerCorners[3] = Vec3.createVectorHelper(-halfWidth, 0, halfLength);//rear left
  }

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
  lowerCorners[0].xCoord = tx1;
  lowerCorners[0].zCoord = tz1;
 
  //front-right corner
  lowerCorners[1].xCoord = tx2;
  lowerCorners[1].zCoord = tz2;
  
  //rear-right corner
  lowerCorners[2].xCoord = -lowerCorners[0].xCoord;
  lowerCorners[2].zCoord = -lowerCorners[0].zCoord;
  
  //rear-left corner
  lowerCorners[3].xCoord = -lowerCorners[1].xCoord;
  lowerCorners[3].zCoord = -lowerCorners[1].zCoord;
  }

public boolean collides(AxisAlignedBB bb)
  {
  return false;
  }

/**
 * Return a corner of the OBB for the given index
 * @param index valid values are 0-3
 * @return the currently calculated corner position for the given index
 */
public Vec3 getCorner(int index){return lowerCorners[index];}

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
