package net.shadowmage.ancientwarfare.automation;

import net.minecraftforge.common.config.Configuration;
import net.shadowmage.ancientwarfare.automation.block.AWAutomationBlockLoader;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.automation.item.AWAutomationItemLoader;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.proxy.CommonProxyBase;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
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
