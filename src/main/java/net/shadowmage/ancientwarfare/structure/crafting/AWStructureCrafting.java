package net.shadowmage.ancientwarfare.structure.crafting;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.api.AWBlocks;
import net.shadowmage.ancientwarfare.core.crafting.AWCraftingManager;
import net.shadowmage.ancientwarfare.core.crafting.RecipeResearched;


public class AWStructureCrafting
{

/**
 * load any recipes for automation module recipes
 */
public static void loadRecipes()
  {
  RecipeResearched recipe;
  recipe = AWCraftingManager.INSTANCE.addRecipe(new ItemStack(AWBlocks.draftingStation), new Object[]{
            "_p_",
            "_w_",
            "_s_",
            '_',Blocks.planks, 'p', Items.paper, 'w', Blocks.crafting_table, 's', Blocks.stone});
  recipe.addResearch("construction");    
  }

}
