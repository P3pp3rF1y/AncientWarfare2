package net.shadowmage.ancientwarfare.core.crafting;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.research.ResearchGoal;

import java.util.HashSet;
import java.util.Set;

public class UnformedRecipeResearched extends ShapelessOreRecipe {
    private final Set<Integer> neededResearch = new HashSet<>();

    public UnformedRecipeResearched(ItemStack result, Object... recipe) {
        super(new ResourceLocation(AncientWarfareCore.modID, result.getItem().getRegistryName().getResourcePath()), result, recipe);
    }

    public Set<Integer> getNeededResearch() {
        return neededResearch;
    }

    private int getRecipeSize() {
        return getIngredients().size();
    }

    public int getRecipeHeight() {
        int sqrt = (int) Math.sqrt(getRecipeSize());
        int diff = getRecipeSize() - (sqrt * sqrt);
        if(diff != 0)
            return sqrt + 1;
        return sqrt;
    }

    public int getRecipeWidth() {
        int h = getRecipeHeight();
        for(int i = 1; i < h + 2; i++){
            if(h * i >= getRecipeSize()){
                return i;
            }
        }
        return h + 2;
    }

    public UnformedRecipeResearched addResearch(String... names) {
        ResearchGoal g;
        for (String name : names) {
            name = name.startsWith("research.") ? name : "research." + name;
            g = ResearchGoal.getGoal(name);
            if (g != null) {
                neededResearch.add(g.getId());
            } else {
                throw new IllegalArgumentException("COULD NOT LOCATE RESEARCH GOAL FOR NAME: " + name);
            }
        }
        return this;
    }
}
