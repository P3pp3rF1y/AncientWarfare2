package net.shadowmage.ancientwarfare.automation.render;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.shadowmage.ancientwarfare.automation.block.BlockTorqueTransportSided;
import net.shadowmage.ancientwarfare.automation.render.property.AutomationProperties;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueSidedCell;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque;

public class TorqueTransportAnimationRenderer extends TorqueAnimationRenderer<TileTorqueSidedCell> {
	public TorqueTransportAnimationRenderer(AnimatedBlockRenderer bakery) {
		super(bakery);
	}

	@Override
	protected IExtendedBlockState handleState(TileTorqueSidedCell transportCell, float partialTicks, IExtendedBlockState state) {
		state = super.handleState(transportCell, partialTicks, state);

		ITorque.ITorqueTile[] neighbors = transportCell.getTorqueCache();
		boolean[] connections = transportCell.getConnections();
		for (EnumFacing facing : EnumFacing.VALUES) {
			state = state.withProperty(BlockTorqueTransportSided.CONNECTIONS[facing.ordinal()], connections[facing.ordinal()]);

			if (connections[facing.ordinal()]) {
				if (!transportCell.canOutputTorque(facing) && neighbors[facing.ordinal()] != null && neighbors[facing.ordinal()].useOutputRotation(null)) {
					float r = -neighbors[facing.ordinal()].getClientOutputRotation(facing.getOpposite(), partialTicks);

					state = state.withProperty(AutomationProperties.ROTATIONS[facing.ordinal()], r);
				}
			}
		}

		return state;
	}
}
