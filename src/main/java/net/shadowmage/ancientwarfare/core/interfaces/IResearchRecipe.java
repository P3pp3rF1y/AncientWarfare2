package net.shadowmage.ancientwarfare.core.interfaces;

import net.minecraft.item.crafting.IRecipe;

import java.util.Set;

public interface IResearchRecipe extends IRecipe {

    Set<Integer> getNeededResearch();

    int getRecipeWidth();

    int getRecipeHeight();

    IResearchRecipe addResearch(String... names);

    Object[] getInputs();
}
