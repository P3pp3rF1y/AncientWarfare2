package net.shadowmage.ancientwarfare.structure.proxy;

import net.minecraftforge.common.MinecraftForge;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.proxy.ClientProxyBase;
import net.shadowmage.ancientwarfare.structure.event.StructureBoundingBoxRenderer;
import net.shadowmage.ancientwarfare.structure.gui.GuiSpawnerAdvanced;
import net.shadowmage.ancientwarfare.structure.gui.GuiSpawnerPlacer;
import net.shadowmage.ancientwarfare.structure.gui.GuiStructureScanner;
import net.shadowmage.ancientwarfare.structure.gui.GuiStructureSelection;

public class ClientProxyStructures extends ClientProxyBase
{

@Override
public void registerClient()
  {
  NetworkHandler.registerGui(NetworkHandler.GUI_SCANNER, GuiStructureScanner.class);
  NetworkHandler.registerGui(NetworkHandler.GUI_BUILDER, GuiStructureSelection.class);
  NetworkHandler.registerGui(NetworkHandler.GUI_SPAWNER, GuiSpawnerPlacer.class);
  NetworkHandler.registerGui(NetworkHandler.GUI_SPAWNER_ADVANCED, GuiSpawnerAdvanced.class);
  NetworkHandler.registerGui(NetworkHandler.GUI_SPAWNER_ADVANCED_BLOCK, GuiSpawnerAdvanced.class);
  MinecraftForge.EVENT_BUS.register(StructureBoundingBoxRenderer.instance());
  }
}
