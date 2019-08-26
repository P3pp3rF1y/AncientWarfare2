package net.shadowmage.ancientwarfare.automation.tile.warehouse2;

import com.google.common.base.Predicate;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.shadowmage.ancientwarfare.core.inventory.ItemHashEntry;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

import javax.annotation.Nullable;

public final class WarehouseInterfaceFilter implements Predicate<ItemStack>, INBTSerializable<NBTTagCompound> {
	private static final String FILTER_TAG = "filter";

	private ItemStack filterItem = ItemStack.EMPTY;
	private int quantity;

	@Override
	@SuppressWarnings("squid:S4449") //if we got null stack here there is something seriously wrong in the code using this and it should be fixed instead
	public boolean apply(@Nullable ItemStack item) {
		//noinspection ConstantConditions
		return InventoryTools.doItemStacksMatchRelaxed(filterItem, item);
	}

	@Override
	public boolean equals(@Nullable Object object) {
		return object instanceof WarehouseInterfaceFilter && ((WarehouseInterfaceFilter) object).quantity == this.quantity && ItemStack.areItemStacksEqual(this.filterItem, ((WarehouseInterfaceFilter) object).filterItem);
	}

	@Override
	public int hashCode() {
		int result = !getFilterItem().isEmpty() ? new ItemHashEntry(getFilterItem()).hashCode() : 0;
		return 31 * result + quantity;
	}

	public final ItemStack getFilterItem() {
		return filterItem;
	}

	public final void setFilterItem(ItemStack item) {
		this.filterItem = item;
	}

	public final int getFilterQuantity() {
		return quantity;
	}

	public final void setFilterQuantity(int filterQuantity) {
		this.quantity = filterQuantity;
	}

	@Override
	public String toString() {
		return "Filter item: " + filterItem + " quantity: " + quantity;
	}

	public WarehouseInterfaceFilter copy() {
		WarehouseInterfaceFilter filter = new WarehouseInterfaceFilter();
		if (!this.filterItem.isEmpty())
			filter.setFilterItem(this.filterItem.copy());
		filter.setFilterQuantity(this.quantity);
		return filter;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("quantity", quantity);
		if (!filterItem.isEmpty()) {
			tag.setTag(FILTER_TAG, filterItem.writeToNBT(new NBTTagCompound()));
		}
		return tag;
	}

	@Override
	public void deserializeNBT(NBTTagCompound tag) {
		quantity = tag.getInteger("quantity");
		if (tag.hasKey(FILTER_TAG)) {
			filterItem = new ItemStack(tag.getCompoundTag(FILTER_TAG));
		}
	}
}
