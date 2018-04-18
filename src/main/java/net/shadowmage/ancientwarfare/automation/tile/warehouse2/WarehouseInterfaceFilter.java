package net.shadowmage.ancientwarfare.automation.tile.warehouse2;

import com.google.common.base.Predicate;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.shadowmage.ancientwarfare.core.inventory.ItemQuantityMap;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

import javax.annotation.Nonnull;

public final class WarehouseInterfaceFilter implements Predicate<ItemStack>, INBTSerializable<NBTTagCompound> {

	@Nonnull
	private ItemStack filterItem = ItemStack.EMPTY;
	private int quantity;

	public WarehouseInterfaceFilter() {
	}

	@Override
	public boolean apply(ItemStack item) {
		if (item.isEmpty()) {
			return false;
		}
		if (filterItem.isEmpty()) {
			return false;
		}//null filter item, invalid filter
		if (item.getItem() != filterItem.getItem()) {
			return false;
		}//item not equivalent, obvious mis-match
		return InventoryTools.doItemStacksMatchRelaxed(filterItem, item);//finally, items were equal, no ignores' -- check both dmg and tag
	}

	@Override
	public boolean equals(Object object) {
		return object instanceof WarehouseInterfaceFilter && ((WarehouseInterfaceFilter) object).quantity == this.quantity && ItemStack
				.areItemStacksEqual(this.filterItem, ((WarehouseInterfaceFilter) object).filterItem);
	}

	@Override
	public int hashCode() {
		int result = !getFilterItem().isEmpty() ? new ItemQuantityMap.ItemHashEntry(getFilterItem()).hashCode() : 0;
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
			tag.setTag("filter", filterItem.writeToNBT(new NBTTagCompound()));
		}
		return tag;
	}

	@Override
	public void deserializeNBT(NBTTagCompound tag) {
		quantity = tag.getInteger("quantity");
		if (tag.hasKey("filter")) {
			filterItem = new ItemStack(tag.getCompoundTag("filter"));
		}
	}
}
