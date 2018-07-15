package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.shadowmage.ancientwarfare.automation.tile.torque.multiblock.TileFlywheelStorage;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.TorqueCell;
import net.shadowmage.ancientwarfare.core.util.WorldTools;

import javax.annotation.Nullable;

public abstract class TileFlywheelController extends TileTorqueSingleCell {
	private static final String POWERED_TAG = "powered";
	private boolean powered;

	private final TorqueCell inputCell;

	public TileFlywheelController() {
		double max = getMaxTransfer();
		double eff = getEfficiency();
		inputCell = new TorqueCell(max, max, max, eff);
		torqueCell = new TorqueCell(max, max, max, eff);
	}

	protected abstract double getEfficiency();

	protected abstract double getMaxTransfer();

	@Override
	public void update() {
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

	private double applyDrainToStorage() {
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
	private void balancePower() {
		TileFlywheelStorage storage = getControlledFlywheel();
		double in = inputCell.getEnergy();
		double out = torqueCell.getEnergy();
		double outputGap = torqueCell.getMaxEnergy() - out;
		double addOutput = Math.min(in, outputGap);
		in -= addOutput;
		out += addOutput;
		if (storage != null) {
			double store = storage.storedEnergy;
			double storeToTransfer = Math.min(store, torqueCell.getMaxEnergy() - out);
			store -= storeToTransfer;
			out += storeToTransfer;

			double addToStore = Math.min(in, storage.maxEnergyStored - store);
			in -= addToStore;
			store += addToStore;
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
	private TileFlywheelStorage getControlledFlywheel() {
		BlockPos controllerPos = pos.offset(EnumFacing.DOWN);
		return WorldTools.getTile(world, controllerPos, TileFlywheelStorage.class).map(t -> {
					if (t.controllerPos != null) {
						BlockPos nextControllerPos = t.controllerPos;
						return WorldTools.getTile(world, nextControllerPos, TileFlywheelStorage.class).orElse(null);
					}
					return null;
				}
		).orElse(null);
	}

	public float getFlywheelRotation(float delta) {
		TileFlywheelStorage storage = getControlledFlywheel();
		return storage == null ? 0 : getRenderRotation(storage.rotation, storage.lastRotationDiff, delta);
	}

	private double getFlywheelEnergy() {
		TileFlywheelStorage storage = getControlledFlywheel();
		return storage == null ? 0 : storage.storedEnergy;
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
		if (world.isRemote && a == 7) {
			powered = b == 1;
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
	public double getMaxTorque(@Nullable EnumFacing from) {
		TorqueCell cell = getCell(from);
		return cell == null ? 0 : cell.getMaxEnergy();
	}

	@Override
	public double getTorqueStored(@Nullable EnumFacing from) {
		TorqueCell cell = getCell(from);
		return cell == null ? 0 : cell.getEnergy();
	}

	@Override
	public double addTorque(@Nullable EnumFacing from, double energy) {
		TorqueCell cell = getCell(from);
		return cell == null ? 0 : cell.addEnergy(energy);
	}

	@Override
	public double drainTorque(@Nullable EnumFacing from, double energy) {
		TorqueCell cell = getCell(from);
		return cell == null ? 0 : cell.drainEnergy(energy);
	}

	@Override
	public double getMaxTorqueInput(@Nullable EnumFacing from) {
		TorqueCell cell = getCell(from);
		return cell == null ? 0 : cell.getMaxTickInput();
	}

	@Nullable
	private TorqueCell getCell(@Nullable EnumFacing from) {
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
		powered = tag.getBoolean(POWERED_TAG);
	}

	@Override
	protected void writeUpdateNBT(NBTTagCompound tag) {
		super.writeUpdateNBT(tag);
		tag.setBoolean(POWERED_TAG, powered);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setBoolean(POWERED_TAG, powered);
		tag.setDouble("torqueEnergyIn", inputCell.getEnergy());
		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		powered = tag.getBoolean(POWERED_TAG);
		inputCell.setEnergy(tag.getDouble("torqueEnergyIn"));
	}

}
