package net.shadowmage.ancientwarfare.npc.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.npc.orders.CombatOrder;

import java.util.ArrayList;
import java.util.Collection;

public class ItemCombatOrder extends ItemOrders {

    @Override
    public Collection<? extends BlockPosition> getPositionsForRender(ItemStack stack) {
        Collection<BlockPosition> positionList = new ArrayList<BlockPosition>();
        CombatOrder order = CombatOrder.getCombatOrder(stack);
        if (order != null && !order.isEmpty()) {
            for (int i = 0; i < order.size(); i++) {
                positionList.add(order.get(i).copy().offset(0, 1, 0));
            }
        }
        return positionList;
    }

    @Override
    public void onRightClick(EntityPlayer player, ItemStack stack) {
        NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_NPC_COMBAT_ORDER, 0, 0, 0);
    }

    @Override
    public void onKeyAction(EntityPlayer player, ItemStack stack, ItemKey key) {
        CombatOrder order = CombatOrder.getCombatOrder(stack);
        if (order == null) {
            return;
        }
        if (player.isSneaking()) {
            order.clear();
            order.write(stack);
        } else {
            BlockPosition pos = BlockTools.getBlockClickedOn(player, player.worldObj, false);
            if (pos != null) {
                order.addPatrolPoint(player.worldObj, pos);
                order.write(stack);
            }
        }
    }

}
