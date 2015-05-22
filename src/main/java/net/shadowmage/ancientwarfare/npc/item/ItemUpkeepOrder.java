package net.shadowmage.ancientwarfare.npc.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.npc.orders.UpkeepOrder;

import java.util.ArrayList;
import java.util.Collection;

public class ItemUpkeepOrder extends ItemOrders {

    @Override
    public Collection<? extends BlockPosition> getPositionsForRender(ItemStack stack) {
        Collection<BlockPosition> positionList = new ArrayList<BlockPosition>();
        UpkeepOrder order = UpkeepOrder.getUpkeepOrder(stack);
        if (order != null && order.getUpkeepPosition() != null)
            positionList.add(order.getUpkeepPosition());
        return positionList;
    }

    @Override
    public void onRightClick(EntityPlayer player, ItemStack stack) {
        NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_NPC_UPKEEP_ORDER, 0, 0, 0);
    }

    @Override
    public void onKeyAction(EntityPlayer player, ItemStack stack, ItemKey key) {
        UpkeepOrder upkeepOrder = UpkeepOrder.getUpkeepOrder(stack);
        if (upkeepOrder != null) {
            BlockPosition hit = BlockTools.getBlockClickedOn(player, player.worldObj, false);
            if (hit != null && player.worldObj.getTileEntity(hit.x, hit.y, hit.z) instanceof IInventory) {
                if (upkeepOrder.addUpkeepPosition(player.worldObj, hit)) {
                    upkeepOrder.write(stack);
                    player.openContainer.detectAndSendChanges();
                    NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_NPC_UPKEEP_ORDER, 0, 0, 0);
                    //TODO add chat output message regarding adding a worksite to the work-orders
                }
            }
        }
    }

}
