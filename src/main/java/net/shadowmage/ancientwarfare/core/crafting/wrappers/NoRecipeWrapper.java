package net.shadowmage.ancientwarfare.core.crafting.wrappers;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.shadowmage.ancientwarfare.core.crafting.ICraftingRecipe;
import net.shadowmage.ancientwarfare.core.crafting.RecipeResourceLocation;

import java.util.Optional;

public class NoRecipeWrapper implements ICraftingRecipe {
	public static final NoRecipeWrapper INSTANCE = new NoRecipeWrapper();

	private NoRecipeWrapper() {
	}

	@Override
	public boolean isValid() {
		return false;
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		return NonNullList.create();
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return ItemStack.EMPTY;
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting invCrafting) {
		return NonNullList.create();
	}

	@Override
	public RecipeResourceLocation getRegistryName() {
		return RecipeResourceLocation.NO_RECIPE_REGISTRY_NAME;
	}

	@Override
	public Optional<String> getNeededResearch() {
		return Optional.empty();
	}

	@Override
	public int getRecipeWidth() {
		return 0;
	}

	@Override
	public int getRecipeHeight() {
		return 0;
	}
}
