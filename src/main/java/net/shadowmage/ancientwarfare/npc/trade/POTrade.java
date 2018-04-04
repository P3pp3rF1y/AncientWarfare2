package net.shadowmage.ancientwarfare.npc.trade;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.IItemHandler;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

public class POTrade extends Trade {

	private NonNullList<ItemStack> compactInput = NonNullList.create();
	private NonNullList<ItemStack> compactOutput = NonNullList.create();

	@Override
	public void setInputStack(int index, ItemStack stack) {
		super.setInputStack(index, stack);
		updateCompactInput();
	}

	@Override
	public void setOutputStack(int index, ItemStack stack) {
		super.setOutputStack(index, stack);
		updateCompactOutput();
	}

	private void updateCompactInput() {
		NonNullList<ItemStack> list = NonNullList.create();
		for (ItemStack temp : input) {
			if (!temp.isEmpty()) {
				list.add(temp.copy());
			}
		}
		compactInput = InventoryTools.compactStackList(list);
	}

	private void updateCompactOutput() {
		NonNullList<ItemStack> list = NonNullList.create();
		for (ItemStack temp : output) {
			if (!temp.isEmpty()) {
				list.add(temp.copy());
			}
		}
		compactOutput = InventoryTools.compactStackList(list);
	}

	/*
	 * Check through the input inventory and ensure it contains all materials necessary to complete this trade.<br>
	 */
	public boolean isAvailable(IItemHandler storage) {
		for (ItemStack stack : compactOutput) {
			if (InventoryTools.getCountOf(storage, stack) < stack.getCount()) {
				return false;
			}
		}
		return InventoryTools.canInventoryHold(storage, compactInput);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		updateCompactInput();
		updateCompactOutput();
	}

}
