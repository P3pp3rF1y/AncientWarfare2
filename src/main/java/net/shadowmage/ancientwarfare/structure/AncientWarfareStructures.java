package net.shadowmage.ancientwarfare.structure;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.config.Configuration;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.network.PacketBase;
import net.shadowmage.ancientwarfare.core.proxy.CommonProxyBase;
import net.shadowmage.ancientwarfare.structure.block.AWStructuresBlockLoader;
import net.shadowmage.ancientwarfare.structure.block.BlockDataManager;
import net.shadowmage.ancientwarfare.structure.config.AWStructureStatics;
import net.shadowmage.ancientwarfare.structure.container.ContainerGateControl;
import net.shadowmage.ancientwarfare.structure.container.ContainerSpawnerAdvanced;
import net.shadowmage.ancientwarfare.structure.container.ContainerSpawnerAdvancedBlock;
import net.shadowmage.ancientwarfare.structure.container.ContainerSpawnerAdvancedInventoryBlock;
import net.shadowmage.ancientwarfare.structure.container.ContainerSpawnerAdvancedInventoryItem;
import net.shadowmage.ancientwarfare.structure.container.ContainerSpawnerPlacer;
import net.shadowmage.ancientwarfare.structure.container.ContainerStructureScanner;
import net.shadowmage.ancientwarfare.structure.container.ContainerStructureSelection;
import net.shadowmage.ancientwarfare.structure.entity.EntityGate;
import net.shadowmage.ancientwarfare.structure.item.AWStructuresItemLoader;
import net.shadowmage.ancientwarfare.structure.network.PacketStructure;
import net.shadowmage.ancientwarfare.structure.network.PacketStructureImageData;
import net.shadowmage.ancientwarfare.structure.network.PacketStructureImageList;
import net.shadowmage.ancientwarfare.structure.template.StructurePluginManager;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateManager;
import net.shadowmage.ancientwarfare.structure.template.WorldGenStructureManager;
import net.shadowmage.ancientwarfare.structure.template.load.TemplateLoader;
import net.shadowmage.ancientwarfare.structure.world_gen.StructureMap;
import net.shadowmage.ancientwarfare.structure.world_gen.WorldStructureGenerator;
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
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod
(
name = "Ancient Warfare Structures",
modid = "ancientwarfarestructures",
version = "@VERSION@",
dependencies = "required-after:ancientwarfare"
)

public class AncientWarfareStructures 
{

@Instance(value="ancientwarfarestructures")
public static AncientWarfareStructures instance;

@SidedProxy
(
clientSide = "net.shadowmage.ancientwarfare.structure.proxy.ClientProxyStructures",
serverSide = "net.shadowmage.ancientwarfare.core.proxy.CommonProxy"
)
public static CommonProxyBase proxy;

public static Configuration config;

public static org.apache.logging.log4j.Logger log;

public static AWStructureStatics statics;


@EventHandler
public void preInit(FMLPreInitializationEvent evt)
  {
  log = AncientWarfareCore.log;
  AWLog.log("Ancient Warfare Structures Pre-Init started"); 
  config = new Configuration(evt.getSuggestedConfigurationFile());
  statics = new AWStructureStatics(config);
  
  /**
   * Forge/FML registry
   */  
  FMLCommonHandler.instance().bus().register(this);
  GameRegistry.registerWorldGenerator(WorldStructureGenerator.instance(), 0);
  EntityRegistry.registerModEntity(EntityGate.class, "aw_gate", 0, this, 250, 200, false);
  /**
   * internal registry
   */
  PacketBase.registerPacketType(NetworkHandler.PACKET_STRUCTURE, PacketStructure.class);   
  PacketBase.registerPacketType(NetworkHandler.PACKET_STRUCTURE_IMAGE_LIST, PacketStructureImageList.class);
  PacketBase.registerPacketType(NetworkHandler.PACKET_STRUCTURE_IMAGE_DATA, PacketStructureImageData.class);
  AWGameData.INSTANCE.registerSaveData("AWStructureMap", StructureMap.class);
  NetworkHandler.registerContainer(NetworkHandler.GUI_SCANNER, ContainerStructureScanner.class);
  NetworkHandler.registerContainer(NetworkHandler.GUI_BUILDER, ContainerStructureSelection.class);
  NetworkHandler.registerContainer(NetworkHandler.GUI_SPAWNER, ContainerSpawnerPlacer.class);
  NetworkHandler.registerContainer(NetworkHandler.GUI_SPAWNER_ADVANCED, ContainerSpawnerAdvanced.class);
  NetworkHandler.registerContainer(NetworkHandler.GUI_SPAWNER_ADVANCED_BLOCK, ContainerSpawnerAdvancedBlock.class);
  NetworkHandler.registerContainer(NetworkHandler.GUI_SPAWNER_ADVANCED_INVENTORY, ContainerSpawnerAdvancedInventoryItem.class);
  NetworkHandler.registerContainer(NetworkHandler.GUI_SPAWNER_ADVANCED_BLOCK_INVENTORY, ContainerSpawnerAdvancedInventoryBlock.class);
  NetworkHandler.registerContainer(NetworkHandler.GUI_GATE_CONTROL, ContainerGateControl.class);
  proxy.registerClient();   
      
  /**
   * load pre-init
   */
  statics.load();
  AWStructuresItemLoader.load();
  AWStructuresBlockLoader.load();
  String path = evt.getModConfigurationDirectory().getAbsolutePath();
  TemplateLoader.instance().initializeAndExportDefaults(path);  

  AWLog.log("Ancient Warfare Structures Pre-Init completed");
  }

@EventHandler
public void init(FMLInitializationEvent evt)
  {
  AWLog.log("Ancient Warfare Structures Init started"); 
  BlockDataManager.instance().load();

  AWLog.log("Ancient Warfare Structures Init completed");
  }

@EventHandler
public void postInit(FMLPostInitializationEvent evt)
  {
  AWLog.log("Ancient Warfare Structures Post-Init started"); 
  StructurePluginManager.instance().loadPlugins();

  WorldGenStructureManager.instance().loadBiomeList();
  TemplateLoader.instance().loadTemplates();
  config.save();
  AWLog.log("Ancient Warfare Structures Post-Init completed.  Successfully completed all loading stages.");
  }

@SubscribeEvent
public void onLogin(PlayerEvent.PlayerLoggedInEvent evt)
  {
  EntityPlayer player = evt.player;
  if(!player.worldObj.isRemote)
    {
    StructureTemplateManager.instance().onPlayerConnect((EntityPlayerMP) player);
    } 
  }

}
