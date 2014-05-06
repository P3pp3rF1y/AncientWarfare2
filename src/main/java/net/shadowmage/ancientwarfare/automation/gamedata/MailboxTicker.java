package net.shadowmage.ancientwarfare.automation.gamedata;

import net.minecraft.server.MinecraftServer;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;

public class MailboxTicker
{

public MailboxTicker()
  {
  // TODO Auto-generated constructor stub
  }


@SubscribeEvent
public void serverTick(ServerTickEvent evt)
  {
  if(evt.phase==Phase.END)
    {
    MinecraftServer server = MinecraftServer.getServer();
    if(server!=null && server.getEntityWorld()!=null)
      {
      MailboxData data = AWGameData.INSTANCE.getData(MailboxData.name, server.getEntityWorld(), MailboxData.class);
      data.onTick(1);
      }
    }
  }

}
