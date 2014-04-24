package net.shadowmage.ancientwarfare.automation.proxy;

import net.minecraftforge.common.MinecraftForge;
import net.shadowmage.ancientwarfare.automation.gui.GuiWorksiteAnimalControl;
import net.shadowmage.ancientwarfare.automation.gui.GuiWorksiteBase;
import net.shadowmage.ancientwarfare.automation.gui.GuiWorksiteBlockSelection;
import net.shadowmage.ancientwarfare.automation.gui.GuiWorksiteInventorySideSelection;
import net.shadowmage.ancientwarfare.automation.render.WorkBoundingBoxRenderer;
import net.shadowmage.ancientwarfare.core.config.ClientOptions;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.proxy.ClientProxyBase;

public class ClientProxyAutomation extends ClientProxyBase
{

@Override
public void registerClient()
  {
  MinecraftForge.EVENT_BUS.register(WorkBoundingBoxRenderer.instance());
  NetworkHandler.registerGui(NetworkHandler.GUI_WORKSITE_INVENTORY, GuiWorksiteBase.class);
  NetworkHandler.registerGui(NetworkHandler.GUI_WORKSITE_INVENTORY_SIDE_ADJUST, GuiWorksiteInventorySideSelection.class);
  NetworkHandler.registerGui(NetworkHandler.GUI_WORKSITE_SET_TARGETS, GuiWorksiteBlockSelection.class);
  NetworkHandler.registerGui(NetworkHandler.GUI_WORKSITE_ANIMAL_CONTROL, GuiWorksiteAnimalControl.class);
  registerClientOptions();
  }

private void registerClientOptions()
  {
  ClientOptions.INSTANCE.registerClientOption("render_work_bounds", "Should work-bounds be rendered for work-sites.", true);
  ClientOptions.INSTANCE.registerClientOption("render_work_points", "Should individual work-targets be rendered for work-sites.", true);  
  }

}
