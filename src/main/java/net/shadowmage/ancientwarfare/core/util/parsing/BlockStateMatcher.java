package net.shadowmage.ancientwarfare.core.util.parsing;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class BlockStateMatcher implements Predicate<IBlockState> {
	private final RegistryNameMatcher nameMatcher;
	private final PropertyMapMatcher propertyMatcher = new PropertyMapMatcher();

	@SuppressWarnings("ConstantConditions")
	public BlockStateMatcher(IBlockState fullState) {
		this(fullState.getBlock().getRegistryName());
		fullState.getProperties().forEach(this::addProperty);
	}

	public BlockStateMatcher(ResourceLocation registryName) {
		nameMatcher = new RegistryNameMatcher(registryName);
	}

	public BlockStateMatcher addProperty(IProperty<?> property, Comparable<?> value) {
		propertyMatcher.addProperty(property, value);
		return this;
	}

	@Override
	public boolean test(IBlockState state) {
		return nameMatcher.test(state.getBlock().getRegistryName()) && propertyMatcher.test(state.getProperties());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		BlockStateMatcher that = (BlockStateMatcher) o;

		if (!nameMatcher.equals(that.nameMatcher))
			return false;
		return propertyMatcher.equals(that.propertyMatcher);
	}

	@Override
	public int hashCode() {
		int result = nameMatcher.hashCode();
		result = 31 * result + propertyMatcher.hashCode();
		return result;
	}

	public static class RegistryNameMatcher implements Predicate<ResourceLocation> {
		private final ResourceLocation registryName;

		RegistryNameMatcher(@Nullable ResourceLocation registryName) {
			this.registryName = registryName;
		}

		@Override

		public boolean test(ResourceLocation resourceLocation) {
			return registryName != null && registryName.equals(resourceLocation);
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;

			RegistryNameMatcher that = (RegistryNameMatcher) o;

			return registryName != null ? registryName.equals(that.registryName) : that.registryName == null;
		}

		@Override
		public int hashCode() {
			return registryName != null ? registryName.hashCode() : 0;
		}
	}

	public static class PropertyMapMatcher implements Predicate<Map<IProperty<?>, Comparable<?>>> {
		private static final Predicate<Map.Entry<IProperty<?>, Comparable<?>>> NOT_FOUND_PROPERTY_MATCHER = propEntry -> true;
		private final Map<IProperty<?>, PropertyMatcher> propertyMatchers = new HashMap<>();

		void addProperty(IProperty<?> property, Comparable<?> value) {
			//noinspection unchecked
			propertyMatchers.put(property, new PropertyMatcher(property, value));
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;

			PropertyMapMatcher that = (PropertyMapMatcher) o;

			return propertyMatchers.equals(that.propertyMatchers);
		}

		@Override
		public int hashCode() {
			return propertyMatchers.hashCode();
		}

		@Override
		public boolean test(Map<IProperty<?>, Comparable<?>> properties) {
			for (Map.Entry<IProperty<?>, Comparable<?>> property : properties.entrySet()) {
				boolean matches;
				if (propertyMatchers.containsKey(property.getKey())) {
					//noinspection unchecked
					matches = propertyMatchers.get(property.getKey()).test(property);
				} else {
					matches = NOT_FOUND_PROPERTY_MATCHER.test(property);
				}
				if (!matches) {
					return false;
				}
			}
			return true;
		}
	}

	public static class PropertyMatcher<T extends Comparable<T>> implements Predicate<Map.Entry<IProperty<T>, Comparable<T>>> {
		private final IProperty<T> property;
		private final Comparable<T> value;

		PropertyMatcher(IProperty<T> property, Comparable<T> value) {
			this.property = property;
			this.value = value;
		}

		@Override
		public boolean test(Map.Entry<IProperty<T>, Comparable<T>> prop) {
			return property.equals(prop.getKey()) && value.equals(prop.getValue());
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;

			PropertyMatcher<?> that = (PropertyMatcher<?>) o;

			if (!property.equals(that.property))
				return false;
			return value.equals(that.value);
		}

		@Override
		public int hashCode() {
			int result = property.hashCode();
			result = 31 * result + value.hashCode();
			return result;
		}
	}
}
