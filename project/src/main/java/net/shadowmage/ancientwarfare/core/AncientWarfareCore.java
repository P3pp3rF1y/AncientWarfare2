package net.shadowmage.ancientwarfare.core;

import net.minecraftforge.common.config.Configuration;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.proxy.CommonProxy;

import org.apache.logging.log4j.core.Logger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod
(
name = "@NAME@",
modid = "@MODID@",
version = "@VERSION@"
)

public class AncientWarfareCore 
{

@Instance
public static AncientWarfareCore instance;

@SidedProxy
(
clientSide = "net.shadowmage.ancientwarfare.core.proxy.CommonProxy",
serverSide = "net.shadowmage.ancientwarfare.core.proxy.CommonProxy"
)
public static CommonProxy proxy;

public static Configuration config;

public static org.apache.logging.log4j.Logger log;

@EventHandler
public void preInit(FMLPreInitializationEvent evt)
  {
  config = new Configuration(evt.getSuggestedConfigurationFile());
  log = evt.getModLog();
  NetworkHandler.INSTANCE.registerChannel();
  }

@EventHandler
public void preInit(FMLInitializationEvent evt)
  {
  
  }

@EventHandler
public void preInit(FMLPostInitializationEvent evt)
  {
  
  }



}
