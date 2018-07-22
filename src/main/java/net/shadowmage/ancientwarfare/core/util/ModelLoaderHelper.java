package net.shadowmage.ancientwarfare.core.util;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;

import java.util.function.Function;

@SideOnly(Side.CLIENT)
public class ModelLoaderHelper {

	private ModelLoaderHelper() {
	}

	public static void registerItem(Item item, String prefix) {
		registerItem(item, prefix, "inventory");
	}

	public static void registerItem(Item item, String prefix, boolean metaSuffix) {
		registerItem(item, prefix, metaSuffix, "inventory");
	}

	public static void registerItem(Block block, String prefix, String variant, boolean metaSuffix) {
		registerItem(Item.getItemFromBlock(block), prefix, metaSuffix, variant);
	}

	public static void registerItem(Block block, String prefix, String variant) {
		registerItem(Item.getItemFromBlock(block), prefix, variant);
	}

	public static void registerItem(Item item, String prefix, String variant) {
		registerItem(item, prefix, true, variant);
	}

	public static void registerItem(Item item, String prefix, boolean metaSuffix, String variant) {
		registerItem(item, prefix, metaSuffix, meta -> variant);
	}

	public static void registerItem(Item item, String prefix, boolean metaSuffix, Function<Integer, String> getVariant) {
		registerItem(item, (it, meta) -> {
			String modelName = AncientWarfareCore.MOD_ID + ":" + (prefix.isEmpty() ? "" : prefix + "/") + it.getRegistryName().getResourcePath();
			String suffix = it.getHasSubtypes() && metaSuffix ? "_" + meta : "";
			return new ModelResourceLocation(modelName + suffix, getVariant.apply(meta));
		});
	}

	public static void registerItem(Block block, ModelResourceLocation modelLocation) {
		registerItem(Item.getItemFromBlock(block), (i, m) -> modelLocation);
	}

	public static void registerItem(Item item, Function2<Item, Integer, ModelResourceLocation> getModelLocation) {
		if (item.getHasSubtypes()) {
			NonNullList<ItemStack> subItems = NonNullList.create();
			item.getSubItems(item.getCreativeTab(), subItems);

			for (ItemStack subItem : subItems) {
				ModelLoader.setCustomModelResourceLocation(item, subItem.getMetadata(), getModelLocation.apply(item, subItem.getMetadata()));
			}
		} else {
			ModelLoader.setCustomModelResourceLocation(item, 0, getModelLocation.apply(item, 0));
		}
	}

	public static void registerItem(Item item, int meta, String modelVariantName) {
		//TODO again hardcoded to just ancientwarfare mod name
		ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(AncientWarfareCore.MOD_ID + ":" + modelVariantName));
	}

	public static void registerItem(Item item, int meta, String modelName, String variant) {
		ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(modelName, variant));
	}

}
