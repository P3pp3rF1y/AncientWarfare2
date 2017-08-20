package net.shadowmage.ancientwarfare.npc.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.npc.orders.UpkeepOrder;

import java.util.ArrayList;
import java.util.Collection;

public class ItemUpkeepOrder extends ItemOrders {

    @Override
    public Collection<? extends BlockPos> getPositionsForRender(ItemStack stack) {
        Collection<BlockPos> positionList = new ArrayList<BlockPos>();
        UpkeepOrder order = UpkeepOrder.getUpkeepOrder(stack);
        if (order != null && order.getUpkeepPosition() != null)
            positionList.add(order.getUpkeepPosition());
        return positionList;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player){
        if(!world.isRemote)
            NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_NPC_UPKEEP_ORDER, 0, 0, 0);
        return stack;
    }

    @Override
    public void onKeyAction(EntityPlayer player, ItemStack stack, ItemKey key) {
        UpkeepOrder upkeepOrder = UpkeepOrder.getUpkeepOrder(stack);
        if (upkeepOrder != null) {
            BlockPos hit = BlockTools.getBlockClickedOn(player, player.world, false);
            if (upkeepOrder.addUpkeepPosition(player.world, hit)) {
                upkeepOrder.write(stack);
                player.addChatComponentMessage(new TextComponentTranslation("guistrings.npc.upkeep_point_set"));
            } else 
                NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_NPC_UPKEEP_ORDER, 0, 0, 0);
        }
    }

}
