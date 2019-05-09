package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.TorqueCell;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite;
import net.shadowmage.ancientwarfare.core.interfaces.IWorker;
import net.shadowmage.ancientwarfare.core.owner.IOwnable;
import net.shadowmage.ancientwarfare.core.owner.Owner;
import net.shadowmage.ancientwarfare.core.upgrade.WorksiteUpgrade;
import net.shadowmage.ancientwarfare.core.util.Trig;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Set;

public class TileHandCrankedGenerator extends TileTorqueSingleCell implements IWorkSite, IOwnable {

	private Owner owner = Owner.EMPTY;
	private final TorqueCell inputCell;

	/*
	 * client side this == 0.0 -> 100.0 (integer percent)
	 */
	private double clientInputEnergy;

	/*
	 * client side this == 0 -> 100.0 (integer percent)
	 */
	private int clientInputDestEnergy;

	/*
	 * used client side for rendering
	 */
	private double inputRotation;
	private double lastInputRotationDiff;

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

	private void balancePower() {
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
	@SideOnly(Side.CLIENT)
	protected void clientNetworkUpdate() {
		if (!MathHelper.epsilonEquals((float) clientEnergyState, clientDestEnergyState) || !MathHelper
				.epsilonEquals((float) clientInputEnergy, clientInputDestEnergy)) {
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
	public void onBlockBroken(IBlockState state) {
		//NOOP
	}

	@Override
	public Set<WorksiteUpgrade> getUpgrades() {
		return EnumSet.noneOf(WorksiteUpgrade.class);
	}

	@Override
	public Set<WorksiteUpgrade> getValidUpgrades() {
		return EnumSet.noneOf(WorksiteUpgrade.class);
	}

	@Override
	public void addUpgrade(WorksiteUpgrade upgrade) {
		// NOOP
	}

	@Override
	public void removeUpgrade(WorksiteUpgrade upgrade) {
		// NOOP
	}

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
	@Nullable
	public Team getTeam() {
		return world.getScoreboard().getPlayersTeam(owner.getName());
	}

	@Override
	public boolean isOwner(EntityPlayer player) {
		return owner.isOwnerOrSameTeamOrFriend(player);
	}

	@Override
	public void setOwner(EntityPlayer player) {
		owner = new Owner(player);
	}

	@Override
	public void setOwner(Owner owner) {
		this.owner = owner;
	}

	@Override
	public Owner getOwner() {
		return owner;
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
		owner = Owner.deserializeFromNBT(tag);
		inputCell.setEnergy(tag.getDouble("inputEnergy"));
		clientInputDestEnergy = tag.getInteger("clientInputEnergy");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		owner.serializeToNBT(tag);
		tag.setDouble("inputEnergy", inputCell.getEnergy());
		tag.setInteger("clientInputEnergy", clientInputDestEnergy);
		return tag;
	}

	@Override
	public double getMaxTorque(@Nullable EnumFacing from) {
		return inputCell.getMaxEnergy() + torqueCell.getMaxEnergy();
	}

	@Override
	public double getTorqueStored(@Nullable EnumFacing from) {
		return inputCell.getEnergy() + torqueCell.getEnergy();
	}

	@Override
	public double addTorque(@Nullable EnumFacing from, double energy) {
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
	public double getMaxTorqueInput(@Nullable EnumFacing from) {
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
	public boolean useOutputRotation(@Nullable EnumFacing from) {
		return true;
	}

	@Override
	protected double getTotalTorque() {
		return inputCell.getEnergy() + torqueCell.getEnergy();
	}

}
