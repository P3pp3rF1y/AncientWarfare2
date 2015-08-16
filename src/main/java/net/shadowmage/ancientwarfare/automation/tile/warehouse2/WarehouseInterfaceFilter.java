package net.shadowmage.ancientwarfare.automation.tile.warehouse2;

import com.google.common.base.Predicate;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.interfaces.INBTSerialable;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

public final class WarehouseInterfaceFilter implements Predicate<ItemStack>, INBTSerialable {

    private ItemStack filterItem;
    private int quantity;

    public WarehouseInterfaceFilter() {
    }

    @Override
    public boolean apply(ItemStack item) {
        if (item == null) {
            return false;
        }
        if (filterItem == null) {
            return false;
        }//null filter item, invalid filter
        if (item.getItem() != filterItem.getItem()) {
            return false;
        }//item not equivalent, obvious mis-match
        return InventoryTools.doItemStacksMatch(item, filterItem);//finally, items were equal, no ignores' -- check both dmg and tag
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof WarehouseInterfaceFilter && ((WarehouseInterfaceFilter) object).quantity == this.quantity && this.apply(((WarehouseInterfaceFilter) object).filterItem);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        quantity = tag.getInteger("quantity");
        if (tag.hasKey("filter")) {
            filterItem = InventoryTools.readItemStack(tag.getCompoundTag("filter"));
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tag.setInteger("quantity", quantity);
        if (filterItem != null) {
            tag.setTag("filter", InventoryTools.writeItemStack(filterItem));
        }
        return tag;
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
        if(this.filterItem!=null)
            filter.setFilterItem(this.filterItem.copy());
        filter.setFilterQuantity(this.quantity);
        return filter;
    }

}
