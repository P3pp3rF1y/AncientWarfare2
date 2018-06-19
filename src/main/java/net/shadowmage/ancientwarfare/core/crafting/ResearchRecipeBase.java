package net.shadowmage.ancientwarfare.core.crafting;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.shadowmage.ancientwarfare.core.registry.ResearchRegistry;

import javax.annotation.Nonnull;

public abstract class ResearchRecipeBase extends IForgeRegistryEntry.Impl<ResearchRecipeBase> {
	private String neededResearch;
	private ItemStack output;
	private NonNullList<Ingredient> input;

	ResearchRecipeBase(String research, NonNullList<Ingredient> input, ItemStack output) {
		addResearch(research);
		this.input = input;
		this.output = output;
	}

	public String getNeededResearch() {
		return neededResearch;
	}

	public ItemStack getCraftingResult() {
		return output.copy();
	}

	public ItemStack getRecipeOutput() {
		return output;
	}

	public NonNullList<Ingredient> getIngredients() {
		return input;
	}

	private void addResearch(String name) {
		if (!ResearchRegistry.researchExists(name)) {
			throw new IllegalArgumentException("COULD NOT LOCATE RESEARCH GOAL FOR NAME: " + name);
		}

		neededResearch = name;
	}

	abstract boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World world);

	abstract IRecipe getCraftingRecipe();
}
