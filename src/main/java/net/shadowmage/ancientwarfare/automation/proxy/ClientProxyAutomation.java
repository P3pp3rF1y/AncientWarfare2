package net.shadowmage.ancientwarfare.automation.proxy;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;
import net.shadowmage.ancientwarfare.automation.AncientWarfareAutomation;
import net.shadowmage.ancientwarfare.automation.block.AWAutomationBlockLoader;
import net.shadowmage.ancientwarfare.automation.gui.GuiChunkLoaderDeluxe;
import net.shadowmage.ancientwarfare.automation.gui.GuiMailboxInventory;
import net.shadowmage.ancientwarfare.automation.gui.GuiTorqueGeneratorSterling;
import net.shadowmage.ancientwarfare.automation.gui.GuiWarehouseControl;
import net.shadowmage.ancientwarfare.automation.gui.GuiWarehouseCraftingStation;
import net.shadowmage.ancientwarfare.automation.gui.GuiWarehouseInterface;
import net.shadowmage.ancientwarfare.automation.gui.GuiWarehouseStockViewer;
import net.shadowmage.ancientwarfare.automation.gui.GuiWarehouseStorage;
import net.shadowmage.ancientwarfare.automation.gui.GuiWorksiteAnimalControl;
import net.shadowmage.ancientwarfare.automation.gui.GuiWorksiteAnimalFarm;
import net.shadowmage.ancientwarfare.automation.gui.GuiWorksiteAutoCrafting;
import net.shadowmage.ancientwarfare.automation.gui.GuiWorksiteBoundsAdjust;
import net.shadowmage.ancientwarfare.automation.gui.GuiWorksiteCropFarm;
import net.shadowmage.ancientwarfare.automation.gui.GuiWorksiteFishControl;
import net.shadowmage.ancientwarfare.automation.gui.GuiWorksiteFishFarm;
import net.shadowmage.ancientwarfare.automation.gui.GuiWorksiteInventorySideSelection;
import net.shadowmage.ancientwarfare.automation.gui.GuiWorksiteMushroomFarm;
import net.shadowmage.ancientwarfare.automation.gui.GuiWorksiteQuarry;
import net.shadowmage.ancientwarfare.automation.gui.GuiWorksiteReedFarm;
import net.shadowmage.ancientwarfare.automation.gui.GuiWorksiteTreeFarm;
import net.shadowmage.ancientwarfare.automation.model.ModelAutoCraftingStation;
import net.shadowmage.ancientwarfare.automation.render.RenderSterlingEngine;
import net.shadowmage.ancientwarfare.automation.render.RenderTileTorqueConduit;
import net.shadowmage.ancientwarfare.automation.render.RenderTileTorqueDistributor;
import net.shadowmage.ancientwarfare.automation.render.RenderTileTorqueGeneratorWaterwheel;
import net.shadowmage.ancientwarfare.automation.render.RenderTileWarehouseStockViewer;
import net.shadowmage.ancientwarfare.automation.render.RenderTileWorksite;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueGeneratorSterling;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueGeneratorWaterwheel;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueTransportConduit;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueTransportConduitHeavy;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueTransportConduitMedium;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueTransportDistributor;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueTransportDistributorHeavy;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueTransportDistributorMedium;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.TileWarehouseBase;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.TileWarehouseStockViewer;
import net.shadowmage.ancientwarfare.automation.tile.worksite.TileAutoCrafting;
import net.shadowmage.ancientwarfare.automation.tile.worksite.TileWorksiteBase;
import net.shadowmage.ancientwarfare.core.config.ClientOptions;
import net.shadowmage.ancientwarfare.core.model.ModelBaseAW;
import net.shadowmage.ancientwarfare.core.model.ModelLoader;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.proxy.ClientProxyBase;
import net.shadowmage.ancientwarfare.core.render.TileCraftingTableRender;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxyAutomation extends ClientProxyBase
{

@Override
public void registerClient()
  {  
  registerClientOptions();
  
  NetworkHandler.registerGui(NetworkHandler.GUI_WORKSITE_INVENTORY_SIDE_ADJUST, GuiWorksiteInventorySideSelection.class);
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
  NetworkHandler.registerGui(NetworkHandler.GUI_WAREHOUSE_STOCK, GuiWarehouseStockViewer.class);
  NetworkHandler.registerGui(NetworkHandler.GUI_WORKSITE_BOUNDS, GuiWorksiteBoundsAdjust.class);
  
  
  ClientRegistry.bindTileEntitySpecialRenderer(TileTorqueGeneratorWaterwheel.class, new RenderTileTorqueGeneratorWaterwheel());  
  ClientRegistry.bindTileEntitySpecialRenderer(TileWorksiteBase.class, new RenderTileWorksite());
  ClientRegistry.bindTileEntitySpecialRenderer(TileWarehouseBase.class, new RenderTileWorksite());
  ClientRegistry.bindTileEntitySpecialRenderer(TileWarehouseStockViewer.class, new RenderTileWarehouseStockViewer());
  
  RenderSterlingEngine sterlingRender = new RenderSterlingEngine();
  ClientRegistry.bindTileEntitySpecialRenderer(TileTorqueGeneratorSterling.class, sterlingRender);
  RenderingRegistry.registerBlockHandler(sterlingRender);
    
  TileCraftingTableRender tctr = new TileCraftingTableRender(new ModelAutoCraftingStation(), "textures/model/automation/tile_auto_crafting.png");
  ClientRegistry.bindTileEntitySpecialRenderer(TileAutoCrafting.class, tctr);
  MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(AWAutomationBlockLoader.worksiteAutoCrafting), tctr);
  
  ModelLoader loader = new ModelLoader();
  ModelBaseAW model = loader.loadModel(getClass().getResourceAsStream("/assets/ancientwarfare/models/automation/torque_conduit.m2f"));  
  
  RenderTileTorqueConduit lightConduitRender = new RenderTileTorqueConduit(model, new ResourceLocation("ancientwarfare", "textures/model/automation/tile_torque_conduit_light_reg.png"), new ResourceLocation("ancientwarfare", "textures/model/automation/tile_torque_conduit_light_out.png"));  
  ClientRegistry.bindTileEntitySpecialRenderer(TileTorqueTransportConduit.class, lightConduitRender);
  ClientRegistry.bindTileEntitySpecialRenderer(TileTorqueTransportConduitMedium.class, lightConduitRender);
  ClientRegistry.bindTileEntitySpecialRenderer(TileTorqueTransportConduitHeavy.class, lightConduitRender);
  
  RenderTileTorqueDistributor lightDistributorRender = new RenderTileTorqueDistributor(model, new ResourceLocation("ancientwarfare", "textures/model/automation/tile_torque_conduit_light_reg.png"), new ResourceLocation("ancientwarfare", "textures/model/automation/tile_torque_conduit_light_out.png"));
  ClientRegistry.bindTileEntitySpecialRenderer(TileTorqueTransportDistributor.class, lightDistributorRender);
  ClientRegistry.bindTileEntitySpecialRenderer(TileTorqueTransportDistributorMedium.class, lightDistributorRender);
  ClientRegistry.bindTileEntitySpecialRenderer(TileTorqueTransportDistributorHeavy.class, lightDistributorRender);
    
//  RenderingRegistry.registerBlockHandler(new RenderTorqueConduit());//TODO re-enable this on a configurable basis
  }

private void registerClientOptions()
  {
  ClientOptions.INSTANCE.registerClientOption(ClientOptions.OPTION_RENDER_WORK_BOUNDS, "Should work-bounds be rendered for work-sites.", true, AncientWarfareAutomation.config);
  ClientOptions.INSTANCE.registerClientOption(ClientOptions.OPTION_RENDER_WORK_POINTS, "Should individual work-targets be rendered for work-sites.", true, AncientWarfareAutomation.config);
  }

}
