package net.shadowmage.ancientwarfare.core.util.parsing;

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

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.function.Function;

public class JsonHelper {

	public static IBlockState getBlockState(JsonObject parent, String elementName) {
		return getBlockState(parent, elementName, Block::getDefaultState, JsonHelper::getBlockState);
	}

	public static BlockStateMatcher getBlockStateMatcher(JsonObject parent, String elementName) {
		return getBlockState(parent, elementName, block -> new BlockStateMatcher(block.getRegistryName()), BlockStateMatcher::addProperty);
	}

	private static <T> T getBlockState(JsonObject parent, String elementName, Function<Block, T> init, AddPropertyFunction<T> addProperty) {
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

	private static Block getBlock(String registryName) {
		Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(registryName));
		if (block == null) {
			throw new MissingResourceException("Unable to find block with registry name \"" + registryName + "\"", Block.class.getName(), registryName);
		}
		return block;
	}

	private static Tuple<String, Map<String, String>> getBlockNameAndProperties(JsonObject parent, String elementName) {
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
	private static <T extends Comparable<T>> T getValueHelper(IProperty<T> property, String valueString) {
		return property.parseValue(valueString).orNull();
	}

	private static <T extends Comparable<T>> IBlockState getBlockState(IBlockState state, IProperty<T> property, Comparable<?> value) {
		//noinspection unchecked
		return state.withProperty(property, (T) value);
	}

	private interface AddPropertyFunction<T> {
		T apply(T obj, IProperty<?> property, Comparable<?> value);
	}
}
