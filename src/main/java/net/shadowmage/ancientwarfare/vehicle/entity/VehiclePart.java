package net.shadowmage.ancientwarfare.vehicle.entity;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;

public class VehiclePart extends Entity
{

private VehicleBase vehicle;

public VehiclePart(VehicleBase vehicle, float width, float height)
  {
  super(vehicle.worldObj);
  this.setSize(width, height);
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
