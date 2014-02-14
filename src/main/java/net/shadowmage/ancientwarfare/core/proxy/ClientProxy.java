package net.shadowmage.ancientwarfare.core.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

public class ClientProxy extends CommonProxy
{

public void registerClient()
  {
  
  }

public EntityPlayer getClientPlayer()
  {  
  return Minecraft.getMinecraft().thePlayer;
  }


}
