package net.shadowmage.ancientwarfare.core.crafting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.api.AWBlocks;
import net.shadowmage.ancientwarfare.core.api.AWItems;
import net.shadowmage.ancientwarfare.core.config.Statics;

public class AWCraftingManager
{

List<RecipeResearched> recipes = new ArrayList<RecipeResearched>();

public static final AWCraftingManager INSTANCE = new AWCraftingManager();
private AWCraftingManager(){}

/**
 * load any recipes for CORE module (research book, engineering station, research station)
 */
public void loadRecipes()
  {
  CraftingManager.getInstance().addRecipe(new ItemStack(AWItems.researchBook), new Object[]{"ILL", "PPP", "ILL", 'I', Items.iron_ingot, 'L', Items.leather, 'P', Items.paper});
  CraftingManager.getInstance().addRecipe(new ItemStack(AWBlocks.engineeringStation), new Object[]{"IWI", "IPI", "ICI", 'I', Items.iron_ingot, 'W', Blocks.planks, 'P', Blocks.crafting_table, 'C', Blocks.chest});
  CraftingManager.getInstance().addRecipe(new ItemStack(AWBlocks.researchStation), new Object[]{"IWI", "GPG", "ICI", 'I', Items.iron_ingot, 'W', Blocks.planks, 'P', Blocks.crafting_table, 'C', Blocks.chest, 'G', Items.gold_ingot});
  }

/**
 * shameless copy of CraftingManager.findMatchingRecipe, with added param for player
 * @param inventory
 * @param world
 * @return
 */
public ItemStack findMatchingRecipe(InventoryCrafting inventory, World world, String playerName)
  {
  ItemStack item1 = CraftingManager.getInstance().findMatchingRecipe(inventory, world);
  if(item1!=null)
    {
    return item1;
    }
  
  int recipeIndex;  
  for (recipeIndex = 0; recipeIndex < this.recipes.size(); ++recipeIndex)
    {
    RecipeResearched recipe = (RecipeResearched)this.recipes.get(recipeIndex);
    if (recipe.matches(inventory, world) && recipe.canPlayerCraft(world, playerName))
      {
      return recipe.getCraftingResult(inventory);
      }
    }
  return null;
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
      String recipeLine = characterInputArray[l];
      ++k;
      j = recipeLine.length();
      recipeCharactersAsSequence = recipeCharactersAsSequence + recipeLine;
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
  if(!Statics.useResearchSystem)
    {
    CraftingManager.getInstance().getRecipeList().add(recipe);
    }
  return recipe;
  }

}
