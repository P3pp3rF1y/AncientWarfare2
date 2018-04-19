package net.shadowmage.ancientwarfare.core.compat.jei;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.recipe.wrapper.IShapedCraftingRecipeWrapper;
import net.shadowmage.ancientwarfare.core.crafting.ShapedResearchRecipe;

public class ShapedResearchRecipeWrapper extends ResearchRecipeWrapper<ShapedResearchRecipe> implements IShapedCraftingRecipeWrapper {
	public ShapedResearchRecipeWrapper(IJeiHelpers jeiHelpers, ShapedResearchRecipe recipe) {
		super(jeiHelpers.getStackHelper(), recipe);
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