package net.shadowmage.ancientwarfare.core.proxy;

import net.shadowmage.ancientwarfare.core.gui.crafting.GuiEngineeringStation;
import net.shadowmage.ancientwarfare.core.gui.research.GuiResearchStation;
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
  FMLCommonHandler.instance().bus().register(InputHandler.instance());
  NetworkHandler.registerGui(NetworkHandler.GUI_CRAFTING, GuiEngineeringStation.class);
  NetworkHandler.registerGui(NetworkHandler.GUI_RESEARCH_STATION, GuiResearchStation.class);
  }

}
