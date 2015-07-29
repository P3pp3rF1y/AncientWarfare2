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

import codechicken.nei.PositionedStack;
import codechicken.nei.api.API;
import codechicken.nei.recipe.DefaultOverlayHandler;
import codechicken.nei.recipe.RecipeInfo;
import codechicken.nei.recipe.TemplateRecipeHandler;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.shadowmage.ancientwarfare.automation.gui.GuiWarehouseCraftingStation;
import net.shadowmage.ancientwarfare.automation.gui.GuiWorksiteAutoCrafting;
import net.shadowmage.ancientwarfare.core.api.ModuleStatus;
import net.shadowmage.ancientwarfare.core.crafting.AWCraftingManager;
import net.shadowmage.ancientwarfare.core.crafting.RecipeResearched;
import net.shadowmage.ancientwarfare.core.gui.crafting.GuiEngineeringStation;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class AWNeiRecipeHandler extends TemplateRecipeHandler {

    public AWNeiRecipeHandler() {
        API.registerRecipeHandler(this);
        API.registerUsageHandler(this);
        register(GuiEngineeringStation.class, new DefaultOverlayHandler(37, 2));
        if(ModuleStatus.automationLoaded){
            register(GuiWarehouseCraftingStation.class, new DefaultOverlayHandler(37, 2));
            register(GuiWorksiteAutoCrafting.class, new DefaultOverlayHandler(37, 2));
        }
    }

    private void register(Class<? extends GuiContainer> cl, DefaultOverlayHandler handler){
        API.registerGuiOverlay(cl, "awcrafting", handler.offsetx, handler.offsety);
        API.registerGuiOverlayHandler(cl, handler, "awcrafting");
        API.registerGuiOverlayHandler(cl, handler, "crafting");
    }

    @Override
    public String getRecipeName() {
        return "AW Crafting";
    }

    @Override
    public String getGuiTexture() {
        return "textures/gui/container/crafting_table.png";
    }

    @Override
    public String getOverlayIdentifier() {
        return "awcrafting";
    }

    @Override
    public TemplateRecipeHandler newInstance() {
        arecipes.clear();
        return this;
    }

    @Override
    public void loadTransferRects() {
        transferRects.add(new RecipeTransferRect(new Rectangle(-31, 7, 18, 18), "awcrafting"));
    }

    @Override
    public List<Class<? extends GuiContainer>> getRecipeTransferRectGuis() {
        LinkedList<Class<? extends GuiContainer>> list = new LinkedList<Class<? extends GuiContainer>>();
        list.add(GuiEngineeringStation.class);
        if(ModuleStatus.automationLoaded){
            list.add(GuiWarehouseCraftingStation.class);
            list.add(GuiWorksiteAutoCrafting.class);
        }
        return list;
    }

    @Override
    public void loadCraftingRecipes(ItemStack result) {
        List<RecipeResearched> allrecipes = AWCraftingManager.INSTANCE.getRecipes();
        for (RecipeResearched irecipe : allrecipes) {
            if (InventoryTools.doItemStacksMatch(irecipe.getRecipeOutput(), result)) {
                arecipes.add(new AWCachedRecipe(irecipe));
            }
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        List<RecipeResearched> allrecipes = AWCraftingManager.INSTANCE.getRecipes();
        for (RecipeResearched irecipe : allrecipes) {
            for (Object target : irecipe.getInput()) {
                if (target == null) {
                    continue;
                }
                if (target instanceof ItemStack) {
                    if (!OreDictionary.itemMatches((ItemStack) target, ingredient, false)) {
                        continue;
                    }
                } else if (target instanceof Iterable) {
                    boolean matched = false;
                    Iterator<?> itr = ((Iterable) target).iterator();
                    while (itr.hasNext() && !matched) {
                        matched = OreDictionary.itemMatches((ItemStack) itr.next(), ingredient, false);
                    }
                    if (!matched) {
                        continue;
                    }
                }
                arecipes.add(new AWCachedRecipe(irecipe));
            }
        }
    }

    @Override
    public boolean hasOverlay(GuiContainer gui, Container container, int recipe) {
        return super.hasOverlay(gui, container, recipe) || RecipeInfo.hasDefaultOverlay(gui, "crafting");
    }

    public class AWCachedRecipe extends CachedRecipe {

        private final ArrayList<PositionedStack> ingredients;
        private final PositionedStack result;

        public AWCachedRecipe(RecipeResearched recipe) {
            result = new PositionedStack(recipe.getRecipeOutput().copy(), 119, 24);
            ingredients = new ArrayList<PositionedStack>();
            setIngredients(recipe.getRecipeWidth(), recipe.getRecipeHeight(), recipe.getInput());
        }

        private void setIngredients(int width, int height, Object[] items) {
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    if (items[y * width + x] == null) {
                        continue;
                    }
                    PositionedStack stack = new PositionedStack(items[y * width + x], 25 + x * 18, 6 + y * 18);
                    stack.setMaxSize(1);
                    ingredients.add(stack);
                }
            }
        }

        @Override
        public List<PositionedStack> getIngredients() {
            return getCycledIngredients(cycleticks / 20, ingredients);
        }

        @Override
        public PositionedStack getResult() {
            return result;
        }
    }
}
