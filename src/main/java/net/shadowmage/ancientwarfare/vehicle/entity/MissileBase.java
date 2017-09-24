package net.shadowmage.ancientwarfare.vehicle.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.shadowmage.ancientwarfare.core.util.Trig;

import java.util.UUID;

public class MissileBase extends Entity implements IEntityAdditionalSpawnData {

    private double moveX, moveZ, moveY;//calculated per-tick movement on x and z axes
    private UUID launcherUniqueId;

    public MissileBase(World world) {
        super(world);
        this.width = 0.5f;
        this.height = 0.5f;
    }

    /*
     * @param launchYaw   yaw angle for launching (global rotation)
     * @param launchPitch pitch angle for launching (0=horizon)
     * @param launchPower velocity in m/s
     * @param launcherID  UUID of the launching entity.  Used to determine hit-callbacks and if the missile should damage entities it hits (will not damage same-team entities)
     */
    public void setLaunchParameters(double moveX, double moveY, double moveZ, UUID launcherID) {
        this.moveX = moveX;
        this.moveY = moveY;
        this.moveZ = moveZ;
        this.launcherUniqueId = launcherID;
    }

    @Override
    protected void entityInit() {

    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        updateTrajectory();
    }

    public void updateTrajectory() {
        posX += moveX;
        posY += moveY;
        posZ += moveZ;
        setPosition(posX, posY, posZ);
        moveY -= Trig.gravityTick;
        if (!world.isRemote && !world.isAirBlock(new BlockPos(MathHelper.floor(posX), MathHelper.floor(posY), MathHelper.floor(posZ)))) {
            setDead();
        }
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound tag) {

    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tag) {

    }

    @Override
    public void writeSpawnData(ByteBuf data) {
        data.writeDouble(moveX);
        data.writeDouble(moveY);
        data.writeDouble(moveZ);
        data.writeBoolean(launcherUniqueId != null);//write a boolean if launcherID has been written
        if (launcherUniqueId != null) {
            data.writeLong(launcherUniqueId.getMostSignificantBits());
            data.writeLong(launcherUniqueId.getLeastSignificantBits());
        }
    }

    @Override
    public void readSpawnData(ByteBuf data) {
        moveX = data.readDouble();
        moveY = data.readDouble();
        moveZ = data.readDouble();
        if (data.readBoolean())//if boolean==true, launcherID should be read from stream
        {
            launcherUniqueId = new UUID(data.readLong(), data.readLong());
        }
    }

}
