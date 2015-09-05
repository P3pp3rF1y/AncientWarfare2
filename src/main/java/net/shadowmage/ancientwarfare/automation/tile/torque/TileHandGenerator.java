package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.scoreboard.Team;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;
import net.shadowmage.ancientwarfare.core.interfaces.IOwnable;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.TorqueCell;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite;
import net.shadowmage.ancientwarfare.core.interfaces.IWorker;
import net.shadowmage.ancientwarfare.core.upgrade.WorksiteUpgrade;

import java.util.EnumSet;

public class TileHandGenerator extends TileTorqueSingleCell implements IWorkSite, IOwnable {

    String ownerName = "";
    private final TorqueCell inputCell;

    /**
     * client side this == 0.0 -> 100.0 (integer percent)
     */
    double clientInputEnergy;

    /**
     * client side this == 0 -> 100.0 (integer percent)
     */
    int clientInputDestEnergy;

    /**
     * used client side for rendering
     */
    double inputRotation, prevInputRotation;

    public TileHandGenerator() {
        double eff = AWAutomationStatics.low_efficiency_factor;
        torqueCell = new TorqueCell(0, 32, 32, eff);
        inputCell = new TorqueCell(32, 0, 150, eff);
    }

    @Override
    public void updateEntity() {
        if (!worldObj.isRemote) {
            serverNetworkUpdate();
            torqueIn = torqueCell.getEnergy() - prevEnergy;
            balancePower();
            torqueOut = transferPowerTo(getPrimaryFacing());
            torqueLoss = applyPowerDrain(torqueCell);
            torqueLoss += applyPowerDrain(inputCell);
            prevEnergy = torqueCell.getEnergy();
        } else {
            clientNetworkUpdate();
            updateRotation();
        }
    }

    protected void balancePower() {
        double trans = Math.min(2.d, torqueCell.getMaxEnergy() - torqueCell.getEnergy());
        trans = Math.min(trans, inputCell.getEnergy());
        inputCell.setEnergy(inputCell.getEnergy() - trans);
        torqueCell.setEnergy(torqueCell.getEnergy() + trans);
    }

    @Override
    protected void serverNetworkSynch() {
        super.serverNetworkSynch();
        int percent = (int) (inputCell.getPercentFull() * 100d);
        if (percent != clientInputDestEnergy) {
            clientInputDestEnergy = percent;
            sendSideRotation(ForgeDirection.UP, percent);
        }
    }

    @Override
    protected void updateRotation() {
        super.updateRotation();
        prevInputRotation = inputRotation;
        if (clientInputEnergy > 0) {
            double r = AWAutomationStatics.low_rpt * clientInputEnergy * 0.01d;
            inputRotation += r;
        }
    }

    @Override
    protected void clientNetworkUpdate() {
        if (clientEnergyState != clientDestEnergyState || clientInputEnergy != clientInputDestEnergy) {
            if (networkUpdateTicks >= 0) {
                clientEnergyState += (clientDestEnergyState - clientEnergyState) / ((double) networkUpdateTicks + 1.d);
                clientInputEnergy += (clientInputDestEnergy - clientInputEnergy) / ((double) networkUpdateTicks + 1.d);
                networkUpdateTicks--;
            } else {
                clientEnergyState = clientDestEnergyState;
                clientInputEnergy = clientInputDestEnergy;
            }
        }
    }

    @Override
    protected void handleClientRotationData(ForgeDirection side, int value) {
        super.handleClientRotationData(side, value);
        if (side == ForgeDirection.UP) {
            clientInputDestEnergy = value;
        }
    }

    @Override
    public void onBlockBroken() {
    }//NOOP

    @Override
    public EnumSet<WorksiteUpgrade> getUpgrades() {
        return EnumSet.noneOf(WorksiteUpgrade.class);
    }// NOOP

    @Override
    public EnumSet<WorksiteUpgrade> getValidUpgrades() {
        return EnumSet.noneOf(WorksiteUpgrade.class);
    }// NOOP

