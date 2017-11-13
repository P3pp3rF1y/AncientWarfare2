package net.shadowmage.ancientwarfare.core.compat.jei;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IStackHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.crafting.ResearchRecipe;
import net.shadowmage.ancientwarfare.core.research.ResearchGoal;

public class ResearchRecipeWrapper implements IRecipeWrapper {
    private final IStackHelper stackHelper;
    private ResearchRecipe recipe;

    ResearchRecipeWrapper(IStackHelper stackHelper, ResearchRecipe recipe) {
        this.stackHelper = stackHelper;
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInputLists(ItemStack.class, stackHelper.expandRecipeItemStackInputs(recipe.getIngredients()));
        ingredients.setOutput(ItemStack.class, recipe.getRecipeOutput());
    }

    public int getWidth() {
        return recipe.getRecipeWidth();
    }

    public int getHeight() {
        return recipe.getRecipeHeight();
    }

    public String getResearch() {
        return I18n.format(ResearchGoal.getGoal(recipe.getNeededResearch()).getName());
    }
}
