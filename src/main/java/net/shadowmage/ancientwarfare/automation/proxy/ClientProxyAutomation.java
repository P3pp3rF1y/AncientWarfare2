package net.shadowmage.ancientwarfare.automation.proxy;

import net.minecraftforge.common.MinecraftForge;
import net.shadowmage.ancientwarfare.automation.gui.GuiMailboxInventory;
import net.shadowmage.ancientwarfare.automation.gui.GuiMechanicalWorker;
import net.shadowmage.ancientwarfare.automation.gui.GuiWarehouseControl;
import net.shadowmage.ancientwarfare.automation.gui.GuiWarehouseCraftingStation;
import net.shadowmage.ancientwarfare.automation.gui.GuiWarehouseInput;
import net.shadowmage.ancientwarfare.automation.gui.GuiWarehouseOutput;
import net.shadowmage.ancientwarfare.automation.gui.GuiWarehouseStorage;
import net.shadowmage.ancientwarfare.automation.gui.GuiWorksiteAnimalControl;
import net.shadowmage.ancientwarfare.automation.gui.GuiWorksiteAutoCrafting;
import net.shadowmage.ancientwarfare.automation.gui.GuiWorksiteBase;
import net.shadowmage.ancientwarfare.automation.gui.GuiWorksiteBlockSelection;
import net.shadowmage.ancientwarfare.automation.gui.GuiWorksiteFishControl;
import net.shadowmage.ancientwarfare.automation.gui.GuiWorksiteInventorySideSelection;
import net.shadowmage.ancientwarfare.automation.gui.GuiWorksiteQuarry;
import net.shadowmage.ancientwarfare.automation.render.RenderTileWarehouseStorageBase;
import net.shadowmage.ancientwarfare.automation.render.WorkBoundingBoxRenderer;
import net.shadowmage.ancientwarfare.automation.tile.TileWarehouseStorageBase;
import net.shadowmage.ancientwarfare.core.config.ClientOptions;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.proxy.ClientProxyBase;
import cpw.mods.fml.client.registry.ClientRegistry;

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
  NetworkHandler.registerGui(NetworkHandler.GUI_WORKSITE_AUTO_CRAFT, GuiWorksiteAutoCrafting.class);
  NetworkHandler.registerGui(NetworkHandler.GUI_WORKSITE_FISH_CONTROL, GuiWorksiteFishControl.class);
  NetworkHandler.registerGui(NetworkHandler.GUI_MAILBOX_INVENTORY, GuiMailboxInventory.class);
  NetworkHandler.registerGui(NetworkHandler.GUI_WAREHOUSE_CONTROL, GuiWarehouseControl.class);
  NetworkHandler.registerGui(NetworkHandler.GUI_WAREHOUSE_STORAGE, GuiWarehouseStorage.class);
  NetworkHandler.registerGui(NetworkHandler.GUI_WAREHOUSE_INPUT, GuiWarehouseInput.class);
  NetworkHandler.registerGui(NetworkHandler.GUI_WAREHOUSE_OUTPUT, GuiWarehouseOutput.class);
  NetworkHandler.registerGui(NetworkHandler.GUI_WAREHOUSE_CRAFTING, GuiWarehouseCraftingStation.class); 
  NetworkHandler.registerGui(NetworkHandler.GUI_MECHANICAL_WORKER, GuiMechanicalWorker.class);
  NetworkHandler.registerGui(NetworkHandler.GUI_WORKSITE_QUARRY, GuiWorksiteQuarry.class);
  ClientRegistry.bindTileEntitySpecialRenderer(TileWarehouseStorageBase.class, new RenderTileWarehouseStorageBase());
  registerClientOptions();
  }

private void registerClientOptions()
  {
  ClientOptions.INSTANCE.registerClientOption(ClientOptions.OPTION_RENDER_WORK_BOUNDS, "Should work-bounds be rendered for work-sites.", true);
  ClientOptions.INSTANCE.registerClientOption(ClientOptions.OPTION_RENDER_WORK_POINTS, "Should individual work-targets be rendered for work-sites.", true);
  }

}
