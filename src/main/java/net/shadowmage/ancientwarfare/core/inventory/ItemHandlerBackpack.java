package net.shadowmage.ancientwarfare.core.inventory;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.shadowmage.ancientwarfare.core.api.AWItems;

import javax.annotation.Nonnull;

public class ItemHandlerBackpack extends ItemStackHandler {
	public ItemHandlerBackpack(int size) {
		super(size);
	}

	@Nonnull
	@Override
	public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
		return stack.getItem() != AWItems.backpack ? super.insertItem(slot, stack, simulate) : stack;
	}
}
