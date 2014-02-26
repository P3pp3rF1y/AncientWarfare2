package net.shadowmage.ancientwarfare.structure;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.config.Configuration;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.gamedata.GameData;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.network.PacketBase;
import net.shadowmage.ancientwarfare.core.proxy.CommonProxyBase;
import net.shadowmage.ancientwarfare.structure.block.BlockDataManager;
import net.shadowmage.ancientwarfare.structure.config.AWStructureStatics;
import net.shadowmage.ancientwarfare.structure.network.PacketStructure;
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
import cpw.mods.fml.common.registry.GameRegistry;

@Mod
(
name = "Ancient Warfare Structures",
modid = "ancientwarfarestructure",
version = "@VERSION@",
dependencies = "required-after:ancientwarfare"
)

public class AncientWarfareStructures 
{

@Instance(value="ancientwarfarestructure")
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
  config = new Configuration(evt.getSuggestedConfigurationFile());
  log = AncientWarfareCore.log;
  statics = new AWStructureStatics(config);
  statics.load();
  
  FMLCommonHandler.instance().bus().register(this);
      
  String path = evt.getModConfigurationDirectory().getAbsolutePath();
  TemplateLoader.instance().initializeAndExportDefaults(path);  
  BlockDataManager.instance().load();
  PacketBase.registerPacketType(NetworkHandler.PACKET_STRUCTURE, PacketStructure.class);
  
//  GUIHandler.instance().registerContainer(Statics.guiStructureBuilderCreative, ContainerCSB.class);
//  GUIHandler.instance().registerContainer(Statics.guiStructureScannerCreative, ContainerStructureScanner.class);
//  GUIHandler.instance().registerContainer(Statics.guiSpawnerPlacer, ContainerSpawnerPlacer.class);
  GameRegistry.registerWorldGenerator(WorldStructureGenerator.instance(), 0);
  GameData.INSTANCE.registerSaveData("AWStructureMap", StructureMap.class);
  
  proxy.registerClient();    
  }

@EventHandler
public void init(FMLInitializationEvent evt)
  {
  
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
