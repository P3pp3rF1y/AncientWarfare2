package net.shadowmage.ancientwarfare.automation.render;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.shadowmage.ancientwarfare.automation.block.BlockTorqueTransportJunction;
import net.shadowmage.ancientwarfare.automation.render.property.AutomationProperties;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueSidedCell;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque;

public class TorqueJunctionAnimationRenderer extends TorqueAnimationRenderer<TileTorqueSidedCell> {
	@Override
	protected IExtendedBlockState handleState(TileTorqueSidedCell junction, float partialTicks, IExtendedBlockState state) {
		state = super.handleState(junction, partialTicks, state);

		ITorque.ITorqueTile[] neighbors = junction.getTorqueCache();
		boolean[] connections = junction.getConnections();
		for(EnumFacing facing: EnumFacing.VALUES) {
			state = state.withProperty(BlockTorqueTransportJunction.CONNECTIONS[facing.ordinal()], connections[facing.ordinal()]);

			if (connections[facing.ordinal()]) {
				if (!junction.canOutputTorque(facing) && neighbors[facing.ordinal()] != null && neighbors[facing.ordinal()].useOutputRotation(null)) {
					float r = neighbors[facing.ordinal()].getClientOutputRotation(facing.getOpposite(), partialTicks);

					int direction = (facing.ordinal() % 2 == 0) ? -1 : 1;//evens rotate in the other direction
					state = state.withProperty(AutomationProperties.ROTATIONS[facing.ordinal()], direction * r);
				}
			}
		}

		return state;
	}

	@Override
	protected int getModelHashCode(IExtendedBlockState exState) {
		int result = super.getModelHashCode(exState);

		for(IUnlistedProperty property : BlockTorqueTransportJunction.CONNECTIONS) {
			result = 31 * result + exState.getValue(property).hashCode();
		}

		return result;
	}
}
