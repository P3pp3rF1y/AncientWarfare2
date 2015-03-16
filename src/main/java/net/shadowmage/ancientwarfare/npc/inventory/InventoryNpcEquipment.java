package net.shadowmage.ancientwarfare.npc.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class InventoryNpcEquipment implements IInventory {

    private final NpcBase npc;

    private final ItemStack[] inventory;

    public InventoryNpcEquipment(NpcBase npc) {
        this.npc = npc;
        inventory = new ItemStack[getSizeInventory()];
        for (int i = 0; i < inventory.length; i++) {
            inventory[i] = npc.getEquipmentInSlot(i) == null ? null : npc.getEquipmentInSlot(i).copy();
        }
    }

    @Override
    public int getSizeInventory() {
        return 8;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        if (npc.worldObj.isRemote) {
            return inventory[slot];
        } else {
            return npc.getEquipmentInSlot(slot);
        }
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        if (npc.worldObj.isRemote) {
            inventory[slot] = stack;
        } else {
            npc.setCurrentItemOrArmor(slot, stack);
        }
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        ItemStack item = getStackInSlot(slot);
        if (item != null) {
            if (amount > item.stackSize) {
                amount = item.stackSize;
            }
            ItemStack copy = item.copy();
            copy.stackSize = amount;
            item.stackSize -= amount;
            if (item.stackSize <= 0) {
                setInventorySlotContents(slot, null);
            }
            return copy;
        }
        return null;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int var1) {
        ItemStack item = getStackInSlot(var1);
        this.setInventorySlotContents(var1, null);
        return item;
    }

    @Override
    public String getInventoryName() {
        return "AWNpcInventoryWrapper";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void markDirty() {
        // TODO Auto-generated method stub
        //TODO inform NPC to update work-types
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer var1) {
        return true;
    }

    @Override
    public void openInventory() {
        // TODO Auto-generated method stub
    }

    @Override
    public void closeInventory() {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean isItemValidForSlot(int var1, ItemStack var2) {
        if (var2 == null || var2.getItem() == null) {
            return false;
        }
        switch (var1) {
            case 0://weapon/tool, open
                // TODO validate input for weapon slot?
                return true;
            case 1://head
                return (var2.getItem() instanceof ItemArmor) && ((ItemArmor) var2.getItem()).armorType == 3;
            case 2://chest
                return (var2.getItem() instanceof ItemArmor) && ((ItemArmor) var2.getItem()).armorType == 2;
            case 3://legs
                return (var2.getItem() instanceof ItemArmor) && ((ItemArmor) var2.getItem()).armorType == 1;
            case 4://boots
                return (var2.getItem() instanceof ItemArmor) && ((ItemArmor) var2.getItem()).armorType == 0;
            case 5:
                return npc.isValidOrdersStack(var2);
            default:
                return true;
        }
    }

}
