package net.shadowmage.ancientwarfare.vehicle.entity;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Vec3;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.util.Trig;

public class VehiclePart extends Entity
{

private VehicleBase vehicle;
private Vec3 offset;
private Vec3 location;

public VehiclePart(VehicleBase vehicle, float width, float height, float xOffset, float zOffset)
  {
  super(vehicle.worldObj);
  this.vehicle = vehicle;
  this.setSize(width, height);
  offset = Vec3.createVectorHelper(xOffset, 0, zOffset);
  location = Vec3.createVectorHelper(xOffset, 0, zOffset);
  updatePosition();
  }

public final void updatePosition()
  {
  float yaw = vehicle.rotationYaw;
  location.xCoord = offset.xCoord;
  location.yCoord = offset.yCoord;
  location.zCoord = offset.zCoord;
  location.rotateAroundY(yaw * Trig.TORADIANS);
  location.xCoord+=vehicle.posX;
  location.yCoord+=vehicle.posY;
  location.zCoord+=vehicle.posZ;
  setPosition(location.xCoord, location.yCoord, location.zCoord);
  AWLog.logDebug("updating position for offset: "+offset+" new loc: "+location+" owner pos: "+vehicle);
  }

@Override
public AxisAlignedBB getCollisionBox(Entity p_70114_1_)
  {
  return p_70114_1_.boundingBox;
  }

@Override
public AxisAlignedBB getBoundingBox()
  {
  return boundingBox;
  }

/**
 * Returns true if other Entities should be prevented from moving through this Entity.
 */
@Override
public boolean canBeCollidedWith()
  {
  return true;
  }

@Override
public boolean canBePushed()
  {
  return true;
  }

/**
 * Called when the entity is attacked.
 */
@Override
public boolean attackEntityFrom(DamageSource src, float amt)
  {
  return this.isEntityInvulnerable() ? false : this.vehicle.attackEntityFromPart(this, src, amt);
  }

/**
 * Returns true if Entity argument is equal to this Entity
 */
@Override
public boolean isEntityEqual(Entity entity)
  {
  return this == entity || this.vehicle == entity;
  }

@Override
protected void entityInit(){}

@Override
protected void readEntityFromNBT(NBTTagCompound p_70037_1_){}

@Override
protected void writeEntityToNBT(NBTTagCompound p_70014_1_){}

}
