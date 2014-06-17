package net.shadowmage.ancientwarfare.core.proxy;

import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.gui.GuiBackpack;
import net.shadowmage.ancientwarfare.core.gui.GuiResearchBook;
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
  NetworkHandler.registerGui(NetworkHandler.GUI_BACKPACK, GuiBackpack.class);
  NetworkHandler.registerGui(NetworkHandler.GUI_RESEARCH_BOOK, GuiResearchBook.class);
  InputHandler.instance().loadConfig(AncientWarfareCore.config);
  }

}
