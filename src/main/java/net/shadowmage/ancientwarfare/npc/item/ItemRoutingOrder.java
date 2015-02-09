package net.shadowmage.ancientwarfare.npc.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.RayTraceUtils;
import net.shadowmage.ancientwarfare.npc.orders.RoutingOrder;

import java.util.ArrayList;
import java.util.Collection;

public class ItemRoutingOrder extends ItemOrders {

    public ItemRoutingOrder(String name) {
        super(name);
        this.setTextureName("ancientwarfare:npc/"+name);
    }

    @Override
    public Collection<? extends BlockPosition> getPositionsForRender(ItemStack stack) {
        Collection<BlockPosition> positionList = new ArrayList<BlockPosition>();
        RoutingOrder order = RoutingOrder.getRoutingOrder(stack);
        if (order != null && order.getEntries().size() > 0) {
            for (RoutingOrder.RoutePoint e : order.getEntries()) {
                positionList.add(e.getTarget());
            }
        }
        return positionList;
    }

    @Override
    public void onRightClick(EntityPlayer player, ItemStack stack) {
        NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_NPC_ROUTING_ORDER, 0, 0, 0);
    }

    @Override
    public void onKeyAction(EntityPlayer player, ItemStack stack, ItemKey key) {
        RoutingOrder order = RoutingOrder.getRoutingOrder(stack);
        if (order != null) {
            MovingObjectPosition hit = RayTraceUtils.getPlayerTarget(player, 5, 0);
            if (hit != null && hit.typeOfHit == MovingObjectType.BLOCK) {
                order.addRoutePoint(player.worldObj, hit.blockX, hit.blockY, hit.blockZ);
                order.getEntries().get(order.getEntries().size() - 1).setBlockSide(hit.sideHit);
                RoutingOrder.writeRoutingOrder(stack, order);
            }
        }
    }

}
