package net.shadowmage.ancientwarfare.core.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public final class SlotLimited extends Slot {
    public SlotLimited(final IInventory inventory, final int slotIndex, int xPos, int yPos) {
        super(inventory, slotIndex, xPos, yPos);
    }

    @Override
    public boolean isItemValid(ItemStack par1ItemStack) {
        return par1ItemStack!=null && inventory.isItemValidForSlot(getSlotIndex(), par1ItemStack);
    }
}
