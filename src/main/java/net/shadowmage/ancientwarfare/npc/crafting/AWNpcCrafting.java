package net.shadowmage.ancientwarfare.npc.crafting;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.api.AWItems;
import net.shadowmage.ancientwarfare.core.crafting.AWCraftingManager;
import net.shadowmage.ancientwarfare.core.crafting.RecipeResearched;
import net.shadowmage.ancientwarfare.core.item.ItemComponent;
import net.shadowmage.ancientwarfare.npc.item.AWNpcItemLoader;
import net.shadowmage.ancientwarfare.npc.item.ItemNpcSpawner;


public class AWNpcCrafting
{

/**
 * load any recipes for automation module recipes
 */
@SuppressWarnings("unchecked")
public static void loadRecipes()
  {
  CraftingManager.getInstance().getRecipeList().add(new OrderCopyingRecipe(AWNpcItemLoader.upkeepOrder));
  CraftingManager.getInstance().getRecipeList().add(new OrderCopyingRecipe(AWNpcItemLoader.routingOrder));
  CraftingManager.getInstance().getRecipeList().add(new OrderCopyingRecipe(AWNpcItemLoader.combatOrder));
  CraftingManager.getInstance().getRecipeList().add(new OrderCopyingRecipe(AWNpcItemLoader.workOrder));
  
  
  ItemStack foodBundle = new ItemStack(AWItems.componentItem, 1, ItemComponent.NPC_FOOD_BUNDLE); 
  
  RecipeResearched recipe;
  
  //food bundle
  recipe = AWCraftingManager.INSTANCE.addRecipe(foodBundle.copy(), 
      "ap",
      "bb",
      "wc",
      'a', Items.apple,
      'b', Items.bread,
      'w', new ItemStack(Items.potionitem),
      'p', Items.cooked_porkchop,
      'b', Items.cooked_beef,
      'c', Items.cooked_chicken);
  recipe.addResearch("leadership");
  
  //worker spawner
  recipe = AWCraftingManager.INSTANCE.addRecipe(ItemNpcSpawner.getStackForNpcType("worker", ""), 
      "gf",
      "gt",
      'f', foodBundle.copy(),
      't', Items.wooden_pickaxe,
      'g', Items.gold_ingot);
  recipe.addResearch("leadership");
  //combat spawner
  recipe = AWCraftingManager.INSTANCE.addRecipe(ItemNpcSpawner.getStackForNpcType("combat", ""), 
      "gf",
      "gt",
      'f', foodBundle.copy(),
      't', Items.wooden_sword,
      'g', Items.gold_ingot);
  recipe.addResearch("conscription");
  //courier bundle
  recipe = AWCraftingManager.INSTANCE.addRecipe(ItemNpcSpawner.getStackForNpcType("courier", ""), 
      "gf",
      "gt",
      'f', foodBundle.copy(),
      't', Blocks.wool,
      'g', Items.gold_ingot);
  recipe.addResearch("trade");
  //trader spawner
  recipe = AWCraftingManager.INSTANCE.addRecipe(ItemNpcSpawner.getStackForNpcType("trader", ""), 
      "gf_",
      "gtb",
      'f', foodBundle.copy(),
      't', Blocks.wool,
      'g', Items.gold_ingot,
      'b', Items.book);
  recipe.addResearch("trade");
  //priest spawner
  recipe = AWCraftingManager.INSTANCE.addRecipe(ItemNpcSpawner.getStackForNpcType("priest", ""), 
      "gf",
      "gb",
      'f', foodBundle.copy(),
      'g', Items.gold_ingot,
      'b', Items.book);
  recipe.addResearch("leadership");
  //bard spawner
  recipe = AWCraftingManager.INSTANCE.addRecipe(ItemNpcSpawner.getStackForNpcType("bard", ""), 
      "gf",
      "gb",
      'f', foodBundle.copy(),
      'g', Items.gold_ingot,
      'b', new ItemStack(AWNpcItemLoader.bardInstrument,1,0));
  recipe.addResearch("leadership");
    
  //command batons
  recipe = AWCraftingManager.INSTANCE.addRecipe(new ItemStack(AWNpcItemLoader.commandBatonWood), 
      "__m",
      "_i_",
      "s__",
      'm', Blocks.planks,
      's', Items.stick,
      'i', Items.iron_ingot);
  recipe.addResearch("command");
  recipe = AWCraftingManager.INSTANCE.addRecipe(new ItemStack(AWNpcItemLoader.commandBatonStone), 
      "__m",
      "_i_",
      "s__",
      'm', Blocks.stone,
      's', Items.stick,
      'i', Items.iron_ingot);
  recipe.addResearch("command");
  recipe = AWCraftingManager.INSTANCE.addRecipe(new ItemStack(AWNpcItemLoader.commandBatonIron), 
      "__m",
      "_i_",
      "s__",
      'm', Items.iron_ingot,
      's', Items.stick,
      'i', Items.iron_ingot);
  recipe.addResearch("command");
  recipe = AWCraftingManager.INSTANCE.addRecipe(new ItemStack(AWNpcItemLoader.commandBatonGold), 
      "__m",
      "_i_",
      "s__",
      'm', Items.gold_ingot,
      's', Items.stick,
      'i', Items.iron_ingot);
  recipe.addResearch("command");
  recipe = AWCraftingManager.INSTANCE.addRecipe(new ItemStack(AWNpcItemLoader.commandBatonDiamond), 
      "__m",
      "_i_",
      "s__",
      'm', Items.diamond,
      's', Items.stick,
      'i', Items.iron_ingot);
  recipe.addResearch("command");
  
  //upkeep order
  recipe = AWCraftingManager.INSTANCE.addRecipe(new ItemStack(AWNpcItemLoader.upkeepOrder),
      "d", 
      "p",
      "w",
      'd', new ItemStack(Items.dye,1,0),//black
      'w', Blocks.planks,
      'p', Items.paper);
  recipe.addResearch("leadership");  
  //work order
  recipe = AWCraftingManager.INSTANCE.addRecipe(new ItemStack(AWNpcItemLoader.workOrder),
      "d", 
      "p",
      "w",
      'd', new ItemStack(Items.dye,1,8),//gray
      'w', Blocks.planks,
      'p', Items.paper);
  recipe.addResearch("leadership");  
  //route order
  recipe = AWCraftingManager.INSTANCE.addRecipe(new ItemStack(AWNpcItemLoader.routingOrder),
      "d", 
      "p",
      "w",
      'd', new ItemStack(Items.dye,1,7),//light gray
      'w', Blocks.planks,
      'p', Items.paper);
  recipe.addResearch("leadership");  
  //combat order
  recipe = AWCraftingManager.INSTANCE.addRecipe(new ItemStack(AWNpcItemLoader.combatOrder),
      "d", 
      "p",
      "w",
      'd', new ItemStack(Items.dye,1,1),//red
      'w', Blocks.planks,
      'p', Items.paper);
  recipe.addResearch("leadership"); 
  
  //lute
  recipe = AWCraftingManager.INSTANCE.addRecipe(new ItemStack(AWNpcItemLoader.bardInstrument,1,0),
      "__s", 
      "pi_",
      "pp_",
      's', Items.stick,
      'p', Blocks.planks,
      'i', Items.iron_ingot);
  recipe.addResearch("leadership");   
  //flute
  recipe = AWCraftingManager.INSTANCE.addRecipe(new ItemStack(AWNpcItemLoader.bardInstrument,1,1),
      "__s", 
      "_s_",
      "i__",
      's', Items.stick,
      'i', Items.iron_ingot);
  recipe.addResearch("leadership");  
  //harp
  recipe = AWCraftingManager.INSTANCE.addRecipe(new ItemStack(AWNpcItemLoader.bardInstrument,1,2),
      "ss_", 
      "SSs",
      "ss_",
      's', Items.stick,
      'S', Items.string);
  recipe.addResearch("leadership");  
  //drum
  recipe = AWCraftingManager.INSTANCE.addRecipe(new ItemStack(AWNpcItemLoader.bardInstrument,1,3),
      "plp",
      "_p_",
      'p', Blocks.planks,
      'l', Items.leather);
  recipe.addResearch("leadership");
  
  
  
  }


private static class OrderCopyingRecipe implements IRecipe
{
Item item;
private OrderCopyingRecipe(Item item){this.item=item;}

@Override
public boolean matches(InventoryCrafting var1, World var2)
  {
  ItemStack order1 = null, order2 = null;
  boolean foundOtherStuff=false;
  ItemStack stack;
  for(int i = 0; i < var1.getSizeInventory(); i++)
    {
    stack = var1.getStackInSlot(i);
    if(stack==null){continue;}
    if(stack.getItem()==item)
      {
      if(order1==null){order1=stack;}
      else if(order2==null){order2=stack;}
      else
        {
        foundOtherStuff=true;
        break;
        }
      }
    else
      {
      foundOtherStuff=true;
      break;
      }
    }
  return !foundOtherStuff && order1!=null && order2!=null;
  }

@Override
public ItemStack getCraftingResult(InventoryCrafting var1)
  {
  ItemStack order1 = null, order2 = null;
  boolean foundOtherStuff=false;
  ItemStack stack;
  for(int i = 0; i < var1.getSizeInventory(); i++)
    {
    stack = var1.getStackInSlot(i);
    if(stack==null){continue;}
    if(stack.getItem()==item)
      {
      if(order1==null){order1=stack;}
      else if(order2==null){order2=stack;}
      else
        {
        foundOtherStuff=true;
        break;
        }
      }
    else
      {
      foundOtherStuff=true;
      break;
      }
    }
  if(foundOtherStuff || order1==null || order2==null){return null;}
  ItemStack retStack = order2.copy();
  if(order1.stackTagCompound!=null)
    {
    retStack.setTagCompound((NBTTagCompound)order1.stackTagCompound.copy());
    }
  else
    {
    retStack.setTagCompound(null);
    }
  retStack.stackSize = 2;
  return retStack;
  }

@Override
public int getRecipeSize()
  {
  return 9;
  }

@Override
public ItemStack getRecipeOutput()
  {
  return null;
  }
}
}
