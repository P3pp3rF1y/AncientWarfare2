package net.shadowmage.ancientwarfare.npc;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.shadowmage.ancientwarfare.core.api.ModuleStatus;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.network.PacketBase;
import net.shadowmage.ancientwarfare.npc.block.AWNPCBlockLoader;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;
import net.shadowmage.ancientwarfare.npc.container.ContainerCombatOrder;
import net.shadowmage.ancientwarfare.npc.container.ContainerNpcInventory;
import net.shadowmage.ancientwarfare.npc.container.ContainerNpcTrade;
import net.shadowmage.ancientwarfare.npc.container.ContainerUpkeepOrder;
import net.shadowmage.ancientwarfare.npc.container.ContainerWorkOrder;
import net.shadowmage.ancientwarfare.npc.entity.AWNPCEntityLoader;
import net.shadowmage.ancientwarfare.npc.faction.FactionTracker;
import net.shadowmage.ancientwarfare.npc.gamedata.FactionData;
import net.shadowmage.ancientwarfare.npc.item.AWNpcItemLoader;
import net.shadowmage.ancientwarfare.npc.network.PacketFactionUpdate;
import net.shadowmage.ancientwarfare.npc.network.PacketNpcCommand;
import net.shadowmage.ancientwarfare.npc.proxy.NpcCommonProxy;
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
clientSide = "net.shadowmage.ancientwarfare.npc.proxy.NpcClientProxy",
serverSide = "net.shadowmage.ancientwarfare.npc.proxy.NpcCommonProxy"
)
public static NpcCommonProxy proxy;

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
  FMLCommonHandler.instance().bus().register(FactionTracker.INSTANCE);
  MinecraftForge.EVENT_BUS.register(net.shadowmage.ancientwarfare.npc.event.EventHandler.INSTANCE);
//  FMLCommonHandler.instance().bus().register(net.shadowmage.ancientwarfare.npc.event.EventHandler.INSTANCE);
  
  /**
   * load items, blocks, and entities
   */
  AWNpcItemLoader.load();
  AWNPCBlockLoader.load();
  AWNPCEntityLoader.load();

  /**
   * register containers
   */
  NetworkHandler.registerContainer(NetworkHandler.GUI_NPC_INVENTORY, ContainerNpcInventory.class);
  NetworkHandler.registerContainer(NetworkHandler.GUI_NPC_WORK_ORDER, ContainerWorkOrder.class);
  NetworkHandler.registerContainer(NetworkHandler.GUI_NPC_UPKEEP_ORDER, ContainerUpkeepOrder.class);
  NetworkHandler.registerContainer(NetworkHandler.GUI_NPC_COMBAT_ORDER, ContainerCombatOrder.class);
  NetworkHandler.registerContainer(NetworkHandler.GUI_NPC_TRADE, ContainerNpcTrade.class);
  PacketBase.registerPacketType(NetworkHandler.PACKET_NPC_COMMAND, PacketNpcCommand.class);
  PacketBase.registerPacketType(NetworkHandler.PACKET_FACTION_UPDATE, PacketFactionUpdate.class);
  
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
  statics.loadDefaultTrades();
  proxy.loadSkins();
  AWNPCEntityLoader.loadNpcSubtypeEquipment();
  config.save();
  AWLog.log("Ancient Warfare NPCs Post-Init completed.  Successfully completed all loading stages.");
  }


}
