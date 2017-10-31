package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.shadowmage.ancientwarfare.automation.tile.torque.multiblock.TileFlywheelStorage;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.TorqueCell;

import javax.annotation.Nullable;

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
    public void update() {
        //TODO is this really not supposed to call super.update?
        if (!world.isRemote) {
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

    /*
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
        if (!powered) {
            super.updateRotation();
        }
    }

    @Nullable
    public TileFlywheelStorage getControlledFlywheel() {
        BlockPos controllerPos = pos.offset(EnumFacing.DOWN);
        TileEntity te = world.getTileEntity(controllerPos);
        if (te instanceof TileFlywheelStorage) {
            TileFlywheelStorage fs = (TileFlywheelStorage) te;
            if (fs.controllerPos != null) {
                controllerPos = fs.controllerPos;
                te = world.getTileEntity(controllerPos);
                if (te instanceof TileFlywheelStorage) {
                    return (TileFlywheelStorage) te;
                }
            }
        }
        return null;
    }

    public float getFlywheelRotation(float delta) {
        TileFlywheelStorage storage = getControlledFlywheel();
        return storage == null ? 0 : getRenderRotation(storage.rotation, storage.lastRotationDiff, delta);
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
        if (!world.isRemote) {
            boolean p = world.getStrongPower(pos) > 0;
            if (p != powered) {
                powered = p;
                sendDataToClient(7, powered ? 1 : 0);
            }
        }
    }

    @Override
    public boolean receiveClientEvent(int a, int b) {
        if (world.isRemote) {
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

    @Nullable
    private TorqueCell getCell(EnumFacing from) {
        if (from == orientation) {
            return torqueCell;
        } else if (from == orientation.getOpposite()) {
            return inputCell;
        }
        return null;
    }

    //************************************** NBT / DATA PACKET ***************************************//

    @Override
    protected void handleUpdateNBT(NBTTagCompound tag) {
        super.handleUpdateNBT(tag);
        powered = tag.getBoolean("powered");
    }

    @Override
    protected void writeUpdateNBT(NBTTagCompound tag) {
        super.writeUpdateNBT(tag);
        tag.setBoolean("powered", powered);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setBoolean("powered", powered);
        tag.setDouble("torqueEnergyIn", inputCell.getEnergy());
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        powered = tag.getBoolean("powered");
        inputCell.setEnergy(tag.getDouble("torqueEnergyIn"));
    }

}
