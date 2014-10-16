package net.shadowmage.ancientwarfare.vehicle.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.vehicle.collision.OBB;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class VehicleBase extends Entity implements IEntityAdditionalSpawnData
{


public float vehicleWidth, vehicleHeight, vehicleLength;

public OBB obb;

public VehicleBase(World world)
  {
  super(world);
  
  vehicleWidth = 2.f;
  vehicleHeight = 1.5f;
  vehicleLength = 3.f;
  this.obb = new OBB(vehicleWidth, vehicleHeight, vehicleLength);
  this.width = vehicleWidth * 0.70f;//approximately the smallest BB the entity could ever occupy, use this for the 'Minecraft Bounding Box'
  this.height = vehicleHeight;
  }

@Override
protected void entityInit()
  {

  }

@Override
public void onUpdate()
  {
  if(!worldObj.isRemote)
    {
    rotationYaw++;    
    }
  super.onUpdate();
  updateBoundingBox();
  }

//************************************* COLLISION HANDLING *************************************//

@Override
protected void setSize(float width, float height)
  {
  super.setSize(width, height);
  updateBoundingBox();
  }

@Override
public void setPosition(double x, double y, double z)
  {
  super.setPosition(x, y, z);
  updateBoundingBox();
  }

@Override
public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int netUpdateFrequencyMaybe)
  {
  super.setPositionAndRotation(x, y, z, yaw,  pitch);
  }

/**
 * Update the vehicles bounding box to the rotated representation of its true bounding box
 */
protected final void updateBoundingBox()
  {
  if(obb==null){return;}//TODO solve issues of super constructor calling setPosition (seriously, WTF..don't call non-final methods in a constructor)  
  obb.updateForRotation(rotationYaw);
  obb.setAABBToOBBExtents(boundingBox);
  boundingBox.offset(posX, posY, posZ);
  }

/**
 * Bounding Box is used for collision with a solid-object entity
 */
@Override
public AxisAlignedBB getBoundingBox()
  {
//  AWLog.logDebug("getBoundingBox! "+boundingBox);
  return boundingBox;
  }

@Override
public void applyEntityCollision(Entity collider)
  {
  AWLog.logDebug("applyEntityCollision: "+collider);
  if(obb.collides(collider.boundingBox.copy().offset(-posX, -posY, -posZ)))
    {
    AWLog.logDebug("Applying collision!!");
    double dx = collider.posX - this.posX;
    double dz = collider.posZ - this.posZ;
    dx *= 0.05d;
    dz *= 0.05d;
    collider.setPosition(collider.posX+dx, collider.posY, collider.posZ+dz);
//    collider.addVelocity(dx*0.05d, 0, dz*0.05d);    
    }
  }

@Override
public void onCollideWithPlayer(EntityPlayer collider)
  {
  if(obb.collides(collider.boundingBox.copy().offset(-posX, -posY, -posZ)))
    {
    AWLog.logDebug("Applying collision!!");
    double dx = collider.posX - this.posX;
    double dz = collider.posZ - this.posZ;
    dx *= 0.05d;
    dz *= 0.05d;
    collider.setPosition(collider.posX+dx, collider.posY, collider.posZ+dz);
//    collider.addVelocity(dx*0.05d, 0, dz*0.05d);    
    }
//  AWLog.logDebug("collide with player!! "+player);
  super.onCollideWithPlayer(collider);
  }

/**
 * Return a collision box to enable the input entity to push this entity.<br>
 * default implementation for pushable entities is to return the input entities bounding box<br>
 * Vehicle implementation returns null to disable pushing 
 */
@Override
public AxisAlignedBB getCollisionBox(Entity entity)
  {
//  AWLog.logDebug("getCollisionBox: "+entity);
  return entity.boundingBox;
  }

@Override
public boolean canBeCollidedWith()
  {
  return true;
  }

@Override
public boolean canBePushed()
  {
  return true;//this is needed for the collidedWith stuff -- will be negating the push to the vehicle while altering the pushback to the collider
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
