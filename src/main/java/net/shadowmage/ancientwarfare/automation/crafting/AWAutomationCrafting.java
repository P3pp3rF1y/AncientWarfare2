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
  recipe = AWCraftingManager.INSTANCE.createRecipe(woodenGear.copy(),
      "s_s",
      "_p_",
      "s_s",
      's', Items.stick,
      'p', Blocks.planks); 
  //iron gear
  recipe = AWCraftingManager.INSTANCE.createRecipe(ironGear.copy(),
      "i_i",
      "_i_",
      "i_i",
      'i', Items.iron_ingot);
  //steel gear
  recipe = AWCraftingManager.INSTANCE.createRecipe(steelGear.copy(),
      "i_i",
      "_i_",
      "i_i",
      'i', AWItems.steel_ingot);
  
  //wooden bushing set
  recipe = AWCraftingManager.INSTANCE.createRecipe(woodenBushing.copy(),
      "s_s",
      "___",
      "s_s",
      's', Items.stick,
      'p', Blocks.planks); 
  //iron bearing
  recipe = AWCraftingManager.INSTANCE.createRecipe(ironBearing.copy(),
      "_i_",
      "i_i",
      "_i_",
      'i', Items.iron_ingot);
  //steel bearing
  recipe = AWCraftingManager.INSTANCE.createRecipe(steelBearing.copy(),
      "_i_",
      "i_i",
      "_i_",
      'i', AWItems.steel_ingot);
  
  //wooden shaft
  recipe = AWCraftingManager.INSTANCE.createRecipe(woodShaft.copy(),
      "_p_",
      "_p_",
      "_p_",
      'p', Blocks.planks); 
  //iron shaft
  recipe = AWCraftingManager.INSTANCE.createRecipe(ironShaft.copy(),
      "_i_",
      "_i_",
      "_i_",
      'i', Items.iron_ingot);  
  //steel shaft
  recipe = AWCraftingManager.INSTANCE.createRecipe(steelShaft.copy(),
      "_i_",
      "_i_",
      "_i_",
      'i', AWItems.steel_ingot);    
    
  recipe = AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWAutomationBlockLoader.worksiteAutoCrafting), "mass_production",
      "_c_",
      "gwg",
      "_i_",
      '_', Blocks.planks,
      'c', Blocks.chest,
      'w', Blocks.crafting_table,
      'i', Items.iron_ingot,
      'g', woodenGear.copy());
  
  recipe = AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWAutomationBlockLoader.worksiteCropFarm), "farming", 
      "___",
      "gwg",
      "_c_",
      '_', Blocks.planks,
      'w', Items.iron_hoe,
      'c', Blocks.chest,
      'g', woodenGear.copy());
  
  recipe = AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWAutomationBlockLoader.worksiteReedFarm), "farming", 
      "___",
      "gwg",
      "_c_",
      '_', Blocks.planks,
      'w', Items.iron_shovel,
      'c', Blocks.chest,
      'g', woodenGear.copy());
  
  recipe = AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWAutomationBlockLoader.worksiteMushroomFarm),"farming", 
      "___",
      "gwg",
      "_c_",
      '_', Blocks.planks,
      'w', Items.wooden_shovel,
      'c', Blocks.chest,
      'g', woodenGear.copy());
  
  recipe = AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWAutomationBlockLoader.worksiteAnimalFarm), "animal_husbandry",
      "___",
      "gwg",
      "_c_",
      '_', Blocks.planks,
      'w', Items.iron_sword,
      'c', Blocks.chest,
      'g', woodenGear.copy());
  
  recipe = AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWAutomationBlockLoader.worksiteQuarry), "mining",
      "___",
      "gwg",
      "_c_",
      '_', Blocks.planks,
      'w', Items.iron_pickaxe,
      'c', Blocks.chest,
      'g', woodenGear.copy());
  
  recipe = AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWAutomationBlockLoader.worksiteForestry), "construction",
      "___",
      "gwg",
      "_c_",
      '_', Blocks.planks,
      'w', Items.iron_axe,
      'c', Blocks.chest,
      'g', woodenGear.copy());
  
  recipe = AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWAutomationBlockLoader.worksiteFishFarm), "fishing",
      "___",
      "gwg",
      "_c_",
      '_', Blocks.planks,
      'w', Items.fishing_rod,
      'c', Blocks.chest,
      'g', woodenGear.copy());
  
  recipe = AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWAutomationBlockLoader.worksiteWarehouse), "trade",
      "_p_",
      "_c_",
      "_c_",
      '_', Blocks.planks,
      'p', Items.paper,
      'c', Blocks.chest);
  
  //warehouse interface
  recipe = AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWAutomationBlockLoader.warehouseInterface), "trade",
      "_p_",
      "_c_",
      "___",
      '_', Blocks.planks,
      'p', Items.paper,
      'c', Blocks.chest);
  
  //warehouse crafting  
  recipe = AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWAutomationBlockLoader.warehouseCrafting), "trade",
      "_p_",
      "_w_",
      "_i_",
      '_', Blocks.planks,
      'p', Items.paper,
      'w', Blocks.crafting_table,
      'i', Items.iron_ingot);
  
  //warehouse small storage
  recipe = AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWAutomationBlockLoader.warehouseStorageBlock,1,0), "trade",
      "p_p",
      "_c_",
      "p_p",
      'p', Blocks.planks,
      'c', Blocks.chest);
  
  //warehouse med storage
  recipe = AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWAutomationBlockLoader.warehouseStorageBlock,1,1), "trade",
      "pip",
      "_c_",
      "pip",
      'p', Blocks.planks,
      'c', Blocks.chest,
      'i', Items.iron_ingot);
  
  //warehouse large storage
  recipe = AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWAutomationBlockLoader.warehouseStorageBlock,1,2), "trade",
      "pip",
      "ici",
      "pip",
      'p', Blocks.planks,
      'c', Blocks.chest,
      'i', Items.iron_ingot);
  
  //warehouse stock-viewer
  recipe = AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWAutomationBlockLoader.warehouseStockViewer), "trade",
      "p",
      "s",
      's', Items.sign,
      'p', Items.paper);
    
  //mailbox
  recipe = AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWAutomationBlockLoader.mailbox), "navigation",
      "ici",
      "i_i",
      "ici",
      'i', Items.iron_ingot,
      'c', Blocks.chest);
  
  //chunkloader simple
  recipe = AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWAutomationBlockLoader.chunkLoaderSimple),
      "bbb",
      "beb",
      "bbb",
      'b', Blocks.stonebrick,
      'e', Items.ender_pearl);//TODO research?
  
  //chunkloader deluxe
  recipe = AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWAutomationBlockLoader.chunkLoaderDeluxe),
      "bbb",
      "beb",
      "bbb",
      'b', Blocks.obsidian,
      'e', Items.ender_pearl);//TODO research?
    
  //torque conduit s
  recipe = AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWAutomationBlockLoader.torqueConduit,1,0), "the_wheel",
      "s",
      "g",
      "s",
      's', woodShaft.copy(),
      'g', woodenGear.copy());
  
