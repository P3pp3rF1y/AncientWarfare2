package net.shadowmage.ancientwarfare.structure.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.api.AWBlocks;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.inventory.InventoryBasic;
import net.shadowmage.ancientwarfare.core.inventory.ItemSlotFilter;
import net.shadowmage.ancientwarfare.core.inventory.SlotFiltered;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.network.PacketGui;
import net.shadowmage.ancientwarfare.structure.tile.SpawnerSettings;

public class ContainerSpawnerAdvancedInventoryBase extends ContainerBase {

    SpawnerSettings settings;
    InventoryBasic inventory;

    public ContainerSpawnerAdvancedInventoryBase(EntityPlayer player, int x, int y, int z) {
        super(player);
    }

    protected void addSettingsInventorySlots() {
        int xPos;
        int yPos;
        int slotNum;

        ItemSlotFilter filter = new ItemSlotFilter() {
            @Override
            public boolean apply(ItemStack stack) {
                if (stack != null && stack.getItem() instanceof ItemBlock) {
                    ItemBlock block = (ItemBlock) stack.getItem();
                    if (block.field_150939_a == AWBlocks.advancedSpawner) {
                        return false;
                    }
                }
                return true;
            }
        };

        for (int y = 0; y < 3; y++) {
            yPos = y * 18 + 8;
            for (int x = 0; x < 3; x++) {
                xPos = x * 18 + 8;//TODO find offset
                slotNum = y * 3 + x;
                addSlotToContainer(new SlotFiltered(inventory, slotNum, xPos, yPos, filter));
            }
        }
    }

    public void sendSettingsToServer() {
        NBTTagCompound tag = new NBTTagCompound();
        settings.writeToNBT(tag);

        PacketGui pkt = new PacketGui();
        pkt.setTag("spawnerSettings", tag);
        NetworkHandler.sendToServer(pkt);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int slotClickedIndex) {
        ItemStack slotStackCopy = null;
        Slot theSlot = this.getSlot(slotClickedIndex);
        if (theSlot != null && theSlot.getHasStack()) {
            ItemStack slotStack = theSlot.getStack();
            slotStackCopy = slotStack.copy();
            int playerSlotEnd = playerSlots;
            int storageSlots = playerSlotEnd + 9;
            if (slotClickedIndex < playerSlotEnd)//player slots...
            {
                if (!this.mergeItemStack(slotStack, playerSlotEnd, storageSlots, false))//merge into storage inventory
                {
                    return null;
                }
            } else if (slotClickedIndex < storageSlots)//storage slots, merge to player inventory
            {
                if (!this.mergeItemStack(slotStack, 0, playerSlotEnd, true))//merge into player inventory
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
