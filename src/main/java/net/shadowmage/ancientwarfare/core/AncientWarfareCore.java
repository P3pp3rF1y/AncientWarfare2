package net.shadowmage.ancientwarfare.core;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.shadowmage.ancientwarfare.core.block.AWCoreBlockLoader;
import net.shadowmage.ancientwarfare.core.command.CommandResearch;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.config.ClientOptions;
import net.shadowmage.ancientwarfare.core.container.ContainerBackpack;
import net.shadowmage.ancientwarfare.core.container.ContainerEngineeringStation;
import net.shadowmage.ancientwarfare.core.container.ContainerResearchBook;
import net.shadowmage.ancientwarfare.core.container.ContainerResearchStation;
import net.shadowmage.ancientwarfare.core.crafting.AWCoreCrafting;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.core.item.AWCoreItemLoader;
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
import cpw.mods.fml.common.event.FMLServerStartingEvent;

@Mod
(
name = "Ancient Warfare Core",
modid = "AncientWarfare",
version = "@VERSION@",
dependencies="after:BuildCraft|Core"
)

public class AncientWarfareCore 
{

@Instance(value="AncientWarfare")
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
  /**
   * setup config file and logger
   */
  config = AWCoreStatics.getConfigFor("AncientWarfare");
  log = evt.getModLog();
  
  AWLog.log("Ancient Warfare Core Pre-Init Started");  
  AWCoreStatics.loadConfig(config);
  
  
  /**
   * register server-side network handler and anything that needs loaded on the event busses
   */
  NetworkHandler.INSTANCE.registerNetwork();//register network handler, server side  
  MinecraftForge.EVENT_BUS.register(AWGameData.INSTANCE);
  MinecraftForge.EVENT_BUS.register(new ItemEventHandler());
  FMLCommonHandler.instance().bus().register(ResearchTracker.instance());
   
  /**
   * register blocks, items, tile entities, and entities
   */
  AWCoreBlockLoader.INSTANCE.load();
  AWCoreItemLoader.INSTANCE.load();
  
  /**
   * register GUIs, containers, client-side network handler, renderers
   */
  proxy.registerClient();
  NetworkHandler.registerContainer(NetworkHandler.GUI_CRAFTING, ContainerEngineeringStation.class);
  NetworkHandler.registerContainer(NetworkHandler.GUI_RESEARCH_STATION, ContainerResearchStation.class);
  NetworkHandler.registerContainer(NetworkHandler.GUI_BACKPACK, ContainerBackpack.class);
  NetworkHandler.registerContainer(NetworkHandler.GUI_RESEARCH_BOOK, ContainerResearchBook.class);
  /**
   * register Saved-data classes for core module
   */
  AWGameData.INSTANCE.registerSaveData(ResearchData.name, ResearchData.class);
  
  /**
   * initialize any other core module information
   */
  ResearchGoal.initializeResearch();
  
  AWLog.log("Ancient Warfare Core Pre-Init Completed");
  }

@EventHandler
public void init(FMLInitializationEvent evt)
  {
  AWLog.log("Ancient Warfare Core Init Started");
  AWCoreCrafting.loadRecipes();
  ClientOptions.INSTANCE.loadClientOptions();
  /**
   * register recipes
   */
  AWLog.log("Ancient Warfare Core Init Completed");
  }

@EventHandler
public void postInit(FMLPostInitializationEvent evt)
  {
  AWLog.log("Ancient Warfare Core Post-Init Started");
  config.save();
  AWLog.log("Ancient Warfare Core Post-Init Completed");
  }

@EventHandler
public void serverStartingEvent(FMLServerStartingEvent evt)
  {
  evt.registerServerCommand(new CommandResearch());
  }
}
