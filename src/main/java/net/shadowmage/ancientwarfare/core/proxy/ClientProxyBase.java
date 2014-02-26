package net.shadowmage.ancientwarfare.core.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.shadowmage.ancientwarfare.core.gui.GuiTest;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.network.PacketHandlerClient;

public class ClientProxyBase extends CommonProxyBase
{

@Override
public void registerClient()
  {
  
  }

public final EntityPlayer getClientPlayer()
  {  
  return Minecraft.getMinecraft().thePlayer;
  }


}
