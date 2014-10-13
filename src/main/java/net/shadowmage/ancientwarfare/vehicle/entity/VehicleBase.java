package net.shadowmage.ancientwarfare.vehicle.entity;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.config.AWLog;

public class VehicleBase extends Entity implements IEntityAdditionalSpawnData
{

public VehicleBase(World world)
  {
  super(world);
  }

@Override
protected void entityInit()
  {

  }

//************************************* COLLISION HANDLING *************************************//

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
