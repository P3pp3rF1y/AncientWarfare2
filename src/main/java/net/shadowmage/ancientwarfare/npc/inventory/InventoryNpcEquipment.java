package net.shadowmage.ancientwarfare.npc.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.item.ItemUpkeepOrder;

import javax.annotation.Nonnull;

public class InventoryNpcEquipment implements IInventory {

    private final NpcBase npc;
    private final NonNullList<ItemStack> inventory;

    public InventoryNpcEquipment(NpcBase npc) {
        this.npc = npc;
        inventory = NonNullList.withSize(getSizeInventory(), ItemStack.EMPTY);
        for (int i = 0; i < inventory.size(); i++) {
            if (!npc.getItemStackFromSlot(i).isEmpty()) {
                inventory.set(i, npc.getItemStackFromSlot(i).copy());
            }
        }
    }

    @Override
    public int getSizeInventory() {
        return 8;
    }

    @Override
    public boolean isEmpty() {
        return inventory.isEmpty();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        if (npc.world.isRemote) {
            return inventory.get(slot);
        } else {
            return npc.getItemStackFromSlot(slot);
        }
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        if (npc.world.isRemote) {
            inventory.set(slot, stack);
        } else {
            npc.setItemStackToSlot(slot, stack);
        }
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        @Nonnull ItemStack item = getStackInSlot(slot);
        if (!item.isEmpty()) {
            if (amount > item.getCount()) {
                amount = item.getCount();
            }
            @Nonnull ItemStack copy = item.copy();
            copy.setCount(amount);
            item.shrink(amount);
            if (item.getCount() <= 0) {
                setInventorySlotContents(slot, ItemStack.EMPTY);
            }
            return copy;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStackFromSlot(int var1) {
        @Nonnull ItemStack item = getStackInSlot(var1);
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
    public ITextComponent getDisplayName() {
        return null;
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
        if (var2 == null || var2.isEmpty() || var1 < 0) {
            return false;
        }
        if(var1 == NpcBase.UPKEEP_SLOT)
            return var2.getItem() instanceof ItemUpkeepOrder;
        else if(var1 == NpcBase.ORDER_SLOT)
            return npc.isValidOrdersStack(var2);
        else if(var1 > 1 && var1 < NpcBase.ORDER_SLOT)//armors
            return var2.getItem().isValidArmor(var2, EntityEquipmentSlot.values()[var1], npc);
        return true;//weapon/tool, shield slot   TODO add slot validation ?
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {

    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
        inventory.clear();
    }

}
