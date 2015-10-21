package net.shadowmage.ancientwarfare.automation.crafting;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.automation.block.AWAutomationBlockLoader;
import net.shadowmage.ancientwarfare.core.api.AWItems;
import net.shadowmage.ancientwarfare.core.crafting.AWCraftingManager;
import net.shadowmage.ancientwarfare.core.upgrade.WorksiteUpgrade;

public class AWAutomationCrafting {

    /**
     * load any recipes for automation module recipes
     */
    public static void loadRecipes() {

        ItemStack upgradeBoundsMed = AWItems.worksiteUpgrade.getSubItem(WorksiteUpgrade.SIZE_MEDIUM.ordinal());//engineering
        ItemStack upgradeBoundsLarge = AWItems.worksiteUpgrade.getSubItem(WorksiteUpgrade.SIZE_LARGE.ordinal());//mathematics
        ItemStack upgradeQuarryMed = AWItems.worksiteUpgrade.getSubItem(WorksiteUpgrade.QUARRY_MEDIUM.ordinal());//mining
        ItemStack upgradeQuarryLarge = AWItems.worksiteUpgrade.getSubItem(WorksiteUpgrade.QUARRY_LARGE.ordinal());//machinery
        ItemStack upgradeEnchantedTools1 = AWItems.worksiteUpgrade.getSubItem(WorksiteUpgrade.ENCHANTED_TOOLS_1.ordinal());//mathematics
        ItemStack upgradeEnchantedTools2 = AWItems.worksiteUpgrade.getSubItem(WorksiteUpgrade.ENCHANTED_TOOLS_2.ordinal());//mass production
        ItemStack upgradeQualityTools1 = AWItems.worksiteUpgrade.getSubItem(WorksiteUpgrade.TOOL_QUALITY_1.ordinal());//engineering
        ItemStack upgradeQualityTools2 = AWItems.worksiteUpgrade.getSubItem(WorksiteUpgrade.TOOL_QUALITY_2.ordinal());//construction
        ItemStack upgradeQualityTools3 = AWItems.worksiteUpgrade.getSubItem(WorksiteUpgrade.TOOL_QUALITY_3.ordinal());//refining
        ItemStack upgradeBasicChunkloader = AWItems.worksiteUpgrade.getSubItem(WorksiteUpgrade.BASIC_CHUNK_LOADER.ordinal());//mathematics
        ItemStack upgradeQuarryChunkloader = AWItems.worksiteUpgrade.getSubItem(WorksiteUpgrade.QUARRY_CHUNK_LOADER.ordinal());//machinery

        AWCraftingManager.INSTANCE.createRecipe(upgradeBoundsMed, "engineering",
                " s ",
                "fff",
                " s ",
                's', Items.string,
                'f', Blocks.fence);

        AWCraftingManager.INSTANCE.createRecipe(upgradeBoundsLarge, "mathematics",
                "fff",
                "sss",
                "fff",
                's', Items.string,
                'f', Blocks.fence);

        AWCraftingManager.INSTANCE.createRecipe(upgradeQuarryMed, "mining",
                "ppp",
                "f f",
                "f f",
                'p', "plankWood",
                'f', Blocks.fence);

        AWCraftingManager.INSTANCE.createRecipe(upgradeQuarryLarge, "machinery",
                "ppp",
                "fif",
                "fif",
                'p', "plankWood",
                'f', Blocks.fence,
                'i', "ingotIron");

        ItemStack enchantedBook = new ItemStack(Items.enchanted_book);
        enchantedBook.addEnchantment(Enchantment.fortune, 1);

        AWCraftingManager.INSTANCE.createRecipe(upgradeEnchantedTools1, "mathematics",
                " b ",
                "iii",
                'b', enchantedBook,
                'i', "ingotIron");

        enchantedBook = new ItemStack(Items.enchanted_book);
        enchantedBook.addEnchantment(Enchantment.fortune, 2);

        AWCraftingManager.INSTANCE.createRecipe(upgradeEnchantedTools2, "mass_production",
                "b b",
                "iii",
                'b', enchantedBook,
                'i', "ingotIron");

        AWCraftingManager.INSTANCE.createRecipe(upgradeQualityTools1, "engineering",
                "psa",
                'p', Items.iron_pickaxe,
                's', Items.iron_shovel,
                'a', Items.iron_axe);

        AWCraftingManager.INSTANCE.createRecipe(upgradeQualityTools2, "construction",
                "psa",
                'p', Items.diamond_pickaxe,
                's', Items.diamond_shovel,
                'a', Items.diamond_axe);

        AWCraftingManager.INSTANCE.createRecipe(upgradeQualityTools3, "refining",
                "oto",
                "psa",
                "oto",
                't', "ingotSteel",
                'o', Blocks.obsidian,
                'p', Items.diamond_pickaxe,
                's', Items.diamond_shovel,
                'a', Items.diamond_axe);

        AWCraftingManager.INSTANCE.createRecipe(upgradeBasicChunkloader, "mathematics",
                " i ",
                "ici",
                " i ",
                'i', "ingotIron",
                'c', AWAutomationBlockLoader.chunkLoaderSimple);

        AWCraftingManager.INSTANCE.createRecipe(upgradeQuarryChunkloader, "machinery",
                " i ",
                "ici",
                " i ",
                'i', "ingotIron",
                'c', AWAutomationBlockLoader.chunkLoaderDeluxe);

    }


}
