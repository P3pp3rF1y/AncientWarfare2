package net.shadowmage.ancientwarfare.core.crafting;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.shadowmage.ancientwarfare.automation.block.AWAutomationBlockLoader;
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
  
  RecipeResearched recipe;
  recipe = AWCraftingManager.INSTANCE.addRecipe(new ItemStack(AWItems.automationHammerWood), 
      "_s_",
      "msm",
      "_s_",
      'm', Blocks.planks,
      's', Items.stick);
  recipe.addResearch("engineering");
  recipe = AWCraftingManager.INSTANCE.addRecipe(new ItemStack(AWItems.automationHammerStone), 
      "_s_",
      "msm",
      "_s_",
      'm', Blocks.stone,
      's', Items.stick);
  recipe.addResearch("engineering");
  recipe = AWCraftingManager.INSTANCE.addRecipe(new ItemStack(AWItems.automationHammerIron), 
      "_s_",
      "msm",
      "_s_",
      'm', Items.iron_ingot,
      's', Items.stick);
  recipe.addResearch("engineering");
  recipe = AWCraftingManager.INSTANCE.addRecipe(new ItemStack(AWItems.automationHammerGold), 
      "_s_",
      "msm",
      "_s_",
      'm', Items.gold_ingot,
      's', Items.stick);
  recipe.addResearch("engineering");
  recipe = AWCraftingManager.INSTANCE.addRecipe(new ItemStack(AWItems.automationHammerDiamond), 
      "_s_",
      "msm",
      "_s_",
      'm', Items.diamond,
      's', Items.stick);
  recipe.addResearch("engineering");
  
  
  //hammer (engineering)
  // .s.
  // msm  
  // .s.
  
  //quill
  // ..f
  // .s.
  // m..
  

  recipe = AWCraftingManager.INSTANCE.addRecipe(new ItemStack(AWItems.quillWood), 
      "__f",
      "_s_",
      "m__",
      'm', Blocks.planks,
      's', Items.stick,
      'f', Items.feather);
  recipe.addResearch("engineering");
  recipe = AWCraftingManager.INSTANCE.addRecipe(new ItemStack(AWItems.quillStone), 
      "__f",
      "_s_",
      "m__",
      'm', Blocks.stone,
      's', Items.stick,
      'f', Items.feather);
  recipe.addResearch("engineering");
  recipe = AWCraftingManager.INSTANCE.addRecipe(new ItemStack(AWItems.quillIron), 
      "__f",
      "_s_",
      "m__",
      'm', Items.iron_ingot,
      's', Items.stick,
      'f', Items.feather);
  recipe.addResearch("engineering");
  recipe = AWCraftingManager.INSTANCE.addRecipe(new ItemStack(AWItems.quillGold), 
      "__f",
      "_s_",
      "m__",
      'm', Items.gold_ingot,
      's', Items.stick,
      'f', Items.feather);
  recipe.addResearch("engineering");
  recipe = AWCraftingManager.INSTANCE.addRecipe(new ItemStack(AWItems.quillDiamond), 
      "__f",
      "_s_",
      "m__",
      'm', Items.diamond,
      's', Items.stick,
      'f', Items.feather);
  recipe.addResearch("engineering");
  
  recipe = AWCraftingManager.INSTANCE.addRecipe(new ItemStack(AWItems.steel_ingot), 
      "c",
      "i",
      'c', Items.coal,
      'i', Items.iron_ingot);
  recipe.addResearch("refining");
  
  
  }

}
