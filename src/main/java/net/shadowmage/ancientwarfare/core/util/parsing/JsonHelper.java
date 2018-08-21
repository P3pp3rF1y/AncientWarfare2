package net.shadowmage.ancientwarfare.core.util.parsing;

import com.google.common.base.Optional;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.MissingResourceException;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.StreamSupport;

public class JsonHelper {

	public static IBlockState getBlockState(JsonObject parent, String elementName) {
		return getBlockState(parent, elementName, Block::getDefaultState, JsonHelper::getBlockState);
	}

	public static BlockStateMatcher getBlockStateMatcher(JsonObject stateJson) {
		//noinspection ConstantConditions
		return getBlockState(stateJson, BlockStateMatcher::new, BlockStateMatcher::addProperty);
	}

	public static BlockStateMatcher getBlockStateMatcher(JsonObject parent, String elementName) {
		//noinspection ConstantConditions
		return getBlockState(parent, elementName, BlockStateMatcher::new, BlockStateMatcher::addProperty);
	}

	public static ItemStack getItemStack(JsonElement json) {
		return getItemStack(json, (i, c, m, t) -> {
			ItemStack stack = new ItemStack(i, c, m);
			stack.setTagCompound(t);
			return stack;
		});
	}

	public static ItemStack getItemStack(JsonObject json, String elementName) {
		if (!JsonUtils.hasField(json, elementName)) {
			throw new JsonParseException("Expected " + elementName + " member in " + json.toString());
		}

		return getItemStack(json.get(elementName));
	}

	private static <T> T getItemStack(JsonElement element, ItemStackCreator<T> creator) {
		if (element.isJsonPrimitive()) {
			return creator.instantiate(getItem(element.getAsString()), 1, -1, null);
		}

		JsonObject obj = element.getAsJsonObject();
		String registryName = JsonUtils.getString(obj, "name");
		Item item = getItem(registryName);

		int count = JsonUtils.hasField(obj, "count") ? JsonUtils.getInt(obj, "count") : 1;

		int meta = -1;
		if (JsonUtils.hasField(obj, "meta")) {
			meta = JsonUtils.getInt(obj, "meta");
		}
		NBTTagCompound tagCompound = null;
		if (JsonUtils.hasField(obj, "nbt")) {
			try {
				tagCompound = JsonToNBT.getTagFromJson(JsonUtils.getString(obj, "nbt"));
			}
			catch (NBTException e) {
				AncientWarfareCore.LOG.error("Error reading item stack nbt ", JsonUtils.getJsonObject(obj, "nbt"));
			}
		}

		return creator.instantiate(item, count, meta, tagCompound);
	}

	public static ItemStackMatcher getItemStackMatcher(JsonElement element) {
		return getItemStack(element, (i, c, m, t) -> new ItemStackMatcher.Builder(i).setMeta(m).setTagCompound(t).build());
	}

	public static ItemStackMatcher getItemStackMatcher(JsonObject parent, String elementName) {
		if (!JsonUtils.hasField(parent, elementName)) {
			throw new JsonParseException("Expected " + elementName + " member in " + parent.toString());
		}

		return getItemStackMatcher(parent.get(elementName));
	}

	private static <T> T getBlockState(JsonObject stateJson, Function<Block, T> init, AddPropertyFunction<T> addProperty) {
		return getBlockState(getBlockNameAndProperties(stateJson), init, addProperty);
	}

	private static <T> T getBlockState(JsonObject parent, String elementName, Function<Block, T> init, AddPropertyFunction<T> addProperty) {
		return getBlockState(getBlockNameAndProperties(parent, elementName), init, addProperty);
	}

	private static <T> T getBlockState(Tuple<String, Map<String, String>> blockProps, Function<Block, T> init, AddPropertyFunction<T> addProperty) {

		String registryName = blockProps.getFirst();
		Map<String, String> properties = blockProps.getSecond();

		Block block = getBlock(registryName);

		T ret = init.apply(block);
		BlockStateContainer stateContainer = block.getBlockState();

		for (Entry<String, String> prop : properties.entrySet()) {
			IProperty<?> property = stateContainer.getProperty(prop.getKey());
			//noinspection ConstantConditions
			Optional<?> value = getValueHelper(property, prop.getValue());

			if (!value.isPresent()) {
				throw new MissingResourceException("Invalid value \"" + prop.getValue() + "\" for property \"" + prop.getKey() + "\"", IProperty.class.getName(), prop.getKey());
			}
			//noinspection ConstantConditions
			ret = addProperty.apply(ret, property, (Comparable<?>) value.get());
		}

		return ret;
	}

	private static Block getBlock(String registryName) {
		return getRegistryEntry(registryName, ForgeRegistries.BLOCKS);
	}

	public static Item getItem(String registryName) {
		return getRegistryEntry(registryName, ForgeRegistries.ITEMS);
	}

	private static <T extends IForgeRegistryEntry<T>> T getRegistryEntry(String registryName, IForgeRegistry<T> registry) {
		ResourceLocation key = new ResourceLocation(registryName);
		if (!registry.containsKey(key)) {
			throw new MissingResourceException("Unable to find entry with registry name \"" + registryName + "\"",
					registry.getRegistrySuperType().getName(), registryName);
		}
		//noinspection ConstantConditions
		return registry.getValue(key);
	}

