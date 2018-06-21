package net.shadowmage.ancientwarfare.core.compat.jei;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IStackHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;
import net.shadowmage.ancientwarfare.core.crafting.ResearchRecipeBase;
import net.shadowmage.ancientwarfare.core.registry.ResearchRegistry;

public class ResearchRecipeWrapper<T extends ResearchRecipeBase> implements IRecipeWrapper {
	private final IStackHelper stackHelper;
	protected T recipe;

	ResearchRecipeWrapper(IStackHelper stackHelper, T recipe) {
		this.stackHelper = stackHelper;
		this.recipe = recipe;

	}

	@Override
	public void getIngredients(IIngredients ingredients) {
		ingredients.setInputLists(ItemStack.class, stackHelper.expandRecipeItemStackInputs(recipe.getIngredients()));
		ingredients.setOutput(ItemStack.class, recipe.getRecipeOutput());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
		//noinspection ConstantConditions
		String research = AWCoreStatics.useResearchSystem ? I18n.format(ResearchRegistry.getResearch(recipe.getNeededResearch()).getUnlocalizedName()) : "Research disabled";
		minecraft.fontRenderer.drawString(research, 60, 0, 0x444444, false);
	}
}
