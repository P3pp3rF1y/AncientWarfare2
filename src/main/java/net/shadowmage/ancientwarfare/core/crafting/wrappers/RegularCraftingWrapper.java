package net.shadowmage.ancientwarfare.core.crafting.wrappers;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.crafting.IShapedRecipe;
import net.shadowmage.ancientwarfare.core.crafting.ICraftingRecipe;
import net.shadowmage.ancientwarfare.core.crafting.RecipeResourceLocation;

import java.util.Optional;

public class RegularCraftingWrapper implements ICraftingRecipe {
	private final IRecipe recipe;
	private final RecipeResourceLocation registryName;

	public RegularCraftingWrapper(IRecipe recipe) {
		this.recipe = recipe;

		if (recipe.getRegistryName() == null) {
			throw new IllegalArgumentException("Null registryName recipes are not allowed here");
		}
		registryName = new RecipeResourceLocation(RecipeResourceLocation.RecipeType.REGULAR, recipe.getRegistryName());
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		return recipe.getIngredients();
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		return recipe.getCraftingResult(inv);
	}

	@Override
	public ItemStack getRecipeOutput() {
		return recipe.getRecipeOutput();
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting invCrafting) {
		return recipe.getRemainingItems(invCrafting);
	}

	@Override
	public RecipeResourceLocation getRegistryName() {
		return registryName;
	}

	@Override
	public Optional<String> getNeededResearch() {
		return Optional.empty();
	}

	@Override
	public int getRecipeWidth() {
		return recipe instanceof IShapedRecipe ? ((IShapedRecipe) recipe).getRecipeWidth() : 3;
	}

	@Override
	public int getRecipeHeight() {
		return recipe instanceof IShapedRecipe ? ((IShapedRecipe) recipe).getRecipeHeight() : 3;
	}
}