	private static Tuple<String, Map<String, String>> getBlockNameAndProperties(JsonObject stateJson) {
		Map<String, String> properties = new HashMap<>();

		if (JsonUtils.hasField(stateJson, "properties")) {
			JsonUtils.getJsonObject(stateJson, "properties").entrySet().forEach(p -> properties.put(p.getKey(), p.getValue().getAsString()));
		}

		return new Tuple<>(JsonUtils.getString(stateJson, "name"), properties);
	}

	private static Tuple<String, Map<String, String>> getBlockNameAndProperties(JsonObject parent, String elementName) {
		if (!JsonUtils.hasField(parent, elementName)) {
			throw new JsonParseException("Expected " + elementName + " member in " + parent.toString());
		}

		if (JsonUtils.isJsonPrimitive(parent, elementName)) {
			return new Tuple<>(JsonUtils.getString(parent, elementName), new HashMap<>());
		}

		return getBlockNameAndProperties(JsonUtils.getJsonObject(parent, elementName));
	}

	private static <T extends Comparable<T>> Optional<T> getValueHelper(IProperty<T> property, String valueString) {
		return property.parseValue(valueString);
	}

	private static <T extends Comparable<T>> IBlockState getBlockState(IBlockState state, IProperty<T> property, Comparable<?> value) {
		//noinspection unchecked
		return state.withProperty(property, (T) value);
	}

	public static Predicate<IBlockState> getBlockStateMatcher(JsonObject json, String arrayElement, String individualElement) {
		if (json.has(arrayElement)) {
			JsonArray stateMatchers = JsonUtils.getJsonArray(json, arrayElement);
			return new MultiBlockStateMatcher(StreamSupport.stream(stateMatchers.spliterator(), false)
					.map(e -> getBlockStateMatcher(JsonUtils.getJsonObject(e, individualElement)))
					.toArray(BlockStateMatcher[]::new));
		}
		return getBlockStateMatcher(json, individualElement);
	}

	private interface AddPropertyFunction<T> {
		T apply(T obj, IProperty<?> property, Comparable<?> value);
	}

	public static PropertyState getPropertyState(IBlockState state, JsonObject parent, String elementName) {
		JsonObject jsonProperty = JsonUtils.getJsonObject(parent, elementName);

		if (jsonProperty.entrySet().isEmpty()) {
			throw new JsonParseException("Expected at least one property defined for " + elementName + " in " + parent.toString());
		}

		Entry<String, JsonElement> propJson = jsonProperty.entrySet().iterator().next();
		String propName = propJson.getKey();
		String propValue = propJson.getValue().getAsString();

		BlockStateContainer stateContainer = state.getBlock().getBlockState();

		IProperty<?> property = stateContainer.getProperty(propName);
		if (property == null) {
			//noinspection ConstantConditions
			throw new MissingResourceException("Block \"" + state.getBlock().getRegistryName().toString() + "\" doesn't have \"" + propName + "\" property",
					IProperty.class.getName(), propName);
		}
		Optional<?> value = getValueHelper(property, propValue);
		if (!value.isPresent()) {
			throw new MissingResourceException("Invalid value \"" + propValue + "\" for property \"" + propName + "\"", IProperty.class.getName(), propName);
		}

		//noinspection unchecked
		return new PropertyState(property, (Comparable) value.get());
	}

	public static PropertyStateMatcher getPropertyStateMatcher(IBlockState state, JsonObject parent, String elementName) {
		return new PropertyStateMatcher(getPropertyState(state, parent, elementName));
	}

	public static <K, V> Map<K, V> mapFromJson(JsonObject json, String propertyName, Function<Entry<String, JsonElement>, K> parseKey,
			Function<Entry<String, JsonElement>, V> parseValue) {
		return mapFromJsonArray(JsonUtils.getJsonArray(json, propertyName), new HashMap<>(), parseKey, parseValue);
	}

	public static <K, V> Map<K, V> mapFromJson(JsonElement json, Function<Entry<String, JsonElement>, K> parseKey,
			Function<Entry<String, JsonElement>, V> parseValue) {
		return mapFromJsonArray(JsonUtils.getJsonArray(json, ""), new HashMap<>(), parseKey, parseValue);
	}

	public static <K, V> void mapFromJson(JsonObject json, String propertyName, Map<K, V> ret, Function<Entry<String, JsonElement>, K> parseKey,
			Function<Entry<String, JsonElement>, V> parseValue) {
		mapFromJsonArray(JsonUtils.getJsonArray(json, propertyName), ret, parseKey, parseValue);
	}

	private static <K, V> Map<K, V> mapFromJsonArray(JsonArray arr, Map<K, V> ret, Function<Entry<String, JsonElement>, K> parseKey,
			Function<Entry<String, JsonElement>, V> parseValue) {

		for (JsonElement e : arr) {
			Entry<String, JsonElement> pair = JsonUtils.getJsonObject(e, "").entrySet().iterator().next();
			ret.put(parseKey.apply(pair), parseValue.apply(pair));
		}

		return ret;
	}

	private interface ItemStackCreator<R> {
		R instantiate(Item item, int count, int meta, @Nullable NBTTagCompound tagCompound);
	}
}
