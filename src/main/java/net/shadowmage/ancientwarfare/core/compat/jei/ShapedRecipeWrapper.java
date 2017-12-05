package net.shadowmage.ancientwarfare.core.compat.jei;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.recipe.wrapper.IShapedCraftingRecipeWrapper;
import mezz.jei.plugins.vanilla.crafting.ShapelessRecipeWrapper;
import net.shadowmage.ancientwarfare.core.crafting.ResearchRecipe;

public class ShapedRecipeWrapper extends ShapelessRecipeWrapper<ResearchRecipe.ShapedWrapper> implements IShapedCraftingRecipeWrapper {
    public ShapedRecipeWrapper(IJeiHelpers jeiHelpers, ResearchRecipe.ShapedWrapper recipe) {
        super(jeiHelpers, recipe);
    }

    @Override
    public int getWidth() {
        return recipe.getRecipeWidth();
    }

    @Override
    public int getHeight() {
        return recipe.getRecipeHeight();
    }
}