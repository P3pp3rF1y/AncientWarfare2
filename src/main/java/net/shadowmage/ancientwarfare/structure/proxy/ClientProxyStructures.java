package net.shadowmage.ancientwarfare.structure.proxy;

import net.minecraftforge.common.MinecraftForge;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.proxy.ClientProxyBase;
import net.shadowmage.ancientwarfare.structure.event.StructureBoundingBoxRenderer;
import net.shadowmage.ancientwarfare.structure.gui.GuiSpawnerPlacer;
import net.shadowmage.ancientwarfare.structure.gui.GuiStructureScanner;
import net.shadowmage.ancientwarfare.structure.gui.GuiStructureSelection;

public class ClientProxyStructures extends ClientProxyBase
{

@Override
public void registerClient()
  {
  NetworkHandler.INSTANCE.registerGui(NetworkHandler.GUI_SCANNER, GuiStructureScanner.class);
  NetworkHandler.INSTANCE.registerGui(NetworkHandler.GUI_BUILDER, GuiStructureSelection.class);
  NetworkHandler.INSTANCE.registerGui(NetworkHandler.GUI_SPAWNER, GuiSpawnerPlacer.class);
  MinecraftForge.EVENT_BUS.register(StructureBoundingBoxRenderer.instance());
  }
}
