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
import net.shadowmage.ancientwarfare.vehicle.entity.movement.VehicleMoveHandlerTest;
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
  
  inputHandler = new VehicleInputHandler(this);
  moveHandler = new VehicleMoveHandlerTest(this);
  
  vehicleWidth = 2.f;
  vehicleHeight = 1.0f;
  vehicleLength = 3.f;
  orientedBoundingBox = new OBB(vehicleWidth, vehicleHeight, vehicleLength);
  
  width = 1.1f * Math.max(vehicleWidth, vehicleLength);//due to not using rotated BBs, this can be set to a minimal square extent for the entity-parts used for collision checking
  height = vehicleHeight;
  stepHeight = 1.0f;  
  orientedBoundingBox.updateForRotation(0);
  orientedBoundingBox.setAABBToOBBExtents(boundingBox);
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
  super.onUpdate();
  inputHandler.onUpdate();
  orientedBoundingBox.updateForRotation(rotationYaw);
  orientedBoundingBox.updateForPosition(posX, posY, posZ);
  orientedBoundingBox.setAABBToOBBExtents(boundingBox);
  updatePartPositions();
  }

@Override
public void moveEntity(double inputXMotion, double inputYMotion, double inputZMotion)
  {
  moveEntityOBB(inputXMotion, inputYMotion, inputZMotion);   
  }

@SuppressWarnings("unchecked")
public void rotateEntity(float rotationDelta)
  {
  prevRotationYaw = rotationYaw;
  
  orientedBoundingBox.updateForRotation(rotationYaw + rotationDelta);
  orientedBoundingBox.updateForPosition(posX, posY, posZ);
  orientedBoundingBox.setAABBToOBBExtents(boundingBox);
  
  Vec3 mtvTempBase = Vec3.createVectorHelper(0,0,0);
  Vec3 mtvTemp = null;
  Vec3 mtv = null;  
  
  List<AxisAlignedBB> aabbs = worldObj.getCollidingBoundingBoxes(this, boundingBox);  
  
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
        mtv = Vec3.createVectorHelper(mtvTemp.xCoord, mtvTemp.yCoord, mtvTemp.zCoord);
        }
      else
        {
        if(Math.abs(mtvTemp.xCoord)>Math.abs(mtv.xCoord)){mtv.xCoord=mtvTemp.xCoord;}
        if(Math.abs(mtvTemp.yCoord)>Math.abs(mtv.yCoord)){mtv.yCoord=mtvTemp.yCoord;}
        if(Math.abs(mtvTemp.zCoord)>Math.abs(mtv.zCoord)){mtv.zCoord=mtvTemp.zCoord;}
        }
      }
    }
  
  if(mtv==null || (mtv.xCoord==0 && mtv.zCoord==0 && mtv.yCoord==0))//set position from move as there is no collision
    {
    rotationYaw += rotationDelta;
    }
  else if(mtv.yCoord!=0)
    {  
    AWLog.logDebug("vertical colllision while rotating, reverting");
    orientedBoundingBox.updateForRotation(rotationYaw);
    orientedBoundingBox.updateForPosition(posX, posY, posZ);
    orientedBoundingBox.setAABBToOBBExtents(boundingBox);
    }  
  else
    {
    AWLog.logDebug("colllision while rotating, attempting rotate + slide");    
    orientedBoundingBox.updateForPosition(posX + mtv.xCoord, posY, posZ + mtv.zCoord);
    orientedBoundingBox.setAABBToOBBExtents(boundingBox);    
    aabbs = worldObj.getCollidingBoundingBoxes(this, boundingBox);      
    bb = null;
    len = aabbs.size();
    for(int i = 0; i< len; i++)
      { 
      bb = aabbs.get(i);
      mtvTemp = orientedBoundingBox.getMinCollisionVector(bb, mtvTempBase);    
      if(mtvTemp!=null)
        {
        AWLog.logDebug("unresolvable collision while rotating, reverting");
        orientedBoundingBox.updateForRotation(rotationYaw);
        orientedBoundingBox.updateForPosition(posX, posY, posZ);
        orientedBoundingBox.setAABBToOBBExtents(boundingBox);        
        break;
        }
      }
    //no unresolvable collision, slide worked
    rotationYaw += rotationDelta;
    setPosition(posX + mtv.xCoord, posY, posZ + mtv.zCoord);//obb and bb already set/updated for rotation and bounds from test    
    }  
  }

