package net.shadowmage.ancientwarfare.automation.crafting;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.automation.block.AWAutomationBlockLoader;
import net.shadowmage.ancientwarfare.automation.item.AWAutomationItemLoader;
import net.shadowmage.ancientwarfare.core.api.AWItems;
import net.shadowmage.ancientwarfare.core.crafting.AWCraftingManager;
import net.shadowmage.ancientwarfare.core.crafting.RecipeResearched;
import net.shadowmage.ancientwarfare.npc.item.AWNpcItemLoader;

public class AWAutomationCrafting
{

/**
 * load any recipes for automation module recipes
 */
public static void loadRecipes()
  {
  RecipeResearched recipe;
  
  recipe = AWCraftingManager.INSTANCE.addRecipe(new ItemStack(AWAutomationBlockLoader.worksiteAutoCrafting), 
      "_c_",
      "_w_",
      "_i_",
      '_', Blocks.planks,
      'c', Blocks.chest,
      'w', Blocks.crafting_table,
      'i', Items.iron_ingot);
  recipe.addResearch("mass_production");
  
  recipe = AWCraftingManager.INSTANCE.addRecipe(new ItemStack(AWAutomationBlockLoader.worksiteCropFarm), 
      "___",
      "_w_",
      "_c_",
      '_', Blocks.planks,
      'w', Items.iron_hoe,
      'c', Blocks.chest);
  recipe.addResearch("farming");
  
  recipe = AWCraftingManager.INSTANCE.addRecipe(new ItemStack(AWAutomationBlockLoader.worksiteReedFarm), 
      "___",
      "_w_",
      "_c_",
      '_', Blocks.planks,
      'w', Items.iron_shovel,
      'c', Blocks.chest);
  recipe.addResearch("farming");
  
  recipe = AWCraftingManager.INSTANCE.addRecipe(new ItemStack(AWAutomationBlockLoader.worksiteMushroomFarm),
      "___",
      "_w_",
      "_c_",
      '_', Blocks.planks,
      'w', Items.wooden_shovel,
      'c', Blocks.chest);
  recipe.addResearch("farming");
  
  recipe = AWCraftingManager.INSTANCE.addRecipe(new ItemStack(AWAutomationBlockLoader.worksiteAnimalFarm),
      "___",
      "_w_",
      "_c_",
      '_', Blocks.planks,
      'w', Items.iron_sword,
      'c', Blocks.chest);
  recipe.addResearch("animal_husbandry");
  
  recipe = AWCraftingManager.INSTANCE.addRecipe(new ItemStack(AWAutomationBlockLoader.worksiteQuarry), 
      "___",
      "_w_",
      "_c_",
      '_', Blocks.planks,
      'w', Items.iron_pickaxe,
      'c', Blocks.chest);
  recipe.addResearch("mining");
  
  recipe = AWCraftingManager.INSTANCE.addRecipe(new ItemStack(AWAutomationBlockLoader.worksiteForestry),
      "___",
      "_w_",
      "_c_",
      '_', Blocks.planks,
      'w', Items.iron_axe,
      'c', Blocks.chest);
  recipe.addResearch("construction");
  
  recipe = AWCraftingManager.INSTANCE.addRecipe(new ItemStack(AWAutomationBlockLoader.worksiteFishFarm), 
      "___",
      "_w_",
      "_c_",
      '_', Blocks.planks,
      'w', Items.fishing_rod,
      'c', Blocks.chest);
  recipe.addResearch("fishing"); 
  
  recipe = AWCraftingManager.INSTANCE.addRecipe(new ItemStack(AWAutomationBlockLoader.worksiteWarehouse),
      "_p_",
      "_c_",
      "_c_",
      '_', Blocks.planks,
      'p', Items.paper,
      'c', Blocks.chest);
  recipe.addResearch("trade");
  
    
  //warehouse crafting
  //warehouse interface
  //warehouse small storage
  //warehouse med storage
  //warehouse large storage  
  
  //mailbox
  
  //chunkloader simple
  //chunkloader deluxe
  
  //torque conduit
  //torque distributor
  //torque flywheel
  //torque generator sterling
  //torque generator waterwheel
  //torque generator hand
  
  
  }


}
