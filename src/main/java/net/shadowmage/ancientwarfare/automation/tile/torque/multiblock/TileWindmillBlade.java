package net.shadowmage.ancientwarfare.automation.tile.torque.multiblock;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.core.util.BlockFinder;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class TileWindmillBlade extends TileEntity {

    double bladeRpm = 20.d;
    double bladeRpt = bladeRpm * AWAutomationStatics.rpmToRpt;

    public BlockPosition controlPos;

    public boolean isControl = false;//set to true if this is the control block for a setup
    protected float rotation, prevRotation;//used in rendering

    /**
     * the raw size of the windmill in blocks tall
     */
    public int windmillSize = 0;

    public double energy = 0;

    /**
     * 0/1 == INVALID
     * 2/3 == north/south face (expands on x-axis)
     * 4/5 == east/west face (expands on z-axis)
     */
    public int windmillDirection = 2;

    public TileWindmillBlade() {

    }

    @Override
    public void updateEntity() {
        if (isControl) {
            if (!worldObj.isRemote)
                energy = windmillSize * AWAutomationStatics.windmill_per_size_output;
            else
                updateRotation();
        }
    }

    public float getRotation(float delta){
        return prevRotation + (rotation - prevRotation) * delta;
    }

    protected void updateRotation() {
        prevRotation = rotation;
        rotation += bladeRpt;
    }

    public void blockPlaced() {
        if(!worldObj.isRemote)
        validateSetup();
    }

    public void blockBroken() {
        if(!worldObj.isRemote)
        informNeighborsToValidate();
    }

    public TileWindmillBlade getMaster() {
        if (controlPos != null) {
            TileEntity te = worldObj.getTileEntity(controlPos.x, controlPos.y, controlPos.z);
            return te instanceof TileWindmillBlade ? (TileWindmillBlade) te : null;
        }else if(isControl)
            return this;
        return null;
    }

    protected boolean validateSetup() {
        List<BlockPosition> connectedPosSet = new ArrayList<BlockPosition>(9*9);
        BlockFinder finder = new BlockFinder(worldObj, getBlockType(), getBlockMetadata());
        Pair<BlockPosition, BlockPosition> corners = finder.cross(new BlockPosition(xCoord, yCoord, zCoord), new BlockPosition(17, 17, 17), connectedPosSet);
        int minX = corners.getLeft().x, minY = corners.getLeft().y, minZ = corners.getLeft().z;
        int xSize = corners.getRight().x - minX + 1, ySize = corners.getRight().y - minY + 1, zSize = corners.getRight().z - minZ + 1;

        /**
         * if y size >= 5
         * and y size <= 17
         * and h%2==1 (is an odd size, 5, 7, 9, etc)
         * and either x or z size == 1 (one needs to be a single block thick)
         * and either x or z == y size (the other needs to be the same size as height)
         * and is full cube (all block spots filled) (will need to modify this check for those sizes with missing corner blocks, create bit mask 2d array to test for proper setup)
         */
        boolean valid = ySize >= 5 && ySize % 2 == 1 && (zSize == 1 || xSize == 1) && (zSize == ySize || xSize == ySize) && finder.box(corners, connectedPosSet);
        if (valid) {
            /**
             * calculate the control block coordinates from the min coordinate and sizes
             */
            int controlX, controlY, controlZ;

            int halfSize = (ySize - 1) / 2;

            controlY = minY + halfSize;//should be the center
            if (xSize > 1)//widest on X axis
            {
                controlX = minX + halfSize;//should be the center
                controlZ = minZ;//only 1 z-coordinate
            } else//widest on Z axis
            {
                controlX = minX;//only 1 x-coordinate
                controlZ = minZ + halfSize;//should be the center
            }
            setValidSetup(connectedPosSet, controlX, controlY, controlZ, xSize, ySize, zSize);
        } else {
            finder.connect(corners.getLeft(), connectedPosSet, ForgeDirection.UP, ForgeDirection.SOUTH, ForgeDirection.EAST);
            setInvalidSetup(connectedPosSet);
        }
        return valid;
    }

    private void informNeighborsToValidate() {
        TileEntity te;
        int x, y, z;
        for (ForgeDirection d : ForgeDirection.VALID_DIRECTIONS) {
            x = xCoord + d.offsetX;
            y = yCoord + d.offsetY;
            z = zCoord + d.offsetZ;
            te = worldObj.getTileEntity(x, y, z);
            if (te instanceof TileWindmillBlade) {
                ((TileWindmillBlade) te).validateSetup();
            }
        }
    }

    private void setController(BlockPosition pos) {
        if(pos == null)
            isControl = false;
        this.controlPos = pos;
        if (!worldObj.isRemote) {
            markDirty();
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }

    private void setInvalidSetup(List<BlockPosition> set) {
        TileEntity te;
        setController(null);
        for (BlockPosition pos : set) {
            te = worldObj.getTileEntity(pos.x, pos.y, pos.z);
            if (te instanceof TileWindmillBlade) {
                ((TileWindmillBlade) te).setController(null);
            }
        }
    }

    private void setValidSetup(List<BlockPosition> set, int cx, int cy, int cz, int xs, int ys, int zs) {
        controlPos = new BlockPosition(cx, cy, cz);
        TileEntity te = worldObj.getTileEntity(controlPos.x, controlPos.y, controlPos.z);
        if (te instanceof TileWindmillBlade) {
            ((TileWindmillBlade) te).setAsController(xs, ys, zs);
            for (BlockPosition pos : set) {
                te = worldObj.getTileEntity(pos.x, pos.y, pos.z);
                if (te instanceof TileWindmillBlade) {
                    ((TileWindmillBlade) te).setController(controlPos);
                }
            }
        }else
            controlPos = null;
    }

    private void setAsController(int xSize, int ySize, int zSize) {
        windmillDirection = xSize == 1 ? 4 : zSize == 1 ? 2 : 0;
        windmillSize = ySize;
        this.isControl = true;
        this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setBoolean("isControl", isControl);
        if (controlPos != null) {
            tag.setTag("controlPos", controlPos.writeToNBT(new NBTTagCompound()));
        }
        if (isControl) {
            tag.setInteger("size", windmillSize);
            tag.setInteger("direction", windmillDirection);
        }
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
        NBTTagCompound tag = pkt.func_148857_g();
        controlPos = tag.hasKey("controlPos") ? new BlockPosition(tag.getCompoundTag("controlPos")) : null;
        isControl = tag.getBoolean("isControl");
        if (isControl) {
            windmillSize = tag.getInteger("size");
            windmillDirection = tag.getInteger("direction");
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        controlPos = tag.hasKey("controlPos") ? new BlockPosition(tag.getCompoundTag("controlPos")) : null;
        isControl = tag.getBoolean("isControl");
        if (isControl) {
            windmillSize = tag.getInteger("size");
            windmillDirection = tag.getInteger("direction");
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setBoolean("isControl", isControl);
        if (controlPos != null) {
            tag.setTag("controlPos", controlPos.writeToNBT(new NBTTagCompound()));
        }
        if (isControl) {
            tag.setInteger("size", windmillSize);
            tag.setInteger("direction", windmillDirection);
        }
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        if (isControl) {
            int expand = (windmillSize - 1) / 2;
            return AxisAlignedBB.getBoundingBox(xCoord - expand, yCoord - expand, zCoord - expand, xCoord + 1 + expand, yCoord + 1 + expand, zCoord + 1 + expand);
        }
        return getBlockType().getCollisionBoundingBoxFromPool(worldObj, xCoord, yCoord, zCoord);
    }
}
