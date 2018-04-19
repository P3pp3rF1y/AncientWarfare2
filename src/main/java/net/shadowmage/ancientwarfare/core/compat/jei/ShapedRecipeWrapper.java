package net.shadowmage.ancientwarfare.core.compat.jei;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.recipe.wrapper.IShapedCraftingRecipeWrapper;
import mezz.jei.plugins.vanilla.crafting.ShapelessRecipeWrapper;
import net.minecraftforge.common.crafting.IShapedRecipe;

public class ShapedRecipeWrapper extends ShapelessRecipeWrapper<IShapedRecipe> implements IShapedCraftingRecipeWrapper {
	public ShapedRecipeWrapper(IJeiHelpers jeiHelpers, IShapedRecipe recipe) {
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
