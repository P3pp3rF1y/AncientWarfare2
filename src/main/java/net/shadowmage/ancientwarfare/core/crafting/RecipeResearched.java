package net.shadowmage.ancientwarfare.core.crafting;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
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

}
