package net.shadowmage.ancientwarfare.automation.render;

import codechicken.lib.render.CCModel;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Transformation;
import codechicken.lib.vec.Translation;
import codechicken.lib.vec.Vector3;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.shadowmage.ancientwarfare.automation.block.BlockTorqueTransportShaft;
import net.shadowmage.ancientwarfare.automation.render.property.AutomationProperties;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueShaft;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.render.property.CoreProperties;

import javax.annotation.Nullable;
import java.util.Collection;

public class TorqueShaftRenderer extends TorqueTieredRenderer<TileTorqueShaft> {

	public static final ModelResourceLocation LIGHT_MODEL_LOCATION = new ModelResourceLocation(AncientWarfareCore.MOD_ID + ":automation/torque_shaft", "light");
	public static final ModelResourceLocation MEDIUM_MODEL_LOCATION = new ModelResourceLocation(AncientWarfareCore.MOD_ID + ":automation/torque_shaft", "medium");
	public static final ModelResourceLocation HEAVY_MODEL_LOCATION = new ModelResourceLocation(AncientWarfareCore.MOD_ID + ":automation/torque_shaft", "heavy");
	public static final TorqueShaftRenderer INSTANCE = new TorqueShaftRenderer();

	private Collection<CCModel> inputHead;
	private Collection<CCModel> outputHead;
	private Collection<CCModel> shaft;
	private Collection<CCModel> gearbox;

	private TorqueShaftRenderer() {
		super("automation/torque_shaft.obj");
		inputHead = removeGroups(s -> s.startsWith("southShaft."));
		outputHead = removeGroups(s -> s.startsWith("northShaft."));
		shaft = removeGroups(s -> s.startsWith("shaft."));
		gearbox = removeGroups(s -> s.startsWith("gearBox."));
	}

	@Override
	protected Transformation getBaseTransformation() {
		return new Translation(0d, 0.5d, 0d);
	}

	@Override
	protected Collection<CCModel> applyModelTransforms(Collection<CCModel> modelGroups, EnumFacing face, IExtendedBlockState state) {
		modelGroups = super.applyModelTransforms(modelGroups, face, state);
		if (!state.getValue(AutomationProperties.DYNAMIC) && !state.getValue(BlockTorqueTransportShaft.HAS_PREVIOUS)) {
			modelGroups.addAll(rotateFacing(gearbox, state.getValue(CoreProperties.UNLISTED_FACING)));
		}

		return modelGroups;
	}

	@Override
	protected void transformMovingParts(Collection<CCModel> transformedGroups, EnumFacing frontFacing, float[] rotations, @Nullable IExtendedBlockState state) {
		float rotation = rotations[frontFacing.ordinal()];

		transformedGroups.addAll(rotateModels(shaft, frontFacing, new Rotation(rotation, 0, 0, 1).at(new Vector3(8d / 16d, 8d / 16d, 8d / 16d))));
		boolean hasPrevious = state != null && state.getValue(BlockTorqueTransportShaft.HAS_PREVIOUS);
		boolean useInput = state != null && state.getValue(AutomationProperties.USE_INPUT);
		boolean hasNext = state != null && state.getValue(BlockTorqueTransportShaft.HAS_NEXT);
		if (!hasNext) {
			transformedGroups.addAll(rotateModels(outputHead, frontFacing, new Rotation(rotation, 0, 0, 1).at(new Vector3(8d / 16d, 8d / 16d, 8d / 16d))));
		}

		if (!hasPrevious) {
			if (useInput) {
				rotation = state.getValue(AutomationProperties.INPUT_ROTATION);
			}
			transformedGroups.addAll(rotateModels(inputHead, frontFacing, new Rotation(rotation, 0, 0, 1).at(new Vector3(8d / 16d, 8d / 16d, 8d / 16d))));
		}
	}

	@Override
	protected IExtendedBlockState handleAdditionalProperties(IExtendedBlockState state, TileTorqueShaft tileEntity) {
		state = super.handleAdditionalProperties(state, tileEntity);
		TileTorqueShaft prev = tileEntity.prev();
		state = state.withProperty(BlockTorqueTransportShaft.HAS_NEXT, false);
		state = state.withProperty(BlockTorqueTransportShaft.HAS_PREVIOUS, prev != null);
		state = state.withProperty(AutomationProperties.USE_INPUT, false);
		state = state.withProperty(AutomationProperties.INPUT_ROTATION, 0f);
		return state;
	}
}