//torque conduit m
  recipe = AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWAutomationBlockLoader.torqueConduit,1,1), "the_wheel",
      "s",
      "g",
      "s",
      's', ironShaft.copy(),
      'g', ironGear.copy());
  
//torque conduit l
  recipe = AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWAutomationBlockLoader.torqueConduit,1,2), new String[]{"the_wheel", "refining"},
      "s",
      "g",
      "s",
      's', ironShaft.copy(),
      'g', ironGear.copy());
    
  //torque distributor s
  recipe = AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWAutomationBlockLoader.torqueDistributor,1,0), "the_wheel",
      "_s_",
      "ggg",
      "_s_",
      's', woodShaft.copy(),
      'g', woodenGear.copy());
  
  //torque distributor m
  recipe = AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWAutomationBlockLoader.torqueDistributor,1,1), "the_wheel",
      "_s_",
      "ggg",
      "_s_",
      's', ironShaft.copy(),
      'g', ironGear.copy());
  
  //torque distributor l
  recipe = AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWAutomationBlockLoader.torqueDistributor,1,2), new String[]{"the_wheel", "refining"},
      "_s_",
      "ggg",
      "_s_",
      's', ironShaft.copy(),
      'g', ironGear.copy());
  
  //torque flywheel s
  recipe = AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWAutomationBlockLoader.flywheel,1,0), "theory_of_gravity",
      "pgp",
      "ppp",
      "pgp",
      'p', Blocks.planks,
      'g', woodenGear.copy());
  
  //torque flywheel m
  recipe = AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWAutomationBlockLoader.flywheel,1,1), "theory_of_gravity",
      "igi",
      "iii",
      "igi",
      'i', Items.iron_ingot,
      'g', ironGear.copy());
  
  //torque flywheel l
  recipe = AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWAutomationBlockLoader.flywheel,1,2), new String[]{"theory_of_gravity", "refining"},
      "igi",
      "iii",
      "igi",
      'i', AWItems.steel_ingot,
      'g', steelGear.copy());
    
  //torque generator hand
  recipe = AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWAutomationBlockLoader.handCrankedEngine), "the_wheel",
      "igi",
      "gig",
      "iii",
      'i', Items.iron_ingot,
      'g', ironGear.copy());
  
  //torque generator waterwheel
  recipe = AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWAutomationBlockLoader.torqueGeneratorWaterwheel), "theory_of_gravity",
      "igi",
      "gwg",
      "iwi",
      'i', Blocks.planks,
      'g', ironGear.copy(),
      'w', woodenGear.copy());
    
  //torque generator sterling   
  recipe = AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWAutomationBlockLoader.torqueGeneratorSterling), "machinery",
      "iii",
      "ggg",
      "igi",
      'i', Items.iron_ingot,
      'g', ironGear.copy());
  }


}
