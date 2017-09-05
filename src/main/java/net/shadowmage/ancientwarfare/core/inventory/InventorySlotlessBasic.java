package net.shadowmage.ancientwarfare.core.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class InventorySlotlessBasic {

    private final int totalSize;
    private int currentSize;
    private final ItemQuantityMap itemMap = new ItemQuantityMap();

    public InventorySlotlessBasic(int totalSize) {
        this.totalSize = totalSize;
    }

    /*
     * add to the input map all items contained in this inventory
     */
    public void getItems(ItemQuantityMap map) {
        map.addAll(itemMap);
    }

    public int getQuantityStored(ItemStack filter) {
        return itemMap.getCount(filter);
    }

    public int getAvailableSpaceFor(ItemStack filter) {
        return totalSize - currentSize;
    }

    public int extractItem(ItemStack filter, int amount) {
        if (amount <= 0 || filter.isEmpty()) {
            return 0;
        }
        int count = itemMap.getCount(filter);
        amount = amount > count ? count : amount;
        itemMap.decreaseCount(filter, amount);
        currentSize -= amount;
        return amount;
    }

    public int insertItem(ItemStack filter, int amount) {
        if (amount <= 0 || filter.isEmpty()) {
            return 0;
        }
        if(amount > (totalSize - currentSize))
            amount = totalSize - currentSize;
        itemMap.addCount(filter, amount);
        currentSize += amount;
        return amount;
    }

    public void readFromNBT(NBTTagCompound tag) {
        itemMap.readFromNBT(tag.getCompoundTag("itemMap"));
        currentSize = itemMap.getTotalItemCount();
    }

    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tag.setTag("itemMap", itemMap.writeToNBT(new NBTTagCompound()));
        return tag;
    }

}
