package net.shadowmage.ancientwarfare.automation.render;

import com.google.common.collect.ImmutableMap;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.shadowmage.ancientwarfare.automation.render.property.AutomationProperties;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueBase;
import net.shadowmage.ancientwarfare.core.render.property.CoreProperties;

import java.util.Optional;

public class TorqueAnimationRenderer<T extends TileTorqueBase> extends BaseAnimationRenderer<T> {

	public TorqueAnimationRenderer(AnimatedBlockRenderer bakery) {
		super(bakery);
	}

	@Override
	protected IExtendedBlockState handleState(T te, float partialTicks, IExtendedBlockState state) {
		EnumFacing facing = te.getPrimaryFacing();
		state = state.withProperty(CoreProperties.UNLISTED_FACING, facing);
		ImmutableMap<IUnlistedProperty<?>, Optional<?>> properties = state.getUnlistedProperties();
		float[] rotations = new float[6];
		for (EnumFacing f : EnumFacing.VALUES) {
			if (properties.containsKey(AutomationProperties.ROTATIONS[f.getIndex()])) {
				float rotation = te.getClientOutputRotation(f, partialTicks);
				rotations[f.getIndex()] = rotation;
				state = state.withProperty(AutomationProperties.ROTATIONS[f.getIndex()], rotation);

			} else {
				state = state.withProperty(AutomationProperties.ROTATIONS[f.getIndex()], 0f);
			}
		}
		state = state.withProperty(AutomationProperties.DYNAMIC, true);
		state = updateAdditionalProperties(state, te);
		return state;
	}

	protected IExtendedBlockState updateAdditionalProperties(IExtendedBlockState state, TileTorqueBase te) {
		return state;
	}
}
