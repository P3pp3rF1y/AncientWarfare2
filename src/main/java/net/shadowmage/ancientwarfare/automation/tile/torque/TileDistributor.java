package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.minecraft.util.EnumFacing;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.TorqueCell;

public abstract class TileDistributor extends TileTorqueSidedCell {

	public TileDistributor() {

	}

	@Override
	protected double transferPower() {
		int in = getPrimaryFacing().getOpposite().ordinal();
		double out = 0;
		for (int i = 0; i < DIRECTION_LENGTH; i++) {
			if (i == in) {
				continue;
			}
			out += transferPowerTo(EnumFacing.values()[i]);
		}
		return out;
	}

	@Override
	protected void balanceStorage() {
		int in = getPrimaryFacing().getOpposite().ordinal();
		TorqueCell input = storage[in];
		double totalRequested = 0;

		TorqueCell out;
		for (int i = 0; i < storage.length; i++) {
			if (i == in) {
				continue;
			}
			out = storage[i];
			if (!getConnections()[i]) {
				continue;
			}
			totalRequested += out.getMaxEnergy() - out.getEnergy();
		}
		if (totalRequested > 0 && input.getEnergy() > 0) {
			double transfer = Math.min(totalRequested, input.getEnergy());
			double percent = transfer / totalRequested;
			double request, trans;
			double transferred = 0;
			for (int i = 0; i < storage.length; i++) {
				if (i == in) {
					continue;
				}
				if (!getConnections()[i]) {
					continue;
				}
				out = storage[i];
				request = out.getMaxEnergy() - out.getEnergy();
				trans = request * percent;
				transferred += trans;
				out.setEnergy(out.getEnergy() + trans);
			}
			input.setEnergy(input.getEnergy() - transferred);
		}
	}

	@Override
	public boolean canInputTorque(EnumFacing from) {
		return from == orientation.getOpposite();
	}

	@Override
	public boolean canOutputTorque(EnumFacing from) {
		return from != orientation.getOpposite();
	}

}
