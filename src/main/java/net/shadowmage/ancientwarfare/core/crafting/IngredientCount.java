package net.shadowmage.ancientwarfare.core.crafting;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class IngredientCount extends Ingredient implements IIngredientCount {
	private final int count;
	private ItemStack[] array = null;

	public IngredientCount(ItemStack stack) {
		super(stack);
		this.count = stack.getCount();
	}

	@Override
	public ItemStack[] getMatchingStacks() {
		if (array == null) {
			List<ItemStack> matchingStacks = Arrays.stream(super.getMatchingStacks()).map(s -> new ItemStack(s.serializeNBT())).collect(Collectors.toList());
			matchingStacks.forEach(s -> s.setCount(count));
			array = matchingStacks.toArray(new ItemStack[matchingStacks.size()]);
		}
		return array;
	}

	@Override
	public boolean apply(@Nullable ItemStack input) {
		return super.apply(input) && input != null && input.getCount() >= count;
	}

	@Override
	public int getCount() {
		return count;
	}
}
