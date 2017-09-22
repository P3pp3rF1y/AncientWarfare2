package net.shadowmage.ancientwarfare.core.util;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;

public class ModelLoaderHelper {

    private ModelLoaderHelper() {}

    public static void registerItem(Item item, String prefix) {
        registerItem(item, prefix, "inventory");
    }

    public static void registerItem(Item item, String prefix, boolean subItemsUseSameModel) {
        registerItem(item, prefix, "inventory", subItemsUseSameModel);
    }

    public static void registerItem(Block block, String prefix, String variant) {
        registerItem(Item.getItemFromBlock(block), prefix, variant);
    }

    public static void registerItem(Item item, String prefix, String variant) {
        registerItem(item, prefix, variant, false);
    }

    public static void registerItem(Item item, String prefix, String variant, boolean subItemsUseSameModel) {
        ResourceLocation registryName = item.getRegistryName();
        //TODO if we really mean the split of mods we should split the resources as well and use registryName.getResourceDomain() here
        String modelName = AncientWarfareCore.modID + ":" + (prefix.isEmpty() ? "" : prefix + "/") + registryName.getResourcePath();

        if (item.getHasSubtypes()) {
            NonNullList<ItemStack> subItems = NonNullList.create();
            item.getSubItems(item.getCreativeTab(), subItems);

            for(ItemStack subItem : subItems) {
                registerItem(item, subItem.getMetadata(), subItemsUseSameModel ? modelName : modelName + "_" + subItem.getMetadata() , variant);
            }
        } else {
            registerItem(item, 0, modelName, variant);
        }
    }

    public static void registerItem(Item item, int meta, String modelName, String variant) {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(modelName, variant));
    }
}
