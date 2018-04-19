package net.shadowmage.ancientwarfare.core.render;

import codechicken.lib.model.bakery.key.IBlockStateKeyGenerator;
import com.google.common.collect.Maps;
import net.minecraft.block.properties.IProperty;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

import java.util.Map;
import java.util.StringJoiner;
import java.util.function.Function;

public class BlockStateKeyGenerator implements IBlockStateKeyGenerator {
	private final Map<IProperty, Function<Object, String>> propertyFormats;
	private final Map<IUnlistedProperty, Function<Object, String>> unlistedPropertyFormats;

	private BlockStateKeyGenerator(Map<IProperty, Function<Object, String>> propertyFormats, Map<IUnlistedProperty, Function<Object, String>> unlistedPropertyFormats) {

		this.propertyFormats = propertyFormats;
		this.unlistedPropertyFormats = unlistedPropertyFormats;
	}

	@Override
	public String generateKey(IExtendedBlockState state) {
		StringJoiner stringJoiner = new StringJoiner("|");

		stringJoiner.add(state.getBlock().getRegistryName().toString());
		for (Map.Entry<IProperty, Function<Object, String>> entry : propertyFormats.entrySet()) {
			stringJoiner.add(entry.getValue().apply(state.getValue(entry.getKey())));
		}

		for (Map.Entry<IUnlistedProperty, Function<Object, String>> entry : unlistedPropertyFormats.entrySet()) {
			stringJoiner.add(entry.getValue().apply(state.getValue(entry.getKey())));
		}

		return stringJoiner.toString();
	}

	public static class Builder {
		private Map<IProperty, Function<Object, String>> propertyFormats = Maps.newHashMap();
		private Map<IUnlistedProperty, Function<Object, String>> unlistedPropertyFormats = Maps.newHashMap();

		public Builder addKeyProperties(IProperty... properties) {
			addKeyProperties(Object::toString, properties);
			return this;
		}

		public Builder addKeyProperties(Function<Object, String> formatValue, IProperty... properties) {
			for (IProperty property : properties) {
				this.propertyFormats.put(property, formatValue);
			}
			return this;
		}

		public Builder addKeyProperties(IUnlistedProperty... properties) {
			addKeyProperties(Object::toString, properties);
			return this;
		}

		public BlockStateKeyGenerator build() {
			return new BlockStateKeyGenerator(this.propertyFormats, this.unlistedPropertyFormats);
		}

		public Builder addKeyProperties(Function<Object, String> getFormat, IUnlistedProperty... properties) {
			for (IUnlistedProperty property : properties) {
				this.unlistedPropertyFormats.put(property, getFormat);
			}
			return this;
		}
	}
}
