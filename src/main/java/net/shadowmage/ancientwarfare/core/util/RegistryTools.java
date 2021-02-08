package net.shadowmage.ancientwarfare.core.util;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;

import java.util.MissingResourceException;

public class RegistryTools {
	public static final ResourceLocation EMPTY_REGISTRY_NAME = new ResourceLocation("", "");

	private RegistryTools() {}

	static Block getBlock(String registryName) {
		return getRegistryEntry(registryName, ForgeRegistries.BLOCKS, Blocks.AIR);
	}

	public static Item getItem(String registryName) {
		return getRegistryEntry(registryName, ForgeRegistries.ITEMS, Items.AIR);
	}

	private static <T extends IForgeRegistryEntry<T>> T getRegistryEntry(String registryName, IForgeRegistry<T> registry, T defaultValue) {
		ResourceLocation key = new ResourceLocation(registryName);
		if (!registry.containsKey(key)) {
			if (!Loader.isModLoaded(key.getResourceDomain())) {
				//noinspection ConstantConditions - registered value does not have null registry name
				AncientWarfareCore.LOG.debug("Mod {} is not loaded. Replacing {} with {}", key::getResourceDomain, key::toString, () -> defaultValue.getRegistryName().toString());
				return defaultValue;
			}
			throw new MissingResourceException("Unable to find entry with registry name \"" + registryName + "\"",
					registry.getRegistrySuperType().getName(), registryName);
		}
		//noinspection ConstantConditions
		return registry.getValue(key);
	}
}
