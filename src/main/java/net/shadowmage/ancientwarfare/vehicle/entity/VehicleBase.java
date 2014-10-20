package net.shadowmage.ancientwarfare.vehicle.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
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
  vehicleLength = 3.f;
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
  parts[0] = new VehiclePart(this, 1, 1, -0.5f,  1.0f);
  parts[1] = new VehiclePart(this, 1, 1, -0.5f,  0.0f);
  parts[2] = new VehiclePart(this, 1, 1, -0.5f, -1.0f);
  parts[3] = new VehiclePart(this, 1, 1,  0.5f,  1.0f);
  parts[4] = new VehiclePart(this, 1, 1,  0.5f,  0.0f);
  parts[5] = new VehiclePart(this, 1, 1,  0.5f, -1.0f);
  
  parts[6] = new VehiclePart(this, 1, 1,  0.0f, -0.5f);
  parts[7] = new VehiclePart(this, 1, 1,  0.0f,  0.5f);  
  updatePartPositions();
  }

protected final void updatePartPositions()
  {
  for(VehiclePart part : getParts())
    {
    part.updatePosition();
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
