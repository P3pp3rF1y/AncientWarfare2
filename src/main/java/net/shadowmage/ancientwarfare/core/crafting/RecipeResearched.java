package net.shadowmage.ancientwarfare.core.crafting;

import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.shadowmage.ancientwarfare.core.research.ResearchGoal;

import java.util.HashSet;
import java.util.Set;

public class RecipeResearched extends ShapedOreRecipe {

    private final Set<Integer> neededResearch = new HashSet<Integer>();
    private int recipeWidth, recipeHeight;

    public RecipeResearched(ItemStack output, Object... input) {
        super(output, input);
        setMirrored(false);
    }

    protected final RecipeResearched addResearch(String... names) {
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

    protected final RecipeResearched addResearch(int... nums) {
        ResearchGoal g;
        for (int k : nums) {
            g = ResearchGoal.getGoal(k);
            if (g != null) {
                neededResearch.add(k);
            }
        }
        return this;
    }

    public Set<Integer> getNeededResearch() {
        return neededResearch;
    }

    public int getRecipeWidth() {
        if (recipeWidth == 0) {
            recipeWidth = ReflectionHelper.getPrivateValue(ShapedOreRecipe.class, this, "width");
        }
        return recipeWidth;
    }

    public int getRecipeHeight() {
        if (recipeHeight == 0) {
            recipeHeight = ReflectionHelper.getPrivateValue(ShapedOreRecipe.class, this, "height");
        }
        return recipeHeight;
    }
}
