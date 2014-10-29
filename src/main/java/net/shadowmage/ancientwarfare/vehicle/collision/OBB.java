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
 * Cached array of Vec3 to use for AABB corners
 */
private Vec3[] aaBBCorners = new Vec3[]
      {
      Vec3.createVectorHelper(0, 0, 0),
      Vec3.createVectorHelper(0, 0, 0),
      Vec3.createVectorHelper(0, 0, 0),
      Vec3.createVectorHelper(0, 0, 0)
      };

/**
 * cached projections to use for overlap testing
 */
private Projection p1 = new Projection(0,0), p2=new Projection(0,0);

/**
 * cached line segments for use in intersection testing
 */
private LineSegment obbEdge1, obbEdge2, obbEdge3, obbEdge4;
private LineSegment obbLine1, obbLine2, obbLine3, obbLine4;

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
  obbEdge1 = new LineSegment(cornerPos[0], cornerPos[1]);
  obbEdge2 = new LineSegment(cornerPos[1], cornerPos[2]);
  obbEdge3 = new LineSegment(cornerPos[2], cornerPos[3]);
  obbEdge4 = new LineSegment(cornerPos[3], cornerPos[0]);
  obbLine1 = new LineSegment(cornerPos[0], copyVec(cornerPos[0]));
  obbLine2 = new LineSegment(cornerPos[1], copyVec(cornerPos[1]));
  obbLine3 = new LineSegment(cornerPos[2], copyVec(cornerPos[2]));
  obbLine4 = new LineSegment(cornerPos[3], copyVec(cornerPos[3]));
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

public final void updateForPositionAndRotation(double x, double y, double z, float yaw)
  {
  updateForRotation(yaw);
  updateForPosition(x, y, z);
  }

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

