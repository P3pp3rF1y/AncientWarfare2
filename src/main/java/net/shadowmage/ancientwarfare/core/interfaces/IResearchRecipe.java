package net.shadowmage.ancientwarfare.core.interfaces;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;

import java.util.Set;

public interface IResearchRecipe extends IRecipe {

    Set<Integer> getNeededResearch();

    int getRecipeWidth();

    int getRecipeHeight();

    IResearchRecipe addResearch(String... names);

    NonNullList<Ingredient> getIngredients();
}
