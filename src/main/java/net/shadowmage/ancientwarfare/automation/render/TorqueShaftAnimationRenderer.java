package net.shadowmage.ancientwarfare.automation.render;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.shadowmage.ancientwarfare.automation.block.BlockTorqueTransportShaft;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueShaft;
import net.shadowmage.ancientwarfare.core.render.property.CoreProperties;

public class TorqueShaftAnimationRenderer extends TorqueAnimationRenderer<TileTorqueShaft> {
    @Override
    protected IExtendedBlockState handleState(TileTorqueShaft shaft, float partialTicks, IExtendedBlockState state) {
        state = super.handleState(shaft, partialTicks, state);
        TileTorqueShaft prev = shaft.prev();
        state = state.withProperty(BlockTorqueTransportShaft.HAS_NEXT, shaft.next() != null);
        state = state.withProperty(BlockTorqueTransportShaft.HAS_PREVIOUS, prev != null);

        EnumFacing facing = state.getValue(CoreProperties.UNLISTED_FACING);

        state = state.withProperty(BlockTorqueTransportShaft.USE_INPUT, prev != null && prev.canOutputTorque(facing.getOpposite()) && prev.useOutputRotation(null));
        state = state.withProperty(BlockTorqueTransportShaft.INPUT_ROTATION, prev != null ? prev.getClientOutputRotation(facing.getOpposite(), 1) : 0);

        return state;
    }

    @Override
    protected int getModelHashCode(IExtendedBlockState exState) {
        return 31 * super.getModelHashCode(exState) + Float.floatToIntBits(exState.getValue(BlockTorqueTransportShaft.INPUT_ROTATION));
    }
}
