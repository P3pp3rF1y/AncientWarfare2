package net.shadowmage.ancientwarfare.nei_plugin;

import net.minecraftforge.common.config.Configuration;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import codechicken.nei.api.API;
import codechicken.nei.recipe.ICraftingHandler;
import codechicken.nei.recipe.IUsageHandler;
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
  AWLog.log("Ancient Warfare NEI Plugin Pre-Init started");
    
  AWLog.log("Ancient Warfare NEI Plugin Pre-Init completed");
  }

@EventHandler
public void init(FMLInitializationEvent evt)
  {
  AWLog.log("Ancient Warfare NEI Plugin Init started"); 

  AWLog.log("Ancient Warfare NEI Plugin Init completed");
  }

@EventHandler
public void postInit(FMLPostInitializationEvent evt)
  {
  AWLog.log("Ancient Warfare NEI Plugin Post-Init started"); 
  AWNeiRecipeHandler handler = new AWNeiRecipeHandler();
  API.registerRecipeHandler((ICraftingHandler)handler);
  API.registerUsageHandler((IUsageHandler)handler);
  AWLog.log("Ancient Warfare NEI Plugin Post-Init completed.  Successfully completed all loading stages.");
  }

}
