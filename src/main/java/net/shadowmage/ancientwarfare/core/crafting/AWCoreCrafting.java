package net.shadowmage.ancientwarfare.core.crafting;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.shadowmage.ancientwarfare.core.api.AWBlocks;
import net.shadowmage.ancientwarfare.core.api.AWItems;

public class AWCoreCrafting {

    /**
     * load any recipes for CORE module (research book, engineering station, research station)
     */
    public static void loadRecipes() {
        RecipeSorter.register("ancientwarfare:researched", RecipeResearched.class, RecipeSorter.Category.SHAPED, "after:minecraft:shaped");
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AWItems.researchBook), "ILL", "PPP", "ILL", 'I', "ingotIron", 'L', Items.leather, 'P', Items.paper));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AWBlocks.engineeringStation), "IWI", "IPI", "ICI", 'I', "ingotIron", 'W', "plankWood", 'P', Blocks.crafting_table, 'C', Blocks.chest));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AWBlocks.researchStation), "IWI", "GPG", "ICI", 'I', "ingotIron", 'W', "plankWood", 'P', Blocks.crafting_table, 'C', Blocks.chest, 'G', "ingotGold"));

        AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWItems.automationHammerWood), "engineering",
                "_s_",
                "msm",
                "_s_",
                'm', Blocks.planks,
                's', Items.stick);

        AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWItems.automationHammerStone), "engineering",
                "_s_",
                "msm",
                "_s_",
                'm', Blocks.stone,
                's', Items.stick);

        AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWItems.automationHammerIron), "engineering",
                "_s_",
                "msm",
                "_s_",
                'm', Items.iron_ingot,
                's', Items.stick);

        AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWItems.automationHammerGold), "engineering",
                "_s_",
                "msm",
                "_s_",
                'm', Items.gold_ingot,
                's', Items.stick);

        AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWItems.automationHammerDiamond), "engineering",
                "_s_",
                "msm",
                "_s_",
                'm', Items.diamond,
                's', Items.stick);

        AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWItems.quillWood), "engineering",
                "__f",
                "_s_",
                "m__",
                'm', Blocks.planks,
                's', Items.stick,
                'f', Items.feather);

        AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWItems.quillStone), "engineering",
                "__f",
                "_s_",
                "m__",
                'm', Blocks.stone,
                's', Items.stick,
                'f', Items.feather);

        AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWItems.quillIron), "engineering",
                "__f",
                "_s_",
                "m__",
                'm', Items.iron_ingot,
                's', Items.stick,
                'f', Items.feather);

        AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWItems.quillGold), "engineering",
                "__f",
                "_s_",
                "m__",
                'm', Items.gold_ingot,
                's', Items.stick,
                'f', Items.feather);

        AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWItems.quillDiamond), "engineering",
                "__f",
                "_s_",
                "m__",
                'm', Items.diamond,
                's', Items.stick,
                'f', Items.feather);

        AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWItems.steel_ingot), "refining",
                "c",
                "i",
                'c', Items.coal,
                'i', Items.iron_ingot);

        AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWItems.backpack, 1, 0), "trade",
                "lwl",
                "www",
                "lwl",
                'l', Items.leather,
                'w', Blocks.wool);

        AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWItems.backpack, 1, 1), "trade",
                "lwl",
                "wcw",
                "lwl",
                'l', Items.leather,
                'w', Blocks.wool,
                'c', Blocks.chest);

        AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWItems.backpack, 1, 2), "trade",
                "lcl",
                "wcw",
                "lwl",
                'l', Items.leather,
                'w', Blocks.wool,
                'c', Blocks.chest);

        AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWItems.backpack, 1, 3), "trade",
                "lcl",
                "wcw",
                "lcl",
                'l', Items.leather,
                'w', Blocks.wool,
                'c', Blocks.chest);

    }

}
