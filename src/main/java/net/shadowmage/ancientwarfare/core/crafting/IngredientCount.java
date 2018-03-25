package net.shadowmage.ancientwarfare.core.crafting;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

import javax.annotation.Nullable;

public class IngredientCount extends Ingredient implements IIngredientCount {
	private final int count;

	public IngredientCount(ItemStack stack) {
		super(stack);
		this.count = stack.getCount();
	}

	@Override
	public boolean apply(@Nullable ItemStack stack) {
		return stack != null && stack.getCount() >= count && super.apply(stack);
	}

	@Override
	public int getCount() {
		return count;
	}
}
