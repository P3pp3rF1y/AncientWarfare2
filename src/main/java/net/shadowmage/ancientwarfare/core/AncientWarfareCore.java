package net.shadowmage.ancientwarfare.core;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.config.Statics;
import net.shadowmage.ancientwarfare.core.container.ContainerTest;
import net.shadowmage.ancientwarfare.core.gamedata.GameData;
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
import cpw.mods.fml.common.network.internal.FMLNetworkHandler;

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
clientSide = "net.shadowmage.ancientwarfare.core.proxy.ClientProxy",
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
  NetworkHandler.INSTANCE.registerContainer(0, ContainerTest.class);
  FMLCommonHandler.instance().bus().register(this);
  MinecraftForge.EVENT_BUS.register(GameData.INSTANCE);
  proxy.registerClient();
  }

@EventHandler
public void init(FMLInitializationEvent evt)
  {
  
  }

@EventHandler
public void postInit(FMLPostInitializationEvent evt)
  {
  config.save();
  }

@SubscribeEvent
public void onLogin(PlayerEvent.PlayerLoggedInEvent evt)
  {
  String msg = "player logged in...";
  AWLog.logDebug(msg);
  if(!evt.player.worldObj.isRemote)
    {
    NetworkHandler.sendToPlayer((EntityPlayerMP)evt.player, new TestPacket());
    FMLNetworkHandler.openGui(evt.player, this, NetworkHandler.GUI_TEST, evt.player.worldObj, 0, 0, 0);
    }  
  }



}
