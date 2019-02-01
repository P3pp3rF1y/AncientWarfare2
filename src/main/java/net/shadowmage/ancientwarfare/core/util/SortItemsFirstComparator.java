package net.shadowmage.ancientwarfare.core.util;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class SortItemsFirstComparator implements Comparator<ItemStack> {
	private Map<Predicate<ItemStack>, Integer> firstElements = new HashMap<>();

	public SortItemsFirstComparator(Object... firstElements) {
		for (int i = 0; i < firstElements.length; i++) {
			Object element = firstElements[i];

			Predicate<ItemStack> matches;
			if (element instanceof Item) {
				matches = s -> s.getItem() == element;
			} else if (element instanceof Block) {
				matches = s -> s.getItem() instanceof ItemBlock && ((ItemBlock) s.getItem()).getBlock() == element;
			} else if (Block.class.isAssignableFrom((Class<?>) element)) {
				matches = s -> s.getItem() instanceof ItemBlock && ((Class<?>) element).isAssignableFrom(((ItemBlock) s.getItem()).getBlock().getClass());
			} else if (Item.class.isAssignableFrom((Class<?>) element)) {
				matches = s -> ((Class<?>) element).isAssignableFrom(s.getItem().getClass());
			} else {
				continue;
			}

			this.firstElements.put(matches, firstElements.length - i);
		}
	}

	@Override
	public int compare(ItemStack o1, ItemStack o2) {
		if (o1 == o2 || o1.getItem() == o2.getItem()) {
			return 0;
		}

		int sortWeight1 = 0;
		int sortWeight2 = 0;
		for (Map.Entry<Predicate<ItemStack>, Integer> entry : firstElements.entrySet()) {
			if (entry.getKey().test(o1)) {
				sortWeight1 = entry.getValue();
			}
			if (entry.getKey().test(o2)) {
				sortWeight2 = entry.getValue();
			}

			if (sortWeight1 > 0 && sortWeight2 > 0) {
				break;
			}
		}

		return sortWeight2 - sortWeight1;
	}
}
