package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.TorqueCell;

/**
 * base template class that includes a single torque cell and rotation synching
 *
 * @author Shadowmage
 */
public abstract class TileTorqueSingleCell extends TileTorqueBase {

    TorqueCell torqueCell;

    /**
     * client side this == 0.0 -> 100, as a whole number percent of max rotation value
     */
    double clientEnergyState;

    /**
     * server side this == 0 -> 100 (integer percent)
     * client side this == 0 -> 100 (integer percent)
     */
    int clientDestEnergyState;

    /**
     * used client side for rendering
     */
    double rotation, prevRotation;

    public TileTorqueSingleCell() {

    }

    @Override
    public void update() {
        if (!world.isRemote) {
            serverNetworkUpdate();
            torqueIn = torqueCell.getEnergy() - prevEnergy;
            torqueLoss = applyPowerDrain(torqueCell);
            torqueOut = transferPowerTo(getPrimaryFacing());
            prevEnergy = torqueCell.getEnergy();
        } else {
            clientNetworkUpdate();
            updateRotation();
        }
    }

    protected double applyPowerLoss() {
        return applyPowerDrain(torqueCell);
    }

    @Override
    protected void serverNetworkSynch() {
        int percent = (int) (torqueCell.getPercentFull() * 100.d);
        int percent2 = (int) ((torqueOut / torqueCell.getMaxOutput()) * 100.d);
        percent = Math.max(percent, percent2);
        if (percent != clientDestEnergyState) {
            clientDestEnergyState = percent;
            sendSideRotation(getPrimaryFacing(), percent);
        }
    }

    @Override
    protected void updateRotation() {
        prevRotation = rotation;
        if (clientEnergyState > 0) {
            rotation += AWAutomationStatics.low_rpt * clientEnergyState * 0.01d;
        }
    }

    @Override
    protected void clientNetworkUpdate() {
        if (clientEnergyState != clientDestEnergyState) {
            if (networkUpdateTicks > 0) {
                clientEnergyState += (clientDestEnergyState - clientEnergyState) / ((double) networkUpdateTicks);
                networkUpdateTicks--;
            } else {
                clientEnergyState = clientDestEnergyState;
            }
        }
    }

    @Override
    protected void handleClientRotationData(EnumFacing side, int value) {
        AWLog.logDebug("receiving sided rotation data: " + side + " :: " + value);
        if (side == orientation) {
            clientDestEnergyState = value;
            networkUpdateTicks = AWAutomationStatics.energyMinNetworkUpdateFrequency;
        }
    }

    @Override
    public double getMaxTorque(EnumFacing from) {
        return torqueCell.getMaxEnergy();
    }

    @Override
    public double getTorqueStored(EnumFacing from) {
        return torqueCell.getEnergy();
    }

    @Override
    public double addTorque(EnumFacing from, double energy) {
        return torqueCell.addEnergy(energy);
    }

    @Override
    public double drainTorque(EnumFacing from, double energy) {
        return torqueCell.drainEnergy(energy);
    }

    @Override
    public double getMaxTorqueOutput(EnumFacing from) {
        return canOutputTorque(from) ? torqueCell.getMaxTickOutput() : 0;
    }

    @Override
    public double getMaxTorqueInput(EnumFacing from) {
        return canInputTorque(from) ? torqueCell.getMaxTickInput() : 0;
    }

    @Override
    public boolean useOutputRotation(EnumFacing from) {
        return true;
    }

    @Override
    protected double getTotalTorque() {
        return torqueCell.getEnergy();
    }

    @Override
    public boolean canOutputTorque(EnumFacing towards) {
        return towards == orientation;
    }

    @Override
    public boolean canInputTorque(EnumFacing from) {
        return from == orientation.getOpposite();
    }

    @Override
    public float getClientOutputRotation(EnumFacing from, float delta) {
        return from == orientation ? getRotation(rotation, prevRotation, delta) : 0;
    }

    @Override
    protected void handleUpdateNBT(NBTTagCompound tag) {
        super.handleUpdateNBT(tag);
        clientDestEnergyState = tag.getInteger("clientEnergy");
    }

    @Override
    protected void writeUpdateNBT(NBTTagCompound tag) {
        super.writeUpdateNBT(tag);
        tag.setInteger("clientEnergy", clientDestEnergyState);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        torqueCell.setEnergy(tag.getDouble("torqueEnergy"));
        clientDestEnergyState = tag.getInteger("clientEnergy");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setDouble("torqueEnergy", torqueCell.getEnergy());
        tag.setInteger("clientEnergy", clientDestEnergyState);
        return tag;
    }
}
