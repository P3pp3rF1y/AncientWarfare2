package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.automation.tile.torque.multiblock.TileWindmillBlade;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.TorqueCell;
import net.shadowmage.ancientwarfare.core.util.WorldTools;

import javax.annotation.Nullable;

public class TileWindmillController extends TileTorqueSingleCell {

	public TileWindmillController() {
		double max = AWAutomationStatics.low_transfer_max;
		torqueCell = new TorqueCell(max, max, max, AWAutomationStatics.low_efficiency_factor);
	}

	@Override
	public void update() {
		super.update();
		if (!world.isRemote) {
			TileWindmillBlade blade = getControlledBlade();
			if (blade != null && blade.getEnergy() > 0) {
				double d = blade.getEnergy();
				blade.setEnergy(0);
				torqueCell.setEnergy(torqueCell.getEnergy() + d);
			}
		}
	}

	private TileWindmillBlade getControlledBlade() {
		BlockPos behind = pos.offset(getPrimaryFacing().getOpposite());
		if (world.isBlockLoaded(behind)) {
			return WorldTools.getTile(world, behind, TileWindmillBlade.class).orElse(null);
		}
		return null;
	}

	@Override
	public boolean canInputTorque(@Nullable EnumFacing from) {
		return false;
	}

}
