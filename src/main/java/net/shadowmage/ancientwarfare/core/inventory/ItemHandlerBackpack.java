package net.shadowmage.ancientwarfare.core.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.shadowmage.ancientwarfare.core.api.AWItems;
import net.shadowmage.ancientwarfare.core.item.ItemBackpack;

import javax.annotation.Nonnull;

public class ItemHandlerBackpack implements IItemHandlerModifiable {
	private ItemStack backpackStack;
	private ItemStackHandler handler;

	public ItemHandlerBackpack(ItemStack backpackStack) {
		this.backpackStack = backpackStack;
		this.handler = getHandler(backpackStack);
	}

	@Override
	public int getSlots() {
		return handler.getSlots();
	}

	@Nonnull
	@Override
	public ItemStack getStackInSlot(int slot) {
		return handler.getStackInSlot(slot);
	}

	@Nonnull
	@Override
	public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
		ItemStack ret = stack;
		if (stack.getItem() != AWItems.backpack) {
			ret = handler.insertItem(slot, stack, simulate);
			saveToStack();
		}
		return ret;
	}

	@Nonnull
	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		ItemStack ret = handler.extractItem(slot, amount, simulate);
		saveToStack();
		return ret;
	}

	@Override
	public int getSlotLimit(int slot) {
		return handler.getSlotLimit(slot);
	}

	@Override
	public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
		handler.setStackInSlot(slot, stack);
		saveToStack();
	}

	private ItemStackHandler getHandler(ItemStack stack) {
		if (!stack.isEmpty() && stack.getItem() instanceof ItemBackpack) {
			ItemStackHandler handler = new ItemStackHandler((stack.getItemDamage() + 1) * 9);
			//noinspection ConstantConditions
			if (stack.hasTagCompound() && stack.getTagCompound().hasKey("backpackItems")) {
				handler.deserializeNBT(stack.getTagCompound().getCompoundTag("backpackItems"));
			}
			return handler;
		}
		return null;
	}

	private void saveToStack() {
		NBTTagCompound invTag = handler.serializeNBT();
		backpackStack.setTagInfo("backpackItems", invTag);
	}
}
