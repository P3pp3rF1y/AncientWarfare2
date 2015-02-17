package net.shadowmage.ancientwarfare.npc.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.RayTraceUtils;
import net.shadowmage.ancientwarfare.npc.orders.TradeOrder;

import java.util.ArrayList;
import java.util.Collection;

public class ItemTradeOrder extends ItemOrders {

    @Override
    public Collection<? extends BlockPosition> getPositionsForRender(ItemStack stack) {
        Collection<BlockPosition> positionList = new ArrayList<BlockPosition>();
        TradeOrder order = TradeOrder.getTradeOrder(stack);
        if (order != null && order.getRoute().size() > 0) {
            for (int i = 0; i < order.getRoute().size(); i++) {
                positionList.add(order.getRoute().get(i).getPosition());
            }
        }
        return positionList;
    }

    @Override
    public boolean onKeyActionClient(EntityPlayer player, ItemStack stack, ItemKey key) {
        return key == ItemKey.KEY_0 || key == ItemKey.KEY_1 || key == ItemKey.KEY_2;
    }

    @Override
    public void onRightClick(EntityPlayer player, ItemStack stack) {
        NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_NPC_TRADE_ORDER, 0, 0, 0);
    }

    @Override
    public void onKeyAction(EntityPlayer player, ItemStack stack, ItemKey key) {
        MovingObjectPosition hit = RayTraceUtils.getPlayerTarget(player, 5, 0);
        if (hit == null || hit.typeOfHit != MovingObjectType.BLOCK) {
            return;
        }
        TradeOrder order = TradeOrder.getTradeOrder(stack);
        BlockPosition pos = new BlockPosition(hit.blockX, hit.blockY, hit.blockZ);
        if (key == ItemKey.KEY_0) {
            order.getRoute().addRoutePoint(pos);
            TradeOrder.writeTradeOrder(stack, order);
        } else if (key == ItemKey.KEY_1) {
            order.getRestockData().setDepositPoint(pos, hit.sideHit);
            TradeOrder.writeTradeOrder(stack, order);
        } else if (key == ItemKey.KEY_2) {
            order.getRestockData().setWithdrawPoint(pos, hit.sideHit);
            TradeOrder.writeTradeOrder(stack, order);
        }
    }

}
