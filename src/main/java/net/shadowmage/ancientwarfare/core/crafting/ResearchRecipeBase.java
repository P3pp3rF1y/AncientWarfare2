package net.shadowmage.ancientwarfare.core.crafting;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.shadowmage.ancientwarfare.core.research.ResearchGoal;

import javax.annotation.Nonnull;

public abstract class ResearchRecipeBase extends IForgeRegistryEntry.Impl<ResearchRecipeBase> {
	private int neededResearch = -1;
	private ItemStack output = ItemStack.EMPTY;
	private NonNullList<Ingredient> input = null;

	public ResearchRecipeBase(String research, NonNullList<Ingredient> input, ItemStack output) {
		addResearch(research);
		this.input = input;
		this.output = output;
	}

	public int getNeededResearch() {
		return neededResearch;
	}

	public ItemStack getCraftingResult(InventoryCrafting inv) {
		return output.copy();
	}

	public ItemStack getRecipeOutput() {
		return output;
	}

	public NonNullList<Ingredient> getIngredients() {
		return input;
	}

	private void addResearch(String name) {
		ResearchGoal g;
		name = name.startsWith("research.") ? name : "research." + name;
		g = ResearchGoal.getGoal(name);
		if (g != null) {
			neededResearch = g.getId();
		} else {
			throw new IllegalArgumentException("COULD NOT LOCATE RESEARCH GOAL FOR NAME: " + name);
		}
	}

	abstract boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World world);

	abstract IRecipe getCraftingRecipe();
}
