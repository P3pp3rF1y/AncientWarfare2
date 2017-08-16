package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.shadowmage.ancientwarfare.automation.tile.torque.multiblock.TileFlywheelStorage;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.TorqueCell;

public abstract class TileFlywheelControl extends TileTorqueSingleCell {

    private boolean powered;

    private final TorqueCell inputCell;

    public TileFlywheelControl() {
        double max = getMaxTransfer();
        double eff = getEfficiency();
        inputCell = new TorqueCell(max, max, max, eff);
        torqueCell = new TorqueCell(max, max, max, eff);
    }

    protected abstract double getEfficiency();

    protected abstract double getMaxTransfer();

    @Override
    public void updateEntity() {
        if (!worldObj.isRemote) {
            serverNetworkUpdate();
            torqueIn = torqueCell.getEnergy() - prevEnergy;
            torqueLoss = applyPowerDrain(torqueCell);
            torqueLoss += applyPowerDrain(inputCell);
            torqueLoss += applyDrainToStorage();
            torqueOut = transferPowerTo(getPrimaryFacing());
            balancePower();
            prevEnergy = torqueCell.getEnergy();
        } else {
            clientNetworkUpdate();
            updateRotation();
        }
    }

    protected double applyDrainToStorage() {
        TileFlywheelStorage storage = getControlledFlywheel();
        if (storage == null) {
            return 0;
        }
        return storage.torqueLoss;
    }

    /**
     * fill output from input
     * fill output from storage
     * fill storage from input
     */
    protected void balancePower() {
        TileFlywheelStorage storage = getControlledFlywheel();
        double in = inputCell.getEnergy();
        double out = torqueCell.getEnergy();
        double transfer = torqueCell.getMaxEnergy() - out;
        transfer = Math.min(in, transfer);
        in -= transfer;
        out += transfer;
        if (storage != null) {
            double store = storage.storedEnergy;
            transfer = Math.min(store, torqueCell.getMaxEnergy() - out);
            store -= transfer;
            out += transfer;

            transfer = Math.min(in, storage.maxEnergyStored - store);
            in -= transfer;
            store += transfer;
            storage.storedEnergy = store;
            torqueLoss += storage.torqueLoss;
        }
        torqueCell.setEnergy(out);
        inputCell.setEnergy(in);
    }

    @Override
    protected void updateRotation() {
        prevRotation = rotation;
        if (!powered) {
            super.updateRotation();
        }
    }

    public TileFlywheelStorage getControlledFlywheel() {
        int x = xCoord;
        int y = yCoord - 1;
        int z = zCoord;
        TileEntity te = worldObj.getTileEntity(x, y, z);
        if (te instanceof TileFlywheelStorage) {
            TileFlywheelStorage fs = (TileFlywheelStorage) te;
            if (fs.controllerPos != null) {
                x = fs.controllerPos.x;
                y = fs.controllerPos.y;
                z = fs.controllerPos.z;
                te = worldObj.getTileEntity(x, y, z);
                if (te instanceof TileFlywheelStorage) {
                    return (TileFlywheelStorage) te;
                }
            }
        }
        return null;
    }

    public float getFlywheelRotation(float delta) {
        TileFlywheelStorage storage = getControlledFlywheel();
        return storage == null ? 0 : getRotation(storage.rotation, storage.prevRotation, delta);
    }

    protected double getFlywheelEnergy() {
        TileFlywheelStorage storage = getControlledFlywheel();
        return storage == null ? 0 : storage.storedEnergy;//TODO
    }

    @Override
    protected double getTotalTorque() {
        return inputCell.getEnergy() + torqueCell.getEnergy() + getFlywheelEnergy();
    }

    @Override
    public void onNeighborTileChanged() {
        super.onNeighborTileChanged();
        if (!worldObj.isRemote) {
            boolean p = worldObj.getBlockPowerInput(xCoord, yCoord, zCoord) > 0;
            if (p != powered) {
                powered = p;
                sendDataToClient(7, powered ? 1 : 0);
            }
        }
    }

    @Override
    public boolean receiveClientEvent(int a, int b) {
        if (worldObj.isRemote) {
            if (a == 7) {
                powered = b == 1;
            }
        }
        return super.receiveClientEvent(a, b);
    }

    @Override
    public double getMaxTorqueOutput(EnumFacing from) {
        if (powered) {
            return 0;
        }
        return torqueCell.getMaxTickOutput();
    }

    @Override
    public double getMaxTorque(EnumFacing from) {
        TorqueCell cell = getCell(from);
        return cell == null ? 0 : cell.getMaxEnergy();
    }

    @Override
    public double getTorqueStored(EnumFacing from) {
        TorqueCell cell = getCell(from);
        return cell == null ? 0 : cell.getEnergy();
    }

    @Override
    public double addTorque(EnumFacing from, double energy) {
        TorqueCell cell = getCell(from);
        return cell == null ? 0 : cell.addEnergy(energy);
    }

    @Override
    public double drainTorque(EnumFacing from, double energy) {
        TorqueCell cell = getCell(from);
        return cell == null ? 0 : cell.drainEnergy(energy);
    }

    @Override
    public double getMaxTorqueInput(EnumFacing from) {
        TorqueCell cell = getCell(from);
        return cell == null ? 0 : cell.getMaxTickInput();
    }

    private TorqueCell getCell(EnumFacing from) {
        if (from == orientation) {
            return torqueCell;
        } else if (from == orientation.getOpposite()) {
            return inputCell;
        }
        return null;
    }

    //*************************************** NBT / DATA PACKET ***************************************//
    @Override
    public NBTTagCompound getDescriptionTag() {
        NBTTagCompound tag = super.getDescriptionTag();
        tag.setBoolean("powered", powered);
        return tag;
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
        NBTTagCompound tag = pkt.func_148857_g();
        powered = tag.getBoolean("powered");
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setBoolean("powered", powered);
        tag.setDouble("torqueEnergyIn", inputCell.getEnergy());
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        powered = tag.getBoolean("powered");
        inputCell.setEnergy(tag.getDouble("torqueEnergyIn"));
    }

}
