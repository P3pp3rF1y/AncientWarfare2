package net.shadowmage.ancientwarfare.nei_plugin;

import net.minecraftforge.common.config.Configuration;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod
(
name = "Ancient Warfare NEI Plugin",
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
//  AWLog.log("Ancient Warfare NEI Plugin Pre-Init started");
//    
//  AWLog.log("Ancient Warfare NEI Plugin Pre-Init completed");
  }

@EventHandler
public void init(FMLInitializationEvent evt)
  {
//  AWLog.log("Ancient Warfare NEI Plugin Init started"); 
//
//  AWLog.log("Ancient Warfare NEI Plugin Init completed");
  }

@EventHandler
public void postInit(FMLPostInitializationEvent evt)
  {
  AWLog.log("Ancient Warfare NEI Plugin Post-Init started"); 
  Class clz;
  try
    {
    clz = Class.forName("codechicken.nei.api.API");
    if(clz!=null)
      {
      AWLog.logDebug("NEI Detected, attempting load of NEI Plugin");
      Class clz2 = Class.forName("net.shadowmage.ancientwarfare.nei_plugin.AWNeiProxyActual");
      try
        {
        AWNeiProxyBase proxy = (AWNeiProxyBase) clz2.newInstance();
        proxy.load();
        } 
      catch (InstantiationException e)
        {
        e.printStackTrace();
        } 
      catch (IllegalAccessException e)
        {
        e.printStackTrace();
        }
      }
    } 
  catch (ClassNotFoundException e)
    {
    e.printStackTrace();
    AWLog.log("Skipping loading of NEI plugin, NEI not found!");
    }
  AWLog.log("Ancient Warfare NEI Plugin Post-Init completed.  Successfully completed all loading stages.");
  }

}
