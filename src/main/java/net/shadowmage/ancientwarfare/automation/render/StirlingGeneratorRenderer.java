package net.shadowmage.ancientwarfare.automation.render;

import codechicken.lib.render.CCModel;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Vector3;
import com.google.common.collect.Maps;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.render.BaseBakery;
import net.shadowmage.ancientwarfare.core.render.BlockRenderProperties;

import java.util.Map;

public class StirlingGeneratorRenderer extends BaseBakery {

	public static final ModelResourceLocation MODEL_LOCATION = new ModelResourceLocation(AncientWarfareCore.modID + ":automation/stirling_generator", "normal");
	public static final StirlingGeneratorRenderer INSTANCE = new StirlingGeneratorRenderer();

	private StirlingGeneratorRenderer() {
		super("automation/stirling_generator.obj");
	}

	@Override
	protected Map<String, CCModel> applyModelTransforms(Map<String, CCModel> modelGroups, EnumFacing face, IExtendedBlockState state) {
		Map<String, CCModel> transformedGroups = Maps.newHashMap();

		for(Map.Entry<String, CCModel> group : modelGroups.entrySet()) {
			transformedGroups.put(group.getKey(), group.getValue().copy().apply(Rotation.quarterRotations[(state.getValue(BlockRenderProperties.UNLISTED_FACING).getHorizontalIndex() + 2) & 3].at(Vector3.center)));
		}

		return transformedGroups;
	}
}