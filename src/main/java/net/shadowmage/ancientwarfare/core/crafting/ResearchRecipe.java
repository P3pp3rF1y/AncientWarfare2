//TODO recipes
package net.shadowmage.ancientwarfare.core.crafting;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.research.ResearchGoal;

public class ResearchRecipe extends ShapedOreRecipe implements IRecipe {

    private int neededResearch = -1;
    private int recipeWidth, recipeHeight;

    public ResearchRecipe(String research, ItemStack output, Object... input) {
        super(new ResourceLocation(AncientWarfareCore.modID, "research_recipe"), output, input);
        setMirrored(false);
        addResearch(research);
        AWCraftingManager.addRecipe(this);
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

    public int getNeededResearch() {
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
