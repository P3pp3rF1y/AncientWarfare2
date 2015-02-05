package net.shadowmage.ancientwarfare.core.inventory;


import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.item.ItemBackpack;

public class InventoryBackpack extends InventoryBasic {

    public InventoryBackpack(int size) {
        super(size);
    }

    @Override
    public String toString() {
        return "Backpack size: " + getSizeInventory();
    }

    @Override
    public boolean isItemValidForSlot(int var1, ItemStack var2) {
        if(var2!=null && var2.getItem() instanceof ItemBackpack)
            return false;
        return super.isItemValidForSlot(var1, var2);
    }
}
