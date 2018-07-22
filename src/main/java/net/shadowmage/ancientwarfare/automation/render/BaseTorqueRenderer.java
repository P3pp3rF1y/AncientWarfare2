package net.shadowmage.ancientwarfare.automation.render;

import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.shadowmage.ancientwarfare.automation.render.property.AutomationProperties;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueBase;
import net.shadowmage.ancientwarfare.core.render.property.CoreProperties;
import net.shadowmage.ancientwarfare.core.util.WorldTools;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public abstract class BaseTorqueRenderer<T extends TileTorqueBase> extends AnimatedBlockRenderer {
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
			for (EnumFacing facing : EnumFacing.VALUES) {
				if (properties.containsKey(AutomationProperties.ROTATIONS[facing.getIndex()])) {
					rotations[facing.getIndex()] = state.getValue(AutomationProperties.ROTATIONS[facing.getIndex()]);
				}
			}
			transformMovingParts(transformedGroups, frontFacing, rotations, state);
		} else {
			transformedGroups.addAll(rotateFacing(modelGroups, frontFacing));
		}

		return transformedGroups;
	}

	@Override
	protected void renderItemModels(CCRenderState ccrs, ItemStack stack) {
		super.renderItemModels(ccrs, stack);
		Set<CCModel> movingParts = Sets.newHashSet();
		transformMovingParts(movingParts, EnumFacing.NORTH, new float[6], null);

		movingParts.forEach(m -> m.render(ccrs, getIconTransform(stack)));
	}

	protected abstract void transformMovingParts(Collection<CCModel> transformedGroups, EnumFacing frontFacing, float[] rotations,
			@Nullable IExtendedBlockState state);

	@Override
	public IExtendedBlockState handleState(IExtendedBlockState state, IBlockAccess access, BlockPos pos) {
		EnumFacing facing = EnumFacing.NORTH;
		Optional<TileTorqueBase> tileentity = WorldTools.getTile(access, pos, TileTorqueBase.class);

		if (tileentity.isPresent()) {
			TileTorqueBase torquePart = tileentity.get();
			facing = torquePart.getPrimaryFacing();
		}

		IExtendedBlockState updatedState = state.withProperty(CoreProperties.UNLISTED_FACING, facing);
		updatedState = updatedState.withProperty(AutomationProperties.DYNAMIC, false);
		for (EnumFacing f : EnumFacing.VALUES) {
			updatedState = updatedState.withProperty(AutomationProperties.ROTATIONS[f.getIndex()], 0f);
		}

		if (tileentity.isPresent()) {
			//noinspection unchecked
			updatedState = handleAdditionalProperties(updatedState, (T) tileentity.get());
		}

		return updatedState;
	}

	protected IExtendedBlockState handleAdditionalProperties(IExtendedBlockState state, T tileEntity) {
		return state;
	}
}
