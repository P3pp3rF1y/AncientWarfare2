package net.shadowmage.ancientwarfare.nei_plugin;

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
import net.shadowmage.ancientwarfare.automation.container.ContainerWarehouseInput;
import net.shadowmage.ancientwarfare.automation.container.ContainerWarehouseOutput;
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
modid = "AncientWarfareNEIPlugin",
version = "@VERSION@",
dependencies = "required-after:AncientWarfare"
)

public class AncientWarfareNEIPlugin
{

@Instance(value="AncientWarfareNEIPlugin")
public static AncientWarfareNEIPlugin instance;


public static Configuration config;


public static AWAutomationStatics statics;


@EventHandler
public void preInit(FMLPreInitializationEvent evt)
  {
  AWLog.log("Ancient Warfare Automation Pre-Init started");
    
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
  
  AWLog.log("Ancient Warfare Automation Post-Init completed.  Successfully completed all loading stages.");
  }

}
