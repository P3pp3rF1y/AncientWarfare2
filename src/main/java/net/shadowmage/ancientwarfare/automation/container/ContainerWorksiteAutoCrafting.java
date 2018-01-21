package net.shadowmage.ancientwarfare.automation.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.SlotItemHandler;
import net.shadowmage.ancientwarfare.automation.tile.worksite.TileAutoCrafting;
import net.shadowmage.ancientwarfare.core.container.ContainerTileBase;
import net.shadowmage.ancientwarfare.core.inventory.SlotResearchCrafting;
import net.shadowmage.ancientwarfare.core.item.ItemResearchBook;

import javax.annotation.Nonnull;

public class ContainerWorksiteAutoCrafting extends ContainerTileBase<TileAutoCrafting> {

    public ContainerWorksiteAutoCrafting(EntityPlayer player, int x, int y, int z) {
        super(player, x, y, z);
        InventoryCrafting inventory = tileEntity.craftMatrix;

        //slot 0 = outputSlot
        //slot 1 = bookSlot
        //slot 2-10 = craftMatrix
        //slot 11-28 = resourceInventory
        //slot 29-37 = outputSlots
        //slot 38-73 = playerInventory

        Slot slot = new SlotResearchCrafting(player, inventory, tileEntity.outputSlot, 0, 3 * 18 + 3 * 18 + 8 + 18, 1 * 18 + 8) {
            @Override
            public boolean canTakeStack(EntityPlayer par1EntityPlayer) {
                return false;
            }
        };
        addSlotToContainer(slot);

        slot = new SlotItemHandler(tileEntity.bookSlot, 0, 8, 18 + 8) {
            @Override
            public boolean isItemValid(ItemStack par1ItemStack) {
                return ItemResearchBook.getResearcherName(par1ItemStack) != null;
            }
        };
        addSlotToContainer(slot);

        int x2, y2, slotNum;
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
                slot = new SlotItemHandler(tileEntity.resourceInventory, slotNum, x2, y2);
                addSlotToContainer(slot);
            }
        }

        y2 = 8 + 3 * 18 + 4;
        for (int x1 = 0; x1 < 9; x1++) {
            x2 = x1 * 18 + 8;
            slotNum = x1;
            slot = new SlotItemHandler(tileEntity.outputInventory, slotNum, x2, y2);
            addSlotToContainer(slot);
        }

        int y1 = 8 + 8 + 3 * 18 + 2 * 18 + 4 + 4 + 18;
        y1 = this.addPlayerSlots(y1);
    }

    @Override
    public void handlePacketData(NBTTagCompound tag) {
        if (!player.world.isRemote && tag.hasKey("craft")) {
            tileEntity.tryCraftItem();
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int slotClickedIndex) {
        @Nonnull ItemStack slotStackCopy = ItemStack.EMPTY;
        Slot theSlot = this.getSlot(slotClickedIndex);
        if (theSlot != null && theSlot.getHasStack()) {
            @Nonnull ItemStack slotStack = theSlot.getStack();
            slotStackCopy = slotStack.copy();

            int storageSlotsStart = 1 + 1 + tileEntity.craftMatrix.getSizeInventory();
            int outputSlotsStart = storageSlotsStart + tileEntity.resourceInventory.getSlots();
            int playerSlotStart = outputSlotsStart + tileEntity.outputInventory.getSlots();
            int playerSlotEnd = playerSlotStart + playerSlots;
            if (slotClickedIndex < 2)//result or book slot
            {
                if (!this.mergeItemStack(slotStack, playerSlotStart, playerSlotEnd, false))//merge into player inventory
                {
                    return ItemStack.EMPTY;
                }
            } else if (slotClickedIndex < storageSlotsStart)//craft matrix
            {
                if (!this.mergeItemStack(slotStack, storageSlotsStart, outputSlotsStart, false))//merge into storage
                {
                    return ItemStack.EMPTY;
                }
            } else if (slotClickedIndex < playerSlotStart)//storage slots
            {
                if (!this.mergeItemStack(slotStack, playerSlotStart, playerSlotEnd, false))//merge into player inventory
                {
                    return ItemStack.EMPTY;
                }
            } else if (slotClickedIndex < playerSlotEnd)//player slots, merge into storage
            {
                if (!this.mergeItemStack(slotStack, storageSlotsStart, outputSlotsStart, false))//merge into storage
                {
                    return ItemStack.EMPTY;
                }
            }
            if (slotStack.getCount() == 0) {
                theSlot.putStack(ItemStack.EMPTY);
            } else {
                theSlot.onSlotChanged();
            }
            if (slotStack.getCount() == slotStackCopy.getCount()) {
                return ItemStack.EMPTY;
            }
            theSlot.onTake(par1EntityPlayer, slotStack);
        }
        return slotStackCopy;
    }

    public void craft() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setBoolean("craft", true);
        sendDataToServer(tag);
    }
}
