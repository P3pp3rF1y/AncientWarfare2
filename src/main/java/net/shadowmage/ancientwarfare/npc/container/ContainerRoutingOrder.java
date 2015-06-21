package net.shadowmage.ancientwarfare.npc.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.npc.orders.RoutingOrder;

public class ContainerRoutingOrder extends ContainerBase {

    private boolean hasChanged;
    public RoutingOrder routingOrder;

    public ContainerRoutingOrder(EntityPlayer player, int x, int y, int z) {
        super(player);
        ItemStack stack = player.getCurrentEquippedItem();
        if (stack == null || stack.getItem() == null) {
            throw new IllegalArgumentException("Cannot open Routing Order GUI for null stack/item.");
        }
        routingOrder = RoutingOrder.getRoutingOrder(stack);
        if (routingOrder == null) {
            throw new IllegalArgumentException("Routing orders was null for some reason");
        }

        addPlayerSlots((256 - (9 * 18)) / 2, 240 - 4 * 18 - 8 - 4, 4);
    }

    @Override
    public void handlePacketData(NBTTagCompound tag) {
        if (tag.hasKey("routingOrder")) {
            routingOrder.readFromNBT(tag.getCompoundTag("routingOrder"));
            hasChanged = true;
        }
    }

    @Override
    public void onContainerClosed(EntityPlayer par1EntityPlayer) {
        super.onContainerClosed(par1EntityPlayer);
        if (hasChanged && !player.worldObj.isRemote) {
            routingOrder.write(player.getCurrentEquippedItem());
        }
    }
}