@SuppressWarnings("unchecked")
protected void moveEntityOBB(double x, double y, double z)
  {
  orientedBoundingBox.updateForPosition(posX + x, posY + y, posZ + z);
  orientedBoundingBox.setAABBToOBBExtents(boundingBox);
  
  Vec3 mtvTempBase = Vec3.createVectorHelper(0,0,0);
  Vec3 mtvTemp = null;
  Vec3 mtv = null;  
  
  List<AxisAlignedBB> aabbs = worldObj.getCollidingBoundingBoxes(this, boundingBox);  
  
  AxisAlignedBB bb = null;
  int len = aabbs.size();
  for(int i = 0; i< len; i++)
    { 
    bb = aabbs.get(i);
    mtvTemp = orientedBoundingBox.getMinCollisionVector(bb, mtvTempBase);    
    if(mtvTemp!=null)
      {
      if(boundingBox.minY <= bb.maxX && boundingBox.maxX >= bb.minY)      
        {      
        double y1 = bb.maxY - boundingBox.minY;
        if(y1 <  stepHeight)
          {   
          mtvTemp = Vec3.createVectorHelper(0, y1, 0);
          } 
        }
      else if(bb.minY < boundingBox.maxY && bb.minY >= boundingBox.minY)
        {
        //is colliding from the top-down, how to handle this while now pushing entity into the ground?
        //might want to just try and move the entity on x/z until clear
        AWLog.logDebug("head collision!!");
        mtvTemp = Vec3.createVectorHelper(0, bb.minY - boundingBox.maxY, 0);
        }
      if(mtv==null)
        {
        mtv = Vec3.createVectorHelper(mtvTemp.xCoord, mtvTemp.yCoord, mtvTemp.zCoord);
        }
      else
        {
        if(Math.abs(mtvTemp.xCoord)>Math.abs(mtv.xCoord)){mtv.xCoord=mtvTemp.xCoord;}
        if(Math.abs(mtvTemp.yCoord)>Math.abs(mtv.yCoord)){mtv.yCoord=mtvTemp.yCoord;}
        if(Math.abs(mtvTemp.zCoord)>Math.abs(mtv.zCoord)){mtv.zCoord=mtvTemp.zCoord;}
        }
      }
    }
  
  if(mtv==null)//set position from move as there is no collision
    {
    setPosition(posX + x, posY + y, posZ + z);//obb already adjusted
    return;
    }
  else if(mtv.xCoord==0 && mtv.zCoord==0)//no collision on x or z axis, move uncollided on x,z, use mtv y value
    {
    y += mtv.yCoord;
    setPosition(posX + x, posY + y, posZ + z);//adjust OBB for y-correction
    orientedBoundingBox.updateForPosition(posX, posY, posZ);
    orientedBoundingBox.setAABBToOBBExtents(boundingBox);  
    return;
    }
  //implicit else mtv!=null && mtv.xCoord!=0 || mtv.zCoord!=0
  
  //there was a collision of some sort.  Set position to known good position and re-test for stepping
  x += mtv.xCoord;
  y += mtv.yCoord;
  z += mtv.zCoord;
  setPosition(posX + x, posY + y, posZ + z);
    
  //reset bounding box to good position + remainder of collided movement + stepheight
  orientedBoundingBox.updateForPosition(posX - mtv.xCoord, posY + stepHeight, posZ - mtv.zCoord);
  orientedBoundingBox.setAABBToOBBExtents(boundingBox);
  //get list of AABBs for stepped position
  aabbs = worldObj.getCollidingBoundingBoxes(this, boundingBox);
  if(aabbs.isEmpty())//absolutely no collision post step
    {
    setPosition(posX - mtv.xCoord, posY+stepHeight, posZ-mtv.xCoord);//obb already adjusted
    return;
    }
 
  Vec3 mtvStep = null;  
  bb = null;
  len = aabbs.size();
  for(int i = 0; i< len; i++)
    { 
    bb = aabbs.get(i);
    mtvTemp = orientedBoundingBox.getMinCollisionVector(bb, mtvTempBase);    
    if(mtvTemp!=null)
      {
      if(mtvStep==null)
        {
        mtvStep = Vec3.createVectorHelper(mtvTemp.xCoord, mtvTemp.yCoord, mtvTemp.zCoord);
        }
      else
        {
        if(Math.abs(mtvTemp.xCoord) > Math.abs(mtvStep.xCoord)){mtvStep.xCoord=mtvTemp.xCoord;}
        if(Math.abs(mtvTemp.yCoord) > Math.abs(mtvStep.yCoord)){mtvStep.yCoord=mtvTemp.yCoord;}
        if(Math.abs(mtvTemp.zCoord) > Math.abs(mtvStep.zCoord)){mtvStep.zCoord=mtvTemp.zCoord;}
        }
      }
    }
  
  if(mtvStep==null)//no collision after step after checking for just OBB
    {
    setPosition(posX - mtv.xCoord, posY+stepHeight, posZ-mtv.xCoord);
    }
  else if(Math.abs(mtvStep.xCoord) < Math.abs(mtv.xCoord) || Math.abs(mtvStep.zCoord) < Math.abs(mtv.zCoord))//minimal collision after step
    {
    mtv.xCoord = Math.abs(mtv.xCoord) < Math.abs(mtvStep.xCoord) ? mtv.xCoord : mtvStep.xCoord;
    mtv.zCoord = Math.abs(mtv.zCoord) < Math.abs(mtvStep.zCoord) ? mtv.zCoord : mtvStep.zCoord;
    setPosition(posX - mtv.xCoord, posY+stepHeight, posZ-mtv.xCoord);
    }  
  orientedBoundingBox.updateForPosition(posX, posY, posZ);
  orientedBoundingBox.setAABBToOBBExtents(boundingBox);  
  }

//************************************* COLLISION HANDLING *************************************//
// Disabled in base class to allow entity-parts to handle the collision handling.  Each vehicle part
// is responsible for updating its own position.  Vehicle base is responsible for resolving collision
// detection with world/entities and vehicleparts.  Vehicle parts bridge all interaction stuff back to
// the owning parent vehicle (interact, attack)

@Override
public AxisAlignedBB getCollisionBox(Entity entity)
  {
  return null;
  }

@Override
public boolean canBeCollidedWith()
  {
  return false;
  }

@Override
public boolean canBePushed()
  {
  return false;
  }

/**
 * Return null so that collisions happen with children pieces
 */
@Override
public AxisAlignedBB getBoundingBox()
  {
  return null;
  }

/**
 * Renderpass 0 for normal rendering<br>
 * Renderpass 1 for debug bounding box rendering<br>
 * TODO remove pass1 when no longer needed
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
  if(!worldObj.isRemote)
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
public void writeSpawnData(ByteBuf buffer)
  {
  // TODO Auto-generated method stub
  
  }

@Override
public void readSpawnData(ByteBuf additionalData)
  {
  // TODO Auto-generated method stub
  
  }

}
