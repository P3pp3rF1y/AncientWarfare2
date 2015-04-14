package net.shadowmage.ancientwarfare.vehicle.entity;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.Trig;
import net.shadowmage.ancientwarfare.vehicle.entity.collision.VehicleOBBMoveHelper;
import net.shadowmage.ancientwarfare.vehicle.entity.movement.VehicleInputHandler;
import net.shadowmage.ancientwarfare.vehicle.input.VehicleBaseInputHandler;

public class VehicleBase extends Entity implements IEntityAdditionalSpawnData {

    public VehicleOBBMoveHelper moveHelper;
    public VehicleBaseInputHandler inputHandler;
    public VehicleInputHandler moveHandler;

    VehiclePart[] parts;

    public float vehicleWidth, vehicleHeight, vehicleLength;

    public VehicleBase(World world) {
        super(world);
        vehicleWidth = 2.f;
        vehicleHeight = 1.50f;
        vehicleLength = 3.f;

        World.MAX_ENTITY_RADIUS = Math.max(World.MAX_ENTITY_RADIUS, Math.max(vehicleWidth * 1.4f, vehicleLength * 1.4f));

        moveHelper = new VehicleOBBMoveHelper(this);
        inputHandler = new VehicleBaseInputHandler(this);

        width = 1.42f * Math.max(vehicleWidth, vehicleLength);//due to not using rotated BBs, this can be set to a minimal square extent for the entity-parts used for collision checking
        height = vehicleHeight;
        stepHeight = 1.0f;

        buildParts();//need to call build parts in the constructor to align entity-ids properly (they are supposed to be sequential)
    }

    /**
     * Add data fields to data-watchers in this block.
     * It is called at the end of the vanilla Entity base class constructor, so you must not rely on any
     * of your sub-class fields being initialized (setting size/health/whatever else happens in the constructor has not happened yet)
     */
    @Override
    protected void entityInit() {

    }

    @Override
    public void onUpdate() {
        worldObj.theProfiler.startSection("AWVehicleTick");
        super.onUpdate();
        inputHandler.onUpdate();
        moveHelper.update();
        updatePartPositions();
        worldObj.theProfiler.endSection();
    }

//************************************* MOVEMENT HANDLING *************************************//
// Custom movement handling using vehicle OBB(s) for terrain collision detection for both movement and rotation.
// Uses SAT for basic overlap tests for y-movement, uses some custom ray-tracing for testing move extents on x/z axes
// 

    /**
     * Overriden to remove applying fall distance to rider
     *
     * @param distance (unused)
     */
    @Override
    protected void fall(float distance) {
    }

    /**
     * Overriden to use OBB for movement collision checks.<br>
     * Currently does not replicate vanilla functionality for contact with fire blocks, web move speed reduction, walk-on-block checks, or distance traveled
     */
    @Override
    public void moveEntity(double inputXMotion, double inputYMotion, double inputZMotion) {
        moveHelper.moveVehicle(inputXMotion, inputYMotion, inputZMotion);
    }

//************************************* COLLISION HANDLING *************************************//
// Disabled in base class to allow entity-parts to handle the collision handling.  Each vehicle part
// is responsible for updating its own position.  Vehicle base is responsible for resolving collision
// detection with world/entities and vehicleparts.  Vehicle parts bridge all interaction stuff back to
// the owning parent vehicle (interact, attack)

    /**
     * Allow child parts to determine entity-entity collision boxes
     *
     * @param entity (unused)
     * @return null for vehicle implementation
     */
    @Override
    public AxisAlignedBB getCollisionBox(Entity entity) {
        return null;
    }

    /**
     * Allow child parts to determine collision status
     *
     * @return false for vehicle implementation
     */
    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    /**
     * Allow child parts to determine push-status for entity-entity interaction
     *
     * @return false for vehicle implementation
     */
    @Override
    public boolean canBePushed() {
        return false;
    }

    /**
     * Return null so that collisions happen with children pieces
     *
     * @return null for vehicle implementation
     */
    @Override
    public AxisAlignedBB getBoundingBox() {
        return null;
    }

    /**
     * Renderpass 0 for normal rendering<br>
     * Renderpass 1 for debug bounding box rendering<br>
     * TODO remove pass1 and override when no longer needed
     */
    @Override
    public boolean shouldRenderInPass(int pass) {
        return pass == 0 || pass == 1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int ticks) {
        inputHandler.handleVanillaSynch(x, y, z, yaw, pitch, ticks);
    }

//************************************* MULTIPART ENTITY HANDLING CODE *************************************//
// Updating of entity parts, handling interaction with specific entity parts.
//

    /**
     * Return an array containing the sub-parts to this entity.  These sub-parts are not added to the world and not synchronized between client and server.
     * Any synchronization is left to the implementing class.<br>
     * These parts must be created DIRECTLY after the vehicle is initialized from constructor, as entity-IDs for parts must be sequential AND match on client/server
     * <br>Changed return type from Entity to VehiclePart for easier use when VehicleBase is the known type
     */
    @Override
    public final VehiclePart[] getParts() {
        return parts;
    }

    /**
     * Will be made abstract for actual classes<br>
     * Implementations should return an array containing the vehicle parts for the given vehicle<br>
     * Each part is responsible for updating its own location relative to vehicle position.<br>
     * May have a config option to -not- use multiple vehicle parts,in which case a single vehicle part should be returned for the vehicle bounds
     */
    protected void buildParts() {
        parts = new VehiclePart[8];
        parts[0] = new VehiclePart(this, 1, height, -0.5f, 1.0f);
        parts[1] = new VehiclePart(this, 1, height, -0.5f, 0.0f);
        parts[2] = new VehiclePart(this, 1, height, -0.5f, -1.0f);
        parts[3] = new VehiclePart(this, 1, height, 0.5f, 1.0f);
        parts[4] = new VehiclePart(this, 1, height, 0.5f, 0.0f);
        parts[5] = new VehiclePart(this, 1, height, 0.5f, -1.0f);

        parts[6] = new VehiclePart(this, 1, height, 0.0f, -0.5f);
        parts[7] = new VehiclePart(this, 1, height, 0.0f, 0.5f);
    }

    protected final void updatePartPositions() {
        for (VehiclePart part : getParts()) {
            part.updatePosition();
        }
    }

    @Override
    public boolean interactFirst(EntityPlayer player) {
        if (!worldObj.isRemote && this.riddenByEntity == null && player.ridingEntity == null) {
            player.mountEntity(this);
        }
        return true;//return true for isHandled
    }

    /**
     * Return a unit-length, normalized look vector for the current rotationYaw of the vehicle
     */
    @Override
    public Vec3 getLookVec() {
        Vec3 vec = Vec3.createVectorHelper(0, 0, -1);
        vec.rotateAroundY(MathHelper.wrapAngleTo180_float(rotationYaw) * Trig.TORADIANS);
        return vec;
    }

    public final boolean attackEntityFromPart(VehiclePart part, DamageSource source, float damage) {
        return attackEntityFrom(source, damage);
    }

//************************************* NBT / NETWORK *************************************//
// Basic read/write from disk / client-data stream
//

    @Override
    protected void readEntityFromNBT(NBTTagCompound var1) {
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound var1) {

    }

    @Override
    public void writeSpawnData(ByteBuf data) {
    }

    @Override
    public void readSpawnData(ByteBuf data) {
    }

}
