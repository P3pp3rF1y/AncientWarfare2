package net.shadowmage.ancientwarfare.vehicle.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.util.Trig;

public class VehiclePart extends Entity {

    private VehicleBase vehicle;
    private Vec3 offset;
    private Vec3 location;

    public VehiclePart(VehicleBase vehicle, float width, float height, float xOffset, float zOffset) {
        super(vehicle.worldObj);
        this.vehicle = vehicle;
        this.setSize(width, height);
        offset = Vec3.createVectorHelper(xOffset, 0, zOffset);
        location = Vec3.createVectorHelper(xOffset, 0, zOffset);
        stepHeight = 1.f;
        updatePosition();
    }

    @Override
    protected void entityInit() {
    }

    public final void updatePosition() {
        location.xCoord = offset.xCoord;
        location.yCoord = offset.yCoord;
        location.zCoord = offset.zCoord;
        location.rotateAroundY(MathHelper.wrapAngleTo180_float(vehicle.rotationYaw) * Trig.TORADIANS);
        location.xCoord += vehicle.posX;
        location.yCoord += vehicle.posY;
        location.zCoord += vehicle.posZ;
        setPosition(location.xCoord, location.yCoord, location.zCoord);
    }

    @Override
    public boolean interactFirst(EntityPlayer player) {
        AWLog.logDebug("interact with part!!");
        return vehicle.interactFirst(player);
    }

    /**
     * Used to in collision detection between this entity and the passed in entity for:<br>
     * getCollidingBoundingBoxes(Entity, AxisAlignedBB)<br>
     * which is called from:
     * <li>moveEntity
     * <li>check for valid spawn
     * <li>canSpawnHere
     * <li>zombie attack and aid summon
     * <li>boat on-right-click
     * <li>client side position synch packets<br><br>
     * Return null to disable others colliding with this entity.
     * Return the input entities bounding box (or other, adjusted) to enable collisions with this entity
     */
    @Override
    public AxisAlignedBB getCollisionBox(Entity collidingEntity) {
        if (collidingEntity instanceof VehiclePart) {
            VehiclePart p = (VehiclePart) collidingEntity;
            if (p.vehicle == vehicle) {
                return null;
            }
            return p.boundingBox;
        }
        return collidingEntity == vehicle ? null : collidingEntity.boundingBox;
    }

    /**
     * Used to get the collision box for this entity, used in:<br>
     * getCollidingBoundingBoxes(Entity, AxisAlignedBB)<br>
     * which is called from:
     * <li>moveEntity
     * <li>check for valid spawn
     * <li>canSpawnHere
     * <li>zombie attack and aid summon
     * <li>boat on-right-click
     * <li>client side position synch packets<br><br>
     * Return null to have no collisions with -this- entity (collisions with parts still function properly)<br>
     * Return boundingBox to enable collisions with -this- entity
     */
    @Override
    public AxisAlignedBB getBoundingBox() {
        return boundingBox;
    }

    /**
     * Used to determine if this entity can be hit by arrows and ray-traces in client getMouseOverObject calls<br>
     * it appears that this is the -canBeCollidedWith()- for interactions
     */
    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    /**
     * Used by entityLiving (or other mobile entities) to determine if it has contacted another mobile/pushable entity<br>
     * Only used by minecart, boat and entityLivingBase for pushability on collision
     */
    @Override
    public boolean canBePushed() {
        return true;
    }

    @Override
    public boolean attackEntityFrom(DamageSource src, float amt) {
        return this.vehicle.attackEntityFromPart(this, src, amt);
    }

    @Override
    public boolean isEntityEqual(Entity entity) {
        if (entity instanceof VehiclePart) {
            VehiclePart in = (VehiclePart) entity;
            return this == in || this.vehicle == in.vehicle;
        }
        return this == entity || this.vehicle == entity;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound p_70037_1_) {
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound p_70014_1_) {
    }

}
