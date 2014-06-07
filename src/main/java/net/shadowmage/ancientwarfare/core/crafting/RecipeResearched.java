package net.shadowmage.ancientwarfare.core.crafting;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.research.ResearchGoal;
import net.shadowmage.ancientwarfare.core.research.ResearchTracker;

public class RecipeResearched extends ShapedRecipes
{

private Set<Integer> neededResearch = new HashSet<Integer>();

public RecipeResearched(int par1, int par2, ItemStack[] par3ArrayOfItemStack, ItemStack par4ItemStack)
  {
  super(par1, par2, par3ArrayOfItemStack, par4ItemStack);
  }

public final RecipeResearched addResearch(String... names)
  {
  ResearchGoal g;  
  for(String name : names)
    {
    if(name.startsWith("research."))
      {
      g = ResearchGoal.getGoal(name);      
      }
    else
      {
      g = ResearchGoal.getGoal("resesarch."+name);
      }
    if(g!=null)
      {
      neededResearch.add(g.getId());
      }
    }
  return this;
  }

public final RecipeResearched addResearch(int... nums)
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

public final boolean canPlayerCraft(World world, String playerName)
  {
  boolean canCraft = true;
  for(Integer i : this.neededResearch)
    {
    if(!ResearchTracker.instance().hasPlayerCompleted(world, playerName, i))
      {
      canCraft = false;
      break;
      }
    }
  return canCraft;
  }

}
