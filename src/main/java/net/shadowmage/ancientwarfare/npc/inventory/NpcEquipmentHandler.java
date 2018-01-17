package net.shadowmage.ancientwarfare.npc.inventory;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemStackHandler;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.item.ItemUpkeepOrder;

import javax.annotation.Nonnull;

public class NpcEquipmentHandler extends ItemStackHandler {

    private static final int SIZE_INVENTORY = 8;
    private final NpcBase npc;

    public NpcEquipmentHandler(NpcBase npc) {
        super(SIZE_INVENTORY);
        this.npc = npc;
        stacks = NonNullList.withSize(SIZE_INVENTORY, ItemStack.EMPTY);
        for (int i = 0; i < SIZE_INVENTORY; i++) {
            if (!npc.getItemStackFromSlot(i).isEmpty()) {
                stacks.set(i, npc.getItemStackFromSlot(i).copy());
            }
        }
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        return isItemStackValidForSlot(slot, stack) ? super.insertItem(slot, stack, simulate) : stack;
    }

    private boolean isItemStackValidForSlot(int slot, @Nonnull ItemStack stack) {
        if (stack.isEmpty() || slot < 0) {
            return false;
        }
        if(slot == NpcBase.UPKEEP_SLOT)
            return stack.getItem() instanceof ItemUpkeepOrder;
        else if(slot == NpcBase.ORDER_SLOT)
            return npc.isValidOrdersStack(stack);
        else if(slot > 1 && slot < NpcBase.ORDER_SLOT)//armors
            return stack.getItem().isValidArmor(stack, EntityEquipmentSlot.values()[slot], npc);
        return true;//weapon/tool, shield slot   TODO add slot validation ?
    }

    @Override
    protected void onContentsChanged(int slot) {
        npc.setItemStackToSlot(slot, stacks.get(slot));
    }
}
