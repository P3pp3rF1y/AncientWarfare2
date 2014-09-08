package net.shadowmage.ancientwarfare.npc.container;

import net.minecraft.entity.player.EntityPlayer;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.npc.orders.TradeOrder;

public class ContainerTradeOrder extends ContainerBase
{

TradeOrder orders;

public ContainerTradeOrder(EntityPlayer player, int x, int y, int z)
  {
  super(player, x, y, z);
  orders = TradeOrder.getTradeOrder(player.getCurrentEquippedItem());
  }

}
