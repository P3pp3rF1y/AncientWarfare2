package net.shadowmage.ancientwarfare.core.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import java.util.function.Predicate;

public class SlotFiltered extends Slot {

    private final Predicate<ItemStack> filter;

    public SlotFiltered(IInventory par1iInventory, int slotIndex, int xPos, int yPos, Predicate<ItemStack> filter) {
        super(par1iInventory, slotIndex, xPos, yPos);
        this.filter = filter;
    }

    @Override
    public final boolean isItemValid(ItemStack par1ItemStack) {
        if (filter != null) {
            return filter.test(par1ItemStack);
        }
        return super.isItemValid(par1ItemStack);
    }

    @Override
    public String toString() {
        return "Filtered slot: " + filter;
    }
}
