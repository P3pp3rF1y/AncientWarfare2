/**
   Copyright 2012-2013 John Cummens (aka Shadowmage, Shadowmage4513)
   This software is distributed under the terms of the GNU General Public License.
   Please see COPYING for precise license information.

   This file is part of Ancient Warfare.

   Ancient Warfare is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   Ancient Warfare is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Ancient Warfare.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.shadowmage.ancientwarfare.nei_plugin;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.crafting.AWCraftingManager;
import net.shadowmage.ancientwarfare.core.crafting.RecipeResearched;
import net.shadowmage.ancientwarfare.core.gui.crafting.GuiEngineeringStation;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.RecipeInfo;
import codechicken.nei.recipe.TemplateRecipeHandler;

public class AWNeiRecipeHandler extends TemplateRecipeHandler
{

/**
 * 
 */
public AWNeiRecipeHandler()
  {
  // TODO Auto-generated constructor stub
  }

@Override
public String getRecipeName()
  {
  return "AW Crafting";
  }

@Override
public String getGuiTexture()
  {
  return "textures/gui/container/crafting_table.png";
  }

@Override
public String getOverlayIdentifier()
  {
  return "awcrafting";
  }

@Override
public void loadTransferRects()
  {
  transferRects.add(new RecipeTransferRect(new Rectangle(84, 23, 24, 18), "awcrafting"));
  }

@Override
public Class<? extends GuiContainer> getGuiClass()
  {
  return GuiEngineeringStation.class;
  }

@Override
public void loadCraftingRecipes(ItemStack result)
  {  
  List<RecipeResearched> allrecipes = AWCraftingManager.INSTANCE.getRecipes();
  for(RecipeResearched irecipe : allrecipes)
    {
    if(InventoryTools.doItemStacksMatch(irecipe.getRecipeOutput(), result))
      {
      this.arecipes.add(new AWCachedRecipe(irecipe));
      }    
    } 
  }

@Override
public void loadUsageRecipes(ItemStack ingredient)
  {
  List<RecipeResearched> allrecipes = AWCraftingManager.INSTANCE.getRecipes();
  for(RecipeResearched irecipe : allrecipes)
    {
    for(ItemStack stack : irecipe.recipeItems)
      {
      if(stack==null){continue;}
      if(ItemStack.areItemStacksEqual(stack, ingredient))
        {
        arecipes.add(new AWCachedRecipe(irecipe));
        }
      }   
    } 
  }

public boolean hasOverlay(GuiContainer gui, Container container, int recipe)
  {
  return super.hasOverlay(gui, container, recipe) || RecipeInfo.hasDefaultOverlay(gui, "awcrafting");
  }

public class AWCachedRecipe extends CachedRecipe
{

public ArrayList<PositionedStack> ingredients;
public PositionedStack result;
public AWCachedRecipe(RecipeResearched recipe)
  {
  result = new PositionedStack(recipe.getRecipeOutput().copy(), 119, 24);
  ingredients = new ArrayList<PositionedStack>();
  setIngredients(recipe.recipeWidth, recipe.recipeHeight, recipe.recipeItems);
  }

public void setIngredients(int width, int height, Object[] items)
  {
  for (int x = 0; x < width; x++)
    {
    for (int y = 0; y < height; y++)
      {
      if (items[y * width + x] == null)
        {
        continue;    
        }
      PositionedStack stack = new PositionedStack(items[y * width + x], 25 + x * 18, 6 + y * 18, false);
      stack.setMaxSize(1);
      ingredients.add(stack);
      }
    }
  }

@Override
public List<PositionedStack> getIngredients()
  {
  return getCycledIngredients(cycleticks / 20, ingredients);
  }

@Override
public PositionedStack getResult()
  {
  return result;
  }
}
}
