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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ShapedResearchRecipe extends ResearchRecipeBase {

	protected int width = 0;
	protected int height = 0;

	public ShapedResearchRecipe(String research, ItemStack output, CraftingHelper.ShapedPrimer primer) {
		super(research, primer.input, output);
		this.width = primer.width;
		this.height = primer.height;
	}

	@Override
	public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World world) {
		for (int x = 0; x <= ShapedOreRecipe.MAX_CRAFT_GRID_WIDTH - width; x++) {
			for (int y = 0; y <= ShapedOreRecipe.MAX_CRAFT_GRID_HEIGHT - height; ++y) {
				if (checkMatch(inv, x, y, false)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Based on {@link net.minecraft.item.crafting.ShapedRecipes#checkMatch(InventoryCrafting, int, int, boolean)}
	 */
	private boolean checkMatch(InventoryCrafting inv, int startX, int startY, boolean mirror) {
		for (int x = 0; x < ShapedOreRecipe.MAX_CRAFT_GRID_WIDTH; x++) {
			for (int y = 0; y < ShapedOreRecipe.MAX_CRAFT_GRID_HEIGHT; y++) {
				int subX = x - startX;
				int subY = y - startY;
				Ingredient target = Ingredient.EMPTY;

				if (subX >= 0 && subY >= 0 && subX < width && subY < height) {
					if (mirror) {
						target = getIngredients().get(width - subX - 1 + subY * width);
					} else {
						target = getIngredients().get(subX + subY * width);
					}
				}

				if (!target.apply(inv.getStackInRowAndColumn(x, y))) {
					return false;
				}
			}
		}

		return true;
	}

	public int getRecipeWidth() {
		return width;
	}

	public int getRecipeHeight() {
		return height;
	}

	@Override
	public IRecipe getCraftingRecipe() {
		return new ShapedWrapper(this);
	}

	public static class ShapedWrapper implements IShapedRecipe, IForgeRegistryEntry<IRecipe> {

		private TypeToken<IRecipe> token = new TypeToken<IRecipe>(getClass()) {
		};
		private ShapedResearchRecipe recipe;

		public ShapedWrapper(ShapedResearchRecipe recipe) {
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

		@Override
		public NonNullList<Ingredient> getIngredients() {
			return recipe.getIngredients();
		}
	}
}
