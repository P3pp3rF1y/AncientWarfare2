package net.shadowmage.ancientwarfare.npc.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.RayTraceUtils;
import net.shadowmage.ancientwarfare.npc.orders.RoutingOrder;

public class ItemRoutingOrder extends ItemOrders
{

public ItemRoutingOrder(String name)
  {
  super(name);
  this.setTextureName("ancientwarfare:npc/routing_order");
  }

@Override
public void onRightClick(EntityPlayer player, ItemStack stack)
  {
  NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_NPC_ROUTING_ORDER, 0, 0, 0);
  }

@Override
public void onKeyAction(EntityPlayer player, ItemStack stack, int keyIndex)
  {
  RoutingOrder order = RoutingOrder.getRoutingOrder(stack);
  if(order!=null)
    {
    MovingObjectPosition hit = RayTraceUtils.getPlayerTarget(player, 5, 0);
    if(hit!=null && hit.typeOfHit==MovingObjectType.BLOCK)
      {
      order.addRoutePoint(player.worldObj, hit.blockX, hit.blockY, hit.blockZ);
      order.getEntries().get(order.getEntries().size()-1).setBlockSide(hit.sideHit);
      RoutingOrder.writeRoutingOrder(stack, order);
      }    
    }
  }

}
