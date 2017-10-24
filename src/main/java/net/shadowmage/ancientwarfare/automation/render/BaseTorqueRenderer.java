package net.shadowmage.ancientwarfare.automation.render;

import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.shadowmage.ancientwarfare.automation.render.property.AutomationProperties;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueBase;
import net.shadowmage.ancientwarfare.core.render.property.CoreProperties;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public abstract class BaseTorqueRenderer extends AnimatedBlockRenderer {
	protected BaseTorqueRenderer(String modelPath) {
		super(modelPath);
	}

	@Override
	protected Collection<CCModel> applyModelTransforms(Collection<CCModel> modelGroups, EnumFacing face, IExtendedBlockState state) {
		Set<CCModel> transformedGroups = Sets.newHashSet();

		EnumFacing frontFacing = state.getValue(CoreProperties.UNLISTED_FACING);

		if (state.getValue(AutomationProperties.DYNAMIC)) {
			ImmutableMap<IUnlistedProperty<?>, Optional<?>> properties = state.getUnlistedProperties();
			float[] rotations = new float[6];
			for(EnumFacing facing : EnumFacing.VALUES) {
				if(properties.containsKey(AutomationProperties.ROTATIONS[facing.getIndex()])) {
					rotations[facing.getIndex()] = state.getValue(AutomationProperties.ROTATIONS[facing.getIndex()]);
				}
			}
			transformMovingParts(transformedGroups, frontFacing, rotations, state);
		} else {
			for(CCModel group : modelGroups) {
				transformedGroups.add(rotateFacing(group.copy(), frontFacing));
			}
		}

		return transformedGroups;
	}

	@Override
	protected void renderItemModels(CCRenderState ccrs) {
		super.renderItemModels(ccrs);
		Set<CCModel> movingParts = Sets.newHashSet();
		transformMovingParts(movingParts, EnumFacing.NORTH, new float[6], null);

		movingParts.forEach(m -> m.render(ccrs, iconTransform));
	}

	protected abstract void transformMovingParts(Collection<CCModel> transformedGroups, EnumFacing frontFacing, float[] rotations, IExtendedBlockState state);

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

		updatedState = handleAdditionalProperties(updatedState, tileentity);

		return updatedState;
	}

	protected IExtendedBlockState handleAdditionalProperties(IExtendedBlockState state, TileEntity tileEntity) {
		return state;
	}
}
