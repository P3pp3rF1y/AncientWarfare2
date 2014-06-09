package net.shadowmage.ancientwarfare.automation.proxy;

import net.minecraftforge.common.MinecraftForge;
import net.shadowmage.ancientwarfare.automation.AncientWarfareAutomation;
import net.shadowmage.ancientwarfare.automation.gui.GuiChunkLoaderDeluxe;
import net.shadowmage.ancientwarfare.automation.gui.GuiMailboxInventory;
import net.shadowmage.ancientwarfare.automation.gui.GuiTorqueGeneratorSterling;
import net.shadowmage.ancientwarfare.automation.gui.GuiWarehouseControl;
import net.shadowmage.ancientwarfare.automation.gui.GuiWarehouseCraftingStation;
import net.shadowmage.ancientwarfare.automation.gui.GuiWarehouseInterface;
import net.shadowmage.ancientwarfare.automation.gui.GuiWarehouseStorage;
import net.shadowmage.ancientwarfare.automation.gui.GuiWorksiteAnimalControl;
import net.shadowmage.ancientwarfare.automation.gui.GuiWorksiteAnimalFarm;
import net.shadowmage.ancientwarfare.automation.gui.GuiWorksiteAutoCrafting;
import net.shadowmage.ancientwarfare.automation.gui.GuiWorksiteBlockSelection;
import net.shadowmage.ancientwarfare.automation.gui.GuiWorksiteCropFarm;
import net.shadowmage.ancientwarfare.automation.gui.GuiWorksiteFishControl;
import net.shadowmage.ancientwarfare.automation.gui.GuiWorksiteFishFarm;
import net.shadowmage.ancientwarfare.automation.gui.GuiWorksiteInventorySideSelection;
import net.shadowmage.ancientwarfare.automation.gui.GuiWorksiteMushroomFarm;
import net.shadowmage.ancientwarfare.automation.gui.GuiWorksiteQuarry;
import net.shadowmage.ancientwarfare.automation.gui.GuiWorksiteReedFarm;
import net.shadowmage.ancientwarfare.automation.gui.GuiWorksiteTreeFarm;
import net.shadowmage.ancientwarfare.automation.render.RenderTileTorqueGeneratorWaterwheel;
import net.shadowmage.ancientwarfare.automation.render.RenderTileWorksite;
import net.shadowmage.ancientwarfare.automation.render.RenderTorqueConduit;
import net.shadowmage.ancientwarfare.automation.render.WorkBoundingBoxRenderer;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueGeneratorWaterwheel;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.TileWarehouseBase;
import net.shadowmage.ancientwarfare.automation.tile.worksite.TileWorksiteBase;
import net.shadowmage.ancientwarfare.core.config.ClientOptions;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.proxy.ClientProxyBase;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;

public class ClientProxyAutomation extends ClientProxyBase
{

@Override
public void registerClient()
  {
  MinecraftForge.EVENT_BUS.register(WorkBoundingBoxRenderer.instance());
  FMLCommonHandler.instance().bus().register(WorkBoundingBoxRenderer.instance());
  NetworkHandler.registerGui(NetworkHandler.GUI_WORKSITE_INVENTORY_SIDE_ADJUST, GuiWorksiteInventorySideSelection.class);
  NetworkHandler.registerGui(NetworkHandler.GUI_WORKSITE_SET_TARGETS, GuiWorksiteBlockSelection.class);
  NetworkHandler.registerGui(NetworkHandler.GUI_WORKSITE_ANIMAL_CONTROL, GuiWorksiteAnimalControl.class);
  NetworkHandler.registerGui(NetworkHandler.GUI_WORKSITE_AUTO_CRAFT, GuiWorksiteAutoCrafting.class);
  NetworkHandler.registerGui(NetworkHandler.GUI_WORKSITE_FISH_CONTROL, GuiWorksiteFishControl.class);
  NetworkHandler.registerGui(NetworkHandler.GUI_MAILBOX_INVENTORY, GuiMailboxInventory.class);
  NetworkHandler.registerGui(NetworkHandler.GUI_WAREHOUSE_CONTROL, GuiWarehouseControl.class);
  NetworkHandler.registerGui(NetworkHandler.GUI_WAREHOUSE_STORAGE, GuiWarehouseStorage.class);
  NetworkHandler.registerGui(NetworkHandler.GUI_WAREHOUSE_OUTPUT, GuiWarehouseInterface.class);
  NetworkHandler.registerGui(NetworkHandler.GUI_WAREHOUSE_CRAFTING, GuiWarehouseCraftingStation.class); 
  NetworkHandler.registerGui(NetworkHandler.GUI_WORKSITE_QUARRY, GuiWorksiteQuarry.class);
  NetworkHandler.registerGui(NetworkHandler.GUI_WORKSITE_TREE_FARM, GuiWorksiteTreeFarm.class);
  NetworkHandler.registerGui(NetworkHandler.GUI_WORKSITE_CROP_FARM, GuiWorksiteCropFarm.class);
  NetworkHandler.registerGui(NetworkHandler.GUI_WORKSITE_MUSHROOM_FARM, GuiWorksiteMushroomFarm.class);
  NetworkHandler.registerGui(NetworkHandler.GUI_WORKSITE_ANIMAL_FARM, GuiWorksiteAnimalFarm.class);
  NetworkHandler.registerGui(NetworkHandler.GUI_WORKSITE_REED_FARM, GuiWorksiteReedFarm.class);
  NetworkHandler.registerGui(NetworkHandler.GUI_WORKSITE_FISH_FARM, GuiWorksiteFishFarm.class);
  NetworkHandler.registerGui(NetworkHandler.GUI_TORQUE_GENERATOR_STERLING, GuiTorqueGeneratorSterling.class);
  NetworkHandler.registerGui(NetworkHandler.GUI_CHUNK_LOADER_DELUXE, GuiChunkLoaderDeluxe.class);  
  ClientRegistry.bindTileEntitySpecialRenderer(TileTorqueGeneratorWaterwheel.class, new RenderTileTorqueGeneratorWaterwheel());  
  ClientRegistry.bindTileEntitySpecialRenderer(TileWorksiteBase.class, new RenderTileWorksite());
  ClientRegistry.bindTileEntitySpecialRenderer(TileWarehouseBase.class, new RenderTileWorksite());
  
  RenderingRegistry.registerBlockHandler(new RenderTorqueConduit());
  registerClientOptions();
  }

private void registerClientOptions()
  {
  ClientOptions.INSTANCE.registerClientOption(ClientOptions.OPTION_RENDER_WORK_BOUNDS, "Should work-bounds be rendered for work-sites.", true, AncientWarfareAutomation.config);
  ClientOptions.INSTANCE.registerClientOption(ClientOptions.OPTION_RENDER_WORK_POINTS, "Should individual work-targets be rendered for work-sites.", true, AncientWarfareAutomation.config);
  }

}
