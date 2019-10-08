package net.shadowmage.ancientwarfare.core.util;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class NBTHelper {
	private NBTHelper() {}

	public static IBlockState getBlockState(NBTTagCompound blockStateTag) {
		return BlockTools.getBlockState(new Tuple<>(blockStateTag.getString("blockName"), getStateProperties(blockStateTag.getCompoundTag("properties"))),
				Block::getDefaultState, BlockTools::updateProperty);
	}

	private static Map<String, String> getStateProperties(NBTTagCompound propertiesTag) {
		Map<String, String> ret = new HashMap<>();
		for (String key : propertiesTag.getKeySet()) {
			if (propertiesTag.hasKey(key, Constants.NBT.TAG_STRING)) {
				ret.put(key, propertiesTag.getString(key));
			}
		}
		return ret;
	}

	public static NBTTagCompound getBlockStateTag(IBlockState state) {
		NBTTagCompound ret = new NBTTagCompound();
		//noinspection ConstantConditions
		ret.setString("blockName", state.getBlock().getRegistryName().toString());
		if (!state.getProperties().isEmpty()) {
			ret.setTag("properties", getStatePropertiesTag(state.getProperties()));
		}
		return ret;
	}

	private static NBTTagCompound getStatePropertiesTag(ImmutableMap<IProperty<?>, Comparable<?>> properties) {
		NBTTagCompound propertiesTag = new NBTTagCompound();

		for (Map.Entry<IProperty<?>, Comparable<?>> property : properties.entrySet()) {
			propertiesTag.setString(property.getKey().getName(), serializeValue(property.getKey(), property.getValue()));
		}

		return propertiesTag;
	}

	private static <T extends Comparable<T>> String serializeValue(IProperty<T> property, Comparable<?> valueString) {
		//noinspection unchecked
		return property.getName((T) valueString);
	}

	public static <T> Set<T> getSet(NBTTagList tagList, Function<NBTBase, T> getElement) {
		Set<T> ret = new HashSet<>();
		for (NBTBase tag : tagList) {
			ret.add(getElement.apply(tag));
		}
		return ret;
	}

	public static Set<String> getStringSet(NBTTagList tagList) {
		return getSet(tagList, tag -> ((NBTTagString) tag).getString());
	}

	public static <T> NBTTagList getTagList(Collection<T> collection, Function<T, NBTBase> serializeElement) {
		NBTTagList ret = new NBTTagList();
		collection.forEach(element -> ret.appendTag(serializeElement.apply(element)));
		return ret;
	}


	public static NBTTagList getNBTStringList(Collection<String> strings) {
		NBTTagList ret = new NBTTagList();
		strings.forEach(str -> ret.appendTag(new NBTTagString(str)));
		return ret;
	}

	public static NBTTagList getNBTUniqueIdList(Collection<UUID> uuids) {
		NBTTagList ret = new NBTTagList();
		uuids.forEach(uuid -> ret.appendTag(new NBTBuilder().setUniqueId("uuid", uuid).build()));
		return ret;
	}

	public static Set<UUID> getUniqueIdSet(NBTBase tag) {
		return getSet(getTagList(tag, Constants.NBT.TAG_COMPOUND), element -> ((NBTTagCompound) element).getUniqueId("uuid"));
	}

	private static NBTTagList getTagList(NBTBase tag, int type) {
		try {
			if (tag.getId() == 9) {
				NBTTagList nbttaglist = (NBTTagList) tag;

				if (!nbttaglist.hasNoTags() && nbttaglist.getTagType() != type) {
					return new NBTTagList();
				}

				return nbttaglist;
			}
		}
		catch (ClassCastException classcastexception) {
			AncientWarfareCore.LOG.error("Error casting tag to taglist: ", tag.toString());
		}

		return new NBTTagList();
	}

	public static NBTTagList serializeItemStackList(List<ItemStack> stacks) {
		NBTTagList nbtStacks = new NBTTagList();
		for (ItemStack stack : stacks) {
			nbtStacks.appendTag(stack.writeToNBT(new NBTTagCompound()));
		}
		return nbtStacks;
	}

	public static List<ItemStack> deserializeItemStackList(NBTTagList nbtStacks) {
		List<ItemStack> stacks = NonNullList.create();
		for (NBTBase nbtStack : nbtStacks) {
			stacks.add(new ItemStack((NBTTagCompound) nbtStack));
		}
		return stacks;
	}

	public static final Collector<NBTBase, NBTTagList, NBTTagList> NBTLIST_COLLECTOR =
			Collector.of(NBTTagList::new, NBTTagList::appendTag, (l1, l2) -> {
				l2.forEach(l1::appendTag);
				return l1;
			});

	public static NBTTagCompound writeBlockPosToNBT(NBTTagCompound tag, BlockPos pos) {
		tag.setInteger("x", pos.getX());
		tag.setInteger("y", pos.getY());
		tag.setInteger("z", pos.getZ());
		return tag;
	}

	public static BlockPos readBlockPosFromNBT(NBTTagCompound tag) {
		return new BlockPos(tag.getInteger("x"), tag.getInteger("y"), tag.getInteger("z"));
	}

	public static <K, V> Map<K, V> getMap(NBTTagList list, Function<NBTTagCompound, K> getKey, Function<NBTTagCompound, V> getValue) {
		Map<K, V> ret = new HashMap<>();
		for (int i=0; i<list.tagCount(); i++) {
			NBTTagCompound tag = list.getCompoundTagAt(i);

			ret.put(getKey.apply(tag), getValue.apply(tag));
		}

		return ret;
	}

	public static <K,V> NBTTagList mapToCompoundList(Map<K, V> map, BiConsumer<NBTTagCompound, K> setKeyTag, BiConsumer<NBTTagCompound, V> setValueTag) {
		NBTTagList list = new NBTTagList();
		for(Map.Entry<K, V> entry : map.entrySet()) {
			NBTTagCompound nbtEntry = new NBTTagCompound();
			setKeyTag.accept(nbtEntry, entry.getKey());
			setValueTag.accept(nbtEntry, entry.getValue());

			list.appendTag(nbtEntry);
		}

		return list;
	}

	public static void writeSerializablesTo(NBTTagCompound tag, String key, List<? extends INBTSerializable> elements) {
		NBTTagList list = new NBTTagList();
		for (INBTSerializable serializable : elements) {
			list.appendTag(serializable.serializeNBT());
		}
		tag.setTag(key, list);
	}

	public static <T extends INBTSerializable<NBTTagCompound>> List<T> deserializeListFrom(NBTTagCompound tag, String key, Supplier<T> supplier) {
		NBTTagList tags = tag.getTagList(key, Constants.NBT.TAG_COMPOUND);
		ArrayList<T> list = new ArrayList<>();
		for (int i = 0; i < tags.tagCount(); i++) {
			T element = supplier.get();
			element.deserializeNBT(tags.getCompoundTagAt(i));
			list.add(element);
		}
		return list;
	}
}
