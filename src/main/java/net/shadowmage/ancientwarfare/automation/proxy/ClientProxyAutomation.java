package net.shadowmage.ancientwarfare.automation.proxy;

import cpw.mods.fml.client.registry.ClientRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.shadowmage.ancientwarfare.automation.gui.GuiWorksiteTest;
import net.shadowmage.ancientwarfare.automation.render.WorkBoundingBoxRenderer;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.proxy.ClientProxyBase;
import net.shadowmage.ancientwarfare.structure.event.StructureBoundingBoxRenderer;
import net.shadowmage.ancientwarfare.structure.gui.GuiSpawnerPlacer;
import net.shadowmage.ancientwarfare.structure.gui.GuiStructureScanner;
import net.shadowmage.ancientwarfare.structure.gui.GuiStructureSelection;

public class ClientProxyAutomation extends ClientProxyBase
{

@Override
public void registerClient()
  {
  MinecraftForge.EVENT_BUS.register(WorkBoundingBoxRenderer.instance());
  NetworkHandler.registerGui(NetworkHandler.GUI_WORKSITE_TEST, GuiWorksiteTest.class);
  }


}
