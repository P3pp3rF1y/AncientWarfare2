package net.shadowmage.ancientwarfare.core;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.config.Configuration;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.config.Statics;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.network.TestPacket;
import net.shadowmage.ancientwarfare.core.proxy.CommonProxy;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

@Mod
(
name = "Ancient Warfare Core",
modid = "ancientwarfare",
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
  Statics.configPath = evt.getModConfigurationDirectory().getAbsolutePath();
  NetworkHandler.INSTANCE.registerNetwork();
  FMLCommonHandler.instance().bus().register(this);
  }

@EventHandler
public void preInit(FMLInitializationEvent evt)
  {
  
  }

@EventHandler
public void preInit(FMLPostInitializationEvent evt)
  {
  
  }

@SubscribeEvent
public void onLogin(PlayerEvent.PlayerLoggedInEvent evt)
  {
  String msg = "player logged in...";
  AWLog.logDebug(msg);
  AWLog.log(msg);
  AWLog.logError(msg);
   
  if(!evt.player.worldObj.isRemote)
    {
    NetworkHandler.sendToPlayer((EntityPlayerMP)evt.player, new TestPacket());
    }
  }



}
