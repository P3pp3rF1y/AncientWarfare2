package net.shadowmage.ancientwarfare.core.item;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraftforge.oredict.OreDictionary;
import net.shadowmage.ancientwarfare.core.api.AWItems;
import net.shadowmage.ancientwarfare.core.block.AWCoreBlockLoader;

import java.util.Locale;

public class AWCoreItemLoader {

    public static final String PREFIX = "ancientwarfare:core/";
    public static final AWCoreItemLoader INSTANCE = new AWCoreItemLoader();

    private AWCoreItemLoader() {
    }

    public void load() {
        AWItems.researchBook = register(new ItemResearchBook(), "research_book", PREFIX);

        AWItems.researchNote = register(new ItemResearchNotes(), "research_note", PREFIX);

        AWItems.backpack = register(new ItemBackpack(), "backpack", PREFIX);

        AWItems.automationHammerWood = new ItemHammer("wooden_hammer", ToolMaterial.WOOD);
        GameRegistry.registerItem(AWItems.automationHammerWood, "wooden_hammer");
        AWItems.automationHammerStone = new ItemHammer("stone_hammer", ToolMaterial.STONE);
        GameRegistry.registerItem(AWItems.automationHammerStone, "stone_hammer");
        AWItems.automationHammerIron = new ItemHammer("iron_hammer", ToolMaterial.IRON);
        GameRegistry.registerItem(AWItems.automationHammerIron, "iron_hammer");
        AWItems.automationHammerGold = new ItemHammer("gold_hammer", ToolMaterial.GOLD);
        GameRegistry.registerItem(AWItems.automationHammerGold, "gold_hammer");
        AWItems.automationHammerDiamond = new ItemHammer("diamond_hammer", ToolMaterial.EMERALD);
        GameRegistry.registerItem(AWItems.automationHammerDiamond, "diamond_hammer");

        AWItems.quillWood = new ItemQuill("wooden_quill", ToolMaterial.WOOD);
        GameRegistry.registerItem(AWItems.quillWood, "wooden_quill");
        AWItems.quillStone = new ItemQuill("stone_quill", ToolMaterial.STONE);
        GameRegistry.registerItem(AWItems.quillStone, "stone_quill");
        AWItems.quillIron = new ItemQuill("iron_quill", ToolMaterial.IRON);
        GameRegistry.registerItem(AWItems.quillIron, "iron_quill");
        AWItems.quillGold = new ItemQuill("gold_quill", ToolMaterial.GOLD);
        GameRegistry.registerItem(AWItems.quillGold, "gold_quill");
        AWItems.quillDiamond = new ItemQuill("diamond_quill", ToolMaterial.EMERALD);
        GameRegistry.registerItem(AWItems.quillDiamond, "diamond_quill");

        AWItems.componentItem = (ItemBase) register(new ItemComponent(), "component");

        AWItems.steel_ingot = register(new Item().setCreativeTab(AWCoreBlockLoader.coreTab), "steel_ingot", PREFIX);
        OreDictionary.registerOre("ingotSteel", AWItems.steel_ingot);
    }

    public Item register(Item item, String name) {
        item.setUnlocalizedName(name);
        GameRegistry.registerItem(item, name);
        return item;
    }

    public Item register(Item item, String name, String textPrefix) {
        item.setTextureName(textPrefix + name);
        return register(item, name);
    }

    public String getName(ToolMaterial material) {
        if (material == ToolMaterial.WOOD)
            return "wooden";
        else if (material == ToolMaterial.EMERALD)
            return "diamond";
        return material.toString().toLowerCase(Locale.ENGLISH);
    }
}
