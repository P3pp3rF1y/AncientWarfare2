package net.shadowmage.ancientwarfare.vehicle.entity;

import io.netty.buffer.ByteBuf;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.util.Trig;
import net.shadowmage.ancientwarfare.vehicle.collision.OBB;
import net.shadowmage.ancientwarfare.vehicle.entity.movement.VehicleMoveHandler;
import net.shadowmage.ancientwarfare.vehicle.entity.movement.VehicleMoveHandlerAirshipTest;
import net.shadowmage.ancientwarfare.vehicle.input.VehicleInputHandler;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class VehicleBase extends Entity implements IEntityAdditionalSpawnData
{

public VehicleInputHandler inputHandler;
public VehicleMoveHandler moveHandler;
public OBB orientedBoundingBox;

VehiclePart[] parts;

public float vehicleWidth, vehicleHeight, vehicleLength;

public VehicleBase(World world)
  {
  super(world);
  vehicleWidth = 3.f;
  vehicleHeight = 2.0f;
  vehicleLength = 5.f;
  
  World.MAX_ENTITY_RADIUS = Math.max(World.MAX_ENTITY_RADIUS, Math.max(vehicleWidth*1.4f, vehicleLength*1.4f));
  
  orientedBoundingBox = new OBB(vehicleWidth, vehicleHeight, vehicleLength);  
  orientedBoundingBox.setRotation(-0);
  orientedBoundingBox.setAABBToOBBExtents(boundingBox);
  inputHandler = new VehicleInputHandler(this);
  moveHandler = new VehicleMoveHandlerAirshipTest(this);  
  
  width = 1.42f * Math.max(vehicleWidth, vehicleLength);//due to not using rotated BBs, this can be set to a minimal square extent for the entity-parts used for collision checking
  height = vehicleHeight;
  stepHeight = 1.0f;    
  
  buildParts();//need to call build parts in the constructor to align entity-ids properly (they are supposed to be sequential)
  }

/**
 * Add data fields to data-watchers in this block.
 * It is called at the end of the vanilla Entity base class constructor, so you must not rely on any
 * of your sub-class fields being initialized (setting size/health/whatever else happens in the constructor has not happened yet)
 */
@Override
protected void entityInit()
  {

  }

@Override
public void onUpdate()
  {  
  worldObj.theProfiler.startSection("AWVehicleTick");
  super.onUpdate();
  inputHandler.onUpdate();
  orientedBoundingBox.updateForPositionAndRotation(posX, posY, posZ, rotationYaw);
  orientedBoundingBox.setAABBToOBBExtents(boundingBox);  
  updatePartPositions();
  worldObj.theProfiler.endSection();
  }

//************************************* MOVEMENT HANDLING *************************************//
// Custom movement handling using OBB for terrain collision detection for both movement and rotation.
// Uses SAT for basic overlap tests for y-movement, uses some custom ray-tracing for testing move extents on x/z axes
// 

/**
 * Overriden to remove applying fall distance to rider
 * @param distance (unused)
 */
@Override
protected void fall(float distance)
  {
  }

/**
 * Overriden to use OBB for movement collision checks.<br>
 * Currently does not replicate vanilla functionality for contact with fire blocks, web move speed reduction, walk-on-block checks, or distance traveled
 * @param inputXMotion
 * @param inputYMotion
 * @param inputZMotion
 */
@Override
public void moveEntity(double inputXMotion, double inputYMotion, double inputZMotion)
  {
  moveEntityOBB(inputXMotion, inputYMotion, inputZMotion);
  }

/**
 * 
 * @param rotationDelta
 */
@SuppressWarnings("unchecked")
public void rotateEntity(float rotationDelta)
  {
  orientedBoundingBox.updateForPositionAndRotation(posX, posY, posZ, rotationYaw + rotationDelta);
  orientedBoundingBox.setAABBToOBBExtents(boundingBox);
  
  Vec3 mtvTempBase = Vec3.createVectorHelper(0,0,0);
  Vec3 mtvTemp = null;
  Vec3 mtv = null;  
  
  List<AxisAlignedBB> aabbs = worldObj.getCollidingBoundingBoxes(this, boundingBox.expand(0.2d, 0, 0.2d));  
  
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
    setPosition(posX + mtv.xCoord, posY, posZ + mtv.zCoord);
    orientedBoundingBox.updateForPositionAndRotation(posX, posY, posZ, rotationYaw + rotationDelta);
    orientedBoundingBox.setAABBToOBBExtents(boundingBox);    
    aabbs = worldObj.getCollidingBoundingBoxes(this, boundingBox.expand(0.2d, 0, 0.2d));      
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
      setPosition(posX, posY, posZ);
      orientedBoundingBox.updateForPositionAndRotation(posX, posY, posZ, rotationYaw);
      orientedBoundingBox.setAABBToOBBExtents(boundingBox); 
      }
    else//slide was no good, revert (do not rotate at all)
      {
      setPosition(posX - mtv.xCoord, posY, posZ-mtv.zCoord);
      orientedBoundingBox.updateForPositionAndRotation(posX, posY, posZ, rotationYaw);
      orientedBoundingBox.setAABBToOBBExtents(boundingBox);
      }    
    }  
  }

