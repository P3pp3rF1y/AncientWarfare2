package net.shadowmage.ancientwarfare.npc.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite;
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
        if (order != null && order.getEntries().size() > 0) {
            for (WorkOrder.WorkEntry e : order.getEntries()) {
                positionList.add(e.getPosition());
            }
        }
        return positionList;
    }

    @Override
    public void onRightClick(EntityPlayer player, ItemStack stack) {
        NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_NPC_WORK_ORDER, 0, 0, 0);
    }

    @Override
    public void onKeyAction(EntityPlayer player, ItemStack stack, ItemKey key) {
        WorkOrder wo = WorkOrder.getWorkOrder(stack);
        if (wo != null) {
            BlockPosition hit = BlockTools.getBlockClickedOn(player, player.worldObj, false);
            if (hit != null && player.worldObj.getTileEntity(hit.x, hit.y, hit.z) instanceof IWorkSite) {
                if (wo.addWorkPosition(player.worldObj, hit, 0)) {
                    WorkOrder.writeWorkOrder(stack, wo);
                    player.openContainer.detectAndSendChanges();
                    //TODO add chat output message regarding adding a worksite to the work-orders
                    //TODO possibly open the gui after setting the work-point?
//        player.setCurrentItemOrArmor(0, stack);//TODO probably un-necessary        
                }
            }
        }
    }


}
