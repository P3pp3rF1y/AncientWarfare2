package net.shadowmage.ancientwarfare.vehicle.entity;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.Trig;

public class VehicleTurreted extends VehicleBase {


    protected int fireDelay = 0;
    protected float launchAngle, launchPower;
    protected Vec3d turretOffsetBase = new Vec3d(0, 1, -1);

    /*
     * location of turret firing point relative to vehicle -- this is the point that the missile will be spawned at.<br>
     * updated from entity on-tick (or whenever vehicle rotation or turret pitch changes)
     */
    private Vec3d turretOffset = new Vec3d(0, 0, 0);

    /*
     * updated from entity on-tick or whenever vehicle rotation or firing params change
     */
    private Vec3d launchVelocity = new Vec3d(0, 0, 0);

    public VehicleTurreted(World world) {
        super(world);
        launchAngle = 45;
        launchPower = 30;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        double sinYaw = Math.sin(Trig.TORADIANS * (rotationYaw + 180.d));//+180 is compensation for z-axis inversion in MC?
        double cosYaw = Math.cos(Trig.TORADIANS * (rotationYaw + 180.d));// ^^
        updateLaunchVelocity(sinYaw, cosYaw);
        updateTurretOffset(sinYaw, cosYaw);
        if (fireDelay > 0) {
            fireDelay--;
        }
    }

    protected void updateLaunchVelocity(double sinYaw, double cosYaw) {
        double sinPitch = Math.sin(Trig.TORADIANS * launchAngle);
        double cosPitch = Math.cos(Trig.TORADIANS * launchAngle);
        double verticalVelocityStart = sinPitch * launchPower * 0.05d;
        double horizontalVelocityStart = cosPitch * launchPower * 0.05d;
        launchVelocity = new Vec3d(sinYaw * horizontalVelocityStart, verticalVelocityStart, cosYaw * horizontalVelocityStart);
    }

    protected void updateTurretOffset(double sinYaw, double cosYaw) {
        turretOffset = new Vec3d(turretOffsetBase.x, turretOffsetBase.y, turretOffsetBase.z);
        turretOffset.rotateYaw(rotationYaw * Trig.TORADIANS);
    }

    public Vec3d getTurretOffset() {
        return turretOffset;
    }

    public Vec3d getLaunchVelocity() {
        return launchVelocity;
    }

//@Override
//public void onFirePressedPilot(Vec3d target)
//  {  
//  AWLog.logDebug("fire pressed. delay left: "+fireDelay);
//  if(fireDelay<=0)
//    {
//    fireDelay = 40;
//    double px = posX + turretOffset.x;
//    double py = posY + turretOffset.y;
//    double pz = posZ + turretOffset.z;
//    double dx = target.x - px;
//    double dy = target.y - py;
//    double dz = target.z - pz;
//    double angleYaw = Math.atan2(dz, dx)*Trig.TODEGREES;
//    double anglePitch = 45.d;
//    double velocity = TrajectoryPlotter.getPowerToHit(dx, dy, dz, (float) anglePitch, 120);
//    
//    
//    AWLog.logDebug("firing at: "+target+" params: "+angleYaw+" : "+anglePitch+" :: "+velocity);
//
//    double sinYaw = Math.sin(Trig.TORADIANS * (rotationYaw + 180.d)); //+180 is compensation for z-axis inversion in MC?
//    double cosYaw = Math.cos(Trig.TORADIANS * (rotationYaw + 180.d)); // ^^
//    this.launchPower = (float)velocity;
//    updateLaunchVelocity(sinYaw, cosYaw);
//    
//    MissileBase missile = new MissileBase(world);
//    missile.setPosition(px, py, pz);
//    missile.setLaunchParameters(launchVelocity.x, launchVelocity.y, launchVelocity.z, getUniqueID());
//    world.spawnEntity(missile);
//    AWLog.logDebug("spawned missile..."+missile);
//    }  
//  }

}