    @Override
    public void addUpgrade(WorksiteUpgrade upgrade) {
    }// NOOP

    @Override
    public void removeUpgrade(WorksiteUpgrade upgrade) {
    }// NOOP

    @Override
    public boolean hasWork() {
        return inputCell.getEnergy() < inputCell.getMaxEnergy();
    }

    @Override
    public void addEnergyFromWorker(IWorker worker) {
        inputCell.setEnergy(inputCell.getEnergy() + AWCoreStatics.energyPerWorkUnit * worker.getWorkEffectiveness(getWorkType()) * AWAutomationStatics.hand_cranked_generator_output);
    }

    @Override
    public void addEnergyFromPlayer(EntityPlayer player) {
        inputCell.setEnergy(inputCell.getEnergy() + AWCoreStatics.energyPerWorkUnit * AWAutomationStatics.hand_cranked_generator_output);
    }

    @Override
    public WorkType getWorkType() {
        return WorkType.CRAFTING;
    }

    @Override
    public Team getTeam() {
        return worldObj.getScoreboard().getPlayersTeam(ownerName);
    }

    @Override
    public void setOwnerName(String name) {
        if (name == null) {
            name = "";
        }
        ownerName = name;
    }

    @Override
    public String getOwnerName() {
        return ownerName;
    }

    @Override
    public NBTTagCompound getDescriptionTag() {
        NBTTagCompound tag = super.getDescriptionTag();
        tag.setInteger("clientInputDestEnergy", clientInputDestEnergy);
        return tag;
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
        NBTTagCompound tag = pkt.func_148857_g();
        clientInputDestEnergy = tag.getInteger("clientInputDestEnergy");
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        if (tag.hasKey("owner")) {
            ownerName = tag.getString("owner");
        }
        inputCell.setEnergy(tag.getDouble("inputEnergy"));
        clientInputDestEnergy = tag.getInteger("clientInputEnergy");
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        if (ownerName != null) {
            tag.setString("owner", ownerName);
        }
        tag.setDouble("inputEnergy", inputCell.getEnergy());
        tag.setInteger("clientInputEnergy", clientInputDestEnergy);
    }

    @Override
    public double getMaxTorque(ForgeDirection from) {
        return inputCell.getMaxEnergy() + torqueCell.getMaxEnergy();
    }

    @Override
    public double getTorqueStored(ForgeDirection from) {
        return inputCell.getEnergy() + torqueCell.getEnergy();
    }

    @Override
    public double addTorque(ForgeDirection from, double energy) {
        if (from == getPrimaryFacing()) {
            return 0;
        } else if (from == ForgeDirection.UP || from == ForgeDirection.UNKNOWN) {
            return inputCell.addEnergy(energy);
        }
        return 0;
    }

    @Override
    public double drainTorque(ForgeDirection from, double energy) {
        if (from == getPrimaryFacing()) {
            return torqueCell.drainEnergy(energy);
        }
        return 0;
    }

    @Override
    public double getMaxTorqueOutput(ForgeDirection from) {
        if (from == getPrimaryFacing()) {
            return torqueCell.getMaxTickOutput();
        }
        return 0;
    }

    @Override
    public double getMaxTorqueInput(ForgeDirection from) {
        return 0;
    }

    @Override
    public boolean canInputTorque(ForgeDirection from) {
        return false;
    }

    @Override
    public float getClientOutputRotation(ForgeDirection from, float delta) {
        if (from == getPrimaryFacing()) {
            return getRotation(rotation, prevRotation, delta);
        } else if (from == ForgeDirection.UP) {
            return getRotation(inputRotation, prevInputRotation, delta);
        }
        return 0;
    }

    @Override
    public boolean useOutputRotation(ForgeDirection from) {
        return true;
    }

    @Override
    protected double getTotalTorque() {
        return inputCell.getEnergy() + torqueCell.getEnergy();
    }

}
