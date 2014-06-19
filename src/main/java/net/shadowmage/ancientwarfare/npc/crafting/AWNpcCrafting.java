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
  recipe = AWCraftingManager.INSTANCE.createRecipe(foodBundle.copy(), "leadership",
      "ap",
      "bb",
      "wc",
      'a', Items.apple,
      'b', Items.bread,
      'w', new ItemStack(Items.potionitem),
      'p', Items.cooked_porkchop,
      'b', Items.cooked_beef,
      'c', Items.cooked_chicken);
  
  //worker spawner
  recipe = AWCraftingManager.INSTANCE.createRecipe(ItemNpcSpawner.getStackForNpcType("worker", ""), "leadership",
      "gf",
      "gt",
      'f', foodBundle.copy(),
      't', Items.wooden_pickaxe,
      'g', Items.gold_ingot);
  //combat spawner
  recipe = AWCraftingManager.INSTANCE.createRecipe(ItemNpcSpawner.getStackForNpcType("combat", ""), "conscription",
      "gf",
      "gt",
      'f', foodBundle.copy(),
      't', Items.wooden_sword,
      'g', Items.gold_ingot);
  //courier bundle
  recipe = AWCraftingManager.INSTANCE.createRecipe(ItemNpcSpawner.getStackForNpcType("courier", ""), "trade",
      "gf",
      "gt",
      'f', foodBundle.copy(),
      't', Blocks.wool,
      'g', Items.gold_ingot);
  //trader spawner
  recipe = AWCraftingManager.INSTANCE.createRecipe(ItemNpcSpawner.getStackForNpcType("trader", ""), "trade",
      "gf_",
      "gtb",
      'f', foodBundle.copy(),
      't', Blocks.wool,
      'g', Items.gold_ingot,
      'b', Items.book);
  //priest spawner
  recipe = AWCraftingManager.INSTANCE.createRecipe(ItemNpcSpawner.getStackForNpcType("priest", ""), "leadership",
      "gf",
      "gb",
      'f', foodBundle.copy(),
      'g', Items.gold_ingot,
      'b', Items.book);
  //bard spawner
  recipe = AWCraftingManager.INSTANCE.createRecipe(ItemNpcSpawner.getStackForNpcType("bard", ""), "leadership",
      "gf",
      "gb",
      'f', foodBundle.copy(),
      'g', Items.gold_ingot,
      'b', new ItemStack(AWNpcItemLoader.bardInstrument,1,0));
    
  //command batons
  recipe = AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWNpcItemLoader.commandBatonWood), "command",
      "__m",
      "_i_",
      "s__",
      'm', Blocks.planks,
      's', Items.stick,
      'i', Items.iron_ingot);
  recipe = AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWNpcItemLoader.commandBatonStone), "command",
      "__m",
      "_i_",
      "s__",
      'm', Blocks.stone,
      's', Items.stick,
      'i', Items.iron_ingot);
  recipe = AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWNpcItemLoader.commandBatonIron), "command",
      "__m",
      "_i_",
      "s__",
      'm', Items.iron_ingot,
      's', Items.stick,
      'i', Items.iron_ingot);
  recipe = AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWNpcItemLoader.commandBatonGold), "command",
      "__m",
      "_i_",
      "s__",
      'm', Items.gold_ingot,
      's', Items.stick,
      'i', Items.iron_ingot);
  recipe = AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWNpcItemLoader.commandBatonDiamond), "command",
      "__m",
      "_i_",
      "s__",
      'm', Items.diamond,
      's', Items.stick,
      'i', Items.iron_ingot);
  
  //upkeep order
  recipe = AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWNpcItemLoader.upkeepOrder), "leadership",
      "d", 
      "p",
      "w",
      'd', new ItemStack(Items.dye,1,0),//black
      'w', Blocks.planks,
      'p', Items.paper);
  //work order
  recipe = AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWNpcItemLoader.workOrder), "leadership",
      "d", 
      "p",
      "w",
      'd', new ItemStack(Items.dye,1,8),//gray
      'w', Blocks.planks,
      'p', Items.paper); 
  //route order
  recipe = AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWNpcItemLoader.routingOrder), "leadership",
      "d", 
      "p",
      "w",
      'd', new ItemStack(Items.dye,1,7),//light gray
      'w', Blocks.planks,
      'p', Items.paper);  
  //combat order
  recipe = AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWNpcItemLoader.combatOrder), "leadership",
      "d", 
      "p",
      "w",
      'd', new ItemStack(Items.dye,1,1),//red
      'w', Blocks.planks,
      'p', Items.paper);
  
  //lute
  recipe = AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWNpcItemLoader.bardInstrument,1,0), "leadership",
      "__s", 
      "pi_",
      "pp_",
      's', Items.stick,
      'p', Blocks.planks,
      'i', Items.iron_ingot); 
  //flute
  recipe = AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWNpcItemLoader.bardInstrument,1,1), "leadership",
      "__s", 
      "_s_",
      "i__",
      's', Items.stick,
      'i', Items.iron_ingot); 
  //harp
  recipe = AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWNpcItemLoader.bardInstrument,1,2), "leadership",
      "ss_", 
      "SSs",
      "ss_",
      's', Items.stick,
      'S', Items.string);
  //drum
  recipe = AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWNpcItemLoader.bardInstrument,1,3), "leadership",
      "plp",
      "_p_",
      'p', Blocks.planks,
      'l', Items.leather);
  
  
  
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
