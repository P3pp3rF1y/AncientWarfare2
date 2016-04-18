package net.shadowmage.ancientwarfare.core.crafting;

import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.shadowmage.ancientwarfare.core.interfaces.IResearchRecipe;
import net.shadowmage.ancientwarfare.core.research.ResearchGoal;

import java.util.HashSet;
import java.util.Set;

public class RecipeResearched extends ShapedOreRecipe implements IResearchRecipe {

    private final Set<Integer> neededResearch = new HashSet<Integer>();
    private int recipeWidth, recipeHeight;

    public RecipeResearched(ItemStack output, Object... input) {
        super(output, input);
        setMirrored(false);
    }

    @Override
    public final IResearchRecipe addResearch(String... names) {
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

    @Override
    public Object[] getInputs() {
        return getInput();
    }

    @Override
    public Set<Integer> getNeededResearch() {
        return neededResearch;
    }

    @Override
    public int getRecipeWidth() {
        if (recipeWidth == 0) {
            recipeWidth = ReflectionHelper.getPrivateValue(ShapedOreRecipe.class, this, "width");
        }
        return recipeWidth;
    }

    @Override
    public int getRecipeHeight() {
        if (recipeHeight == 0) {
            recipeHeight = ReflectionHelper.getPrivateValue(ShapedOreRecipe.class, this, "height");
        }
        return recipeHeight;
    }
}
