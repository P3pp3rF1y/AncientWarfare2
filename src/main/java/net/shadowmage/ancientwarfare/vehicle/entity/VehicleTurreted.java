package net.shadowmage.ancientwarfare.vehicle.entity;

import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.Trig;

public class VehicleTurreted extends VehicleBase
{


protected float launchAngle, launchPower;
protected Vec3 turretOffsetBase = Vec3.createVectorHelper(0, 1, -1);

/**
 * location of turret firing point relative to vehicle -- this is the point that the missile will be spawned at.<br>
 * updated from entity on-tick (or whenever vehicle rotation or turret pitch changes)
 */
private Vec3 turretOffset = Vec3.createVectorHelper(0, 0, 0);

/**
 * updated from entity on-tick or whenever vehicle rotation or firing params change 
 */
private Vec3 launchVelocity = Vec3.createVectorHelper(0, 0, 0);

public VehicleTurreted(World world)
  {
  super(world);
  launchAngle = 45;
  launchPower = 30;
  }

@Override
public void onUpdate()
  {
  super.onUpdate();
  double sinYaw = Math.sin(Trig.TORADIANS * (rotationYaw + 180.d));//+180 is compensation for z-axis inversion in MC?
  double cosYaw = Math.cos(Trig.TORADIANS * (rotationYaw + 180.d));// ^^
  updateLaunchVelocity(sinYaw, cosYaw);
  updateTurretOffset(sinYaw, cosYaw);
  }

protected void updateLaunchVelocity(double sinYaw, double cosYaw)
  {
  double sinPitch = Math.sin(Trig.TORADIANS * launchAngle);
  double cosPitch = Math.cos(Trig.TORADIANS * launchAngle);
  double verticalVelocityStart = sinPitch * launchPower * 0.05d;
  double horizontalVelocityStart = cosPitch * launchPower * 0.05d;
  launchVelocity.xCoord = sinYaw * horizontalVelocityStart;
  launchVelocity.zCoord = cosYaw * horizontalVelocityStart;
  launchVelocity.yCoord = verticalVelocityStart;  
  }

protected void updateTurretOffset(double sinYaw, double cosYaw)
  {
  turretOffset.xCoord = turretOffsetBase.xCoord;
  turretOffset.yCoord = turretOffsetBase.yCoord;
  turretOffset.zCoord = turretOffsetBase.zCoord;
  turretOffset.rotateAroundY(rotationYaw*Trig.TORADIANS);
  }

public Vec3 getTurretOffset()
  {
  return turretOffset;
  }

public Vec3 getLaunchVelocity()
  {
  return launchVelocity;
  }

}
