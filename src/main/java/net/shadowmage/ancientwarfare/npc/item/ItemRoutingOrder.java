package net.shadowmage.ancientwarfare.npc.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.RayTraceResult.MovingObjectType;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.RayTraceUtils;
import net.shadowmage.ancientwarfare.npc.orders.RoutingOrder;

import java.util.ArrayList;
import java.util.Collection;

public class ItemRoutingOrder extends ItemOrders {

    @Override
    public Collection<? extends BlockPos> getPositionsForRender(ItemStack stack) {
        Collection<BlockPos> positionList = new ArrayList<BlockPos>();
        RoutingOrder order = RoutingOrder.getRoutingOrder(stack);
        if (order != null && !order.isEmpty()) {
            for (RoutingOrder.RoutePoint e : order.getEntries()) {
                positionList.add(e.getTarget());
            }
        }
        return positionList;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        if(!world.isRemote)
            NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_NPC_ROUTING_ORDER, 0, 0, 0);
        return stack;
    }

    @Override
    public void onKeyAction(EntityPlayer player, ItemStack stack, ItemKey key) {
        RoutingOrder order = RoutingOrder.getRoutingOrder(stack);
        if (order != null) {
            RayTraceResult hit = RayTraceUtils.getPlayerTarget(player, 5, 0);
            if (hit != null && hit.typeOfHit == MovingObjectType.BLOCK) {
                order.addRoutePoint(hit.sideHit, hit.blockX, hit.blockY, hit.blockZ);
                order.write(stack);
                addMessage(player);
            }
        }
    }

}
