package net.shadowmage.ancientwarfare.automation.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.automation.tile.worksite.TileAutoCrafting;
import net.shadowmage.ancientwarfare.core.api.AWItems;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.container.ContainerTileBase;
import net.shadowmage.ancientwarfare.core.item.ItemResearchBook;

public class ContainerWorksiteAutoCrafting extends ContainerTileBase<TileAutoCrafting> {

    public ContainerWorksiteAutoCrafting(EntityPlayer player, int x, int y, int z) {
        super(player, x, y, z);
        IInventory inventory = tileEntity.craftMatrix;


        //slot 0 = outputSlot
        //slot 1 = bookSlot
        //slot 2-10 = craftMatrix
        //slot 11-28 = resourceInventory
        //slot 29-37 = outputSlots
        //slot 38-73 = playerInventory

        Slot slot;

        slot = new SlotCrafting(player, inventory, tileEntity.outputSlot, 0, 3 * 18 + 3 * 18 + 8 + 18, 1 * 18 + 8) {
            @Override
            public boolean canTakeStack(EntityPlayer par1EntityPlayer) {
                return false;
            }
        };
        addSlotToContainer(slot);

        slot = new Slot(tileEntity.bookSlot, 0, 8, 18 + 8) {
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
                slot = new Slot(inventory, slotNum, x2, y2);
                addSlotToContainer(slot);
            }
        }

        for (int y1 = 0; y1 < 2; y1++) {
            y2 = y1 * 18 + 8 + 4 * 18 + 4 + 4;
            for (int x1 = 0; x1 < 9; x1++) {
                x2 = x1 * 18 + 8;
                slotNum = y1 * 9 + x1;
                slot = new Slot(tileEntity.resourceInventory, slotNum, x2, y2);
                addSlotToContainer(slot);
            }
        }

        y2 = 8 + 3 * 18 + 4;
        for (int x1 = 0; x1 < 9; x1++) {
            x2 = x1 * 18 + 8;
            slotNum = x1;
            slot = new Slot(tileEntity.outputInventory, slotNum, x2, y2);
            addSlotToContainer(slot);
        }

        int y1 = 8 + 8 + 3 * 18 + 2 * 18 + 4 + 4 + 18;
        y1 = this.addPlayerSlots(8, y1, 4);
    }

    @Override
    public void handlePacketData(NBTTagCompound tag) {
        if (!player.worldObj.isRemote && tag.hasKey("craft")) {
            tileEntity.tryCraftItem();
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int slotClickedIndex) {
        ItemStack slotStackCopy = null;
        Slot theSlot = this.getSlot(slotClickedIndex);
        if (theSlot != null && theSlot.getHasStack()) {
            ItemStack slotStack = theSlot.getStack();
            slotStackCopy = slotStack.copy();

            int playerSlotStart = 1 + 1 + 18 + 9 + 9;
            int storageSlotsStart = 1 + 1 + 9;
            int outputSlotsStart = 1 + 1 + 9 + 18;
            if (slotClickedIndex == 0)//result slot
            {
                if (!this.mergeItemStack(slotStack, playerSlotStart, playerSlotStart + 36, false))//merge into player inventory
                {
                    return null;
                }
            } else if (slotClickedIndex == 1)//book slot
            {
                if (!this.mergeItemStack(slotStack, playerSlotStart, playerSlotStart + 36, false))//merge into player inventory
                {
                    return null;
                }
            } else if (slotClickedIndex >= 2 && slotClickedIndex < 2 + 9)//craft matrix
            {
                if (!this.mergeItemStack(slotStack, storageSlotsStart, storageSlotsStart + 18, false))//merge into storage
                {
                    return null;
                }
            } else if (slotClickedIndex >= storageSlotsStart && slotClickedIndex < storageSlotsStart + 18)//storage slots
            {
                if (!this.mergeItemStack(slotStack, playerSlotStart, playerSlotStart + 36, false))//merge into player inventory
                {
                    return null;
                }
            } else if (slotClickedIndex >= outputSlotsStart && slotClickedIndex < outputSlotsStart + 9)//storage slots
            {
                if (!this.mergeItemStack(slotStack, playerSlotStart, playerSlotStart + 36, false))//merge into player inventory
                {
                    return null;
                }
            } else if (slotClickedIndex >= playerSlotStart && slotClickedIndex < 36 + playerSlotStart)//player slots, merge into storage
            {
                if (!this.mergeItemStack(slotStack, storageSlotsStart, storageSlotsStart + 18, false))//merge into storage
                {
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
