package net.shadowmage.ancientwarfare.core.crafting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;

public class AWCraftingManager
{

public static final AWCraftingManager INSTANCE = new AWCraftingManager();
private AWCraftingManager(){}

List<RecipeResearched> recipes = new ArrayList<RecipeResearched>();
private static final String[] emptyStringArray = new String[]{};

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
    if (recipe.matches(inventory, world))
      {
      if(recipe.canPlayerCraft(world, playerName))
        {
        return recipe.getCraftingResult(inventory);        
        }
      else
        {
        return null;
        }
      }
    }
  return null;
  }

private void addRecipe(RecipeResearched recipe)
  {
  Item item = recipe.getRecipeOutput().getItem();
  boolean craftable = AWCoreStatics.isItemCraftable(item);
  if(craftable)
    {
    if(!recipe.getNeededResearch().isEmpty() && AWCoreStatics.isItemResearched(item))
      {
      this.recipes.add(recipe);
      }
    else
      {
      CraftingManager.getInstance().getRecipeList().add(recipe);      
      }
    }
  }

public RecipeResearched createRecipe(ItemStack result, Object ... inputArray)
  {
  return createRecipe(result, emptyStringArray, inputArray);
  }

public RecipeResearched createRecipe(ItemStack result, String research, Object ... inputArray)
  {
  return createRecipe(result, research, inputArray);
  }

@SuppressWarnings("unchecked")
public RecipeResearched createRecipe(ItemStack result, String[] research, Object ... inputArray)
  {
  String recipeCharactersAsSequence = "";
  int index = 0;
  int recipeWidth = 0;
  int recipeHeight = 0;

  if (inputArray[index] instanceof String[])
    {
    String[] characterInputArray = (String[])((String[])inputArray[index++]);

    for (int l = 0; l < characterInputArray.length; ++l)
      {
      String recipeLine = characterInputArray[l];
      ++recipeHeight;
      recipeWidth = recipeLine.length();
      recipeCharactersAsSequence = recipeCharactersAsSequence + recipeLine;
      }
    }
  else
    {
    while (inputArray[index] instanceof String)
      {
      String s2 = (String)inputArray[index++];
      ++recipeHeight;
      recipeWidth = s2.length();
      recipeCharactersAsSequence = recipeCharactersAsSequence + s2;
      }
    }

  HashMap<Character, ItemStack> characterToStack;

  for (characterToStack = new HashMap<Character, ItemStack>(); index < inputArray.length; index += 2)
    {
    Character itemCharacter = (Character)inputArray[index];
    ItemStack stackForCharacter = null;
    if (inputArray[index + 1] instanceof Item)
      {
      stackForCharacter = new ItemStack((Item)inputArray[index + 1]);
      }
    else if (inputArray[index + 1] instanceof Block)
      {
      stackForCharacter = new ItemStack((Block)inputArray[index + 1], 1, 32767);
      }
    else if (inputArray[index + 1] instanceof ItemStack)
      {
      stackForCharacter = (ItemStack)inputArray[index + 1];
      }
    characterToStack.put(itemCharacter, stackForCharacter);
    }

  ItemStack[] recipeItemArray = new ItemStack[recipeWidth * recipeHeight];

  for (int i1 = 0; i1 < recipeWidth * recipeHeight; ++i1)
    {
    char c0 = recipeCharactersAsSequence.charAt(i1);

    if (characterToStack.containsKey(Character.valueOf(c0)))
      {
      recipeItemArray[i1] = ((ItemStack)characterToStack.get(Character.valueOf(c0))).copy();
      }
    else
      {
      recipeItemArray[i1] = null;
      }
    }
  RecipeResearched recipe = new RecipeResearched(recipeWidth, recipeHeight, recipeItemArray, result);
  recipe.addResearch(research);
  addRecipe(recipe);
  return recipe;
  }

public List<RecipeResearched> getRecipes(){return recipes;}

}
