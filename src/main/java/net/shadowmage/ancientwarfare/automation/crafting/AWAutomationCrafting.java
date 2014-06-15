package net.shadowmage.ancientwarfare.automation.crafting;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.automation.block.AWAutomationBlockLoader;
import net.shadowmage.ancientwarfare.core.api.AWItems;
import net.shadowmage.ancientwarfare.core.crafting.AWCraftingManager;
import net.shadowmage.ancientwarfare.core.crafting.RecipeResearched;
import net.shadowmage.ancientwarfare.core.item.ItemComponent;

public class AWAutomationCrafting
{

/**
 * load any recipes for automation module recipes
 */
public static void loadRecipes()
  {
  RecipeResearched recipe;

  ItemStack woodenGear = new ItemStack(AWItems.componentItem,1,ItemComponent.WOODEN_GEAR_SET);
  ItemStack ironGear = new ItemStack(AWItems.componentItem,1,ItemComponent.IRON_GEAR_SET);  
  ItemStack steelGear = new ItemStack(AWItems.componentItem,1,ItemComponent.STEEL_GEAR_SET);  
  ItemStack woodenBushing = new ItemStack(AWItems.componentItem,1,ItemComponent.WOODEN_BUSHINGS);  
  ItemStack ironBearing = new ItemStack(AWItems.componentItem,1,ItemComponent.IRON_BEARINGS);  
  ItemStack steelBearing = new ItemStack(AWItems.componentItem,1,ItemComponent.STEEL_BEARINGS);  
  ItemStack woodShaft = new ItemStack(AWItems.componentItem,1,ItemComponent.WOODEN_TORQUE_SHAFT);  
  ItemStack ironShaft = new ItemStack(AWItems.componentItem,1,ItemComponent.IRON_TORQUE_SHAFT);  
  ItemStack steelShaft = new ItemStack(AWItems.componentItem,1,ItemComponent.STEEL_TORQUE_SHAFT);  
  
  //wooden gear set
  recipe = AWCraftingManager.INSTANCE.addRecipe(woodenGear.copy(),
      "s_s",
      "_p_",
      "s_s",
      's', Items.stick,
      'p', Blocks.planks); 
  //iron gear
  recipe = AWCraftingManager.INSTANCE.addRecipe(ironGear.copy(),
      "i_i",
      "_i_",
      "i_i",
      'i', Items.iron_ingot);
  //steel gear
  recipe = AWCraftingManager.INSTANCE.addRecipe(steelGear.copy(),
      "i_i",
      "_i_",
      "i_i",
      'i', AWItems.steel_ingot);
  
  //wooden bushing set
  recipe = AWCraftingManager.INSTANCE.addRecipe(woodenBushing.copy(),
      "s_s",
      "___",
      "s_s",
      's', Items.stick,
      'p', Blocks.planks); 
  //iron bearing
  recipe = AWCraftingManager.INSTANCE.addRecipe(ironBearing.copy(),
      "_i_",
      "i_i",
      "_i_",
      'i', Items.iron_ingot);
  //steel bearing
  recipe = AWCraftingManager.INSTANCE.addRecipe(steelBearing.copy(),
      "_i_",
      "i_i",
      "_i_",
      'i', AWItems.steel_ingot);
  
  //wooden shaft
  recipe = AWCraftingManager.INSTANCE.addRecipe(woodShaft.copy(),
      "_p_",
      "_p_",
      "_p_",
      'p', Blocks.planks); 
  //iron shaft
  recipe = AWCraftingManager.INSTANCE.addRecipe(ironShaft.copy(),
      "_i_",
      "_i_",
      "_i_",
      'i', Items.iron_ingot);
  
  //steel shaft
  recipe = AWCraftingManager.INSTANCE.addRecipe(steelShaft.copy(),
      "_i_",
      "_i_",
      "_i_",
      'i', AWItems.steel_ingot);
    
    
  recipe = AWCraftingManager.INSTANCE.addRecipe(new ItemStack(AWAutomationBlockLoader.worksiteAutoCrafting), 
      "_c_",
      "gwg",
      "_i_",
      '_', Blocks.planks,
      'c', Blocks.chest,
      'w', Blocks.crafting_table,
      'i', Items.iron_ingot,
      'g', woodenGear.copy());
  recipe.addResearch("mass_production");
  
  recipe = AWCraftingManager.INSTANCE.addRecipe(new ItemStack(AWAutomationBlockLoader.worksiteCropFarm), 
      "___",
      "gwg",
      "_c_",
      '_', Blocks.planks,
      'w', Items.iron_hoe,
      'c', Blocks.chest,
      'g', woodenGear.copy());
  recipe.addResearch("farming");
  
  recipe = AWCraftingManager.INSTANCE.addRecipe(new ItemStack(AWAutomationBlockLoader.worksiteReedFarm), 
      "___",
      "gwg",
      "_c_",
      '_', Blocks.planks,
      'w', Items.iron_shovel,
      'c', Blocks.chest,
      'g', woodenGear.copy());
  recipe.addResearch("farming");
  
  recipe = AWCraftingManager.INSTANCE.addRecipe(new ItemStack(AWAutomationBlockLoader.worksiteMushroomFarm),
      "___",
      "gwg",
      "_c_",
      '_', Blocks.planks,
      'w', Items.wooden_shovel,
      'c', Blocks.chest,
      'g', woodenGear.copy());
  recipe.addResearch("farming");
  
  recipe = AWCraftingManager.INSTANCE.addRecipe(new ItemStack(AWAutomationBlockLoader.worksiteAnimalFarm),
      "___",
      "gwg",
      "_c_",
      '_', Blocks.planks,
      'w', Items.iron_sword,
      'c', Blocks.chest,
      'g', woodenGear.copy());
  recipe.addResearch("animal_husbandry");
  
  recipe = AWCraftingManager.INSTANCE.addRecipe(new ItemStack(AWAutomationBlockLoader.worksiteQuarry), 
      "___",
      "gwg",
      "_c_",
      '_', Blocks.planks,
      'w', Items.iron_pickaxe,
      'c', Blocks.chest,
      'g', woodenGear.copy());
  recipe.addResearch("mining");
  
  recipe = AWCraftingManager.INSTANCE.addRecipe(new ItemStack(AWAutomationBlockLoader.worksiteForestry),
      "___",
      "gwg",
      "_c_",
      '_', Blocks.planks,
      'w', Items.iron_axe,
      'c', Blocks.chest,
      'g', woodenGear.copy());
  recipe.addResearch("construction");
  
  recipe = AWCraftingManager.INSTANCE.addRecipe(new ItemStack(AWAutomationBlockLoader.worksiteFishFarm), 
      "___",
      "gwg",
      "_c_",
      '_', Blocks.planks,
      'w', Items.fishing_rod,
      'c', Blocks.chest,
      'g', woodenGear.copy());
  recipe.addResearch("fishing"); 
  
  recipe = AWCraftingManager.INSTANCE.addRecipe(new ItemStack(AWAutomationBlockLoader.worksiteWarehouse),
      "_p_",
      "_c_",
      "_c_",
      '_', Blocks.planks,
      'p', Items.paper,
      'c', Blocks.chest);
  recipe.addResearch("trade");
  
  //warehouse interface
  recipe = AWCraftingManager.INSTANCE.addRecipe(new ItemStack(AWAutomationBlockLoader.warehouseInterface),
      "_p_",
      "_c_",
      "___",
      '_', Blocks.planks,
      'p', Items.paper,
      'c', Blocks.chest);
  recipe.addResearch("trade");
  
  //warehouse crafting  
  recipe = AWCraftingManager.INSTANCE.addRecipe(new ItemStack(AWAutomationBlockLoader.warehouseCrafting),
      "_p_",
      "_w_",
      "_i_",
      '_', Blocks.planks,
      'p', Items.paper,
      'w', Blocks.crafting_table,
      'i', Items.iron_ingot);
  recipe.addResearch("trade");
  
  //warehouse small storage
  recipe = AWCraftingManager.INSTANCE.addRecipe(new ItemStack(AWAutomationBlockLoader.warehouseStorageBlock,1,0),
      "p_p",
      "_c_",
      "p_p",
      'p', Blocks.planks,
      'c', Blocks.chest);
  recipe.addResearch("trade");  
  
  //warehouse med storage
  recipe = AWCraftingManager.INSTANCE.addRecipe(new ItemStack(AWAutomationBlockLoader.warehouseStorageBlock,1,1),
      "pip",
      "_c_",
      "pip",
      'p', Blocks.planks,
      'c', Blocks.chest,
      'i', Items.iron_ingot);
  recipe.addResearch("trade");
  
  //warehouse large storage
  recipe = AWCraftingManager.INSTANCE.addRecipe(new ItemStack(AWAutomationBlockLoader.warehouseStorageBlock,1,2),
      "pip",
      "ici",
      "pip",
      'p', Blocks.planks,
      'c', Blocks.chest,
      'i', Items.iron_ingot);
  recipe.addResearch("trade");
  
  //warehouse stock-viewer
  recipe = AWCraftingManager.INSTANCE.addRecipe(new ItemStack(AWAutomationBlockLoader.warehouseStockViewer),
      "_p_",
      "_s_",
      's', Items.sign,
      'p', Items.paper);
  recipe.addResearch("trade");
    
  //mailbox
  recipe = AWCraftingManager.INSTANCE.addRecipe(new ItemStack(AWAutomationBlockLoader.mailbox),
      "ici",
      "i_i",
      "ici",
      'i', Items.iron_ingot,
      'c', Blocks.chest);
  recipe.addResearch("navigation");
  
  //chunkloader simple
  recipe = AWCraftingManager.INSTANCE.addRecipe(new ItemStack(AWAutomationBlockLoader.chunkLoaderSimple),
      "bbb",
      "beb",
      "bbb",
      'b', Blocks.stonebrick,
      'e', Items.ender_pearl);//TODO research?
  
  //chunkloader deluxe
  recipe = AWCraftingManager.INSTANCE.addRecipe(new ItemStack(AWAutomationBlockLoader.chunkLoaderDeluxe),
      "bbb",
      "beb",
      "bbb",
      'b', Blocks.obsidian,
      'e', Items.ender_pearl);//TODO research?
    
  //torque conduit s/m/l
  //torque distributor s/m/l
  //torque flywheel s/m/l
  //torque generator hand
  //torque generator waterwheel
  //torque generator sterling     
  }


}
