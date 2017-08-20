package net.shadowmage.ancientwarfare.npc.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.npc.orders.CombatOrder;

import java.util.ArrayList;
import java.util.Collection;

public class ItemCombatOrder extends ItemOrders {

    @Override
    public Collection<? extends BlockPos> getPositionsForRender(ItemStack stack) {
        Collection<BlockPos> positionList = new ArrayList<BlockPos>();
        CombatOrder order = CombatOrder.getCombatOrder(stack);
        if (order != null && !order.isEmpty()) {
            for (int i = 0; i < order.size(); i++) {
                positionList.add(order.get(i).offset(0, 1, 0));
            }
        }
        return positionList;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        if(!world.isRemote)
            NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_NPC_COMBAT_ORDER, 0, 0, 0);
        return stack;
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
            BlockPos pos = BlockTools.getBlockClickedOn(player, player.world, false);
            if (pos != null) {
                order.addPatrolPoint(player.world, pos);
                order.write(stack);
                addMessage(player);
            }
        }
    }

}
