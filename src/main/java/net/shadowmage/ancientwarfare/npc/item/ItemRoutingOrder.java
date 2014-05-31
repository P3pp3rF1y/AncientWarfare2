package net.shadowmage.ancientwarfare.npc.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.npc.orders.RoutingOrder;

public class ItemRoutingOrder extends ItemOrders
{

public ItemRoutingOrder(String name)
  {
  super(name);
  }

@Override
public void onRightClick(EntityPlayer player, ItemStack stack)
  {
  NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_NPC_ROUTING_ORDER, 0, 0, 0);
  }

@Override
public void onKeyAction(EntityPlayer player, ItemStack stack)
  {
  RoutingOrder order = RoutingOrder.getRoutingOrder(stack);
  if(order!=null)
    {
    BlockPosition pos = BlockTools.getBlockClickedOn(player, player.worldObj, false);
    order.addRoutePoint(player.worldObj, pos.x, pos.y, pos.z);
    RoutingOrder.writeRoutingOrder(stack, order);
    }
  }

}
