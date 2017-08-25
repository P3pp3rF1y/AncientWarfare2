package net.shadowmage.ancientwarfare.core.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.inventory.InventoryBackpack;
import net.shadowmage.ancientwarfare.core.item.ItemBackpack;

public class ContainerBackpack extends ContainerBase {

    public final int backpackSlotIndex;
    public final int guiHeight;

    private final InventoryBackpack inventory;

    public ContainerBackpack(EntityPlayer player, int x, int y, int z) {
        super(player);

        @Nonnull ItemStack stack = player.getCurrentEquippedItem();
        backpackSlotIndex = player.inventory.currentItem;

        inventory = ItemBackpack.getInventoryFor(stack);
        int xPos, yPos;
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            xPos = (i % 9) * 18 + 8;
            yPos = (i / 9) * 18 + 8;
            addSlotToContainer(new Slot(inventory, i, xPos, yPos) {
                @Override
                public boolean isItemValid(ItemStack itemStack) {
                    return this.inventory.isItemValidForSlot(this.getSlotIndex(), itemStack);
                }
            });
        }
        int height = (stack.getItemDamage() + 1) * 18 + 8;
        guiHeight = addPlayerSlots(height + 8) + 8;
    }

    @Override
    public void onContainerClosed(EntityPlayer par1EntityPlayer) {
        super.onContainerClosed(par1EntityPlayer);
        if (!par1EntityPlayer.world.isRemote) {
            ItemBackpack.writeBackpackToItem(inventory, par1EntityPlayer.getCurrentEquippedItem());
        }
    }

    @Override
    protected int addPlayerSlots(int tx, int ty, int gap) {
        int y;
        int x;
        int slotNum;
        int xPos;
        int yPos;
        for (x = 0; x < 9; ++x)//add player hotbar slots
        {
            slotNum = x;
            if (slotNum == backpackSlotIndex) {
                continue;
            }//TODO add fake slot in gui
            xPos = tx + x * 18;
            yPos = ty + gap + 3 * 18;
            this.addSlotToContainer(new Slot(player.inventory, x, xPos, yPos));
        }
        for (y = 0; y < 3; ++y) {
            for (x = 0; x < 9; ++x) {
                slotNum = y * 9 + x + 9;// +9 is to increment past hotbar slots
                xPos = tx + x * 18;
                yPos = ty + y * 18;
                this.addSlotToContainer(new Slot(player.inventory, slotNum, xPos, yPos));
            }
        }
        playerSlots = 35;
        return ty + (4 * 18) + gap;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int slotClickedIndex) {
        @Nonnull ItemStack slotStackCopy = ItemStack.EMPTY;
        Slot theSlot = this.getSlot(slotClickedIndex);
        int size = inventory.getSizeInventory();
        if (theSlot != null && theSlot.getHasStack()) {
            @Nonnull ItemStack slotStack = theSlot.getStack();
            slotStackCopy = slotStack.copy();
            if (slotClickedIndex < size)//clicked in backpack
            {
                if (!this.mergeItemStack(slotStack, size, size + playerSlots, false))//merge into player inventory
                {
                    return null;
                }
            } else {
                if (!this.mergeItemStack(slotStack, 0, size, false))//merge into backpack
                {
                    return null;
                }
            }
            if (slotStack.getCount() == 0) {
                theSlot.putStack(ItemStack.EMPTY);
            } else {
                theSlot.onSlotChanged();
            }
            if (slotStack.getCount() == slotStackCopy.getCount()) {
                return null;
            }
            theSlot.onPickupFromSlot(par1EntityPlayer, slotStack);
        }
        return slotStackCopy;
    }


}
