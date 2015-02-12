package net.shadowmage.ancientwarfare.npc.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.npc.orders.CombatOrder;

public class ContainerCombatOrder extends ContainerBase {

    private boolean hasChanged;
    public CombatOrder combatOrder;

    public ContainerCombatOrder(EntityPlayer player, int x, int y, int z) {
        super(player, x, y, z);
        ItemStack stack = player.getCurrentEquippedItem();
        if (stack == null || stack.getItem() == null) {
            throw new IllegalArgumentException("Cannot open Work Order GUI for null stack/item.");
        }
        combatOrder = CombatOrder.getCombatOrder(stack);
        if (combatOrder == null) {
            throw new IllegalArgumentException("Upkeep orders was null for some reason");
        }
    }

    @Override
    public void handlePacketData(NBTTagCompound tag) {
        if (tag.hasKey("combatOrder")) {
            combatOrder.readFromNBT(tag.getCompoundTag("combatOrder"));
            hasChanged = true;
        }
    }

    @Override
    public void onContainerClosed(EntityPlayer par1EntityPlayer) {
        super.onContainerClosed(par1EntityPlayer);
        if (hasChanged && !player.worldObj.isRemote) {
            CombatOrder.writeCombatOrder(player.getCurrentEquippedItem(), combatOrder);
        }
    }
}
