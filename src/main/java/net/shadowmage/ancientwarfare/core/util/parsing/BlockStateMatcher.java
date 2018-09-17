package net.shadowmage.ancientwarfare.core.util.parsing;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class BlockStateMatcher implements Predicate<IBlockState> {
	private final Block block;
	private final PropertyMapMatcher propertyMatcher = new PropertyMapMatcher();

	public BlockStateMatcher(IBlockState fullState) {
		this(fullState.getBlock());
		fullState.getProperties().forEach(this::addProperty);
	}

	public BlockStateMatcher(Block block) {
		this.block = block;
	}

	BlockStateMatcher addProperty(IProperty<?> property, Comparable<?> value) {
		propertyMatcher.addProperty(property, value);
		return this;
	}

	@Override
	public boolean test(IBlockState state) {
		return block == state.getBlock() && propertyMatcher.test(state.getProperties());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		BlockStateMatcher that = (BlockStateMatcher) o;

		return block.equals(that.block) && propertyMatcher.equals(that.propertyMatcher);
	}

	@Override
	public int hashCode() {
		int result = block.hashCode();
		result = 31 * result + propertyMatcher.hashCode();
		return result;
	}

	public static class PropertyMapMatcher implements Predicate<Map<IProperty<?>, Comparable<?>>> {
		private final Map<IProperty<?>, Comparable<?>> propertyValues = new HashMap<>();

		void addProperty(IProperty<?> property, Comparable<?> value) {
			//noinspection unchecked
			propertyValues.put(property, value);
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;

			PropertyMapMatcher that = (PropertyMapMatcher) o;

			return propertyValues.equals(that.propertyValues);
		}

		@Override
		public int hashCode() {
			return propertyValues.hashCode();
		}

		@Override
		public boolean test(Map<IProperty<?>, Comparable<?>> properties) {
			for (Map.Entry<IProperty<?>, Comparable<?>> property : propertyValues.entrySet()) {
				if (!properties.containsKey(property.getKey()) || !property.getValue().equals(properties.get(property.getKey()))) {
					return false;
				}
			}
			return true;
		}
	}
}
