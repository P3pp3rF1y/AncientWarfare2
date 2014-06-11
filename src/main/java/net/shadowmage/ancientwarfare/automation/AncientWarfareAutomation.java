package net.shadowmage.ancientwarfare.automation;

import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.config.Configuration;
import net.shadowmage.ancientwarfare.automation.block.AWAutomationBlockLoader;
import net.shadowmage.ancientwarfare.automation.chunkloader.AWChunkLoader;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.automation.container.ContainerChunkLoaderDeluxe;
import net.shadowmage.ancientwarfare.automation.container.ContainerMailbox;
import net.shadowmage.ancientwarfare.automation.container.ContainerTorqueGeneratorSterling;
import net.shadowmage.ancientwarfare.automation.container.ContainerWarehouseControl;
import net.shadowmage.ancientwarfare.automation.container.ContainerWarehouseCraftingStation;
import net.shadowmage.ancientwarfare.automation.container.ContainerWarehouseInterface;
import net.shadowmage.ancientwarfare.automation.container.ContainerWarehouseStockViewer;
import net.shadowmage.ancientwarfare.automation.container.ContainerWarehouseStorage;
import net.shadowmage.ancientwarfare.automation.container.ContainerWorksiteAnimalControl;
import net.shadowmage.ancientwarfare.automation.container.ContainerWorksiteAnimalFarm;
import net.shadowmage.ancientwarfare.automation.container.ContainerWorksiteAutoCrafting;
import net.shadowmage.ancientwarfare.automation.container.ContainerWorksiteBlockSelection;
import net.shadowmage.ancientwarfare.automation.container.ContainerWorksiteCropFarm;
import net.shadowmage.ancientwarfare.automation.container.ContainerWorksiteFishControl;
import net.shadowmage.ancientwarfare.automation.container.ContainerWorksiteFishFarm;
import net.shadowmage.ancientwarfare.automation.container.ContainerWorksiteInventorySideSelection;
import net.shadowmage.ancientwarfare.automation.container.ContainerWorksiteMushroomFarm;
import net.shadowmage.ancientwarfare.automation.container.ContainerWorksiteQuarry;
import net.shadowmage.ancientwarfare.automation.container.ContainerWorksiteReedFarm;
import net.shadowmage.ancientwarfare.automation.container.ContainerWorksiteTreeFarm;
import net.shadowmage.ancientwarfare.automation.crafting.AWAutomationCrafting;
import net.shadowmage.ancientwarfare.automation.gamedata.MailboxData;
import net.shadowmage.ancientwarfare.automation.gamedata.MailboxTicker;
import net.shadowmage.ancientwarfare.automation.item.AWAutomationItemLoader;
import net.shadowmage.ancientwarfare.automation.proxy.BCProxy;
import net.shadowmage.ancientwarfare.core.api.ModuleStatus;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.proxy.CommonProxyBase;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod
(
name = "Ancient Warfare Automation",
modid = "AncientWarfareAutomation",
version = "@VERSION@",
dependencies = "required-after:AncientWarfare"
)

