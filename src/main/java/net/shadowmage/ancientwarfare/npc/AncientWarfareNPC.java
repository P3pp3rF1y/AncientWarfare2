package net.shadowmage.ancientwarfare.npc;

import net.minecraftforge.common.config.Configuration;
import net.shadowmage.ancientwarfare.core.api.ModuleStatus;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.proxy.CommonProxyBase;
import net.shadowmage.ancientwarfare.npc.block.AWNPCBlockLoader;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;
import net.shadowmage.ancientwarfare.npc.container.ContainerNpcInventory;
import net.shadowmage.ancientwarfare.npc.entity.AWNPCEntityLoader;
import net.shadowmage.ancientwarfare.npc.gamedata.FactionData;
import net.shadowmage.ancientwarfare.npc.item.AWNPCItemLoader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod
(
name = "Ancient Warfare NPCs",
modid = "ancientwarfarenpc",
version = "@VERSION@",
dependencies = "required-after:ancientwarfare"
)

public class AncientWarfareNPC 
{

@Instance(value="ancientwarfarenpc")
public static AncientWarfareNPC instance;

@SidedProxy
(
clientSide = "net.shadowmage.ancientwarfare.npc.proxy.ClientProxyNPC",
serverSide = "net.shadowmage.ancientwarfare.core.proxy.CommonProxy"
)
public static CommonProxyBase proxy;

public static Configuration config;

public static AWNPCStatics statics;

@EventHandler
public void preInit(FMLPreInitializationEvent evt)
  {
  AWLog.log("Ancient Warfare NPCs Pre-Init started");
  
  ModuleStatus.npcsLoaded = true; 
  
  /**
   * setup module-owned config file and config-access class
   */
  config = new Configuration(evt.getSuggestedConfigurationFile());
  statics = new AWNPCStatics(config);
    
  /**
   * load pre-init
   */  
  proxy.registerClient();
  statics.load();//load config settings
  
  /**
   * load items, blocks, and entities
   */
  AWNPCItemLoader.load();
  AWNPCBlockLoader.load();
  AWNPCEntityLoader.load();

  /**
   * register containers
   */
  NetworkHandler.registerContainer(NetworkHandler.GUI_NPC_INVENTORY, ContainerNpcInventory.class);
  
  /**
   * register persistent game-data handlers
   */
  AWGameData.INSTANCE.registerSaveData(FactionData.name, FactionData.class);
  
  /**
   * register tick-handlers
   */
  
  AWLog.log("Ancient Warfare NPCs Pre-Init completed");
  }

@EventHandler
public void init(FMLInitializationEvent evt)
  {
  AWLog.log("Ancient Warfare NPCs Init started"); 
  
  /**
   * construct recipes, load plugins
   */
  AWLog.log("Ancient Warfare NPCs Init completed");
  }

@EventHandler
public void postInit(FMLPostInitializationEvent evt)
  {
  AWLog.log("Ancient Warfare NPCs Post-Init started"); 
   /**
    * save config for any changes that were made during loading stages
    */
  config.save();
  AWLog.log("Ancient Warfare NPCs Post-Init completed.  Successfully completed all loading stages.");
  }

}
