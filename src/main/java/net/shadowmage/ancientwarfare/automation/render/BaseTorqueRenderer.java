package net.shadowmage.ancientwarfare.automation.render;

import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Transformation;
import codechicken.lib.vec.Vector3;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.shadowmage.ancientwarfare.automation.render.property.AutomationProperties;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueBase;
import net.shadowmage.ancientwarfare.core.render.BaseBakery;
import net.shadowmage.ancientwarfare.core.render.property.CoreProperties;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class BaseTorqueRenderer extends BaseBakery {
	protected BaseTorqueRenderer(String modelPath) {
		super(modelPath);
	}

	protected Map<String, CCModel> removeGroups(Function<String, Boolean> filter) {
		Map<String, CCModel> ret = Maps.newHashMap();

		Iterator<Map.Entry<String, CCModel>> iterator = groups.entrySet().iterator();
		while(iterator.hasNext()) {
			Map.Entry<String, CCModel> entry = iterator.next();

			if(filter.apply(entry.getKey())) {
				ret.put(entry.getKey(), entry.getValue());
				iterator.remove();
			}
		}

		return ret;
	}

	@Override
	protected Map<String, CCModel> applyModelTransforms(Map<String, CCModel> modelGroups, EnumFacing face, IExtendedBlockState state) {
		Map<String, CCModel> transformedGroups = Maps.newHashMap();

		EnumFacing frontFacing = state.getValue(CoreProperties.UNLISTED_FACING);

		if (state.getValue(AutomationProperties.DYNAMIC)) {
			ImmutableMap<IUnlistedProperty<?>, Optional<?>> properties = state.getUnlistedProperties();
			float[] rotations = new float[6];
			for(EnumFacing facing : EnumFacing.VALUES) {
				if(properties.containsKey(AutomationProperties.ROTATIONS[facing.getIndex()])) {
					rotations[facing.getIndex()] = state.getValue(AutomationProperties.ROTATIONS[facing.getIndex()]);
				}
			}
			transformMovingParts(transformedGroups, frontFacing, rotations);
		} else {
			for(Map.Entry<String, CCModel> group : modelGroups.entrySet()) {
				transformedGroups.put(group.getKey(), rotateFacing(group.getValue().copy(), frontFacing));
			}
		}

		return transformedGroups;
	}

	@Override
	protected void renderItemModels(CCRenderState ccrs) {
		super.renderItemModels(ccrs);
		Map<String, CCModel> movingParts = Maps.newHashMap();
		transformMovingParts(movingParts, EnumFacing.NORTH, new float[6]);

		movingParts.forEach((k, m) -> m.render(ccrs, iconTransform));
	}

	protected abstract void transformMovingParts(Map<String, CCModel> transformedGroups, EnumFacing frontFacing, float[] rotations);

	protected Map<String, CCModel> rotateModels(Map<String, CCModel> groups, EnumFacing frontFacing, Transformation transform) {
		return groups.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e-> rotateFacing(e.getValue().copy().apply(transform), frontFacing)));
	}

	private CCModel rotateFacing(CCModel group, EnumFacing frontFacing) {
		return group.apply(Rotation.quarterRotations[(frontFacing.getHorizontalIndex() + 2) & 3].at(Vector3.center));
	}

	@Override
	public IExtendedBlockState handleState(IExtendedBlockState state, IBlockAccess access, BlockPos pos) {
		EnumFacing facing = EnumFacing.NORTH;
		TileEntity tileentity = access.getTileEntity(pos);

		if (tileentity instanceof TileTorqueBase) {
			TileTorqueBase torquePart = ((TileTorqueBase) tileentity);
			facing = torquePart.getPrimaryFacing();
		}

		IExtendedBlockState updatedState = state.withProperty(CoreProperties.UNLISTED_FACING, facing);
		updatedState = updatedState.withProperty(AutomationProperties.DYNAMIC, false);
		for(EnumFacing f : EnumFacing.VALUES) {
			updatedState = updatedState.withProperty(AutomationProperties.ROTATIONS[f.getIndex()], 0f);
		}

		return updatedState;
	}
}
