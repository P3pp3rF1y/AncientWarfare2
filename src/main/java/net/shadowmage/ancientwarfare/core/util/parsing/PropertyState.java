package net.shadowmage.ancientwarfare.core.util.parsing;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;

public class PropertyState<T extends Comparable<T>, V extends T> {
	private IProperty<T> property;
	private V value;

	public PropertyState(IProperty<T> property, V value) {
		this.property = property;
		this.value = value;
	}

	public IProperty<T> getProperty() {
		return property;
	}

	public V getValue() {
		return value;
	}

	public IBlockState update(IBlockState state) {
		return state.withProperty(property, value);
	}
}
