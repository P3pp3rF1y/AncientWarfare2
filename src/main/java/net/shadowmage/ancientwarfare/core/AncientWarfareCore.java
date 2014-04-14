package net.shadowmage.ancientwarfare.core;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.config.Statics;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.core.item.ItemEventHandler;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.proxy.CommonProxyBase;
import net.shadowmage.ancientwarfare.core.research.ResearchData;
import net.shadowmage.ancientwarfare.core.research.ResearchGoal;
import net.shadowmage.ancientwarfare.core.research.ResearchTracker;
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
name = "Ancient Warfare Core",
modid = "ancientwarfare",
version = "@VERSION@"
)

public class AncientWarfareCore 
{

@Instance(value="ancientwarfare")
public static AncientWarfareCore instance;

@SidedProxy
(
clientSide = "net.shadowmage.ancientwarfare.core.proxy.ClientProxy",
serverSide = "net.shadowmage.ancientwarfare.core.proxy.CommonProxy"
)
public static CommonProxyBase proxy;

public static Configuration config;

public static org.apache.logging.log4j.Logger log;

@EventHandler
public void preInit(FMLPreInitializationEvent evt)
  {
  config = new Configuration(evt.getSuggestedConfigurationFile());
  log = evt.getModLog();
  AWLog.log("Ancient Warfare Core Pre-Init Started"); 
  Statics.configPath = evt.getModConfigurationDirectory().getAbsolutePath();
  NetworkHandler.INSTANCE.registerNetwork();
  MinecraftForge.EVENT_BUS.register(AWGameData.INSTANCE);
  MinecraftForge.EVENT_BUS.register(new ItemEventHandler());
  FMLCommonHandler.instance().bus().register(ResearchTracker.instance());
  AWGameData.INSTANCE.registerSaveData(ResearchData.name, ResearchData.class);
  ResearchGoal.initializeResearch();
  proxy.registerClient();
  AWLog.log("Ancient Warfare Core Pre-Init Completed");
  }

@EventHandler
public void init(FMLInitializationEvent evt)
  {
  AWLog.log("Ancient Warfare Core Init Started");
  AWLog.log("Ancient Warfare Core Init Completed");
  }

@EventHandler
public void postInit(FMLPostInitializationEvent evt)
  {
  AWLog.log("Ancient Warfare Core Post-Init Started");
  config.save();
  AWLog.log("Ancient Warfare Core Post-Init Completed");
  }

}
