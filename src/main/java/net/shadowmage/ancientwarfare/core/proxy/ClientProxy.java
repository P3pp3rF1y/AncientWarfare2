package net.shadowmage.ancientwarfare.core.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.shadowmage.ancientwarfare.core.gui.GuiTest;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;

public class ClientProxy extends CommonProxy
{

public void registerClient()
  {
  NetworkHandler.INSTANCE.registerGui(0, GuiTest.class);
  }

public EntityPlayer getClientPlayer()
  {  
  return Minecraft.getMinecraft().thePlayer;
  }


}
