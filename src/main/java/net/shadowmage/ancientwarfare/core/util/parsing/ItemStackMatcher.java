package net.shadowmage.ancientwarfare.core.util.parsing;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.function.Predicate;

public class ItemStackMatcher implements Predicate<ItemStack> {
	private Item item;
	private Predicate<Integer> metaMatches;

	public ItemStackMatcher(Item item) {
		this.item = item;
		metaMatches = i -> true;
	}
	public ItemStackMatcher(Item item, int meta) {
		this.item = item;
		this.metaMatches = i -> i == meta;
	}

	@Override
	public boolean test(ItemStack input) {
		return input != null && input.getItem() == item && metaMatches.test(input.getMetadata());
	}
}
