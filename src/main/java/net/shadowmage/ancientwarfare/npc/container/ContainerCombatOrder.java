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
        super(player);
        ItemStack stack = player.getCurrentEquippedItem();
        if (stack == null || stack.getItem() == null) {
            throw new IllegalArgumentException("Cannot open Combat Order GUI for null stack/item.");
        }
        combatOrder = CombatOrder.getCombatOrder(stack);
        if (combatOrder == null) {
            throw new IllegalArgumentException("Combat orders was null for some reason");
        }
        addPlayerSlots();
        removeSlots();
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
            combatOrder.write(player.getCurrentEquippedItem());
        }
    }

    public void close() {
        NBTTagCompound outer = new NBTTagCompound();
        outer.setTag("combatOrder", combatOrder.writeToNBT(new NBTTagCompound()));
        sendDataToServer(outer);
    }
}
