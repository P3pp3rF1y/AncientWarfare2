package net.shadowmage.ancientwarfare.core.crafting;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import net.shadowmage.ancientwarfare.core.research.ResearchGoal;

public class RecipeResearched extends ShapedRecipes
{

private Set<Integer> neededResearch = new HashSet<Integer>();

public RecipeResearched(int par1, int par2, ItemStack[] par3ArrayOfItemStack, ItemStack par4ItemStack)
  {
  super(par1, par2, par3ArrayOfItemStack, par4ItemStack);
  }

protected final RecipeResearched addResearch(String... names)
  {
  ResearchGoal g;  
  for(String name : names)
    {
    name = name.startsWith("research.") ? name : "research."+name;
    g = ResearchGoal.getGoal(name);
    if(g!=null)
      {
      neededResearch.add(g.getId());
      }
    else
      {
      throw new IllegalArgumentException("COULD NOT LOCATE RESEARCH GOAL FOR NAME: "+name);
      }
    }
  return this;
  }

protected final RecipeResearched addResearch(int... nums)
  {
  ResearchGoal g;
  for(int k : nums)
    {
    g = ResearchGoal.getGoal(k);
    if(g!=null)
      {
      neededResearch.add(k);
      }
    }
  return this;
  }

public Set<Integer> getNeededResearch(){return neededResearch;}

@Override
public boolean matches(InventoryCrafting inv, World world)
  {
  for(int x = 0; x <= 3 - recipeWidth; x++)
    {
    for(int y = 0; y <= 3 - recipeHeight; ++y)
      {
      if(checkMatch(inv, x, y, false))
        {
        return true;
        }
      }
    }
  return false;
  }

private boolean checkMatch(InventoryCrafting inv, int startX, int startY, boolean mirror)
  {
  int width = recipeWidth;
  int height = recipeHeight;
  for(int x = 0; x < 3; x++)
    {
    for(int y = 0; y < 3; y++)
      {
      int subX = x - startX;
      int subY = y - startY;
      Object target = null;
      if(subX >= 0 && subY >= 0 && subX < width && subY < height)
        {
        if(mirror)
          {
          target = recipeItems[width - subX - 1 + subY * width];
          }
        else
          {
          target = recipeItems[subX + subY * width];
          }
        }
      ItemStack slot = inv.getStackInRowAndColumn(x, y);
      if(target instanceof ItemStack)
        {
        if(!OreDictionary.itemMatches((ItemStack)target, slot, false))
          {
          return false;
          }
        }
      else if(target == null && slot != null)
        {
        return false;
        }
      }
    }
  return true;
  }

}
