package net.shadowmage.ancientwarfare.automation.tile.worksite.treefarm;

import net.minecraft.item.ItemStack;

import java.util.function.Predicate;

public class Sapling implements ISapling {
	private final Predicate<ItemStack> itemStackPredicate;
	private final boolean rightClick;

	public Sapling(Predicate<ItemStack> itemStackPredicate, boolean rightClick) {

		this.itemStackPredicate = itemStackPredicate;
		this.rightClick = rightClick;
	}

	@Override
	public boolean matches(ItemStack stack) {
		return itemStackPredicate.test(stack);
	}

	@Override
	public boolean isRightClick() {
		return rightClick;
	}
}
