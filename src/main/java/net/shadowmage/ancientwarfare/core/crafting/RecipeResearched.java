package net.shadowmage.ancientwarfare.core.crafting;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.research.ResearchTracker;

public class RecipeResearched extends ShapedRecipes
{

Set<Integer> neededResearch = new HashSet<Integer>();

public RecipeResearched(int par1, int par2, ItemStack[] par3ArrayOfItemStack, ItemStack par4ItemStack)
  {
  super(par1, par2, par3ArrayOfItemStack, par4ItemStack);
  }

public boolean canPlayerCraft(World world, String playerName)
  {
  if(world.isRemote)
    {
    boolean canCraft = true;
    for(Integer i : this.neededResearch)
      {
      if(!ResearchTracker.instance().hasClientCompleted(i))
        {
        canCraft = false;
        break;
        }
      }
    return canCraft;
    }
  else
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

}
