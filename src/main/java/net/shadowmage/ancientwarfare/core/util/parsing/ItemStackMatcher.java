package net.shadowmage.ancientwarfare.core.util.parsing;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.function.Predicate;

public class ItemStackMatcher implements Predicate<ItemStack> {
	private Item item;
	private int meta;

	public ItemStackMatcher(Item item, int meta) {
		this.item = item;
		this.meta = meta;
	}

	@Override
	public boolean test(ItemStack input) {
		return input != null && input.getItem() == item && input.getMetadata() == meta;
	}
}
