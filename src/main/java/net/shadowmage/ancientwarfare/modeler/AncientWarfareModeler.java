package net.shadowmage.ancientwarfare.modeler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.core.item.ItemClickable;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.network.PacketBase;
import net.shadowmage.ancientwarfare.core.proxy.CommonProxyBase;
import net.shadowmage.ancientwarfare.structure.block.BlockDataManager;
import net.shadowmage.ancientwarfare.structure.config.AWStructureStatics;
import net.shadowmage.ancientwarfare.structure.container.ContainerStructureScanner;
import net.shadowmage.ancientwarfare.structure.item.AWStructuresItemLoader;
import net.shadowmage.ancientwarfare.structure.item.ItemStructureScanner;
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
name = "Ancient Warfare Model Editor",
modid = "ancientwarfaremodeler",
version = "@VERSION@",
dependencies = "required-after:ancientwarfare"
)

public class AncientWarfareModeler 
{

@Instance(value="ancientwarfaremodeler")
public static AncientWarfareModeler instance;

@SidedProxy
(
clientSide = "net.shadowmage.ancientwarfare.modeler.proxy.ClientProxyModeler",
serverSide = "net.shadowmage.ancientwarfare.core.proxy.CommonProxy"
)
public static CommonProxyBase proxy;

public static Configuration config;

public static org.apache.logging.log4j.Logger log;


@EventHandler
public void preInit(FMLPreInitializationEvent evt)
  {
  AWLog.log("Ancient Warfare Modeler Pre-Init started"); 
  log = AncientWarfareCore.log;
  config = new Configuration(evt.getSuggestedConfigurationFile());
  
  /**
   * internal registry
   */
  proxy.registerClient();   
      
  /**
   * load pre-init
   */
  AWLog.log("Ancient Warfare Modeler Pre-Init completed.");
  }

@EventHandler
public void init(FMLInitializationEvent evt)
  {
  AWLog.log("Ancient Warfare Modeler Init started"); 

  AWLog.log("Ancient Warfare Modeler Init completed.");
  }

@EventHandler
public void postInit(FMLPostInitializationEvent evt)
  {
  AWLog.log("Ancient Warfare Modeler Post-Init started"); 

  config.save();
  AWLog.log("Ancient Warfare Modeler Post-Init completed.  Successfully completed all loading stages.");
  }

}
