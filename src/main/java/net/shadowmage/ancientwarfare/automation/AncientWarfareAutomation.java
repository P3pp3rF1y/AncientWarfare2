package net.shadowmage.ancientwarfare.automation;

import net.minecraftforge.common.config.Configuration;
import net.shadowmage.ancientwarfare.automation.block.AWAutomationBlockLoader;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.automation.container.ContainerMailbox;
import net.shadowmage.ancientwarfare.automation.container.ContainerWarehouseStorage;
import net.shadowmage.ancientwarfare.automation.container.ContainerWarehouseStorageFilter;
import net.shadowmage.ancientwarfare.automation.container.ContainerWorksiteAnimalControl;
import net.shadowmage.ancientwarfare.automation.container.ContainerWorksiteAutoCrafting;
import net.shadowmage.ancientwarfare.automation.container.ContainerWorksiteBase;
import net.shadowmage.ancientwarfare.automation.container.ContainerWorksiteBlockSelection;
import net.shadowmage.ancientwarfare.automation.container.ContainerWorksiteFishControl;
import net.shadowmage.ancientwarfare.automation.container.ContainerWorksiteInventorySideSelection;
import net.shadowmage.ancientwarfare.automation.gamedata.MailboxData;
import net.shadowmage.ancientwarfare.automation.gamedata.MailboxTicker;
import net.shadowmage.ancientwarfare.automation.item.AWAutomationItemLoader;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.api.ModuleStatus;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.proxy.CommonProxyBase;
import cpw.mods.fml.common.FMLCommonHandler;
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
modid = "ancientwarfareautomation",
version = "@VERSION@",
dependencies = "required-after:ancientwarfare"
)

public class AncientWarfareAutomation 
{

@Instance(value="ancientwarfareautomation")
public static AncientWarfareAutomation instance;

@SidedProxy
(
clientSide = "net.shadowmage.ancientwarfare.automation.proxy.ClientProxyAutomation",
serverSide = "net.shadowmage.ancientwarfare.core.proxy.CommonProxy"
)
public static CommonProxyBase proxy;

public static Configuration config;

public static org.apache.logging.log4j.Logger log;

public static AWAutomationStatics statics;


@EventHandler
public void preInit(FMLPreInitializationEvent evt)
  {
  ModuleStatus.automationLoaded = true;
  log = AncientWarfareCore.log;
  AWLog.log("Ancient Warfare Automation Pre-Init started"); 
  config = new Configuration(evt.getSuggestedConfigurationFile());
  statics = new AWAutomationStatics(config);
  proxy.registerClient();
  
  /**
   * load pre-init
   */
  statics.load();
  
  AWAutomationBlockLoader.load();
  AWAutomationItemLoader.load();

  NetworkHandler.registerContainer(NetworkHandler.GUI_WORKSITE_INVENTORY, ContainerWorksiteBase.class);
  NetworkHandler.registerContainer(NetworkHandler.GUI_WORKSITE_INVENTORY_SIDE_ADJUST, ContainerWorksiteInventorySideSelection.class);
  NetworkHandler.registerContainer(NetworkHandler.GUI_WORKSITE_SET_TARGETS, ContainerWorksiteBlockSelection.class);
  NetworkHandler.registerContainer(NetworkHandler.GUI_WORKSITE_ANIMAL_CONTROL, ContainerWorksiteAnimalControl.class);
  NetworkHandler.registerContainer(NetworkHandler.GUI_WORKSITE_AUTO_CRAFT, ContainerWorksiteAutoCrafting.class);
  NetworkHandler.registerContainer(NetworkHandler.GUI_WORKSITE_FISH_CONTROL, ContainerWorksiteFishControl.class);
  NetworkHandler.registerContainer(NetworkHandler.GUI_MAILBOX_INVENTORY, ContainerMailbox.class);
  //warehouse control
  NetworkHandler.registerContainer(NetworkHandler.GUI_WAREHOUSE_STORAGE, ContainerWarehouseStorage.class);
  NetworkHandler.registerContainer(NetworkHandler.GUI_WAREHOUSE_STORAGE_FILTER, ContainerWarehouseStorageFilter.class);
  AWGameData.INSTANCE.registerSaveData(MailboxData.name, MailboxData.class);
  
  FMLCommonHandler.instance().bus().register(new MailboxTicker());
  AWLog.log("Ancient Warfare Automation Pre-Init completed");
  }

@EventHandler
public void init(FMLInitializationEvent evt)
  {
  AWLog.log("Ancient Warfare Automation Init started"); 
  
  AWLog.log("Ancient Warfare Automation Init completed");
  }

@EventHandler
public void postInit(FMLPostInitializationEvent evt)
  {
  AWLog.log("Ancient Warfare Automation Post-Init started"); 
 
  config.save();
  AWLog.log("Ancient Warfare Automation Post-Init completed.  Successfully completed all loading stages.");
  }

}
