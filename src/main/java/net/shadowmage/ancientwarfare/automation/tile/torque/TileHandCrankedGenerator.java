package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.EnumFacing;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;
import net.shadowmage.ancientwarfare.core.interfaces.IOwnable;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.TorqueCell;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite;
import net.shadowmage.ancientwarfare.core.interfaces.IWorker;
import net.shadowmage.ancientwarfare.core.upgrade.WorksiteUpgrade;
import net.shadowmage.ancientwarfare.core.util.EntityTools;
import net.shadowmage.ancientwarfare.core.util.Trig;

import java.util.EnumSet;
import java.util.UUID;

public class TileHandCrankedGenerator extends TileTorqueSingleCell implements IWorkSite, IOwnable {

	String ownerName = "";
	UUID ownerId = null;
	private final TorqueCell inputCell;

	/*
	 * client side this == 0.0 -> 100.0 (integer percent)
	 */ double clientInputEnergy;

	/*
	 * client side this == 0 -> 100.0 (integer percent)
	 */ int clientInputDestEnergy;

	/*
	 * used client side for rendering
	 */ double inputRotation;
	double lastInputRotationDiff;

	public TileHandCrankedGenerator() {
		double eff = AWAutomationStatics.low_efficiency_factor;
		torqueCell = new TorqueCell(0, 32, 32, eff);
		inputCell = new TorqueCell(32, 0, 150, eff);
	}

	@Override
	public void update() {
		if (!world.isRemote) {
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
			sendSideRotation(EnumFacing.UP, percent);
		}
	}

	@Override
	protected void updateRotation() {
		super.updateRotation();
		if (clientInputEnergy > 0) {
			lastInputRotationDiff = -(AWAutomationStatics.low_rpt * clientInputEnergy * 0.01d) * Trig.TORADIANS;
			inputRotation += lastInputRotationDiff;
			inputRotation %= Trig.PI * (float) 2;
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
	protected void handleClientRotationData(EnumFacing side, int value) {
		super.handleClientRotationData(side, value);
		if (side == EnumFacing.UP) {
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
		inputCell.setEnergy(inputCell.getEnergy() + AWCoreStatics.energyPerWorkUnit * worker
				.getWorkEffectiveness(getWorkType()) * AWAutomationStatics.hand_cranked_generator_output);
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
		return world.getScoreboard().getPlayersTeam(ownerName);
	}

	@Override
	public boolean isOwner(EntityPlayer player) {
		return EntityTools.isOwnerOrSameTeam(player, ownerId, ownerName);
	}

	@Override
	public void setOwner(EntityPlayer player) {
		this.ownerName = player.getName();
		this.ownerId = player.getUniqueID();
	}

	@Override
	public void setOwner(String ownerName, UUID ownerUuid) {
		this.ownerName = ownerName;
		this.ownerId = ownerUuid;
	}

	@Override
	public String getOwnerName() {
		return ownerName;
	}

	@Override
	public UUID getOwnerUuid() {
		return ownerId;
	}

	@Override
	protected void handleUpdateNBT(NBTTagCompound tag) {
		super.handleUpdateNBT(tag);
		clientInputDestEnergy = tag.getInteger("clientInputDestEnergy");
	}

	@Override
	protected void writeUpdateNBT(NBTTagCompound tag) {
		super.writeUpdateNBT(tag);
		tag.setInteger("clientInputDestEnergy", clientInputDestEnergy);
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
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		if (ownerName != null) {
			tag.setString("owner", ownerName);
		}
		tag.setDouble("inputEnergy", inputCell.getEnergy());
		tag.setInteger("clientInputEnergy", clientInputDestEnergy);
		return tag;
	}

	@Override
	public double getMaxTorque(EnumFacing from) {
		return inputCell.getMaxEnergy() + torqueCell.getMaxEnergy();
	}

	@Override
	public double getTorqueStored(EnumFacing from) {
		return inputCell.getEnergy() + torqueCell.getEnergy();
	}

	@Override
	public double addTorque(EnumFacing from, double energy) {
		if (from == getPrimaryFacing()) {
			return 0;
		} else if (from == EnumFacing.UP || from == null) {
			return inputCell.addEnergy(energy);
		}
		return 0;
	}

	@Override
	public double drainTorque(EnumFacing from, double energy) {
		if (from == getPrimaryFacing()) {
			return torqueCell.drainEnergy(energy);
		}
		return 0;
	}

	@Override
	public double getMaxTorqueOutput(EnumFacing from) {
		if (from == getPrimaryFacing()) {
			return torqueCell.getMaxTickOutput();
		}
		return 0;
	}

	@Override
	public double getMaxTorqueInput(EnumFacing from) {
		return 0;
	}

	@Override
	public boolean canInputTorque(EnumFacing from) {
		return false;
	}

	@Override
	public float getClientOutputRotation(EnumFacing from, float delta) {
		float ret = 0;
		if (from == getPrimaryFacing()) {
			ret = getRenderRotation(rotation, lastRotationDiff, delta);
		} else if (from == EnumFacing.UP) {
			ret = getRenderRotation(inputRotation, lastInputRotationDiff, delta);
		}
		return ret;
	}

	@Override
	public boolean useOutputRotation(EnumFacing from) {
		return true;
	}

	@Override
	protected double getTotalTorque() {
		return inputCell.getEnergy() + torqueCell.getEnergy();
	}

}