private static void setVector(Vec3 toSet, double x, double y, double z)
  {
  toSet.xCoord = x;
  toSet.yCoord = y;
  toSet.zCoord = z;
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

public final void setRotation(float yaw)
  {
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

/**
 * Update this OBB for the input yaw rotation. Updates local-origin corners and axis only, does not update world-position corners -- call updateForPosition() for that.<br>
 * Will only update if the input yaw is different than the previous yaw
 * @param yaw
 */
public final void updateForRotation(float yaw)
  {
  yaw = -yaw;//TODO figure out why yaw is inverted for OBB, figure out where else it might be inverted for other calculations
  if(yaw==this.yaw){return;}//do not recalc if yaw has not changed
  setRotation(yaw);
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
public final Vec3 getMinCollisionVector(OBB bb, Vec3 mtvOut)
  {
  return getMinCollisionVector(bb.cornerPos, bb.axis1, bb.axis2, mtvOut);
  }

public final Vec3 getMinCollisionVector(AxisAlignedBB bb, Vec3 mtvOut)
  {
  setVector(aaBBCorners[0], bb.minX, 0, bb.minZ);
  setVector(aaBBCorners[1], bb.maxX, 0, bb.minZ);
  setVector(aaBBCorners[2], bb.maxX, 0, bb.maxZ);
  setVector(aaBBCorners[3], bb.minX, 0, bb.maxZ);
  return getMinCollisionVector(aaBBCorners, aabbAxis1, aabbAxis2, mtvOut);
  }

private Vec3 getMinCollisionVector(Vec3[] inCorners, Axis axis3, Axis axis4, Vec3 mtvOut)
  {
  double minOverlap = Double.MAX_VALUE;
  double overlap = 0;  
  Axis overlapAxis = null;
  
  p1 = axis1.projectShape(cornerPos, p1);
  p2 = axis1.projectShape(inCorners, p2);
  if(!p1.doesOverlap(p2)){return null;}//no collision on that axis
  overlap = p1.getOverlap(p2);
  if(overlap==0){return null;}
  if(Math.abs(overlap)<Math.abs(minOverlap))
    {
    minOverlap = overlap;
    overlapAxis = axis1;
    }
  
  p1 = axis2.projectShape(cornerPos, p1);
  p2 = axis2.projectShape(inCorners, p2);
  if(!p1.doesOverlap(p2)){return null;}//no collision on that axis
  overlap = p1.getOverlap(p2);
  if(overlap==0){return null;}
  if(Math.abs(overlap)<Math.abs(minOverlap))
    {
    minOverlap = overlap;
    overlapAxis = axis2;
    }
  
  p1 = axis3.projectShape(cornerPos, p1);
  p2 = axis3.projectShape(inCorners, p2);
  if(!p1.doesOverlap(p2)){return null;}//no collision on that axis
  overlap = p1.getOverlap(p2);
  if(overlap==0){return null;}
  if(Math.abs(overlap)<Math.abs(minOverlap))
    {
    minOverlap = overlap;
    overlapAxis = axis3;
    }
  
  p1 = axis4.projectShape(cornerPos, p1);
  p2 = axis4.projectShape(inCorners, p2);
  if(!p1.doesOverlap(p2)){return null;}//no collision on that axis
  overlap = p1.getOverlap(p2);
  if(overlap==0){return null;}
  if(Math.abs(overlap)<Math.abs(minOverlap))
    {
    minOverlap = overlap;
    overlapAxis = axis4;
    }
  
  return Vec3.createVectorHelper(overlapAxis.axisX * minOverlap, 0, overlapAxis.axisZ * minOverlap);
  }

public static boolean debug = false;

public final Vec3 getLongCollisionVectorForAxis(AxisAlignedBB bb, Vec3 mtvOut, double xMove, double zMove)
  {
  setVector(aaBBCorners[0], bb.minX, cornerPos[0].yCoord, bb.minZ);
  setVector(aaBBCorners[1], bb.maxX, cornerPos[0].yCoord, bb.minZ);
  setVector(aaBBCorners[2], bb.maxX, cornerPos[0].yCoord, bb.maxZ);
  setVector(aaBBCorners[3], bb.minX, cornerPos[0].yCoord, bb.maxZ);
  return getLongCollisionVectorForAxis(aaBBCorners, aabbAxis1, aabbAxis2, mtvOut, xMove, zMove);
  }

private Vec3 getLongCollisionVectorForAxis(Vec3[] inCorners, Axis axis3, Axis axis4, Vec3 mtvOut, double xMove, double zMove)
  {
  Vec3 mtv = getMinCollisionVector(inCorners, axis3, axis4, mtvOut);
  if(mtv==null){return mtv;}
//  if(mtv.xCoord==0 || mtv.zCoord==0){return mtv;}//was already optimal along a single axis
  
  Vec3 o1 = null;
  Vec3 o2 = null;
  
  Vec3 isec = Vec3.createVectorHelper(0, 0, 0);
  
  int cp1, cp2, icp1, icp2;
  for(int i = 0; i < 4; i++)
    {
    cp1 = i;
    cp2 = (i+1) % 4;
    for(int k = 0; k < 4; k++)
      {
      icp1 = k;
      icp2 = (k + 1) % 4;
      if(getLineIntersection2(cornerPos[cp1], cornerPos[cp2], inCorners[icp1], inCorners[icp2], isec))
        {
        if(o1==null){o1=copyVec(isec);}
        else if(o2==null){o2=copyVec(isec);}
        }
      if(o1!=null && o2!=null){break;}
      }
    if(o1!=null && o2!=null){break;}
    }
  
  if(o1==null || o2==null){return mtv;}
  
  double xc = Math.abs(o1.xCoord-o2.xCoord);
  double zc = Math.abs(o1.zCoord-o2.zCoord);
  if(xMove > 0){xc=-xc;}
  if(zMove > 0){zc=-zc;}  
  if(debug){AWLog.logDebug("intersect data: "+mtv+" : "+o1+" : "+o2 + " :: "+xc+" : "+zc);}
  if(Math.abs(mtv.xCoord)<Math.abs(xc)){mtv.xCoord=xc;}
  if(Math.abs(mtv.zCoord)<Math.abs(zc)){mtv.zCoord=zc;}
     
//  /**
//   * create a triangle out of the intersection, using mtv and obb   we know one length (mtv-length), and two angles.
//   */  
//  float angleA = 90.f;//corner between mtv origin + OBB edge
//  float angleB = Math.abs((yaw) % 90.f);
//  float angleC = 180.f - angleA - angleB;
//  
////  AWLog.logDebug("angles: "+angleA+" : "+angleB+" : "+angleC);
//    
//  float sinA = Trig.TODEGREES * MathHelper.sin(angleA*Trig.TORADIANS);
//  float sinB = Trig.TODEGREES * MathHelper.sin(angleB*Trig.TORADIANS);
//  float sinC = Trig.TODEGREES * MathHelper.sin(angleC*Trig.TORADIANS);  
//  
//  float sideB1 = (float)mtv.lengthVector();  
//  float sideA1 = (sideB1 * sinA)/sinB;  
//  float sideC1 = (sideB1 * sinC)/sinB;  
//  
//  /**
//   * reseat angles for full triangle test now that we know the length of sideA
//   */  
//  angleC = 90;
//  angleA = 180 - angleB - angleC;
//  //side A1 is a known value now
////  AWLog.logDebug("angles2: "+angleA+" : "+angleB+" : "+angleC);
//  
//  sinA = Trig.TODEGREES * MathHelper.sin(angleA*Trig.TORADIANS);
//  sinC = Trig.TODEGREES * MathHelper.sin(angleC*Trig.TORADIANS); 
//  
//  sideB1 = (sideA1 * sinB)/sinA;//need to find B as it is the other possible vector
//  sideC1 = (sideA1 * sinC)/sinA;//don't really need sideC as it is the..un-needed side
//      
//  double bbhw = (inCorners[1].xCoord - inCorners[0].xCoord) / 2.d;
//  double bbhl = (inCorners[2].zCoord - inCorners[1].zCoord) / 2.d;
//  
//  double dx = (inCorners[0].xCoord + bbhw) - x;
//  double dz = (inCorners[0].zCoord + bbhl) - z;
//  
//  if(dx>0){sideA1 = -sideA1;}
//  if(dz>0){sideB1 = -sideB1;}
//  
////  mtv.zCoord = sideA1;
////  mtv.xCoord = sideB1;
//  
//  cornerVec.xCoord = sideA1;
//  cornerVec.yCoord = sideB1;
//  cornerVec.zCoord = sideC1;
//  if(debug){AWLog.logDebug("cornervec: "+cornerVec);}
  
  return mtv;
  }

private boolean getLineIntersection(Vec3 p0, Vec3 p1, Vec3 p2, Vec3 p3, Vec3 out)
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

private boolean getLineIntersection2(Vec3 p0, Vec3 p1, Vec3 p2, Vec3 p3, Vec3 out)
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

public Vec3 getCollisionVectorXMovement(AxisAlignedBB bb, double xMove)
  {    
  setVector(aaBBCorners[0], bb.minX, cornerPos[0].yCoord, bb.minZ);//front left
  setVector(aaBBCorners[1], bb.maxX, cornerPos[0].yCoord, bb.minZ);//front right
  setVector(aaBBCorners[2], bb.maxX, cornerPos[0].yCoord, bb.maxZ);//rear right
  setVector(aaBBCorners[3], bb.minX, cornerPos[0].yCoord, bb.maxZ);//rear left
  setupOBBCollisionLines(xMove, 0);  
  Vec3 interceptBase = Vec3.createVectorHelper(0, 0, 0);
  Vec3 intercept;
  
  double adjustedXMove = xMove;
  
  /**
   * test vectors from the corners of the OBB into the BB
   */
  LineSegment bbEdge = xMove > 0 ? new LineSegment(aaBBCorners[0], aaBBCorners[3]) : new LineSegment(aaBBCorners[1], aaBBCorners[2]);  
  if(cornerPos[0].zCoord>=bb.minZ && cornerPos[0].zCoord<=bb.maxZ)
    {
    intercept = bbEdge.getIntersect(obbLine1, interceptBase); 
    if(intercept!=null)
      {   
      double x = intercept.xCoord - cornerPos[0].xCoord;
      if(Math.abs(x) < Math.abs(adjustedXMove))
        {
        adjustedXMove=x;
        }
      }
    }
  if(cornerPos[1].zCoord>=bb.minZ && cornerPos[1].zCoord<=bb.maxZ)
    {
    intercept = bbEdge.getIntersect(obbLine2, interceptBase);
    if(intercept!=null)
      {
      double x = intercept.xCoord - cornerPos[1].xCoord;
      if(Math.abs(x) < Math.abs(adjustedXMove))
        {
        adjustedXMove=x;
        }
      }
    }
  if(cornerPos[2].zCoord>=bb.minZ && cornerPos[2].zCoord<=bb.maxZ)
    {
    intercept = bbEdge.getIntersect(obbLine3, interceptBase);
    if(intercept!=null)
      {
      double x = intercept.xCoord - cornerPos[2].xCoord;
      if(Math.abs(x) < Math.abs(adjustedXMove))
        {
        adjustedXMove=x;
        }
      }
    }
  if(cornerPos[3].zCoord >= bb.minZ && cornerPos[3].zCoord <= bb.maxZ)
    {
    intercept = bbEdge.getIntersect(obbLine4, interceptBase);
    if(intercept!=null)
      {
      double x = intercept.xCoord - cornerPos[3].xCoord;
      if(Math.abs(x) < Math.abs(adjustedXMove))
        {
        adjustedXMove=x;
        }
      }
    } 
   
  /**
   * need to figure out if should test left or right side of BB, by testing vector from x,y,z to bb center  
   */  
  Vec3 bbVec1 = xMove>0? aaBBCorners[0] : aaBBCorners[1];
  Vec3 bbVec2 = xMove>0? aaBBCorners[3] : aaBBCorners[2]; 
  LineSegment bbLine = new LineSegment(bbVec1, bbVec1.addVector(-xMove, 0, 0));
  LineSegment bbLine2 = new LineSegment(bbVec2, bbVec2.addVector(-xMove, 0, 0));
  //TODO find which obb lines I should be testing against...
   
  //test OBBEdge1
  if(bbVec1.zCoord >= Trig.min(cornerPos[0].zCoord, cornerPos[1].zCoord) && bbVec1.zCoord <= Trig.max(cornerPos[0].zCoord, cornerPos[1].zCoord))
    {    
    intercept = obbEdge1.getIntersect(bbLine, interceptBase);
    if(intercept!=null)
      {
      double x = bbVec1.xCoord - intercept.xCoord;
      if(Math.abs(x) < Math.abs(adjustedXMove))
        {
        adjustedXMove=x;
        }
      }
    }   
  if(bbVec2.zCoord >= Trig.min(cornerPos[0].zCoord, cornerPos[1].zCoord) && bbVec2.zCoord <= Trig.max(cornerPos[0].zCoord, cornerPos[1].zCoord))
    {
    intercept = obbEdge1.getIntersect(bbLine2, interceptBase);
    if(intercept!=null)
      {
      double x = bbVec2.xCoord - intercept.xCoord;
      if(Math.abs(x) < Math.abs(adjustedXMove))
        {
        adjustedXMove=x;
        }
      }
    }  
  
  //test OBBEdge2
  if(bbVec1.zCoord >= Trig.min(cornerPos[1].zCoord, cornerPos[2].zCoord) && bbVec1.zCoord <= Trig.max(cornerPos[1].zCoord, cornerPos[2].zCoord))
    {    
    intercept = obbEdge2.getIntersect(bbLine, interceptBase);
    if(intercept!=null)
      {
      double x = bbVec1.xCoord - intercept.xCoord;
      if(Math.abs(x) < Math.abs(adjustedXMove))
        {
        adjustedXMove=x;
        }
      }
    }   
  if(bbVec2.zCoord >= Trig.min(cornerPos[1].zCoord, cornerPos[2].zCoord) && bbVec2.zCoord <= Trig.max(cornerPos[1].zCoord, cornerPos[2].zCoord))
    {
    intercept = obbEdge2.getIntersect(bbLine2, interceptBase);
    if(intercept!=null)
      {
      double x = bbVec2.xCoord - intercept.xCoord;
      if(Math.abs(x) < Math.abs(adjustedXMove))
        {
        adjustedXMove=x;
        }
      }
    }  
  
  //test OBBEdge3
  if(bbVec1.zCoord >= Trig.min(cornerPos[2].zCoord, cornerPos[3].zCoord) && bbVec1.zCoord <= Trig.max(cornerPos[2].zCoord, cornerPos[3].zCoord))
    {    
    intercept = obbEdge3.getIntersect(bbLine, interceptBase);
    if(intercept!=null)
      {
      double x = bbVec1.xCoord - intercept.xCoord;
      if(Math.abs(x) < Math.abs(adjustedXMove))
        {
        adjustedXMove=x;
        }
      }
    }   
  if(bbVec2.zCoord >= Trig.min(cornerPos[2].zCoord, cornerPos[3].zCoord) && bbVec2.zCoord <= Trig.max(cornerPos[2].zCoord, cornerPos[3].zCoord))
    {
    intercept = obbEdge3.getIntersect(bbLine2, interceptBase);
    if(intercept!=null)
      {
      double x = bbVec2.xCoord - intercept.xCoord;
      if(Math.abs(x) < Math.abs(adjustedXMove))
        {
        adjustedXMove=x;
        }
      }
    }  
  
  //test OBBEdge3
  if(bbVec1.zCoord >= Trig.min(cornerPos[3].zCoord, cornerPos[0].zCoord) && bbVec1.zCoord <= Trig.max(cornerPos[3].zCoord, cornerPos[0].zCoord))
    {    
    intercept = obbEdge4.getIntersect(bbLine, interceptBase);
    if(intercept!=null)
      {
      double x = bbVec1.xCoord - intercept.xCoord;
      if(Math.abs(x) < Math.abs(adjustedXMove))
        {
        adjustedXMove=x;
        }
      }
    }   
  if(bbVec2.zCoord >= Trig.min(cornerPos[3].zCoord, cornerPos[0].zCoord) && bbVec2.zCoord <= Trig.max(cornerPos[3].zCoord, cornerPos[0].zCoord))
    {
    intercept = obbEdge4.getIntersect(bbLine2, interceptBase);
    if(intercept!=null)
      {
      double x = bbVec2.xCoord - intercept.xCoord;
      if(Math.abs(x) < Math.abs(adjustedXMove))
        {
        adjustedXMove=x;
        }
      }
    }  
  
  //TODO find proper epsilon value
  if(adjustedXMove!=x){adjustedXMove*=0.95d;}//minor correction so that entity should never clip into geometry, even with rounding errors...  
  
  Vec3 closestIntercept = Vec3.createVectorHelper(adjustedXMove, 0, 0);
  return closestIntercept;
  }

private void setupOBBCollisionLines(double x, double z)
  {
  obbLine1.end.xCoord = obbLine1.start.xCoord + x;
  obbLine1.end.yCoord = obbLine1.start.yCoord;
  obbLine1.end.zCoord = obbLine1.start.zCoord + z;

  obbLine2.end.xCoord = obbLine2.start.xCoord + x;
  obbLine2.end.yCoord = obbLine2.start.yCoord;
  obbLine2.end.zCoord = obbLine2.start.zCoord + z;

  obbLine3.end.xCoord = obbLine3.start.xCoord + x;
  obbLine3.end.yCoord = obbLine3.start.yCoord;
  obbLine3.end.zCoord = obbLine3.start.zCoord + z;

  obbLine4.end.xCoord = obbLine4.start.xCoord + x;
  obbLine4.end.yCoord = obbLine4.start.yCoord;
  obbLine4.end.zCoord = obbLine4.start.zCoord + z;
  }

public final Vec3 getCollisionVectorZMovement(AxisAlignedBB bb, double zMove)
  {  
  setVector(aaBBCorners[0], bb.minX, cornerPos[0].yCoord, bb.minZ);//front left
  setVector(aaBBCorners[1], bb.maxX, cornerPos[0].yCoord, bb.minZ);//front right
  setVector(aaBBCorners[2], bb.maxX, cornerPos[0].yCoord, bb.maxZ);//rear right
  setVector(aaBBCorners[3], bb.minX, cornerPos[0].yCoord, bb.maxZ);//rear left
  setupOBBCollisionLines(0, zMove);  
  Vec3 interceptBase = Vec3.createVectorHelper(0, 0, 0);
  Vec3 intercept;
  
  double adjustedZMove = zMove;
  
  //TODO find what OBB lines I should be testing against
  /**
   * test vectors from the corners of the OBB into the BB
   */
  LineSegment bbEdge = zMove > 0 ? new LineSegment(aaBBCorners[0], aaBBCorners[1]) : new LineSegment(aaBBCorners[3], aaBBCorners[2]);  
  if(cornerPos[0].xCoord>=bb.minX && cornerPos[0].xCoord<=bb.maxX)
    {
    intercept = bbEdge.getIntersect(obbLine1, interceptBase); 
    if(intercept!=null)
      {   
      double z = intercept.zCoord - cornerPos[0].zCoord;
      if(Math.abs(z) < Math.abs(adjustedZMove))
        {
        adjustedZMove=z;
        }
      }
    }
  if(cornerPos[1].xCoord>=bb.minX && cornerPos[1].xCoord<=bb.maxX)
    {
    intercept = bbEdge.getIntersect(obbLine2, interceptBase);
    if(intercept!=null)
      {
      double z = intercept.zCoord - cornerPos[1].zCoord;
      if(Math.abs(z) < Math.abs(adjustedZMove))
        {
        adjustedZMove=z;
        }
      }
    }
  if(cornerPos[2].xCoord>=bb.minX && cornerPos[2].xCoord<=bb.maxX)
    {
    intercept = bbEdge.getIntersect(obbLine3, interceptBase);
    if(intercept!=null)
      {
      double z = intercept.zCoord - cornerPos[2].zCoord;
      if(Math.abs(z) < Math.abs(adjustedZMove))
        {
        adjustedZMove=z;
        }
      }
    }
  if(cornerPos[3].xCoord >= bb.minX && cornerPos[3].xCoord <= bb.maxX)
    {
    intercept = bbEdge.getIntersect(obbLine4, interceptBase);
    if(intercept!=null)
      {
      double z = intercept.zCoord - cornerPos[3].zCoord;
      if(Math.abs(z) < Math.abs(adjustedZMove))
        {
        adjustedZMove=z;
        }
      }
    } 
   
  /**
   * need to figure out if should test left or right side of BB, by testing vector from x,y,z to bb center  
   */  
  Vec3 bbVec1 = zMove>0? aaBBCorners[0] : aaBBCorners[3];//minX and either min or maxZ
  Vec3 bbVec2 = zMove>0? aaBBCorners[1] : aaBBCorners[2];//maxX and either min or maxZ
  
  LineSegment bbLine = new LineSegment(bbVec1, bbVec1.addVector(0, 0, -zMove));
  LineSegment bbLine2 = new LineSegment(bbVec2, bbVec2.addVector(0, 0, -zMove));  
  //TODO find which obb lines I should be testing against...//   
  //test OBBEdge1
  if(bbVec1.xCoord >= Trig.min(cornerPos[0].xCoord, cornerPos[1].xCoord) && bbVec1.xCoord <= Trig.max(cornerPos[0].xCoord, cornerPos[1].xCoord))
    {    
    intercept = obbEdge1.getIntersect(bbLine, interceptBase);
    if(intercept!=null)
      {
      double z = bbVec1.zCoord - intercept.zCoord;
      if(Math.abs(z) < Math.abs(adjustedZMove))
        {
        adjustedZMove=z;
        }
      }
    }   
  if(bbVec2.xCoord >= Trig.min(cornerPos[0].xCoord, cornerPos[1].xCoord) && bbVec2.xCoord <= Trig.max(cornerPos[0].xCoord, cornerPos[1].xCoord))
    {
    intercept = obbEdge1.getIntersect(bbLine2, interceptBase);
    if(intercept!=null)
      {
      double z = bbVec2.zCoord - intercept.zCoord;
      if(Math.abs(z) < Math.abs(adjustedZMove))
        {
        adjustedZMove=z;
        }
      }
    }    
  //test OBBEdge2
  if(bbVec1.xCoord >= Trig.min(cornerPos[1].xCoord, cornerPos[2].xCoord) && bbVec1.xCoord <= Trig.max(cornerPos[1].xCoord, cornerPos[2].xCoord))
    {    
    intercept = obbEdge2.getIntersect(bbLine, interceptBase);
    if(intercept!=null)
      {
      double z = bbVec1.zCoord - intercept.zCoord;
      if(Math.abs(z) < Math.abs(adjustedZMove))
        {
        adjustedZMove=z;
        }
      }
    }   
  if(bbVec2.xCoord >= Trig.min(cornerPos[1].xCoord, cornerPos[2].xCoord) && bbVec2.xCoord <= Trig.max(cornerPos[1].xCoord, cornerPos[2].xCoord))
    {
    intercept = obbEdge2.getIntersect(bbLine2, interceptBase);
    if(intercept!=null)
      {
      double z = bbVec2.zCoord - intercept.zCoord;
      if(Math.abs(z) < Math.abs(adjustedZMove))
        {
        adjustedZMove=z;
        }
      }
    }      
  //test OBBEdge3
  if(bbVec1.xCoord >= Trig.min(cornerPos[2].xCoord, cornerPos[3].xCoord) && bbVec1.xCoord <= Trig.max(cornerPos[2].xCoord, cornerPos[3].xCoord))
    {    
    intercept = obbEdge3.getIntersect(bbLine, interceptBase);
    if(intercept!=null)
      {
      double z = bbVec1.zCoord - intercept.zCoord;
      if(Math.abs(z) < Math.abs(adjustedZMove))
        {
        adjustedZMove=z;
        }
      }
    }   
  if(bbVec2.xCoord >= Trig.min(cornerPos[2].xCoord, cornerPos[3].xCoord) && bbVec2.xCoord <= Trig.max(cornerPos[2].xCoord, cornerPos[3].xCoord))
    {
    intercept = obbEdge3.getIntersect(bbLine2, interceptBase);
    if(intercept!=null)
      {
      double z = bbVec2.zCoord - intercept.zCoord;
      if(Math.abs(z) < Math.abs(adjustedZMove))
        {
        adjustedZMove=z;
        }
      }
    }        
  //test OBBEdge4
  if(bbVec1.xCoord >= Trig.min(cornerPos[3].xCoord, cornerPos[0].xCoord) && bbVec1.xCoord <= Trig.max(cornerPos[3].xCoord, cornerPos[0].xCoord))
    {    
    intercept = obbEdge4.getIntersect(bbLine, interceptBase);
    if(intercept!=null)
      {
      double z = bbVec1.zCoord - intercept.zCoord;
      if(Math.abs(z) < Math.abs(adjustedZMove))
        {
        adjustedZMove=z;
        }
      }
    }   
  if(bbVec2.xCoord >= Trig.min(cornerPos[3].xCoord, cornerPos[0].xCoord) && bbVec2.xCoord <= Trig.max(cornerPos[3].xCoord, cornerPos[0].xCoord))
    {
    intercept = obbEdge4.getIntersect(bbLine2, interceptBase);
    if(intercept!=null)
      {
      double z = bbVec2.zCoord - intercept.zCoord;
      if(Math.abs(z) < Math.abs(adjustedZMove))
        {
        adjustedZMove=z;
        }
      }
    }  
  
  //TODO find proper epsilon value
  if(adjustedZMove!=z){adjustedZMove*=0.95d;}//minor correction so that entity should never clip into geometry, even with rounding errors...  
  
  Vec3 closestIntercept = Vec3.createVectorHelper(0, 0, adjustedZMove);
  return closestIntercept;
  }

public final Vec3 cornerVec = Vec3.createVectorHelper(0, 0, 0);//TODO debug var, remove...

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

@Override
public String toString()
  {
  // TODO Auto-generated method stub
  return "OBB: "+cornerPos[0]+" : "+cornerPos[1] + " : " +cornerPos[2]+" : "+cornerPos[3];
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

private Projection projectShape(Vec3[] corners, Projection p)
  {
  double d = dot(corners[0]);
  double min = d;
  double max = d;
  int len = corners.length;
  for(int i = 1; i < len; i++)//skip first corner, already used to set starting min/max
    {
    d = dot(corners[i]);
    if(i==0)
      {
      min=max=d;
      }
    else
      {
      if(d < min){min=d;}
      if(d > max){max=d;}
      }
    }
  p.min = min;
  p.max = max;
  return p;
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

//overlap code skimmed from: https://github.com/ghost7/collision/blob/master/sat/Polygon.cpp
public double getOverlap(Projection p)
  {
  if(min > p.max || max < p.min)
    {
    return 0;
    }
  else if (min < p.min)
    {
    return p.min - max;
    }
  else 
    {
    return p.max - min;
    }
  }

@Override
public String toString()
  {
  return String.format("Proj: %.2f |<->| %.2f", min, max);
  }
}

public static final class LineSegment
{

public Vec3 start, end;

public LineSegment()
  {
  start = Vec3.createVectorHelper(0, 0, 0);
  end = Vec3.createVectorHelper(0, 0, 0);
  }

public LineSegment(Vec3 start, Vec3 end)
  {
  this.start=start;
  this.end=end;
  }

public Vec3 getIntersect(LineSegment otherLine, Vec3 out)
  {
  if(getLineIntersection(start, end, otherLine.start, otherLine.end, out)){return out;}
  return null;
  }

private boolean getLineIntersection(Vec3 p0, Vec3 p1, Vec3 p2, Vec3 p3, Vec3 out)
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
}

}
