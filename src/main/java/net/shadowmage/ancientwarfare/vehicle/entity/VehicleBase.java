package net.shadowmage.ancientwarfare.vehicle.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.util.Trig;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class VehicleBase extends Entity implements IEntityAdditionalSpawnData
{

public float length;

public VehicleBase(World world)
  {
  super(world);
  this.width = 2.0f;
  this.height = 1.5f;
  this.length = 3.0f;
  this.rotationYaw = 45.f;
  this.prevRotationYaw = 45.f;
  }

@Override
protected void entityInit()
  {

  }

@Override
public void onUpdate()
  {
  rotationYaw++;
  super.onUpdate();
  }

//************************************* COLLISION HANDLING *************************************//

@Override
public void setPosition(double x, double y, double z)
  {
  super.setPosition(x, y, z);
  updateBoundingBox();
  }

/**
 * Update the vehicles bounding box to the rotated representation of its true bounding box
 */
protected void updateBoundingBox()
  {
  float yawRad = Trig.TORADIANS*rotationYaw;
  float cos = MathHelper.cos(yawRad);
  float sin = MathHelper.sin(yawRad);
  //x1, z1 = TopLeftCorner (if viewed top-down)
  float x1 = -(width/2);
  float z1 = -(length/2);
  //x2, z2 = TopRightCorner (if viewed top-down)
  float x2 = -x1;
  float z2 = z1;//need to invert an axis for some dumb reason, or the entire thing collapses to a zero-size box upon rotation 
  float tx1 = x1 * cos - z1 * sin;
  float tz1 = x1 * sin + z1 * cos;
  float tx2 = x2 * cos - z2 * sin;
  float tz2 = x2 * sin + z2 * cos;
  float xHalfSize = Math.max(Math.abs(tx1), Math.abs(tx2));
  float zHalfSize = Math.max(Math.abs(tz1), Math.abs(tz2));  
  boundingBox.setBounds(posX-xHalfSize, posY, posZ-zHalfSize, posX+xHalfSize, posY+height, posZ+zHalfSize);
  }

/**
 * Bounding Box is used for collision with a solid-object entity
 */
@Override
public AxisAlignedBB getBoundingBox()
  {
  AWLog.logDebug("getBoundingBox! "+boundingBox);
  return boundingBox;
  }

@Override
public void applyEntityCollision(Entity collider)
  {
  AWLog.logDebug("applyEntityCollision: "+collider);
  super.applyEntityCollision(collider);
  }

@Override
public void onCollideWithPlayer(EntityPlayer player)
  {
  AWLog.logDebug("collide with player!! "+player);
  super.onCollideWithPlayer(player);
  }

/**
 * Return a collision box to enable the input entity to push this entity.<br>
 * default implementation for pushable entities is to return the input entities bounding box<br>
 * Vehicle implementation returns null to disable pushing 
 */
@Override
public AxisAlignedBB getCollisionBox(Entity entity)
  {
  AWLog.logDebug("getCollisionBox: "+entity);
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
