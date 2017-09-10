package net.shadowmage.ancientwarfare.npc.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.npc.orders.WorkOrder;

import java.util.ArrayList;
import java.util.List;

public class ItemWorkOrder extends ItemOrders {

    @Override
    public List<BlockPos> getPositionsForRender(ItemStack stack) {
        List<BlockPos> positionList = new ArrayList<>();
        WorkOrder order = WorkOrder.getWorkOrder(stack);
        if (order != null && !order.isEmpty()) {
            for (WorkOrder.WorkEntry e : order.getEntries()) {
                positionList.add(e.getPosition());
            }
        }
        return positionList;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
    {
        if(!world.isRemote)
            NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_NPC_WORK_ORDER, 0, 0, 0);
        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }

    @Override
    public void onKeyAction(EntityPlayer player, ItemStack stack, ItemKey key) {
        WorkOrder wo = WorkOrder.getWorkOrder(stack);
        if (wo != null) {
            BlockPos hit = BlockTools.getBlockClickedOn(player, player.world, false);
            if (wo.addWorkPosition(player.world, hit)) {
                wo.write(stack);
                addMessage(player);
            }else{
                NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_NPC_WORK_ORDER, 0, 0, 0);
            }
        }
    }


}
