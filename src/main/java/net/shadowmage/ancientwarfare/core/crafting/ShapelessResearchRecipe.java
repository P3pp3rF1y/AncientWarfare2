package net.shadowmage.ancientwarfare.core.crafting;

import com.google.common.reflect.TypeToken;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShapelessResearchRecipe extends ResearchRecipeBase {

	public ShapelessResearchRecipe(String research, NonNullList<Ingredient> ings, ItemStack result) {
		super(research, ings, result);
	}

	@Override
	public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World world) {
		NonNullList<Ingredient> ingredients = getIngredients();
		List<Boolean> ingredientsMatched = new ArrayList<>(Collections.nCopies(ingredients.size(), false));

		for (int i = 0; i < inv.getSizeInventory(); ++i) {
			ItemStack itemstack = inv.getStackInSlot(i);
			if (itemstack.isEmpty()) {
				continue;
			}
			boolean matched = false;
			for (int j = 0; j < ingredients.size(); j++) {
				if (ingredients.get(j).apply(itemstack)) {
					ingredientsMatched.set(j, true);
					matched = true;
					break;
				}
			}
			if (!matched) {
				return false;
			}
		}

		return !ingredientsMatched.contains(false);
	}

	@Override
	public IRecipe getCraftingRecipe() {
		return new ShapelessWrapper(this);
	}

	public static class ShapelessWrapper implements IRecipe, IForgeRegistryEntry<IRecipe> {

		private TypeToken<IRecipe> token = new TypeToken<IRecipe>(getClass()) {
		};
		private final ShapelessResearchRecipe recipe;

		ShapelessWrapper(ShapelessResearchRecipe recipe) {
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
			return recipe.getIngredients().size() >= width * height;
		}

		@Override
		public ItemStack getRecipeOutput() {
			return recipe.getRecipeOutput();
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
		@SuppressWarnings("unchecked")
		public Class<IRecipe> getRegistryType() {
			return (Class<IRecipe>) token.getRawType();
		}
	}
}
