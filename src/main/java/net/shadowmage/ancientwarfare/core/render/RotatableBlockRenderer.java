package net.shadowmage.ancientwarfare.core.render;

import codechicken.lib.render.CCModel;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Vector3;
import com.google.common.collect.Sets;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.shadowmage.ancientwarfare.core.render.property.CoreProperties;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class RotatableBlockRenderer extends BaseBakery {

	protected RotatableBlockRenderer(String modelPath) {
		super(modelPath);
	}

	@Override
	protected Collection<CCModel> applyModelTransforms(Collection<CCModel> modelGroups, EnumFacing face, IExtendedBlockState state) {
		Set<CCModel> transformedGroups = Sets.newHashSet();

		for (CCModel group : modelGroups) {
			transformedGroups.add(rotateFacing(group.copy(), getFacing(state)));
		}

		return transformedGroups;
	}

	protected EnumFacing getFacing(IExtendedBlockState state) {
		return state.getValue(CoreProperties.UNLISTED_HORIZONTAL_FACING);
	}

	protected Collection<CCModel> rotateFacing(Collection<CCModel> groups, EnumFacing frontFacing) {
		return groups.stream().map(e -> rotateFacing(e.copy(), frontFacing)).collect(Collectors.toSet());
	}

	protected CCModel rotateFacing(CCModel group, EnumFacing frontFacing) {
		double angle = Math.PI / 2d * (frontFacing.getAxis() == EnumFacing.Axis.Y ? frontFacing.getFrontOffsetY() : -((frontFacing.getHorizontalIndex() + 2) & 3));

		return group.apply(new Rotation(angle, frontFacing.getAxis() == EnumFacing.Axis.Y ? 1 : 0, frontFacing.getAxis() != EnumFacing.Axis.Y ? 1 : 0, 0).at(Vector3.center));
	}
}
