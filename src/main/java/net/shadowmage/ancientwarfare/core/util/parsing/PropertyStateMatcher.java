package net.shadowmage.ancientwarfare.core.util.parsing;

import net.minecraft.block.state.IBlockState;

import java.util.function.Predicate;

public class PropertyStateMatcher implements Predicate<IBlockState> {
	private PropertyState propertyState;

	public PropertyStateMatcher(PropertyState propertyState) {
		this.propertyState = propertyState;
	}

	@Override
	public boolean test(IBlockState iBlockState) {
		return iBlockState.getValue(propertyState.getProperty()).equals(propertyState.getValue());
	}
}
