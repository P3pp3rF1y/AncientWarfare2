package net.shadowmage.ancientwarfare.vehicle.entity.collision;

import java.util.List;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.vehicle.collision.OBB;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;

/**
 * Base OBB Movement helper.<br>
 * Responsible for updating position of the vehicle.<br>
 * Has a single OBB to represent the main collision box of the vehicle<br>
 * @author John
 *
 */
public class VehicleOBBMoveHelper
{

private VehicleBase vehicle;
private OBB orientedBoundingBox;

public VehicleOBBMoveHelper(VehicleBase vehicle)
  {
  this.vehicle = vehicle;
  orientedBoundingBox = new OBB(vehicle.vehicleWidth, vehicle.vehicleHeight, vehicle.vehicleLength);  
  orientedBoundingBox.setRotation(-0);
  orientedBoundingBox.setAABBToOBBExtents(vehicle.boundingBox);
  }

public void update()
  {
  orientedBoundingBox.updateForPositionAndRotation(vehicle.posX, vehicle.posY, vehicle.posZ, vehicle.rotationYaw);
  }

@SuppressWarnings("unchecked")
public void moveVehicle(double x, double y, double z)
  {
  AxisAlignedBB boundingBox = vehicle.boundingBox;
  World worldObj = vehicle.worldObj;
  double posX = vehicle.posX;
  double posY = vehicle.posY;
  double posZ = vehicle.posZ;
  float rotationYaw = vehicle.rotationYaw;
  float stepHeight = vehicle.stepHeight;
  if(Math.abs(x)<0.001d){x=0.d;}
  if(Math.abs(z)<0.001d){z=0.d;}
  orientedBoundingBox.updateForPositionAndRotation(posX, posY, posZ, rotationYaw);
  orientedBoundingBox.setAABBToOBBExtents(boundingBox);
  List<AxisAlignedBB>  aabbs = worldObj.getCollidingBoundingBoxes(vehicle, boundingBox.expand(Math.abs(x) + 0.2d, Math.abs(y) + stepHeight + 0.2d, Math.abs(z) + 0.2d));
  //first do Y movement test, use basic OBB vs bbs test, move downard if not collided 
  double xMove, yMove, zMove;
  
  yMove = y < 0 ? getYNegativeMove(y, aabbs) : y > 0 ? getYPositiveMove(y, aabbs) : 0;
  if(yMove!=0)
    {
    vehicle.setPosition(posX, posY+yMove, posZ);
    orientedBoundingBox.updateForPositionAndRotation(posX, posY, posZ, rotationYaw);
    orientedBoundingBox.setAABBToOBBExtents(boundingBox);
    }
  xMove = getXmove(x, aabbs);
  if(xMove!=0)
    {
    vehicle.setPosition(posX+xMove, posY, posZ);
    orientedBoundingBox.updateForPositionAndRotation(posX, posY, posZ, rotationYaw);
    orientedBoundingBox.setAABBToOBBExtents(boundingBox);   
    }
  zMove = getZMove(z, aabbs);
  if(zMove!=0)
    {
    vehicle.setPosition(posX, posY, posZ+zMove);
    orientedBoundingBox.updateForPositionAndRotation(posX, posY, posZ, rotationYaw);
    orientedBoundingBox.setAABBToOBBExtents(boundingBox);    
    }
    
  if(stepHeight>0 && yMove <= 0 && (x!=xMove || z!=zMove))//attempt to step upwards by step-height
    {    
    //remainder of movement for x and z axes
    double mx = x - xMove;
    double mz = z - zMove;
    orientedBoundingBox.updateForPositionAndRotation(posX+mx, posY, posZ+mz, rotationYaw);
    orientedBoundingBox.setAABBToOBBExtents(boundingBox);
    double my = getYStepHeight(aabbs);
    if(my>0)
      {
      vehicle.setPosition(posX+mx, posY+my, posZ+mz);
      }
    orientedBoundingBox.updateForPositionAndRotation(posX, posY, posZ, rotationYaw);
    orientedBoundingBox.setAABBToOBBExtents(boundingBox);
    }  
  }

@SuppressWarnings("unchecked")
public void rotateVehicle(float rotationDelta)
  {
  AxisAlignedBB boundingBox = vehicle.boundingBox;
  World worldObj = vehicle.worldObj;
  double posX = vehicle.posX;
  double posY = vehicle.posY;
  double posZ = vehicle.posZ;
  float rotationYaw = vehicle.rotationYaw;
  orientedBoundingBox.updateForPositionAndRotation(posX, posY, posZ, rotationYaw + rotationDelta);
  orientedBoundingBox.setAABBToOBBExtents(boundingBox);
  
  Vec3 mtvTempBase = Vec3.createVectorHelper(0,0,0);
  Vec3 mtvTemp = null;
  Vec3 mtv = null;  
  
  List<AxisAlignedBB> aabbs = worldObj.getCollidingBoundingBoxes(vehicle, boundingBox.expand(0.2d, 0, 0.2d));  
  
  AxisAlignedBB bb = null;
  int len = aabbs.size();
  for(int i = 0; i< len; i++)
    { 
    bb = aabbs.get(i);
    mtvTemp = orientedBoundingBox.getMinCollisionVector(bb, mtvTempBase);    
    if(mtvTemp!=null)
      {
      if(mtv==null)
        {
        mtv = Vec3.createVectorHelper(mtvTemp.xCoord, 0, mtvTemp.zCoord);
        }
      else
        {
        if(Math.abs(mtvTemp.xCoord)>Math.abs(mtv.xCoord)){mtv.xCoord=mtvTemp.xCoord;}
        if(Math.abs(mtvTemp.zCoord)>Math.abs(mtv.zCoord)){mtv.zCoord=mtvTemp.zCoord;}
        }
      }
    }
  
  if(mtv==null)//set position from move as there is no collision
    {
    rotationYaw += rotationDelta;
    }
  else
    {
    mtv.xCoord*=1.1d;
    mtv.zCoord*=1.1d;
    vehicle.setPosition(posX + mtv.xCoord, posY, posZ + mtv.zCoord);
    orientedBoundingBox.updateForPositionAndRotation(posX, posY, posZ, rotationYaw + rotationDelta);
    orientedBoundingBox.setAABBToOBBExtents(boundingBox);    
    aabbs = worldObj.getCollidingBoundingBoxes(vehicle, boundingBox.expand(0.2d, 0, 0.2d));      
    bb = null;
    len = aabbs.size();
    mtvTemp = null;
    for(int i = 0; i< len; i++)
      { 
      bb = aabbs.get(i);
      mtvTemp = orientedBoundingBox.getMinCollisionVector(bb, mtvTempBase);    
      if(mtvTemp!=null)
        {
        orientedBoundingBox.updateForRotation(rotationYaw);
        orientedBoundingBox.updateForPosition(posX, posY, posZ);
        orientedBoundingBox.setAABBToOBBExtents(boundingBox);        
        break;
        }
      }
    if(mtvTemp==null)//slide was good
      {
      rotationYaw += rotationDelta;
      vehicle.setPosition(posX, posY, posZ);
      orientedBoundingBox.updateForPositionAndRotation(posX, posY, posZ, rotationYaw);
      orientedBoundingBox.setAABBToOBBExtents(boundingBox); 
      }
    else//slide was no good, revert (do not rotate at all)
      {
      vehicle.setPosition(posX - mtv.xCoord, posY, posZ-mtv.zCoord);
      orientedBoundingBox.updateForPositionAndRotation(posX, posY, posZ, rotationYaw);
      orientedBoundingBox.setAABBToOBBExtents(boundingBox);
      }    
    }  
  }

/**
 * locate the closest AABB top that the vehicle is resting on or will rest on given the input yMotion<br>
 * return adjusted y motion to rest on top of the highest colliding bounding-box.
 * @param yMotion must be a negative value
 * @param aabbs a list of -potentially- colliding AABBs
 * @return
 */
private double getYNegativeMove(double yMotion, List<AxisAlignedBB> aabbs)
  {
  Vec3 mtvTempBase = Vec3.createVectorHelper(0, 0, 0);
  Vec3 mtvTemp = null;
  AxisAlignedBB bb = null;
  int len = aabbs.size();
  double maxFoundY = vehicle.posY + yMotion;
  for(int i = 0; i < len; i++)
    {
    bb = aabbs.get(i);
    if(bb.maxY <= maxFoundY){continue;}//to far below to care
    if(bb.minY >= vehicle.boundingBox.maxY){continue;}//to far above to care
    mtvTemp = orientedBoundingBox.getMinCollisionVector(bb, mtvTempBase);//check each bb vs the OBB for x/z collision
    if(mtvTemp!=null)//it collides, check for top height
      {
      if(bb.maxY>maxFoundY){maxFoundY=bb.maxY;}
      }
    }
  return maxFoundY - vehicle.posY;
  }

/**
 * Locate the maximum amount this entity can move in the positive Y direction before encountering a colliding object.
 * @param yMotion
 * @param aabbs
 * @return
 */
private double getYPositiveMove(double yMotion, List<AxisAlignedBB> aabbs)
  {
  Vec3 mtvTempBase = Vec3.createVectorHelper(0, 0, 0);
  Vec3 mtvTemp = null;
  AxisAlignedBB bb = null;
  int len = aabbs.size();
  double minFoundY = vehicle.posY + vehicle.vehicleHeight + yMotion;//the max to check
  for(int i = 0; i < len; i++)
    {
    bb = aabbs.get(i);
    if(bb.maxY < vehicle.posY+vehicle.height){continue;}//dont care about stuff already colliding or below the vehicle
    if(bb.minY >= minFoundY){continue;}//also dont care about stuff too high to reach from input movement
    mtvTemp = orientedBoundingBox.getMinCollisionVector(bb, mtvTempBase);//check each bb vs the OBB for x/z collision
    if(mtvTemp!=null)//it collides, check for top height
      {
      if(bb.minY<minFoundY){minFoundY=bb.minY;}
      }
    }
  return minFoundY - (vehicle.posY+vehicle.vehicleHeight);
  }

/**
 * vehicle OBB should alread be moved into the position to step upwards from before this method is called.<br>
 * (test both axes simultaneously, do not step if -any- collision would be found)<br>
 * does not move vehicle, only returns the move that -should- be made.
 * @return < 0 for invalid step (no fit found), >=0 for valid step-up operation (position should be valid and uncollided post step)
 */
private double getYStepHeight(List<AxisAlignedBB> aabbs)
  {
  double maxYCheck = vehicle.posY+vehicle.stepHeight+vehicle.vehicleHeight;
  double maxYPosition = vehicle.posY+vehicle.stepHeight;
  double minYPosition = vehicle.posY;
  
  double maxRestingY = vehicle.posY;
  double minHeadHight = maxYCheck;
  
  Vec3 mtvTempBase = Vec3.createVectorHelper(0, 0, 0);
  Vec3 mtvTemp = null;
  AxisAlignedBB bb = null;
  int len = aabbs.size();
  for(int i = 0; i < len; i++)
    {
    bb = aabbs.get(i);
    if(bb.maxY <= minYPosition){continue;}//too low to care about, already on top of or below existing y position
    if(bb.minY >= maxYCheck){continue;}//too high to check, would be above max step position collision range    
    mtvTemp = orientedBoundingBox.getMinCollisionVector(bb, mtvTempBase);//check each bb vs the OBB for x/z collision
    if(mtvTemp!=null)//it collides, check for min/max values
      {
      if(bb.maxY <= maxYPosition)
        {
        if(bb.maxY > maxRestingY){maxRestingY=bb.maxY;}
        }
      else
        {
        if(bb.minY < minHeadHight){minHeadHight = bb.minY;}
        }      
      }
    if(minHeadHight - maxRestingY < vehicle.vehicleHeight){return -1;}//early out if at any phase we have detected the vehicle cannot fit into the space specified
    }
  return maxRestingY - vehicle.posY;
  }

/**
 * Return the maximum amount (up to input) that the vehicle can move on the X axis in the input direction before collision.
 * @param xMotion
 * @param aabbs
 * @return
 */
private double getXmove(double xMotion, List<AxisAlignedBB> aabbs)
  {
  AxisAlignedBB bb;
  int len = aabbs.size();  
  double xMove = xMotion;
  Vec3 vec;
  for(int i = 0; i< len; i++)
    { 
    bb = aabbs.get(i);
    if(bb.maxY <= vehicle.boundingBox.minY || bb.minY >= vehicle.boundingBox.maxY){continue;}//no collision at all, skip
    AWLog.logDebug("testing vs aabb x: "+bb);
    vec = orientedBoundingBox.getCollisionVectorXMovement(bb, xMotion);
    if(vec!=null)
      {
      if(Math.abs(vec.xCoord) < Math.abs(xMove))
        {
        xMove = vec.xCoord;
        }
      }
    } 
  if(Math.abs(xMove)<0.001d)
    {
    xMove=0; 
    }  
  return xMove;
  }

/**
 * Return the maximum amount (up to input) that the vehicle can move on the Z axis in the input direction before collision.
 * @param zMotion
 * @param aabbs
 * @return
 */
private double getZMove(double zMotion, List<AxisAlignedBB> aabbs)
  {
  AxisAlignedBB bb;
  int len = aabbs.size();  
  
  double zMove = zMotion;
  Vec3 vec;
  for(int i = 0; i< len; i++)
    { 
    bb = aabbs.get(i);
    if(bb.maxY <= vehicle.boundingBox.minY || bb.minY >= vehicle.boundingBox.maxY){continue;}//no collision at all, skip
    AWLog.logDebug("testing vs aabb z: "+bb);
    vec = orientedBoundingBox.getCollisionVectorZMovement(bb, zMotion);
    if(vec!=null)
      {
      if(Math.abs(vec.zCoord) < Math.abs(zMove))
        {
        zMove = vec.zCoord;
        }
      }
    } 
  if(Math.abs(zMove)<0.001d)
    {
    zMove=0; 
    }  
  return zMove;
  }
}
