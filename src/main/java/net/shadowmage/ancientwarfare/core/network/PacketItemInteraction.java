package net.shadowmage.ancientwarfare.core.network;

import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.interfaces.IItemKeyInterface;
import io.netty.buffer.ByteBuf;

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
  AWLog.logDebug("executing item use packet...verifying item...");
  if(player!=null && player.inventory.getCurrentItem()!=null && player.inventory.getCurrentItem().getItem() instanceof IItemKeyInterface)
    {
    AWLog.logDebug("item verified, calling use method");
    IItemKeyInterface interf = (IItemKeyInterface)player.inventory.getCurrentItem().getItem();
    interf.onKeyAction(player, player.inventory.getCurrentItem());
    }
  }

}
