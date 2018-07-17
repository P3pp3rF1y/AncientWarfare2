package net.shadowmage.ancientwarfare.automation.render;

import codechicken.lib.render.CCModel;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Vector3;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.shadowmage.ancientwarfare.automation.block.BlockFlywheelController;
import net.shadowmage.ancientwarfare.automation.render.property.AutomationProperties;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileFlywheelController;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;

import javax.annotation.Nullable;
import java.util.Collection;

public class FlywheelControllerRenderer extends TorqueTieredRenderer<TileFlywheelController> {
	public static final ModelResourceLocation LIGHT_MODEL_LOCATION = new ModelResourceLocation(AncientWarfareCore.MOD_ID + ":automation/flywheel_controller", "light");
	public static final ModelResourceLocation MEDIUM_MODEL_LOCATION = new ModelResourceLocation(AncientWarfareCore.MOD_ID + ":automation/flywheel_controller", "medium");
	public static final ModelResourceLocation HEAVY_MODEL_LOCATION = new ModelResourceLocation(AncientWarfareCore.MOD_ID + ":automation/flywheel_controller", "heavy");

	public static final FlywheelControllerRenderer INSTANCE = new FlywheelControllerRenderer();

	private Collection<CCModel> controlInput;
	private Collection<CCModel> controlOutput;
	private Collection<CCModel> controlSpindle;

	private FlywheelControllerRenderer() {
		super("automation/flywheel_controller.obj");
		controlInput = removeGroups(s -> s.startsWith("inputGear"));
		controlOutput = removeGroups(s -> s.startsWith("outputGear"));
		controlSpindle = removeGroups(s -> s.startsWith("spindle"));
	}

	@Override
	protected void transformMovingParts(Collection<CCModel> transformedGroups, EnumFacing frontFacing, float[] rotations, @Nullable IExtendedBlockState state) {
		float outputRotation = 0;
		float inputRotation = 0;
		float flywheelRotation = 0;

		if (state != null) {
			outputRotation = state.getValue(AutomationProperties.ROTATIONS[frontFacing.ordinal()]);
			inputRotation = outputRotation;
			flywheelRotation = state.getValue(BlockFlywheelController.FLYWHEEL_ROTATION);

			if (state.getValue(AutomationProperties.USE_INPUT)) {
				inputRotation = state.getValue(AutomationProperties.INPUT_ROTATION);
			}
		}

		transformedGroups.addAll(rotateModels(controlInput, frontFacing, new Rotation(inputRotation, 0, 0, 1).at(Vector3.center)));
		transformedGroups.addAll(rotateModels(controlOutput, frontFacing, new Rotation(outputRotation, 0, 0, 1).at(Vector3.center)));
		transformedGroups.addAll(rotateModels(controlSpindle, frontFacing, new Rotation(flywheelRotation, 0, 1, 0).at(Vector3.center)));
	}

	@Override
	protected IExtendedBlockState handleAdditionalProperties(IExtendedBlockState state, TileFlywheelController tileEntity) {
		state = super.handleAdditionalProperties(state, tileEntity);
		state = state.withProperty(BlockFlywheelController.FLYWHEEL_ROTATION, 0f);
		state = state.withProperty(AutomationProperties.USE_INPUT, false);
		state = state.withProperty(AutomationProperties.INPUT_ROTATION, 0f);
		return state;
	}
}
