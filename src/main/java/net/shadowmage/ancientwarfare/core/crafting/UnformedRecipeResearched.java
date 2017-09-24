package net.shadowmage.ancientwarfare.core.crafting;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.interfaces.IResearchRecipe;
import net.shadowmage.ancientwarfare.core.research.ResearchGoal;

import java.util.HashSet;
import java.util.Set;

public class UnformedRecipeResearched extends ShapelessOreRecipe implements IResearchRecipe{
    private final Set<Integer> neededResearch = new HashSet<>();

    public UnformedRecipeResearched(ItemStack result, Object... recipe) {
        super(new ResourceLocation(AncientWarfareCore.modID, result.getItem().getRegistryName().getResourcePath()), result, recipe);
    }

    @Override
    public Set<Integer> getNeededResearch() {
        return neededResearch;
    }

    private int getRecipeSize() {
        return getIngredients().size();
    }

    @Override
    public int getRecipeHeight() {
        int sqrt = (int) Math.sqrt(getRecipeSize());
        int diff = getRecipeSize() - (sqrt * sqrt);
        if(diff != 0)
            return sqrt + 1;
        return sqrt;
    }

    @Override
    public int getRecipeWidth() {
        int h = getRecipeHeight();
        for(int i = 1; i < h + 2; i++){
            if(h * i >= getRecipeSize()){
                return i;
            }
        }
        return h + 2;
    }

    @Override
    public IResearchRecipe addResearch(String... names) {
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
