package net.shadowmage.ancientwarfare.npc.crafting;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.npc.item.AWNpcItemLoader;


public class AWNpcCrafting
{

/**
 * load any recipes for automation module recipes
 */
public static void loadRecipes()
  {
  CraftingManager.getInstance().getRecipeList().add(new OrderCopyingRecipe(AWNpcItemLoader.upkeepOrder));
  CraftingManager.getInstance().getRecipeList().add(new OrderCopyingRecipe(AWNpcItemLoader.routingOrder));
  CraftingManager.getInstance().getRecipeList().add(new OrderCopyingRecipe(AWNpcItemLoader.combatOrder));
  CraftingManager.getInstance().getRecipeList().add(new OrderCopyingRecipe(AWNpcItemLoader.workOrder));
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
