package net.shadowmage.ancientwarfare.core.compat.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.ICraftingGridHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.config.Constants;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;

import javax.annotation.Nullable;
import java.util.List;

public abstract class ResearchRecipeCategory {
	private static final int CRAFT_OUTPUT_SLOT = 0;
	private static final int CRAFT_INPUT_SLOT = 1;
	private static final int WIDTH = 116;
	private static final int HEIGHT = 54;
	private final IDrawable background;
	private final ICraftingGridHelper craftingGridHelper;
	private final IDrawable icon;

	public ResearchRecipeCategory(IGuiHelper guiHelper) {
		ResourceLocation location = Constants.RECIPE_GUI_VANILLA;
		background = guiHelper.createDrawable(location, 0, 60, WIDTH, HEIGHT);
		craftingGridHelper = guiHelper.createCraftingGridHelper(CRAFT_INPUT_SLOT, CRAFT_OUTPUT_SLOT);
		icon = guiHelper.createDrawable(new ResourceLocation(AncientWarfareCore.MOD_ID + ":textures/items/core/research_book.png"), 0, 0, 16, 16, 16, 16);
	}

	@Nullable
	public IDrawable getIcon() {
		return icon;
	}

	public String getModName() {
		return AncientWarfareCore.MOD_ID;
	}

	public IDrawable getBackground() {
		return background;
	}

	public void setRecipe(IRecipeLayout recipeLayout, ResearchRecipeWrapper recipeWrapper, IIngredients ingredients) {
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

		guiItemStacks.init(CRAFT_OUTPUT_SLOT, false, 94, 18);

		for (int y = 0; y < 3; ++y) {
			for (int x = 0; x < 3; ++x) {
				int index = CRAFT_INPUT_SLOT + x + (y * 3);
				guiItemStacks.init(index, true, x * 18, y * 18);
			}
		}

		List<List<ItemStack>> inputs = ingredients.getInputs(ItemStack.class);
		List<List<ItemStack>> outputs = ingredients.getOutputs(ItemStack.class);

		if (recipeWrapper instanceof ShapedResearchRecipeWrapper) {
			ShapedResearchRecipeWrapper shapedWrapper = (ShapedResearchRecipeWrapper) recipeWrapper;
			craftingGridHelper.setInputs(guiItemStacks, inputs, shapedWrapper.getWidth(), shapedWrapper.getHeight());
		} else {
			craftingGridHelper.setInputs(guiItemStacks, inputs);
		}
		guiItemStacks.set(CRAFT_OUTPUT_SLOT, outputs.get(0));
	}
}