public class AncientWarfareAutomation
{

@Instance(value="AncientWarfareAutomation")
public static AncientWarfareAutomation instance;

@SidedProxy
(
clientSide = "net.shadowmage.ancientwarfare.automation.proxy.ClientProxyAutomation",
serverSide = "net.shadowmage.ancientwarfare.core.proxy.CommonProxy"
)
public static CommonProxyBase proxy;

public static Configuration config;


public static AWAutomationStatics statics;


@EventHandler
public void preInit(FMLPreInitializationEvent evt)
  {
  AWLog.log("Ancient Warfare Automation Pre-Init started");
  
  ModuleStatus.automationLoaded = true;  
  if(Loader.isModLoaded("BuildCraft|Core"))
    {
    ModuleStatus.buildCraftLoaded = true;
    AWLog.log("Detecting BuildCraft|Core is loaded, enabling BC Compatibility");
    }   
  BCProxy.loadInstance();
  
  /**
   * setup module-owned config file and config-access class
   */
  config = new Configuration(evt.getSuggestedConfigurationFile());
  statics = new AWAutomationStatics(config);  
    
  /**
   * load pre-init
   */  
  proxy.registerClient();
  statics.load();//load config settings
  
  /**
   * load items and blocks
   */
  AWAutomationBlockLoader.load();
  AWAutomationItemLoader.load();

  /**
   * register containers
   */
  NetworkHandler.registerContainer(NetworkHandler.GUI_WORKSITE_INVENTORY_SIDE_ADJUST, ContainerWorksiteInventorySideSelection.class);
  NetworkHandler.registerContainer(NetworkHandler.GUI_WORKSITE_SET_TARGETS, ContainerWorksiteBlockSelection.class);
  NetworkHandler.registerContainer(NetworkHandler.GUI_WORKSITE_ANIMAL_CONTROL, ContainerWorksiteAnimalControl.class);
  NetworkHandler.registerContainer(NetworkHandler.GUI_WORKSITE_AUTO_CRAFT, ContainerWorksiteAutoCrafting.class);
  NetworkHandler.registerContainer(NetworkHandler.GUI_WORKSITE_FISH_CONTROL, ContainerWorksiteFishControl.class);
  NetworkHandler.registerContainer(NetworkHandler.GUI_MAILBOX_INVENTORY, ContainerMailbox.class);
  NetworkHandler.registerContainer(NetworkHandler.GUI_WAREHOUSE_CONTROL, ContainerWarehouseControl.class);
  NetworkHandler.registerContainer(NetworkHandler.GUI_WAREHOUSE_STORAGE, ContainerWarehouseStorage.class);
  NetworkHandler.registerContainer(NetworkHandler.GUI_WAREHOUSE_OUTPUT, ContainerWarehouseInterface.class);
  NetworkHandler.registerContainer(NetworkHandler.GUI_WAREHOUSE_CRAFTING, ContainerWarehouseCraftingStation.class);
  NetworkHandler.registerContainer(NetworkHandler.GUI_WORKSITE_QUARRY, ContainerWorksiteQuarry.class);
  NetworkHandler.registerContainer(NetworkHandler.GUI_WORKSITE_TREE_FARM, ContainerWorksiteTreeFarm.class);
  NetworkHandler.registerContainer(NetworkHandler.GUI_WORKSITE_CROP_FARM, ContainerWorksiteCropFarm.class);
  NetworkHandler.registerContainer(NetworkHandler.GUI_WORKSITE_MUSHROOM_FARM, ContainerWorksiteMushroomFarm.class);
  NetworkHandler.registerContainer(NetworkHandler.GUI_WORKSITE_ANIMAL_FARM, ContainerWorksiteAnimalFarm.class);
  NetworkHandler.registerContainer(NetworkHandler.GUI_WORKSITE_REED_FARM, ContainerWorksiteReedFarm.class);
  NetworkHandler.registerContainer(NetworkHandler.GUI_WORKSITE_FISH_FARM, ContainerWorksiteFishFarm.class);
  NetworkHandler.registerContainer(NetworkHandler.GUI_TORQUE_GENERATOR_STERLING, ContainerTorqueGeneratorSterling.class);
  NetworkHandler.registerContainer(NetworkHandler.GUI_CHUNK_LOADER_DELUXE, ContainerChunkLoaderDeluxe.class);
  NetworkHandler.registerContainer(NetworkHandler.GUI_WAREHOUSE_STOCK, ContainerWarehouseStockViewer.class);
  /**
   * register persistent game-data handlers
   */
  AWGameData.INSTANCE.registerSaveData(MailboxData.name, MailboxData.class);
  
  /**
   * register tick-handlers
   */
  FMLCommonHandler.instance().bus().register(new MailboxTicker());
  
  ForgeChunkManager.setForcedChunkLoadingCallback(this, new AWChunkLoader());
  
  AWLog.log("Ancient Warfare Automation Pre-Init completed");
  }

@EventHandler
public void init(FMLInitializationEvent evt)
  {
  AWLog.log("Ancient Warfare Automation Init started"); 
  /**
   * construct recipes, load plugins
   */
  AWAutomationCrafting.loadRecipes();
  AWLog.log("Ancient Warfare Automation Init completed");
  }

@EventHandler
public void postInit(FMLPostInitializationEvent evt)
  {
  AWLog.log("Ancient Warfare Automation Post-Init started"); 
   /**
    * save config for any changes that were made during loading stages
    */
  config.save();
  AWLog.log("Ancient Warfare Automation Post-Init completed.  Successfully completed all loading stages.");
  }

}
