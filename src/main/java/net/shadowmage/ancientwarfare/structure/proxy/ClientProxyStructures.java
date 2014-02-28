package net.shadowmage.ancientwarfare.structure.proxy;

import cpw.mods.fml.common.network.NetworkRegistry;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.proxy.ClientProxyBase;
import net.shadowmage.ancientwarfare.structure.gui.GuiStructureScanner;

public class ClientProxyStructures extends ClientProxyBase
{

@Override
public void registerClient()
  {
  NetworkHandler.INSTANCE.registerGui(NetworkHandler.GUI_SCANNER, GuiStructureScanner.class);
  }
}
