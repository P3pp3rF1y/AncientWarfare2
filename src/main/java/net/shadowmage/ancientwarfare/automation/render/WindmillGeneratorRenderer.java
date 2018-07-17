package net.shadowmage.ancientwarfare.automation.render;

import codechicken.lib.render.CCModel;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Vector3;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileWindmillController;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;

import java.util.Collection;

public class WindmillGeneratorRenderer extends BaseTorqueRenderer<TileWindmillController> {

	public static final ModelResourceLocation MODEL_LOCATION = new ModelResourceLocation(AncientWarfareCore.MOD_ID + ":automation/windmill_generator", "normal");
	public static final WindmillGeneratorRenderer INSTANCE = new WindmillGeneratorRenderer();

	private final Collection<CCModel> outputGear;

	private WindmillGeneratorRenderer() {
		super("automation/windmill_generator.obj");
		outputGear = removeGroups(s -> s.startsWith("base.outputGear."));
	}

	@Override
	protected void transformMovingParts(Collection<CCModel> transformedGroups, EnumFacing frontFacing, float[] rotations, IExtendedBlockState state) {
		float outR = rotations[frontFacing.getIndex()];
		transformedGroups.addAll(rotateModels(outputGear, frontFacing, new Rotation(outR, 0, 0, 1).at(new Vector3(8d / 16d, 8d / 16d, 8d / 16d))));
	}
}