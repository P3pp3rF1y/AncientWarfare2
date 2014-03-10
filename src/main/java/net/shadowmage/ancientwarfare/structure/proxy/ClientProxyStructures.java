package net.shadowmage.ancientwarfare.structure.proxy;

import net.minecraftforge.common.MinecraftForge;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.proxy.ClientProxyBase;
import net.shadowmage.ancientwarfare.structure.event.StructureBoundingBoxRenderer;
import net.shadowmage.ancientwarfare.structure.gui.GuiStructureScanner;

public class ClientProxyStructures extends ClientProxyBase
{

@Override
public void registerClient()
  {
  NetworkHandler.INSTANCE.registerGui(NetworkHandler.GUI_SCANNER, GuiStructureScanner.class);
  MinecraftForge.EVENT_BUS.register(StructureBoundingBoxRenderer.instance());
  }
}