@SuppressWarnings("unchecked")
protected void moveEntityOBB(double x, double y, double z)
  {
  if(Math.abs(x)<0.001d){x=0.d;}
  if(Math.abs(z)<0.001d){z=0.d;}
  orientedBoundingBox.updateForPositionAndRotation(posX, posY, posZ, rotationYaw);
  orientedBoundingBox.setAABBToOBBExtents(boundingBox);
  List<AxisAlignedBB>  aabbs = worldObj.getCollidingBoundingBoxes(this, boundingBox.expand(Math.abs(x) + 0.2d, Math.abs(y) + stepHeight + 0.2d, Math.abs(z) + 0.2d));
  //first do Y movement test, use basic OBB vs bbs test, move downard if not collided 
  double xMove, yMove, zMove;
  
  yMove = y < 0 ? getYNegativeMove(y, aabbs) : y > 0 ? getYPositiveMove(y, aabbs) : 0;
  if(yMove!=0)
    {
    setPosition(posX, posY+yMove, posZ);
    orientedBoundingBox.updateForPositionAndRotation(posX, posY, posZ, rotationYaw);
    orientedBoundingBox.setAABBToOBBExtents(boundingBox);
    }
  xMove = getXmove(x, aabbs);
  if(xMove!=0)
    {
    setPosition(posX+xMove, posY, posZ);
    orientedBoundingBox.updateForPositionAndRotation(posX, posY, posZ, rotationYaw);
    orientedBoundingBox.setAABBToOBBExtents(boundingBox);   
    }
  zMove = getZMove(z, aabbs);
  if(zMove!=0)
    {
    setPosition(posX, posY, posZ+zMove);
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
      setPosition(posX+mx, posY+my, posZ+mz);
      }
    orientedBoundingBox.updateForPositionAndRotation(posX, posY, posZ, rotationYaw);
    orientedBoundingBox.setAABBToOBBExtents(boundingBox);
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
  double maxFoundY = posY + yMotion;
  for(int i = 0; i < len; i++)
    {
    bb = aabbs.get(i);
    if(bb.maxY <= maxFoundY){continue;}//to far below to care
    if(bb.minY >= boundingBox.maxY){continue;}//to far above to care
    mtvTemp = orientedBoundingBox.getMinCollisionVector(bb, mtvTempBase);//check each bb vs the OBB for x/z collision
    if(mtvTemp!=null)//it collides, check for top height
      {
      if(bb.maxY>maxFoundY){maxFoundY=bb.maxY;}
      }
    }
  return maxFoundY - posY;
  }

private double getYPositiveMove(double yMotion, List<AxisAlignedBB> aabbs)
  {
  Vec3 mtvTempBase = Vec3.createVectorHelper(0, 0, 0);
  Vec3 mtvTemp = null;
  AxisAlignedBB bb = null;
  int len = aabbs.size();
  double minFoundY = posY + vehicleHeight + yMotion;//the max to check
  for(int i = 0; i < len; i++)
    {
    bb = aabbs.get(i);
    if(bb.maxY < posY+height){continue;}//dont care about stuff already colliding or below the vehicle
    if(bb.minY >= minFoundY){continue;}//also dont care about stuff too high to reach from input movement
    mtvTemp = orientedBoundingBox.getMinCollisionVector(bb, mtvTempBase);//check each bb vs the OBB for x/z collision
    if(mtvTemp!=null)//it collides, check for top height
      {
      if(bb.minY<minFoundY){minFoundY=bb.minY;}
      }
    }
  return minFoundY - (posY+vehicleHeight);
  }

/**
 * vehicle OBB should alread be moved into the position to step upwards from before this method is called.<br>
 * (test both axes simultaneously, do not step if -any- collision would be found)<br>
 * does not move vehicle, only returns the move that -should- be made.
 * @return < 0 for invalid step (no fit found), >=0 for valid step-up operation (position should be valid and uncollided post step)
 */
private double getYStepHeight(List<AxisAlignedBB> aabbs)
  {
  double maxYCheck = posY+stepHeight+vehicleHeight;
  double maxYPosition = posY+stepHeight;
  double minYPosition = posY;
  
  double maxRestingY = posY;
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
    if(minHeadHight - maxRestingY < vehicleHeight){return -1;}//early out if at any phase we have detected the vehicle cannot fit into the space specified
    }
  return maxRestingY - posY;
  }

