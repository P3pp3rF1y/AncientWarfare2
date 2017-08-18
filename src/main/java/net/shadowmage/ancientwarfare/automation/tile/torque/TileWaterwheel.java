package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.TorqueCell;

public class TileWaterwheel extends TileTorqueSingleCell {

    public float wheelRotation;
    public float prevWheelRotation;

    private float rotTick;
    private byte rotationDirection = 1;

    private int updateTick;

    public boolean validSetup = false;

    public TileWaterwheel() {
        torqueCell = new TorqueCell(0, 4, AWAutomationStatics.low_transfer_max, AWAutomationStatics.low_efficiency_factor);
        float maxWheelRpm = 20;
        rotTick = maxWheelRpm * AWAutomationStatics.rpmToRpt;
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (!worldObj.isRemote) {
            updateTick--;
            if (updateTick <= 0) {
                updateTick = 20;
                boolean valid = validateBlocks();
                if (valid != validSetup) {
                    validSetup = valid;
                    worldObj.notifyBlockUpdate(xCoord, yCoord, zCoord);
                }
            }
            if (validSetup)//server, update power gen
            {
                torqueCell.setEnergy(torqueCell.getEnergy() + AWAutomationStatics.waterwheel_generator_output);
            }
        }
    }

    @Override
    protected void updateRotation() {
        super.updateRotation();
        prevWheelRotation = wheelRotation;
        if (validSetup) {
            wheelRotation += rotTick * (float) rotationDirection;
        }
    }

    @Override
    public NBTTagCompound getDescriptionTag() {
        NBTTagCompound tag = super.getDescriptionTag();
        tag.setBoolean("validSetup", validSetup);
        tag.setByte("rotationDirection", rotationDirection);
        return tag;
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
        NBTTagCompound tag = pkt.func_148857_g();
        validSetup = tag.getBoolean("validSetup");
        rotationDirection = tag.getByte("rotationDirection");
    }

    private EnumFacing getRight(EnumFacing in) {
        switch (in) {
            case NORTH: {
                return EnumFacing.EAST;
            }
            case EAST: {
                return EnumFacing.SOUTH;
            }
            case SOUTH: {
                return EnumFacing.WEST;
            }
            case WEST: {
                return EnumFacing.NORTH;
            }
            default:
                return EnumFacing.NORTH;
        }
    }

    private boolean validateBlocks() {
        EnumFacing d = orientation.getOpposite();
        int x = xCoord + d.offsetX;
        int y = yCoord + d.offsetY;
        int z = zCoord + d.offsetZ;
        //quick check for invalid setup
        //must have air inside the inner two blocks
        if(getValidationType(x, y + 1, z) != 1 || getValidationType(x, y, z) != 1)
            return false;
        EnumFacing dr = getRight(d);
        EnumFacing dl = dr.getOpposite();
        int x1 = x + dr.offsetX;
        int z1 = z + dr.offsetZ;
        int x2 = x + dl.offsetX;
        int z2 = z + dl.offsetZ;
        byte[] validationGrid = new byte[6];
        validationGrid[0] = getValidationType(x2, y + 1, z2);
        validationGrid[1] = getValidationType(x1, y + 1, z1);
        validationGrid[2] = getValidationType(x2, y, z2);
        validationGrid[3] = getValidationType(x1, y, z1);
        validationGrid[4] = getValidationType(x2, y - 1, z2);
        validationGrid[5] = getValidationType(x1, y - 1, z1);
        for (byte aValue : validationGrid) {
            if (aValue == 0) {
                return false;
            }
        }
        if (validationGrid[2] == 2 && validationGrid[4] == 2)//left side water flowing down
        {
            //check opposite side for air (underneath has already been checked by quick block check above)
            if (validationGrid[1] == 1 && validationGrid[3] == 1) {
                rotationDirection = -1;//TODO check if these are correct rotation directions
                return true;
            }
            return false;
        } else if (validationGrid[3] == 2 && validationGrid[5] == 2)//right side water flowing down
        {
            //check opposite side for air (underneath has already been checked by quick block check above)
            if (validationGrid[0] == 1 && validationGrid[2] == 1) {
                rotationDirection = 1;
                return true;
            }
            return false;
        } else//not a direct flow downwards, check underneath for flow
        {
            if (validationGrid[4] != 2 || validationGrid[5] != 2 || getValidationType(x, y - 1, z) != 2) {
                return false;
            }
            int metaLeft = worldObj.getBlockMetadata(x2, y - 1, z2);
            int metaRight = worldObj.getBlockMetadata(x1, y - 1, z1);
            rotationDirection = (byte) (metaLeft < metaRight ? -1 : metaRight < metaLeft ? 1 : 0);
            return true;
        }
    }

    private byte getValidationType(int x, int y, int z) {
        Block block = worldObj.getBlock(x, y, z);
        if (block.isAir(worldObj, x, y, z)) {
            return 1;
        }
        if (block.getMaterial() == Material.water) {
            return 2;
        }
        return 0;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return AxisAlignedBB.getBoundingBox(xCoord - 3, yCoord - 3, zCoord - 3, xCoord + 4, yCoord + 4, zCoord + 4);
    }

}
