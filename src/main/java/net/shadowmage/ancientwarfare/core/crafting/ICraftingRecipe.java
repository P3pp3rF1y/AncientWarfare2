package net.shadowmage.ancientwarfare.core.crafting;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;

import java.util.Optional;

public interface ICraftingRecipe {
	boolean isValid();

	NonNullList<Ingredient> getIngredients();

	ItemStack getCraftingResult(InventoryCrafting inv);

	ItemStack getRecipeOutput();

	NonNullList<ItemStack> getRemainingItems(InventoryCrafting invCrafting);

	RecipeResourceLocation getRegistryName();

	Optional<String> getNeededResearch();

	int getRecipeWidth();

	int getRecipeHeight();
}
