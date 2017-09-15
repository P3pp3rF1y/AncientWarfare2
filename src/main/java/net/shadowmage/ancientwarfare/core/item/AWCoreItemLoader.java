package net.shadowmage.ancientwarfare.core.item;

import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.api.AWItems;
import net.shadowmage.ancientwarfare.core.block.AWCoreBlockLoader;

import java.util.Locale;

@Mod.EventBusSubscriber(modid = AncientWarfareCore.modID)
public class AWCoreItemLoader {

    public static final String PREFIX = "ancientwarfare:core/";
    public static final AWCoreItemLoader INSTANCE = new AWCoreItemLoader();

    private AWCoreItemLoader() {
    }

    public void load() {
        OreDictionary.registerOre("ingotSteel", AWItems.steel_ingot);
    }

    @SubscribeEvent
    public static void register(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();

        registry.register(new ItemResearchBook());

        registry.register(new ItemResearchNotes());

        registry.register(new ItemBackpack());

        registry.register(new ItemHammer("wooden_hammer", ToolMaterial.WOOD));
        registry.register(new ItemHammer("stone_hammer", ToolMaterial.STONE));
        registry.register(new ItemHammer("iron_hammer", ToolMaterial.IRON));
        registry.register(new ItemHammer("gold_hammer", ToolMaterial.GOLD));
        registry.register(new ItemHammer("diamond_hammer", ToolMaterial.DIAMOND));

        registry.register(new ItemQuill("wooden_quill", ToolMaterial.WOOD));
        registry.register(new ItemQuill("stone_quill", ToolMaterial.STONE));
        registry.register(new ItemQuill("iron_quill", ToolMaterial.IRON));
        registry.register(new ItemQuill("gold_quill", ToolMaterial.GOLD));
        registry.register(new ItemQuill("diamond_quill", ToolMaterial.DIAMOND));

        registry.register(new ItemComponent());

        registry.register(new Item()
                .setCreativeTab(AWCoreBlockLoader.coreTab)
                .setUnlocalizedName("steel_ingot")
                .setRegistryName(new ResourceLocation(AncientWarfareCore.modID, "steel_ingot")));
    }

//    public Item register(Item item, String name, String textPrefix) {
//        item.setTextureName(textPrefix + name);
//        return register(item, name);
//    }

    public String getName(ToolMaterial material) {
        if (material == ToolMaterial.WOOD)
            return "wooden";
        else if (material == ToolMaterial.DIAMOND)
            return "diamond";
        return material.toString().toLowerCase(Locale.ENGLISH);
    }
}
