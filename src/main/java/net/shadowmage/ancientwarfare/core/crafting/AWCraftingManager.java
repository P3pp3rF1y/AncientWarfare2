package net.shadowmage.ancientwarfare.core.crafting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class AWCraftingManager
{

List<RecipeResearched> recipes = new ArrayList<RecipeResearched>();

public static final AWCraftingManager INSTANCE = new AWCraftingManager();
private AWCraftingManager(){}

/**
 * shameless copy of CraftingManager.findMatchingRecipe, with added param for player
 * @param inventory
 * @param world
 * @return
 */
public ItemStack findMatchingRecipe(InventoryCrafting inventory, World world, EntityPlayer player)
  {
  int itemStackCounter = 0;
  ItemStack itemstack = null;
  ItemStack itemstack1 = null;
  int slotIndex;

  for (slotIndex = 0; slotIndex < inventory.getSizeInventory(); ++slotIndex)
    {
    ItemStack itemstack2 = inventory.getStackInSlot(slotIndex);
    if (itemstack2 != null)
      {
      if (itemStackCounter == 0)
        {
        itemstack = itemstack2;
        }
      if (itemStackCounter == 1)
        {
        itemstack1 = itemstack2;
        }
      ++itemStackCounter;
      }
    }
  if (itemStackCounter == 2 && itemstack.getItem() == itemstack1.getItem() && itemstack.stackSize == 1 && itemstack1.stackSize == 1 && itemstack.getItem().isRepairable())
    {
    Item item = itemstack.getItem();
    int j1 = item.getMaxDamage() - itemstack.getItemDamageForDisplay();
    int k = item.getMaxDamage() - itemstack1.getItemDamageForDisplay();
    int l = j1 + k + item.getMaxDamage() * 5 / 100;
    int i1 = item.getMaxDamage() - l;
    if (i1 < 0)
      {
      i1 = 0;
      }
    return new ItemStack(itemstack.getItem(), 1, i1);
    }
  else
    {
    for (slotIndex = 0; slotIndex < this.recipes.size(); ++slotIndex)
      {
      RecipeResearched recipe = (RecipeResearched)this.recipes.get(slotIndex);
      if (recipe.matches(inventory, world) && recipe.canPlayerCraft(player.worldObj, player.getCommandSenderName()))
        {
        return recipe.getCraftingResult(inventory);
        }
      }
    return null;
    }
  }

public RecipeResearched addRecipe(ItemStack par1ItemStack, Object ... par2ArrayOfObj)
  {
  String recipeCharactersAsSequence = "";
  int i = 0;
  int j = 0;
  int k = 0;

  if (par2ArrayOfObj[i] instanceof String[])
    {
    String[] characterInputArray = (String[])((String[])par2ArrayOfObj[i++]);

    for (int l = 0; l < characterInputArray.length; ++l)
      {
      String testChar = characterInputArray[l];
      ++k;
      j = testChar.length();
      recipeCharactersAsSequence = recipeCharactersAsSequence + testChar;
      }
    }
  else
    {
    while (par2ArrayOfObj[i] instanceof String)
      {
      String s2 = (String)par2ArrayOfObj[i++];
      ++k;
      j = s2.length();
      recipeCharactersAsSequence = recipeCharactersAsSequence + s2;
      }
    }

  HashMap<Character, ItemStack> charKeyToItem;

  for (charKeyToItem = new HashMap<Character, ItemStack>(); i < par2ArrayOfObj.length; i += 2)
    {
    Character character = (Character)par2ArrayOfObj[i];
    ItemStack itemstack1 = null;

    if (par2ArrayOfObj[i + 1] instanceof Item)
      {
      itemstack1 = new ItemStack((Item)par2ArrayOfObj[i + 1]);
      }
    else if (par2ArrayOfObj[i + 1] instanceof Block)
      {
      itemstack1 = new ItemStack((Block)par2ArrayOfObj[i + 1], 1, 32767);
      }
    else if (par2ArrayOfObj[i + 1] instanceof ItemStack)
      {
      itemstack1 = (ItemStack)par2ArrayOfObj[i + 1];
      }

    charKeyToItem.put(character, itemstack1);
    }

  ItemStack[] recipeItemArray = new ItemStack[j * k];

  for (int i1 = 0; i1 < j * k; ++i1)
    {
    char c0 = recipeCharactersAsSequence.charAt(i1);

    if (charKeyToItem.containsKey(Character.valueOf(c0)))
      {
      recipeItemArray[i1] = ((ItemStack)charKeyToItem.get(Character.valueOf(c0))).copy();
      }
    else
      {
      recipeItemArray[i1] = null;
      }
    }

  RecipeResearched recipe = new RecipeResearched(j, k, recipeItemArray, par1ItemStack);
  this.recipes.add(recipe);
  return recipe;
  }

}
