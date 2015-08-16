package net.shadowmage.ancientwarfare.automation.tile.warehouse2;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.interfaces.INBTSerialable;
import net.shadowmage.ancientwarfare.core.inventory.ItemQuantityMap.ItemHashEntry;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

public class WarehouseStorageFilter implements INBTSerialable {

    ItemHashEntry hashKey;
    ItemStack item;

    public WarehouseStorageFilter() {
    }

    public WarehouseStorageFilter(ItemStack filter) {
        setFilterItem(filter);
    }

    public ItemStack getFilterItem() {
        return item;
    }

    public void setFilterItem(ItemStack itemStack) {
        item = itemStack;
        hashKey = item == null ? null : new ItemHashEntry(item);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        if(tag.hasKey("item"))
            setFilterItem(InventoryTools.readItemStack(tag.getCompoundTag("item")));
        else
            setFilterItem(null);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        if (item != null) {
            tag.setTag("item", InventoryTools.writeItemStack(item));
        }
        return tag;
    }
}
