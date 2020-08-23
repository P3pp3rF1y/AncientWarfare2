package net.shadowmage.ancientwarfare.core.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.shadowmage.ancientwarfare.core.init.AWCoreItems;
import net.shadowmage.ancientwarfare.core.item.ItemBackpack;

public class ItemHandlerBackpack implements IItemHandlerModifiable {
	private static final String BACKPACK_ITEMS_TAG = "backpackItems";
	private final ItemStackHandler backpackInventory;
	private final ItemStack backpackStack;

	public ItemHandlerBackpack(ItemStack backpackStack) {
		backpackInventory = getHandler(backpackStack);
		this.backpackStack = backpackStack;
	}

	@Override
	public int getSlots() {
		return backpackInventory.getSlots();
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return backpackInventory.getStackInSlot(slot);
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		ItemStack ret = stack;
		if (stack.getItem() != AWCoreItems.BACKPACK) {
			ret = backpackInventory.insertItem(slot, stack, simulate);
			if (ret.getCount() < stack.getCount()) {
				saveToStack(backpackInventory);
			}
		}
		return ret;
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		ItemStack ret = backpackInventory.extractItem(slot, amount, simulate);
		if (!ret.isEmpty()) {
			saveToStack(backpackInventory);
		}
		return ret;
	}

	@Override
	public int getSlotLimit(int slot) {
		return backpackInventory.getSlotLimit(slot);
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
		backpackInventory.setStackInSlot(slot, stack);
		saveToStack(backpackInventory);
	}

	private ItemStackHandler getHandler(ItemStack stack) {
		if (!stack.isEmpty() && stack.getItem() instanceof ItemBackpack) {
			ItemStackHandler handler = new ItemStackHandler((stack.getItemDamage() + 1) * 9);
			//noinspection ConstantConditions
			if (stack.hasTagCompound() && stack.getTagCompound().hasKey(BACKPACK_ITEMS_TAG)) {
				handler.deserializeNBT(stack.getTagCompound().getCompoundTag(BACKPACK_ITEMS_TAG));
			}
			return handler;
		}
		return new ItemStackHandler();
	}

	private void saveToStack(ItemStackHandler handler) {
		NBTTagCompound invTag = handler.serializeNBT();
		backpackStack.setTagInfo(BACKPACK_ITEMS_TAG, invTag);
	}
}
