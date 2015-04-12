package net.shadowmage.ancientwarfare.core.crafting;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.shadowmage.ancientwarfare.core.api.AWBlocks;
import net.shadowmage.ancientwarfare.core.api.AWItems;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;

public class AWCoreCrafting {

    /**
     * load any recipes for CORE module (research book, engineering station, research station)
     */
    public static void loadRecipes() {
        RecipeSorter.register("ancientwarfare:researched", RecipeResearched.class, RecipeSorter.Category.SHAPED, "after:minecraft:shaped");
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AWItems.researchBook), "ILL", "PPP", "ILL", 'I', "ingotIron", 'L', Items.leather, 'P', Items.paper));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AWBlocks.engineeringStation), "IWI", "IPI", "ICI", 'I', "ingotIron", 'W', "plankWood", 'P', Blocks.crafting_table, 'C', Blocks.chest));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AWBlocks.researchStation), "IWI", "GPG", "ICI", 'I', "ingotIron", 'W', "plankWood", 'P', Blocks.crafting_table, 'C', Blocks.chest, 'G', "ingotGold"));
        AWCraftingManager.INSTANCE.parseRecipes(AWCoreStatics.resourcePath + "research_crafts.csv");
    }
}
