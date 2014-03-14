package net.shadowmage.ancientwarfare.core.proxy;

import net.shadowmage.ancientwarfare.core.gui.GuiTest;
import net.shadowmage.ancientwarfare.core.input.InputHandler;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.network.PacketHandlerClient;
import cpw.mods.fml.common.FMLCommonHandler;

/**
 * client-proxy for AW-Core
 * @author Shadowmage
 *
 */
public class ClientProxy extends ClientProxyBase
{

@Override
public void registerClient()
  {
  NetworkHandler.registerClientHandler(new PacketHandlerClient());
  NetworkHandler.INSTANCE.registerGui(0, GuiTest.class);
  FMLCommonHandler.instance().bus().register(InputHandler.instance());
  }

}
