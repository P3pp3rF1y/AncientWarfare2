package net.shadowmage.ancientwarfare.npc.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.npc.orders.TradeOrder;

public class ContainerTradeOrder extends ContainerBase
{

public final TradeOrder orders;

public ContainerTradeOrder(EntityPlayer player, int x, int y, int z)
  {
  super(player, x, y, z);
  orders = TradeOrder.getTradeOrder(player.getCurrentEquippedItem());
  
  int startY = 240-4-8-4*18;  
  addPlayerSlots(player, 8, startY, 4);
  }

@Override
public void handlePacketData(NBTTagCompound tag)
  {
  if(tag.hasKey("tradeOrder"))
    {
    orders.readFromNBT(tag.getCompoundTag("tradeOrder"));
    }  
  }

@Override
public void onContainerClosed(EntityPlayer par1EntityPlayer)
  {  
  if(!player.worldObj.isRemote)
    {
    TradeOrder.writeTradeOrder(player.getCurrentEquippedItem(), orders);
    }
  super.onContainerClosed(par1EntityPlayer);
  }
}
