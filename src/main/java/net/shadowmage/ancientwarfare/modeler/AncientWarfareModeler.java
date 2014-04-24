package net.shadowmage.ancientwarfare.modeler;

import net.minecraftforge.common.config.Configuration;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.api.ModuleStatus;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.modeler.item.ItemModelEditor;
import net.shadowmage.ancientwarfare.modeler.proxy.CommonProxyModeler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod
(
name = "Ancient Warfare Model Editor",
modid = "ancientwarfaremodeler",
version = "@VERSION@",
dependencies = "required-after:ancientwarfare"
)

public class AncientWarfareModeler 
{

@Instance(value="ancientwarfaremodeler")
public static AncientWarfareModeler instance;

@SidedProxy
(
clientSide = "net.shadowmage.ancientwarfare.modeler.proxy.ClientProxyModeler",
serverSide = "net.shadowmage.ancientwarfare.modeler.proxy.CommonProxyModeler"
)
public static CommonProxyModeler proxy;

public static Configuration config;

public static org.apache.logging.log4j.Logger log;

public static ItemModelEditor editorOpener;

@EventHandler
public void preInit(FMLPreInitializationEvent evt)
  {
  AWLog.log("Ancient Warfare Modeler Pre-Init started"); 
  ModuleStatus.modelerLoaded = true;
  log = AncientWarfareCore.log;
  config = new Configuration(evt.getSuggestedConfigurationFile());
  
  /**
   * internal registry
   */
  
  
  editorOpener = new ItemModelEditor("editor_opener");
  editorOpener.setTextureName("ancientwarfare:modeler/editor_opener");
  GameRegistry.registerItem(editorOpener, "editor_opener");
      
  /**
   * load pre-init
   */
  AWLog.log("Ancient Warfare Modeler Pre-Init completed.");
  }

@EventHandler
public void init(FMLInitializationEvent evt)
  {
  AWLog.log("Ancient Warfare Modeler Init started"); 

  AWLog.log("Ancient Warfare Modeler Init completed.");
  }

@EventHandler
public void postInit(FMLPostInitializationEvent evt)
  {
  AWLog.log("Ancient Warfare Modeler Post-Init started"); 

  config.save();
  AWLog.log("Ancient Warfare Modeler Post-Init completed.  Successfully completed all loading stages.");
  }

}
