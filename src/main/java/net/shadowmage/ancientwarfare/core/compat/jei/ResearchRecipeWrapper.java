package net.shadowmage.ancientwarfare.core.compat.jei;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IStackHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.crafting.ResearchRecipeBase;
import net.shadowmage.ancientwarfare.core.research.ResearchGoal;

public class ResearchRecipeWrapper implements IRecipeWrapper {
	private final IStackHelper stackHelper;
	private ResearchRecipeBase recipe;

	ResearchRecipeWrapper(IStackHelper stackHelper, ResearchRecipeBase recipe) {
		this.stackHelper = stackHelper;
		this.recipe = recipe;

	}

	@Override
	public void getIngredients(IIngredients ingredients) {
		ingredients.setInputLists(ItemStack.class, stackHelper.expandRecipeItemStackInputs(recipe.getIngredients()));
		ingredients.setOutput(ItemStack.class, recipe.getRecipeOutput());
	}

	public int getWidth() {
		return 3;
	}

	public int getHeight() {
		return 3;
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
		minecraft.fontRenderer.drawString(I18n.format(ResearchGoal.getGoal(recipe.getNeededResearch()).getName()), 60, 0, 0x444444, false);
	}
}
