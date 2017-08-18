package net.shadowmage.ancientwarfare.npc.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.npc.orders.WorkOrder;

import java.util.ArrayList;
import java.util.Collection;

public class ItemWorkOrder extends ItemOrders {

    @Override
    public Collection<? extends BlockPosition> getPositionsForRender(ItemStack stack) {
        Collection<BlockPosition> positionList = new ArrayList<BlockPosition>();
        WorkOrder order = WorkOrder.getWorkOrder(stack);
        if (order != null && !order.isEmpty()) {
            for (WorkOrder.WorkEntry e : order.getEntries()) {
                positionList.add(e.getPosition());
            }
        }
        return positionList;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        if(!world.isRemote)
            NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_NPC_WORK_ORDER, 0, 0, 0);
        return stack;
    }

    @Override
    public void onKeyAction(EntityPlayer player, ItemStack stack, ItemKey key) {
        WorkOrder wo = WorkOrder.getWorkOrder(stack);
        if (wo != null) {
            BlockPosition hit = BlockTools.getBlockClickedOn(player, player.world, false);
            if (wo.addWorkPosition(player.world, hit)) {
                wo.write(stack);
                addMessage(player);
            }else{
                NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_NPC_WORK_ORDER, 0, 0, 0);
            }
        }
    }


}
