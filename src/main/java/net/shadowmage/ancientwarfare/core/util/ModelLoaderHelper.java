package net.shadowmage.ancientwarfare.core.util;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.model.ModelLoader;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;

import java.util.function.Function;

public class ModelLoaderHelper {

    private ModelLoaderHelper() {}

    public static void registerItem(Item item, String prefix) {
        registerItem(item, prefix, "inventory");
    }

    public static void registerItem(Item item, String prefix, boolean subItemsUseSameModel) {
        registerItem(item, prefix, "inventory", subItemsUseSameModel);
    }

    public static void registerItem(Block block, String prefix, String variant, boolean subItemsUseSameModel) {
        registerItem(Item.getItemFromBlock(block), prefix, variant, subItemsUseSameModel);
    }

    public static void registerItem(Block block, String prefix, String variant) {
        registerItem(Item.getItemFromBlock(block), prefix, variant);
    }

    public static void registerItem(Item item, String prefix, String variant) {
        registerItem(item, prefix, variant, false);
    }

    public static void registerItem(Item item, String prefix, String variant, boolean subItemsUseSameModel) {
        registerItem(item, prefix, true, meta -> variant, subItemsUseSameModel);
    }

    public static void registerItem(Item item, String prefix, boolean metaSuffix, Function<Integer, String> getVariant, boolean subItemsUseSameModel) {
        registerItem(item, (it, meta) -> {
			String modelName = AncientWarfareCore.modID + ":" + (prefix.isEmpty() ? "" : prefix + "/") + it.getRegistryName().getResourcePath();
			String suffix = it.getHasSubtypes() && !subItemsUseSameModel && metaSuffix ? "_" + meta : "";
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

            for(ItemStack subItem : subItems) {
                ModelLoader.setCustomModelResourceLocation(item, subItem.getMetadata(), getModelLocation.apply(item, subItem.getMetadata()));
            }
        } else {
            ModelLoader.setCustomModelResourceLocation(item, 0, getModelLocation.apply(item, 0));
        }
    }

    public static void registerItem(Item item, int meta, String modelVariantName) {
        //TODO again hardcoded to just ancientwarfare mod name
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(AncientWarfareCore.modID + ":" + modelVariantName));
    }

    public static void registerItem(Item item, int meta, String modelName, String variant) {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(modelName, variant));
    }

    private interface Function2<T1, T2, R> {
        R apply(T1 paramOne, T2 paramTwo);
    }
}