private double getXmove(double xMotion, List<AxisAlignedBB> aabbs)
  {
  AxisAlignedBB bb;
  int len = aabbs.size();  
  double xMove = xMotion;
  Vec3 vec;
  for(int i = 0; i< len; i++)
    { 
    bb = aabbs.get(i);
    if(bb.maxY <= boundingBox.minY || bb.minY >= boundingBox.maxY){continue;}//no collision at all, skip
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
  AWLog.logDebug("moved x: "+xMove);
  if(Math.abs(xMove)<0.001d)
    {
    xMove=0; 
    }  
  return xMove;
  }

private double getZMove(double zMotion, List<AxisAlignedBB> aabbs)
  {
  AxisAlignedBB bb;
  int len = aabbs.size();  
  
  double zMove = zMotion;
  Vec3 vec;
  for(int i = 0; i< len; i++)
    { 
    bb = aabbs.get(i);
    if(bb.maxY <= boundingBox.minY || bb.minY >= boundingBox.maxY){continue;}//no collision at all, skip
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
  AWLog.logDebug("moved z: "+zMove);
  return zMove;
  }

//************************************* COLLISION HANDLING *************************************//
// Disabled in base class to allow entity-parts to handle the collision handling.  Each vehicle part
// is responsible for updating its own position.  Vehicle base is responsible for resolving collision
// detection with world/entities and vehicleparts.  Vehicle parts bridge all interaction stuff back to
// the owning parent vehicle (interact, attack)

/**
 * Allow child parts to determine entity-entity collision boxes
 * @param entity (unused)
 * @return null for vehicle implementation
 */
@Override
public AxisAlignedBB getCollisionBox(Entity entity)
  {
  return null;
  }

/**
 * Allow child parts to determine collision status
 * @return false for vehicle implementation
 */
@Override
public boolean canBeCollidedWith()
  {
  return false;
  }

/**
 * Allow child parts to determine push-status for entity-entity interaction
 * @return false for vehicle implementation
 */
@Override
public boolean canBePushed()
  {
  return false;
  }

/**
 * Return null so that collisions happen with children pieces
 * @return null for vehicle implementation
 */
@Override
public AxisAlignedBB getBoundingBox()
  {
  return null;
  }

/**
 * Renderpass 0 for normal rendering<br>
 * Renderpass 1 for debug bounding box rendering<br>
 * TODO remove pass1 and override when no longer needed
 */
@Override
public boolean shouldRenderInPass(int pass)
  {
  return pass==0 || pass==1;
  }

@Override
@SideOnly(Side.CLIENT)
public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int ticks)
  {
  inputHandler.handleVanillaSynch(x, y, z, yaw, pitch, ticks);
  }

//************************************* MULTIPART ENTITY HANDLING CODE *************************************//
//
/**
 * Return an array containing the sub-parts to this entity.  These sub-parts are not added to the world and not synchronized between client and server.
 * Any synchronization is left to the implementing class.
 * <br>Changed return type from Entity to VehiclePart for easier use when VehicleBase is the known type
 * @return
 */
@Override
public final VehiclePart[] getParts()
  {
  if(parts==null)
    {
    buildParts();
    updatePartPositions();    
    }//lazy initialization of parts, don't even bother to construct until they are first asked for...perhaps change this to init parts in entity-init?
  return parts;
  }

/**
 * Will be made abstract for actual classes<br>
 * Implementations should return an array containing the vehicle parts for the given vehicle<br>
 * Each part is responsible for updating its own location relative to vehicle position.<br>
 * May have a config option to -not- use multiple vehicle parts,in which case a single vehicle part should be returned for the vehicle bounds
 */
protected void buildParts()
  {  
  parts = new VehiclePart[8];
  parts[0] = new VehiclePart(this, 1, height, -0.5f,  1.0f);
  parts[1] = new VehiclePart(this, 1, height, -0.5f,  0.0f);
  parts[2] = new VehiclePart(this, 1, height, -0.5f, -1.0f);
  parts[3] = new VehiclePart(this, 1, height,  0.5f,  1.0f);
  parts[4] = new VehiclePart(this, 1, height,  0.5f,  0.0f);
  parts[5] = new VehiclePart(this, 1, height,  0.5f, -1.0f);
  
  parts[6] = new VehiclePart(this, 1, height,  0.0f, -0.5f);
  parts[7] = new VehiclePart(this, 1, height,  0.0f,  0.5f);  
  }

protected final void updatePartPositions()
  {
  for(VehiclePart part : getParts())
    {
    part.updatePosition();
    }
  }

@Override
public boolean interactFirst(EntityPlayer player)
  {
  AWLog.logDebug("interact with vehicle: "+player);
  if(!worldObj.isRemote && this.riddenByEntity==null && player.ridingEntity==null)
    {
    player.mountEntity(this);
    }
  return true;//return true for isHandled
  }

/**
 * Return a unit-length, normalized look vector for the current rotationYaw of the vehicle
 * @return
 */
@Override
public Vec3 getLookVec()
  {
  Vec3 vec = Vec3.createVectorHelper(0, 0, -1);
  vec.rotateAroundY(MathHelper.wrapAngleTo180_float(rotationYaw) * Trig.TORADIANS);  
  return vec;
  }

public final boolean attackEntityFromPart(VehiclePart part, DamageSource p_70965_2_, float p_70965_3_)
  {
  return attackEntityFrom(p_70965_2_, p_70965_3_);
  }

//************************************* NBT / NETWORK *************************************//

@Override
protected void readEntityFromNBT(NBTTagCompound var1)
  {
  }

@Override
protected void writeEntityToNBT(NBTTagCompound var1)
  {
  
  }

@Override
public void writeSpawnData(ByteBuf data)
  {
  }

@Override
public void readSpawnData(ByteBuf data)
  {
  }

}
