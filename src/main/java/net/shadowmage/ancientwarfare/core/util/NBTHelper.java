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
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
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
		ret.setTag("properties", getStatePropertiesTag(state.getProperties()));
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

	public static Set<String> getStringSet(NBTTagList tagList) {
		Set<String> ret = new HashSet<>();
		for (NBTBase tag : tagList) {
			ret.add(((NBTTagString) tag).getString());
		}
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
		NBTTagList tagList = getTagList(tag, Constants.NBT.TAG_COMPOUND);
		Set<UUID> ret = new HashSet<>();
		for (NBTBase element : tagList) {
			ret.add(((NBTTagCompound) element).getUniqueId("uuid"));
		}
		return ret;
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
}
