package net.shadowmage.ancientwarfare.vehicle.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.util.Trig;
import net.shadowmage.ancientwarfare.vehicle.input.VehicleInputHandler;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class VehicleBase extends Entity implements IEntityAdditionalSpawnData
{

private VehicleInputHandler inputHandler;
private VehicleMoveHandler moveHandler;

VehiclePart[] parts;

public float vehicleWidth, vehicleHeight, vehicleLength;


public VehicleBase(World world)
  {
  super(world);
  
  vehicleWidth = 2.f;
  vehicleHeight = 1.0f;
  vehicleLength = 5.f;
  this.width = Math.max(vehicleWidth, vehicleLength);//due to not using rotated BBs, this can be set to a minimal square extent for the entity-parts used for collision checking
  this.height = vehicleHeight;
  
  inputHandler = new VehicleInputHandler(this);
  moveHandler = new VehicleMoveHandler(this);
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
//  inputHandler.onUpdate();
  if(!worldObj.isRemote){rotationYaw++;}
  super.onUpdate();
  updatePartPositions();
  }

//************************************* COLLISION HANDLING *************************************//
// disabled in base class to allow entity-parts to handle the collision handling.  each vehicle
// part is responsible for its own collision detection and handling.
//

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

@Override
public AxisAlignedBB getBoundingBox()
  {
  return null;
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
public VehiclePart[] getParts()
  {
  if(parts==null){buildParts();}//lazy initialization of parts, don't even bother to construct until they are first asked for...perhaps change this to init parts in entity-init?
  return parts;
  }

protected final void buildParts()
  {  
  float min = Math.min(vehicleLength, vehicleWidth);
  float max = Math.max(vehicleLength, vehicleWidth);
  int num = (int)Math.ceil(max / min);
  parts = new VehiclePart[num];
  for(int i = 0; i < num; i++)
    {
    parts[i] = new VehiclePart(this, min, height);
    }
  updatePartPositions();
  }

protected void updatePartPositions()
  {
  VehiclePart[] parts = getParts();
  float min = Math.min(vehicleLength, vehicleWidth);
  float max = Math.max(vehicleLength, vehicleWidth);
  int num = parts.length;
  
  if(num==1)//only a single part, set to vehicle position
    {
    getParts()[0].setPosition(posX, posY, posZ);
    return;
    }
  
  float edgeSeparation = min / 2.f;  
  float innerSeparation = (max - min) / (num-1);
  
  float pos = 0;//current position along vehicle axis line, from front->rear
   
  Vec3 lookVec = getLookVec();

  double x, z;
  double frontX, frontZ;
  frontX = lookVec.xCoord * (max*0.5d) + posX;
  frontZ = lookVec.zCoord * (max*0.5d) + posZ;
  
  x = frontX;
  z = frontZ;
  for(int i = 0; i < num; i++)
    {
    if(i==0)//first part
      {
      pos = edgeSeparation;
      }
    else if(i==num-1)//last part
      {
      pos = max-edgeSeparation;
      }
    else
      {
      pos += innerSeparation;
      }
    x = frontX - pos * lookVec.xCoord;
    z = frontZ - pos * lookVec.zCoord;
    parts[i].setPosition(x, posY, z);
    AWLog.logDebug("set part pos to: "+parts[i].posX + ","+parts[i].posZ + " for entity pos: "+posX+","+posZ +  parts[i].boundingBox);
    //calc x, y along vehicle axis vector
    //set part to that position
    }
  }

/**
 * Return a unit-length, normalized look vector for the current rotationYaw of the vehicle
 * @return
 */
@Override
public Vec3 getLookVec()
  {
  Vec3 vec = Vec3.createVectorHelper(0, 0, -1);
  vec.rotateAroundY(rotationYaw * Trig.TORADIANS);  
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
