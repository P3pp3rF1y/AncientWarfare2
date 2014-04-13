package net.shadowmage.ancientwarfare.core.network;

import io.netty.buffer.ByteBuf;
import net.shadowmage.ancientwarfare.core.interfaces.IItemKeyInterface;

public class PacketItemInteraction extends PacketBase
{

public PacketItemInteraction()
  {
  
  }

@Override
protected void writeToStream(ByteBuf data)
  {

  }

@Override
protected void readFromStream(ByteBuf data)
  {

  }

@Override
protected void execute()
  {
  if(player!=null && player.inventory.getCurrentItem()!=null && player.inventory.getCurrentItem().getItem() instanceof IItemKeyInterface)
    {
    IItemKeyInterface interf = (IItemKeyInterface)player.inventory.getCurrentItem().getItem();
    interf.onKeyAction(player, player.inventory.getCurrentItem());
    }
  }

}
