package net.shadowmage.ancientwarfare.automation.tile.torque.multiblock;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.network.PacketBlockEvent;
import net.shadowmage.ancientwarfare.core.util.BlockFinder;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;

import java.util.HashSet;
import java.util.Set;

public class TileFlywheelStorage extends TileEntity {

    public BlockPosition controllerPos;
    public boolean isControl = false;//set to true if this is the control block for a setup
    public int setWidth, setHeight, setCube, setType;//validation params, only 'valid' in the control block.  used by rendering
    public double storedEnergy, maxEnergyStored, maxRpm = 100;
    public double torqueLoss;
    public double rotation, prevRotation;//used in rendering
    private int clientEnergy, clientDestEnergy;
    private int networkUpdateTicks = 0;

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (isControl) {
            if (worldObj.isRemote) {
                clientNetworkUpdate();
            } else {
                serverNetworkUpdate();
                applyPowerLoss();
            }
        }
    }

    protected void clientNetworkUpdate() {
        updateRotation();
        if (networkUpdateTicks > 0) {
            int diff = clientDestEnergy - clientEnergy;
            clientEnergy += diff / networkUpdateTicks;
            networkUpdateTicks--;
        }
    }

    protected void applyPowerLoss() {
        double eff = 1.d - getEfficiency();
        eff *= 0.1d;
        torqueLoss = storedEnergy * eff;
        storedEnergy -= torqueLoss;
    }

    protected double getEfficiency() {
        int meta = getBlockMetadata();
        switch (meta) {
            case 0:
                return AWAutomationStatics.low_efficiency_factor;
            case 1:
                return AWAutomationStatics.med_efficiency_factor;
            case 2:
                return AWAutomationStatics.high_efficiency_factor;
            default:
                return AWAutomationStatics.low_efficiency_factor;
        }
    }

    protected void serverNetworkUpdate() {
        if (!AWAutomationStatics.enable_energy_network_updates) {
            return;
        }
        networkUpdateTicks--;
        if (networkUpdateTicks <= 0) {
            double percentStored = storedEnergy / maxEnergyStored;
            int total = (int) (percentStored * 100.d);
            if (total != clientEnergy) {
                clientEnergy = total;
                sendDataToClient(1, clientEnergy);
            }
            networkUpdateTicks = AWAutomationStatics.energyMinNetworkUpdateFrequency;
        }
    }

    protected final void sendDataToClient(int type, int data) {
        PacketBlockEvent pkt = new PacketBlockEvent();
        pkt.setParams(xCoord, yCoord, zCoord, getBlockType(), (byte) type, (short) data);
        NetworkHandler.sendToAllTrackingChunk(worldObj, xCoord >> 4, zCoord >> 4, pkt);
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return pass == 0 || pass == 1;
    }

    @Override
    public boolean receiveClientEvent(int a, int b) {
        if (worldObj.isRemote) {
            if (a == 1) {
                clientDestEnergy = b;
                networkUpdateTicks = AWAutomationStatics.energyMinNetworkUpdateFrequency;
            }
        }
        return true;
    }

    protected void updateRotation() {
        double maxRpm = this.maxRpm;
        double rpm = (double) clientEnergy * 0.01d * maxRpm;
        prevRotation = rotation;
        rotation += rpm * 360.d / 20.d / 60.d;
    }

    public void blockBroken() {
        if (isControl) {
            informNeighborsToValidate();
        } else if (controllerPos != null) {
            TileFlywheelStorage controller = getController();
            if (controller == null || controller == this) {
                informNeighborsToValidate();
            } else {
                controller.validateSetup();
            }
        } else {
            informNeighborsToValidate();
        }
    }

    public final void blockPlaced() {
        validateSetup();
    }

    public final void setController(BlockPosition pos) {
        this.controllerPos = pos == null ? null : pos.copy();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    protected boolean validateSetup() {
        Set<BlockPosition> connectedPosSet = new HashSet<BlockPosition>();
        BlockFinder.findConnectedSixWay(worldObj, xCoord, yCoord, zCoord, getBlockType(), getBlockMetadata(), connectedPosSet);
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, minZ = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE, maxZ = Integer.MIN_VALUE;
        for (BlockPosition pos : connectedPosSet) {
            if (pos.x < minX) {
                minX = pos.x;
            }
            if (pos.x > maxX) {
                maxX = pos.x;
            }
            if (pos.y < minY) {
                minY = pos.y;
            }
            if (pos.y > maxY) {
                maxY = pos.y;
            }
            if (pos.z < minZ) {
                minZ = pos.z;
            }
            if (pos.z > maxZ) {
                maxZ = pos.z;
            }
        }
        int w = maxX - minX + 1;
        int l = maxZ - minZ + 1;
        int h = maxY - minY + 1;
        int cube = w * l * h;
        boolean valid = cube == connectedPosSet.size() && ((w == 1 && l == 1) || (w == 3 && l == 3));
        if (valid) {
            int cx = w == 1 ? minX : minX + 1;
            int cz = l == 1 ? minZ : minZ + 1;
            int cy = minY;
            setValidSetup(connectedPosSet, cx, cy, cz, w, h, getBlockMetadata());
        } else {
            setInvalidSetup(connectedPosSet);
        }
        return valid;
    }

    private void setValidSetup(Set<BlockPosition> set, int cx, int cy, int cz, int size, int height, int type) {
        TileEntity te;
        BlockPosition cp = new BlockPosition(cx, cy, cz);
        controllerPos = cp;
        for (BlockPosition pos : set) {
            te = worldObj.getTileEntity(pos.x, pos.y, pos.z);
            if (te instanceof TileFlywheelStorage) {
                ((TileFlywheelStorage) te).setController(cp);
            }
            ((TileFlywheelStorage) te).isControl = (pos.x == cx && pos.y == cy && pos.z == cz);
        }
        setTileAsController(cp.x, cp.y, cp.z, size, height, type);
    }

    private void setTileAsController(int x, int y, int z, int size, int height, int type) {
        TileEntity te = worldObj.getTileEntity(x, y, z);
        if (te instanceof TileFlywheelStorage) {
            ((TileFlywheelStorage) te).setAsController(size, height, type);
        }
    }

    private void setAsController(int size, int height, int type) {
        this.isControl = true;
        this.setWidth = size;
        this.setHeight = height;
        this.setType = type;
        this.setCube = size * size * height;
        double energyPerBlockForType = 1600;
        switch (type) {
            case 0: {
                energyPerBlockForType = AWAutomationStatics.low_storage_energy_max;
                break;
            }
            case 1: {
                energyPerBlockForType = AWAutomationStatics.med_storage_energy_max;
                break;
            }
            case 2: {
                energyPerBlockForType = AWAutomationStatics.high_storage_energy_max;
                break;
            }
        }
        this.maxEnergyStored = (double) setCube * energyPerBlockForType;
        this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    private void setInvalidSetup(Set<BlockPosition> set) {
        TileEntity te;
        controllerPos = null;
        isControl = false;
        for (BlockPosition pos : set) {
            te = worldObj.getTileEntity(pos.x, pos.y, pos.z);
            if (te instanceof TileFlywheelStorage) {
                ((TileFlywheelStorage) te).setController(null);
                ((TileFlywheelStorage) te).isControl = false;
            }
        }
    }

    private void informNeighborsToValidate() {
        TileEntity te;
        ForgeDirection d;
        int x, y, z;
        for (int i = 0; i < 6; i++) {
            d = ForgeDirection.getOrientation(i);
            x = xCoord + d.offsetX;
            y = yCoord + d.offsetY;
            z = zCoord + d.offsetZ;
            te = worldObj.getTileEntity(x, y, z);
            if (te instanceof TileFlywheelStorage) {
                ((TileFlywheelStorage) te).validateSetup();
            }
        }
    }

    public TileFlywheelStorage getController() {
        if (controllerPos != null) {
            TileEntity te = worldObj.getTileEntity(controllerPos.x, controllerPos.y, controllerPos.z);
            return te instanceof TileFlywheelStorage ? (TileFlywheelStorage) te : null;
        }
        return null;
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        if (controllerPos != null) {
            tag.setTag("controllerPos", controllerPos.writeToNBT(new NBTTagCompound()));
            if (isControl) {
                tag.setBoolean("isControl", true);
                tag.setInteger("setWidth", setWidth);
                tag.setInteger("setHeight", setHeight);
                tag.setInteger("setType", setType);
                tag.setInteger("clientEnergy", clientEnergy);
            }
        }
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
        NBTTagCompound tag = pkt.func_148857_g();
        controllerPos = tag.hasKey("controllerPos") ? new BlockPosition(tag.getCompoundTag("controllerPos")) : null;
        if (controllerPos != null) {
            isControl = tag.getBoolean("isControl");
            if (isControl) {
                setHeight = tag.getInteger("setHeight");
                setWidth = tag.getInteger("setWidth");
                setCube = setWidth * setWidth * setHeight;
                setType = tag.getInteger("type");
                clientEnergy = tag.getInteger("clientEnergy");
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        controllerPos = tag.hasKey("controllerPos") ? new BlockPosition(tag.getCompoundTag("controllerPos")) : null;
        if (controllerPos != null) {
            isControl = tag.getBoolean("isControl");
            if (isControl) {
                maxEnergyStored = tag.getDouble("maxEnergy");
                setHeight = tag.getInteger("setHeight");
                setWidth = tag.getInteger("setWidth");
                setCube = setWidth * setWidth * setHeight;
                setType = tag.getInteger("type");
            }
        }
        storedEnergy = tag.getDouble("storedEnergy");
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        if (controllerPos != null) {
            tag.setTag("controllerPos", controllerPos.writeToNBT(new NBTTagCompound()));
            if (isControl) {
                tag.setBoolean("isControl", true);
                tag.setDouble("maxEnergy", maxEnergyStored);
                tag.setInteger("setWidth", setWidth);
                tag.setInteger("setHeight", setHeight);
                tag.setInteger("setType", setType);
            }
        }
        tag.setDouble("storedEnergy", storedEnergy);
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return AxisAlignedBB.getBoundingBox(xCoord - 1, yCoord - 1, zCoord - 1, xCoord + 2, yCoord + setHeight, zCoord + 2);
    }

}
