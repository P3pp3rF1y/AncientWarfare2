package net.shadowmage.ancientwarfare.automation.render;

import codechicken.lib.render.CCModel;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Vector3;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.shadowmage.ancientwarfare.automation.block.BlockWaterwheelGenerator;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileWaterwheelGenerator;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;

import javax.annotation.Nullable;
import java.util.Collection;

public class WaterwheelGeneratorRenderer extends BaseTorqueRenderer<TileWaterwheelGenerator> {

	public static final ModelResourceLocation MODEL_LOCATION = new ModelResourceLocation(AncientWarfareCore.MOD_ID + ":automation/waterwheel_generator", "normal");
	public static final WaterwheelGeneratorRenderer INSTANCE = new WaterwheelGeneratorRenderer();

	private final Collection<CCModel> waterwheel;
	private final Collection<CCModel> outputGear;

	private WaterwheelGeneratorRenderer() {
		super("automation/waterwheel_generator.obj");

		waterwheel = removeGroups(s -> s.startsWith("waterwheelBase.waterwheelSpindle."));
		outputGear = removeGroups(s -> s.startsWith("base.outputGear."));
	}

	@Override
	protected void transformMovingParts(Collection<CCModel> transformedGroups, EnumFacing frontFacing, float[] rotations, @Nullable IExtendedBlockState state) {
		float wheelR = rotations[frontFacing.getOpposite().getIndex()];
		float outR = rotations[frontFacing.getIndex()];
		transformedGroups.addAll(rotateModels(outputGear, frontFacing, new Rotation(outR, 0, 0, 1).at(new Vector3(8d / 16d, 8d / 16d, 8d / 16d))));
		if (state != null && state.getValue(BlockWaterwheelGenerator.VALID_SETUP)) {
			transformedGroups.addAll(rotateModels(waterwheel, frontFacing, new Rotation(wheelR, 0, 0, 1).at(new Vector3(8d / 16d, 8d / 16d, 16d / 16d))));
		}
	}
}