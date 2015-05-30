package net.shadowmage.ancientwarfare.core.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.api.AWItems;
import net.shadowmage.ancientwarfare.core.item.ItemResearchBook;
import net.shadowmage.ancientwarfare.core.tile.TileEngineeringStation;

public class ContainerEngineeringStation extends ContainerBase {

    public TileEngineeringStation station;

    public ContainerEngineeringStation(EntityPlayer player, int x, int y, int z) {
        super(player);
        station = (TileEngineeringStation) player.worldObj.getTileEntity(x, y, z);
        if (station == null) {
            throw new IllegalArgumentException(" tile may not be null!!");
        }

        Slot slot = new SlotCrafting(player, station.layoutMatrix, station.result, 0, 3 * 18 + 3 * 18 + 8 + 18, 1 * 18 + 8) {
            @Override
            public void onPickupFromSlot(EntityPlayer par1EntityPlayer, ItemStack par2ItemStack) {
                station.preItemCrafted();
                super.onPickupFromSlot(par1EntityPlayer, par2ItemStack);
                station.onItemCrafted();
            }
        };
        addSlotToContainer(slot);

        slot = new Slot(station.bookInventory, 0, 8, 18 + 8) {
            @Override
            public boolean isItemValid(ItemStack par1ItemStack) {
                return par1ItemStack != null && par1ItemStack.getItem() == AWItems.researchBook && ItemResearchBook.getResearcherName(par1ItemStack) != null;
            }
        };
        addSlotToContainer(slot);

        int x2, y2, slotNum = 0;
        for (int y1 = 0; y1 < 3; y1++) {
            y2 = y1 * 18 + 8;
            for (int x1 = 0; x1 < 3; x1++) {
                x2 = x1 * 18 + 8 + 3 * 18;
                slotNum = y1 * 3 + x1;
                slot = new Slot(station.layoutMatrix, slotNum, x2, y2);
                addSlotToContainer(slot);
            }
        }

        for (int y1 = 0; y1 < 2; y1++) {
            y2 = y1 * 18 + 8 + 3 * 18 + 4;
            for (int x1 = 0; x1 < 9; x1++) {
                x2 = x1 * 18 + 8;
                slotNum = y1 * 9 + x1;
                slot = new Slot(station.extraSlots, slotNum, x2, y2);
                addSlotToContainer(slot);
            }
        }

        int y1 = 8 + 3 * 18 + 8 + 2 * 18 + 4;
        y1 = this.addPlayerSlots(y1);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int slotClickedIndex) {
        ItemStack slotStackCopy = null;
        Slot theSlot = this.getSlot(slotClickedIndex);
        if (theSlot != null && theSlot.getHasStack()) {
            ItemStack slotStack = theSlot.getStack();
            slotStackCopy = slotStack.copy();
            int craftSlotStart = 2;
            int storageSlotsStart = craftSlotStart + station.layoutMatrix.getSizeInventory();
            int playerSlotStart = storageSlotsStart + station.extraSlots.getSizeInventory();
            int playerSlotEnd = playerSlotStart + 36;
            if (slotClickedIndex < craftSlotStart)//book or result slot
            {
                if (!this.mergeItemStack(slotStack, playerSlotStart, playerSlotEnd, false))//merge into player inventory
                    return null;
            } else {
                if (slotClickedIndex < storageSlotsStart) {//craft matrix
                    if (!this.mergeItemStack(slotStack, storageSlotsStart, playerSlotStart, false))//merge into storage
                        return null;
                } else if (slotClickedIndex < playerSlotStart) {//storage slots
                    if (!this.mergeItemStack(slotStack, playerSlotStart, playerSlotEnd, false))//merge into player inventory
                        return null;
                } else if (slotClickedIndex < playerSlotEnd) {//player slots, merge into storage
                    if (!this.mergeItemStack(slotStack, storageSlotsStart, playerSlotStart, false))//merge into storage
                        return null;
                }
            }
            if (slotStack.stackSize == 0) {
                theSlot.putStack(null);
            } else {
                theSlot.onSlotChanged();
            }
            if (slotStack.stackSize == slotStackCopy.stackSize) {
                return null;
            }
            theSlot.onPickupFromSlot(par1EntityPlayer, slotStack);
        }
        return slotStackCopy;
    }

}
