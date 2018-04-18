package net.shadowmage.ancientwarfare.automation.render;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.shadowmage.ancientwarfare.automation.block.BlockFlywheelController;
import net.shadowmage.ancientwarfare.automation.render.property.AutomationProperties;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileFlywheelController;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque;

public class FlywheelControllerAnimationRenderer extends TorqueAnimationRenderer<TileFlywheelController> {
	public FlywheelControllerAnimationRenderer() {
		super(FlywheelControllerRenderer.INSTANCE);
	}

	@Override
	protected IExtendedBlockState handleState(TileFlywheelController te, float partialTicks, IExtendedBlockState state) {
		state = super.handleState(te, partialTicks, state);

		state = state.withProperty(BlockFlywheelController.FLYWHEEL_ROTATION, te.getFlywheelRotation(partialTicks));
		EnumFacing d = te.getPrimaryFacing();
		ITorque.ITorqueTile inputNeighbor = te.getTorqueCache()[d.getOpposite().ordinal()];
		state = state.withProperty(AutomationProperties.USE_INPUT, inputNeighbor != null && inputNeighbor.canOutputTorque(d) && inputNeighbor.useOutputRotation(d.getOpposite()));
		state = state.withProperty(AutomationProperties.INPUT_ROTATION, inputNeighbor != null ? inputNeighbor.getClientOutputRotation(d, partialTicks) : 0);

		return state;
	}
}
