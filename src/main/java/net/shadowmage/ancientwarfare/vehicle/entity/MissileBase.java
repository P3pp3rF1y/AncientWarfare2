package net.shadowmage.ancientwarfare.vehicle.entity;

import io.netty.buffer.ByteBuf;

import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.Trig;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class MissileBase extends Entity implements IEntityAdditionalSpawnData
{

float launchYaw;
float launchAngle;
float launchPower;
double moveX, moveZ, moveY;//calculated per-tick movement on x and z axes
UUID launcherUniqueId;

public MissileBase(World p_i1582_1_)
  {
  super(p_i1582_1_);
  }

/**
 * @param launchYaw yaw angle for launching (global rotation)
 * @param launchPitch pitch angle for launching (0=horizon)
 * @param launchPower velocity in m/s
 * @param launcherID UUID of the launching entity.  Used to determine hit-callbacks and if the missile should damage entities it hits (will not damage same-team entities)
 */
public void setLaunchParameters(float launchYaw, float launchPitch, float launchPower, UUID launcherID)
  {
  this.launchYaw = launchYaw;
  this.launchAngle = launchPitch;
  this.launchPower = launchPower;
  this.launcherUniqueId = launcherID;
  double sinPitch = Math.sin(Trig.TORADIANS*launchPitch);
  double cosPitch = Math.cos(Trig.TORADIANS*launchPitch);
  double sinYaw = Math.sin(Trig.TORADIANS*launchYaw);
  double cosYaw = Math.cos(Trig.TORADIANS*launchYaw);
  double verticalVelocityStart = sinPitch * launchPower * 0.05d;
  double horizontalVelocityStart = cosPitch * launchPower * 0.05d;
  moveX = sinYaw * horizontalVelocityStart;
  moveZ = cosYaw * horizontalVelocityStart;
  moveY = verticalVelocityStart;
  }

@Override
protected void entityInit()
  {
  
  }

@Override
public void onUpdate()
  {
  super.onUpdate();
  updateTrajectory();
  }

public void updateTrajectory()
  {
  
  }

@Override
protected void readEntityFromNBT(NBTTagCompound p_70037_1_)
  {
  
  }

@Override
protected void writeEntityToNBT(NBTTagCompound p_70014_1_)
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
