package net.shadowmage.ancientwarfare.core.crafting;

import com.google.common.reflect.TypeToken;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IShapedRecipe;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.shadowmage.ancientwarfare.core.research.ResearchGoal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ResearchRecipe extends IForgeRegistryEntry.Impl<ResearchRecipe> {

    private int neededResearch = -1;
    protected ItemStack output = ItemStack.EMPTY;
    protected NonNullList<Ingredient> input = null;
    protected int width = 0;
    protected int height = 0;

    public ResearchRecipe(String research, ItemStack output, CraftingHelper.ShapedPrimer primer) {
        this.output = output;
        this.width = primer.width;
        this.height = primer.height;
        this.input = primer.input;

        addResearch(research);
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

    public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World world)
    {
        for (int x = 0; x <= ShapedOreRecipe.MAX_CRAFT_GRID_WIDTH - width; x++)
        {
            for (int y = 0; y <= ShapedOreRecipe.MAX_CRAFT_GRID_HEIGHT - height; ++y)
            {
                if (checkMatch(inv, x, y, false))
                {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Based on {@link net.minecraft.item.crafting.ShapedRecipes#checkMatch(InventoryCrafting, int, int, boolean)}
     */
    protected boolean checkMatch(InventoryCrafting inv, int startX, int startY, boolean mirror)
    {
        for (int x = 0; x < ShapedOreRecipe.MAX_CRAFT_GRID_WIDTH; x++)
        {
            for (int y = 0; y < ShapedOreRecipe.MAX_CRAFT_GRID_HEIGHT; y++)
            {
                int subX = x - startX;
                int subY = y - startY;
                Ingredient target = Ingredient.EMPTY;

                if (subX >= 0 && subY >= 0 && subX < width && subY < height)
                {
                    if (mirror)
                    {
                        target = input.get(width - subX - 1 + subY * width);
                    }
                    else
                    {
                        target = input.get(subX + subY * width);
                    }
                }

                if (!target.apply(inv.getStackInRowAndColumn(x, y)))
                {
                    return false;
                }
            }
        }

        return true;
    }

    public ItemStack getCraftingResult(InventoryCrafting inv) {
        return output.copy();
    }

    public ItemStack getRecipeOutput() {
        return output;
    }

    public int getRecipeWidth() {
        return width;
    }

    public int getRecipeHeight() {
        return height;
    }

    public NonNullList<Ingredient> getIngredients() {
        return input;
    }

    public static class IRecipeWrapper implements IShapedRecipe, IForgeRegistryEntry<IRecipe> {

        private TypeToken<IRecipe> token = new TypeToken<IRecipe>(getClass()){};
        private ResearchRecipe recipe;
        public IRecipeWrapper(ResearchRecipe recipe) {
            this.recipe = recipe;
        }

		@Override
		public boolean matches(InventoryCrafting inv, World worldIn) {
			return recipe.matches(inv, worldIn);
		}

		@Override
		public ItemStack getCraftingResult(InventoryCrafting inv) {
			return recipe.getCraftingResult(inv);
		}

		@Override
		public boolean canFit(int width, int height) {
			return width >= this.getRecipeWidth() && height >= this.getRecipeHeight();
		}

		@Override
		public ItemStack getRecipeOutput() {
			return recipe.getRecipeOutput();
		}

		@Override
		public int getRecipeWidth() {
			return recipe.getRecipeWidth();
		}

		@Override
		public int getRecipeHeight() {
			return recipe.getRecipeHeight();
		}

        @Override
        public IRecipe setRegistryName(ResourceLocation name) {
            recipe.setRegistryName(name);
            return this;
        }

        @Nullable
        @Override
        public ResourceLocation getRegistryName() {
            return recipe.getRegistryName();
        }

        @Override
        public Class<IRecipe> getRegistryType() {
            return (Class<IRecipe>) token.getRawType();
        }
    }
}
