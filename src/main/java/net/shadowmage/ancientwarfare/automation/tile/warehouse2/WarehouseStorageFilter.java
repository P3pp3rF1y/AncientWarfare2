package net.shadowmage.ancientwarfare.automation.tile.warehouse2;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.shadowmage.ancientwarfare.core.inventory.ItemHashEntry;

public class WarehouseStorageFilter implements INBTSerializable<NBTTagCompound> {
	ItemHashEntry hashKey;
	ItemStack item = ItemStack.EMPTY;

	public WarehouseStorageFilter() {}

	public WarehouseStorageFilter(ItemStack filter) {
		setFilterItem(filter);
	}

	public ItemStack getFilterItem() {
		return item;
	}

	public void setFilterItem(ItemStack itemStack) {
		item = itemStack;
		hashKey = item.isEmpty() ? null : new ItemHashEntry(item);
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setTag("item", item.writeToNBT(new NBTTagCompound()));
		return tag;
	}

	@Override
	public void deserializeNBT(NBTTagCompound tag) {
		if (tag.hasKey("item"))
			setFilterItem(new ItemStack(tag.getCompoundTag("item")));
		else
			setFilterItem(ItemStack.EMPTY);
	}
}
