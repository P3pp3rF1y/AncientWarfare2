package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.core.api.ModuleStatus;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueTile;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.SidedTorqueCell;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.TorqueCell;
import net.shadowmage.ancientwarfare.core.util.Trig;

public abstract class TileTorqueSidedCell extends TileTorqueBase {

	boolean connections[] = null;
	final SidedTorqueCell[] storage = new SidedTorqueCell[DIRECTION_LENGTH];

	/*
	 * client side this == 0.0 -> 100.0
	 */ double clientEnergyState;

	/*
	 * server side this == 0 -> 100 (integer percent)
	 * client side this == 0 -> 100 (integer percent)
	 */ int clientDestEnergyState;

	/*
	 * used client side for rendering
	 */ double rotation;
	double lastRotationDiff;

	public TileTorqueSidedCell() {
		double max = getMaxTransfer();
		double eff = getEfficiency();
		for (int i = 0; i < storage.length; i++) {
			storage[i] = new SidedTorqueCell(max, max, max, eff, EnumFacing.values()[i], this);
		}
	}

	protected abstract double getEfficiency();

	protected abstract double getMaxTransfer();

	@Override
	public void update() {
		if (!world.isRemote) {
			serverNetworkUpdate();
			torqueIn = getTotalTorque() - prevEnergy;
			balanceStorage();
			torqueLoss = applyPowerLoss();
			torqueOut = transferPower();
			prevEnergy = getTotalTorque();
		} else {
			clientNetworkUpdate();
			updateRotation();
		}
	}

	protected double applyPowerLoss() {
		double loss = 0;
		for (SidedTorqueCell aStorage : storage) {
			loss += applyPowerDrain(aStorage);
		}
		return loss;
	}

	protected double transferPower() {
		return transferPowerTo(getPrimaryFacing());
	}

	protected void balanceStorage() {
		int face = getPrimaryFacing().ordinal();
		TorqueCell out = storage[face];
		double total = 0;
		TorqueCell in;
		for (int i = 0; i < storage.length; i++) {
			if (i == face) {
				continue;
			}
			total += storage[i].getEnergy();
		}
		if (total > 0) {
			double transfer = Math.min(total, out.getMaxEnergy() - out.getEnergy());
			double percent = transfer / total;
			transfer = 0;
			double fromEach;
			for (int i = 0; i < storage.length; i++) {
				if (i == face) {
					continue;
				}
				in = storage[i];
				fromEach = in.getEnergy() * percent;
				transfer += fromEach;
				in.setEnergy(in.getEnergy() - fromEach);
			}
			out.setEnergy(out.getEnergy() + transfer);
		}
	}

	@Override
	protected void serverNetworkSynch() {
		int percent = (int) (storage[getPrimaryFacing().ordinal()].getPercentFull() * 100.d);
		int percent2 = (int) ((torqueOut / storage[getPrimaryFacing().ordinal()].getMaxOutput()) * 100.d);
		percent = Math.max(percent, percent2);
		if (percent != clientDestEnergyState) {
			clientDestEnergyState = percent;
			sendSideRotation(getPrimaryFacing(), percent);
		}
	}

	@Override
	protected void updateRotation() {
		if (clientEnergyState > 0) {
			lastRotationDiff = -(AWAutomationStatics.low_rpt * clientEnergyState * 0.01d) * Trig.TORADIANS;
			rotation += lastRotationDiff;
			rotation %= Trig.PI * 2;
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
		clientDestEnergyState = value;
		this.networkUpdateTicks = AWAutomationStatics.energyMinNetworkUpdateFrequency;
	}

	@Override
	public boolean canInputTorque(EnumFacing from) {
		return from != orientation;
	}

	@Override
	public boolean canOutputTorque(EnumFacing towards) {
		return towards == orientation;
	}

	public boolean[] getConnections() {
		if (connections == null) {
			buildConnections();
		}
		return connections;
	}

	@Override
	public void onNeighborTileChanged() {
		super.onNeighborTileChanged();
		connections = null;
	}

	protected void buildConnections() {
		boolean[] connections = new boolean[DIRECTION_LENGTH];
		ITorqueTile[] cache = getTorqueCache();
		EnumFacing dir;
		for (int i = 0; i < cache.length; i++) {
			dir = EnumFacing.values()[i];
			if (cache[i] != null) {
				connections[i] = (cache[i].canInputTorque(dir.getOpposite()) && canOutputTorque(dir)) || (cache[i]
						.canOutputTorque(dir.getOpposite()) && canInputTorque(dir));
			}
		}
		if (ModuleStatus.redstoneFluxEnabled) {
			TileEntity[] tes = getRFCache();
			for (int i = 0; i < tes.length; i++) {
				if (cache[i] != null) {
					continue;
				}//already examined that side..
				if (tes[i] != null) {
					connections[i] = true;
				}
			}
		}
		this.connections = connections;
	}

	@Override
	public double getMaxTorque(EnumFacing from) {
		return storage[from.ordinal()].getMaxEnergy();
	}

	@Override
	public double getTorqueStored(EnumFacing from) {
		if (from == null) {
			return 0D; // some mods pass null into RF compat so let's return 0 in that case
		}
		return storage[from.ordinal()].getEnergy();
	}

	@Override
	public double addTorque(EnumFacing from, double energy) {
		return storage[from.ordinal()].addEnergy(energy);
	}

	@Override
	public double drainTorque(EnumFacing from, double energy) {
		return storage[from.ordinal()].drainEnergy(energy);
	}

	@Override
	public double getMaxTorqueOutput(EnumFacing from) {
		return storage[from.ordinal()].getMaxTickOutput();
	}

	@Override
	public double getMaxTorqueInput(EnumFacing from) {
		return storage[from.ordinal()].getMaxTickInput();
	}

	@Override
	public boolean useOutputRotation(EnumFacing from) {
		return true;
	}

	@Override
	public float getClientOutputRotation(EnumFacing from, float delta) {
		return getRenderRotation(rotation, lastRotationDiff, delta);
	}

	@Override
	protected void writeUpdateNBT(NBTTagCompound tag) {
		super.writeUpdateNBT(tag);
		tag.setInteger("clientEnergy", clientDestEnergyState);
	}

	@Override
	protected void handleUpdateNBT(NBTTagCompound tag) {
		super.handleUpdateNBT(tag);
		clientDestEnergyState = tag.getInteger("clientEnergy");
		connections = null; //clear connections on update
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		NBTTagList list = tag.getTagList("energyList", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < storage.length; i++) {
			storage[i].readFromNBT(list.getCompoundTagAt(i));
		}
		clientDestEnergyState = tag.getInteger("clientEnergy");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		NBTTagList list = new NBTTagList();
		for (SidedTorqueCell aStorage : storage) {
			list.appendTag(aStorage.writeToNBT(new NBTTagCompound()));
		}
		tag.setTag("energyList", list);
		tag.setInteger("clientEnergy", clientDestEnergyState);

		return tag;
	}

	@Override
	protected double getTotalTorque() {
		double d = 0;
		EnumFacing dir;
		for (int i = 0; i < storage.length; i++) {
			dir = EnumFacing.values()[i];
			if (canInputTorque(dir) || canOutputTorque(dir)) {
				d += storage[i].getEnergy();
			}
		}
		return d;
	}

}
