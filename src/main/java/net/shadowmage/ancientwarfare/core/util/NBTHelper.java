package net.shadowmage.ancientwarfare.core.util;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Tuple;
import net.minecraftforge.common.util.Constants;

import java.util.HashMap;
import java.util.Map;

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
}
