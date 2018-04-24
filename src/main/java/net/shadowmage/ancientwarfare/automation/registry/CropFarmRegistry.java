package net.shadowmage.ancientwarfare.automation.registry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public class CropFarmRegistry {
	private static final Map<BlockStateMatcher, IBlockState> tillableBlocks = new HashMap<>();
	//TODO add likely additional registry to keep a list of plantable blocks in addition to just those that can be tilled
	private static final Set<BlockStateMatcher> plantableBlocks = new HashSet<>();

	public static boolean isTillable(IBlockState state) {
		return tillableBlocks.keySet().stream().anyMatch(m -> m.test(state));
	}

	public static IBlockState getTilledState(IBlockState tillable) {
		return tillableBlocks.entrySet().stream().filter(e -> e.getKey().test(tillable)).map(Map.Entry::getValue).findFirst().orElse(tillable);
	}

	public static boolean isPlantable(IBlockState state) {
		return plantableBlocks.stream().anyMatch(matcher -> matcher.test(state));
	}

	public static class Parser implements IRegistryDataParser {
		@Override
		public String getName() {
			return "tillable_blocks";
		}

		@Override
		public void parse(JsonElement json) {
			try {
				JsonArray tillables = JsonUtils.getJsonArray(json, "");

				for (JsonElement t : tillables) {
					JsonObject tillableMapping = JsonUtils.getJsonObject(t, "");

					BlockStateMatcher tillableState = getBlockStateMatcher(tillableMapping, "tillable");
					IBlockState tilledState = getBlockState(tillableMapping, "tilled");

					tillableBlocks.put(tillableState, tilledState);
					plantableBlocks.add(getBlockStateMatcher(tillableMapping, "tilled"));
				}
			}
			catch (JsonParseException e) {
				AncientWarfareCore.log.error("Error parsing tillables: \n" + e.getMessage());
			}
			catch (MissingResourceException e) {
				AncientWarfareCore.log.error(e.getMessage());
			}
		}

		private IBlockState getBlockState(JsonObject parent, String elementName) {
			return getBlockState(parent, elementName, Block::getDefaultState, this::getBlockState);
		}

		private BlockStateMatcher getBlockStateMatcher(JsonObject parent, String elementName) {
			return getBlockState(parent, elementName, block -> new BlockStateMatcher(block.getRegistryName()), BlockStateMatcher::addProperty);
		}

		private <T> T getBlockState(JsonObject parent, String elementName, Function<Block, T> init, AddPropertyFunction<T> addProperty) {
			Tuple<String, Map<String, String>> blockProps = getBlockNameAndProperties(parent, elementName);

			String registryName = blockProps.getFirst();
			Map<String, String> properties = blockProps.getSecond();

			Block block = getBlock(registryName);

			T ret = init.apply(block);
			BlockStateContainer stateContainer = block.getBlockState();

			for (Map.Entry<String, String> prop : properties.entrySet()) {
				IProperty<?> property = stateContainer.getProperty(prop.getKey());
				//noinspection ConstantConditions
				Comparable<?> value = getValueHelper(property, prop.getValue());
				//noinspection ConstantConditions
				ret = addProperty.apply(ret, property, value);
			}

			return ret;
		}

		private Block getBlock(String registryName) {
			Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(registryName));
			if (block == null) {
				throw new MissingResourceException("Unable to find block with registry name \"" + registryName + "\"", Block.class.getName(), registryName);
			}
			return block;
		}

		private Tuple<String, Map<String, String>> getBlockNameAndProperties(JsonObject parent, String elementName) {
			if (!JsonUtils.hasField(parent, elementName)) {
				throw new JsonParseException("Expected " + elementName + " member in " + parent.toString());
			}

			String registryName;
			Map<String, String> properties = new HashMap<>();

			if (JsonUtils.isJsonPrimitive(parent, elementName)) {
				registryName = JsonUtils.getString(parent, elementName);
			} else {
				JsonObject obj = JsonUtils.getJsonObject(parent, elementName);
				registryName = JsonUtils.getString(obj, "name");

				if (JsonUtils.hasField(obj, "properties")) {
					JsonObject props = JsonUtils.getJsonObject(obj, "properties");
					for (Map.Entry<String, JsonElement> prop : props.entrySet()) {
						properties.put(prop.getKey(), prop.getValue().getAsString());
					}
				}
			}
			return new Tuple<>(registryName, properties);
		}

		@Nullable
		private <T extends Comparable<T>> T getValueHelper(IProperty<T> property, String valueString) {
			return property.parseValue(valueString).orNull();
		}

		private <T extends Comparable<T>> IBlockState getBlockState(IBlockState state, IProperty<T> property, Comparable<?> value) {
			//noinspection unchecked
			return state.withProperty(property, (T) value);
		}

		private interface AddPropertyFunction<T> {
			T apply(T obj, IProperty<?> property, Comparable<?> value);
		}
	}

	public static class BlockStateMatcher implements Predicate<IBlockState> {
		private final RegistryNameMatcher nameMatcher;
		private final PropertyMapMatcher propertyMatcher = new PropertyMapMatcher();

		BlockStateMatcher(ResourceLocation registryName) {
			nameMatcher = new RegistryNameMatcher(registryName);
		}

		BlockStateMatcher addProperty(IProperty<?> property, Comparable<?> value) {
			propertyMatcher.addProperty(property, value);
			return this;
		}

		@Override
		public boolean test(IBlockState state) {
			return nameMatcher.test(state.getBlock().getRegistryName()) && propertyMatcher.test(state.getProperties());
		}
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
	}

	public static class PropertyMapMatcher implements Predicate<Map<IProperty<?>, Comparable<?>>> {
		private static final Predicate<Map.Entry<IProperty<?>, Comparable<?>>> NOT_FOUND_PROPERTY_MATCHER = propEntry -> true;
		private final Map<IProperty<?>, PropertyMatcher> propertyMatchers = new HashMap<>();

		void addProperty(IProperty<?> property, Comparable<?> value) {
			//noinspection unchecked
			propertyMatchers.put(property, new PropertyMatcher(property, value));
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
	}
}
