package net.shadowmage.ancientwarfare.automation.tile.warehouse2;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.shadowmage.ancientwarfare.core.inventory.ItemQuantityMap.ItemHashEntry;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

import java.util.List;

public class WarehouseStorageFilter {

    ItemHashEntry hashKey;
    ItemStack item;

    private WarehouseStorageFilter() {
    }

    public WarehouseStorageFilter(ItemStack filter) {
        item = filter;
        hashKey = filter == null ? null : new ItemHashEntry(filter);
    }

    public ItemStack getFilterItem() {
        return item;
    }

    public void setFilterItem(ItemStack itemStack) {
        item = itemStack;
        hashKey = item == null ? null : new ItemHashEntry(item);
    }

    public void readFromNBT(NBTTagCompound tag) {
        item = tag.hasKey("item") ? InventoryTools.readItemStack(tag.getCompoundTag("item")) : null;
        hashKey = item == null ? null : new ItemHashEntry(item);
    }

    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        if (item != null) {
            tag.setTag("item", InventoryTools.writeItemStack(item));
        }
        return tag;
    }

    public static NBTTagList writeFilterList(List<WarehouseStorageFilter> itemFilters) {
        NBTTagList list = new NBTTagList();
        for (WarehouseStorageFilter filter : itemFilters) {
            list.appendTag(filter.writeToNBT(new NBTTagCompound()));
        }
        return list;
    }

    public static List<WarehouseStorageFilter> readFilterList(NBTTagList tagList, List<WarehouseStorageFilter> list) {
        WarehouseStorageFilter filter;
        for (int i = 0; i < tagList.tagCount(); i++) {
            filter = new WarehouseStorageFilter();
            filter.readFromNBT(tagList.getCompoundTagAt(i));
            list.add(filter);
        }
        return list;
    }

}
