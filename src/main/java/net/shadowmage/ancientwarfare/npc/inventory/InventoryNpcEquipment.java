package net.shadowmage.ancientwarfare.npc.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.item.ItemUpkeepOrder;

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
        return NpcBase.SHIELD_SLOT + 1;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        if (npc.world.isRemote) {
            return inventory[slot];
        } else {
            return npc.getEquipmentInSlot(slot);
        }
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        if (npc.world.isRemote) {
            inventory[slot] = stack;
        } else {
            npc.setCurrentItemOrArmor(slot, stack);
        }
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        ItemStack item = getStackInSlot(slot);
        if (item != null) {
            if (amount > item.getCount()) {
                amount = item.getCount();
            }
            ItemStack copy = item.copy();
            copy.setCount(amount);
            item.shrink(amount);
            if (item.getCount() <= 0) {
                setInventorySlotContents(slot, ItemStack.EMPTY);
            }
            return copy;
        }
        return null;
    }

    @Override
    public ItemStack removeStackFromSlot(int var1) {
        ItemStack item = getStackInSlot(var1);
        this.setInventorySlotContents(var1, ItemStack.EMPTY);
        return item;
    }

    @Override
    public String getName() {
        return "AWNpcInventoryWrapper";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 1;
    }

    @Override
    public void markDirty() {

    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer var1) {
        return npc.isEntityAlive();
    }

    @Override
    public void openInventory(EntityPlayer player) {

    }

    @Override
    public void closeInventory(EntityPlayer player) {

    }

    @Override
    public boolean isItemValidForSlot(int var1, ItemStack var2) {
        if (var2 == null || var2.getItem() == null || var1 < 0) {
            return false;
        }
        if(var1 == NpcBase.UPKEEP_SLOT)
            return var2.getItem() instanceof ItemUpkeepOrder;
        else if(var1 == NpcBase.ORDER_SLOT)
            return npc.isValidOrdersStack(var2);
        else if(var1 != 0 && var1 < NpcBase.ORDER_SLOT)//armors
            return var2.getItem().isValidArmor(var2, NpcBase.ORDER_SLOT - 1 - var1, npc);
        return true;//weapon/tool, shield slot   TODO add slot validation ?
    }

}
