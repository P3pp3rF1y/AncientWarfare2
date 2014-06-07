package net.shadowmage.ancientwarfare.core.crafting;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.shadowmage.ancientwarfare.core.api.AWBlocks;
import net.shadowmage.ancientwarfare.core.api.AWItems;

public class AWCoreCrafting
{

/**
 * load any recipes for CORE module (research book, engineering station, research station)
 */
public static void loadRecipes()
  {
  CraftingManager.getInstance().addRecipe(new ItemStack(AWItems.researchBook), new Object[]{"ILL", "PPP", "ILL", 'I', Items.iron_ingot, 'L', Items.leather, 'P', Items.paper});
  CraftingManager.getInstance().addRecipe(new ItemStack(AWBlocks.engineeringStation), new Object[]{"IWI", "IPI", "ICI", 'I', Items.iron_ingot, 'W', Blocks.planks, 'P', Blocks.crafting_table, 'C', Blocks.chest});
  CraftingManager.getInstance().addRecipe(new ItemStack(AWBlocks.researchStation), new Object[]{"IWI", "GPG", "ICI", 'I', Items.iron_ingot, 'W', Blocks.planks, 'P', Blocks.crafting_table, 'C', Blocks.chest, 'G', Items.gold_ingot});
    
  }

}
