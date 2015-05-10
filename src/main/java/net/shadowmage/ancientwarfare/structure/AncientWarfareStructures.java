package net.shadowmage.ancientwarfare.structure;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.config.Configuration;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.api.ModuleStatus;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.network.PacketBase;
import net.shadowmage.ancientwarfare.core.proxy.CommonProxyBase;
import net.shadowmage.ancientwarfare.structure.block.AWStructuresBlockLoader;
import net.shadowmage.ancientwarfare.structure.block.BlockDataManager;
import net.shadowmage.ancientwarfare.structure.command.CommandStructure;
import net.shadowmage.ancientwarfare.structure.config.AWStructureStatics;
import net.shadowmage.ancientwarfare.structure.container.*;
import net.shadowmage.ancientwarfare.structure.crafting.AWStructureCrafting;
import net.shadowmage.ancientwarfare.structure.entity.EntityGate;
import net.shadowmage.ancientwarfare.structure.gamedata.StructureMap;
import net.shadowmage.ancientwarfare.structure.item.AWStructuresItemLoader;
import net.shadowmage.ancientwarfare.structure.network.PacketStructure;
import net.shadowmage.ancientwarfare.structure.network.PacketStructureRemove;
import net.shadowmage.ancientwarfare.structure.template.StructurePluginManager;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateManager;
import net.shadowmage.ancientwarfare.structure.template.WorldGenStructureManager;
import net.shadowmage.ancientwarfare.structure.template.load.TemplateLoader;
import net.shadowmage.ancientwarfare.structure.town.WorldTownGenerator;
import net.shadowmage.ancientwarfare.structure.world_gen.WorldGenTickHandler;
import net.shadowmage.ancientwarfare.structure.world_gen.WorldStructureGenerator;

@Mod
        (
                name = "Ancient Warfare Structures",
                modid = "AncientWarfareStructure",
                version = "@VERSION@",
                dependencies = "required-after:AncientWarfare"
        )

public class AncientWarfareStructures {

    @Instance(value = "AncientWarfareStructure")
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
    public void preInit(FMLPreInitializationEvent evt) {
        ModuleStatus.structuresLoaded = true;
        log = AncientWarfareCore.log;
        config = AWCoreStatics.getConfigFor("AncientWarfareStructures");
        statics = new AWStructureStatics(config);

        /**
         * Forge/FML registry
         */
        FMLCommonHandler.instance().bus().register(this);
        FMLCommonHandler.instance().bus().register(WorldGenTickHandler.INSTANCE);
        if (AWStructureStatics.enableStructureGeneration)
            GameRegistry.registerWorldGenerator(WorldStructureGenerator.INSTANCE, 1);
        if (AWStructureStatics.enableTownGeneration)
            GameRegistry.registerWorldGenerator(WorldTownGenerator.INSTANCE, 2);
        EntityRegistry.registerModEntity(EntityGate.class, "aw_gate", 0, this, 250, 200, false);
        /**
         * internal registry
         */
        PacketBase.registerPacketType(NetworkHandler.PACKET_STRUCTURE, PacketStructure.class);
        PacketBase.registerPacketType(NetworkHandler.PACKET_STRUCTURE_REMOVE, PacketStructureRemove.class);
        NetworkHandler.registerContainer(NetworkHandler.GUI_SCANNER, ContainerStructureScanner.class);
        NetworkHandler.registerContainer(NetworkHandler.GUI_BUILDER, ContainerStructureSelection.class);
        NetworkHandler.registerContainer(NetworkHandler.GUI_SPAWNER, ContainerSpawnerPlacer.class);
        NetworkHandler.registerContainer(NetworkHandler.GUI_SPAWNER_ADVANCED, ContainerSpawnerAdvanced.class);
        NetworkHandler.registerContainer(NetworkHandler.GUI_SPAWNER_ADVANCED_BLOCK, ContainerSpawnerAdvancedBlock.class);
        NetworkHandler.registerContainer(NetworkHandler.GUI_SPAWNER_ADVANCED_INVENTORY, ContainerSpawnerAdvancedInventoryItem.class);
        NetworkHandler.registerContainer(NetworkHandler.GUI_SPAWNER_ADVANCED_BLOCK_INVENTORY, ContainerSpawnerAdvancedInventoryBlock.class);
        NetworkHandler.registerContainer(NetworkHandler.GUI_GATE_CONTROL, ContainerGateControl.class);
        NetworkHandler.registerContainer(NetworkHandler.GUI_DRAFTING_STATION, ContainerDraftingStation.class);
        NetworkHandler.registerContainer(NetworkHandler.GUI_SOUND_BLOCK, ContainerSoundBlock.class);
        proxy.registerClient();

        /**
         * load pre-init
         */
        statics.load();
        AWStructuresItemLoader.load();
        AWStructuresBlockLoader.load();
        String path = evt.getModConfigurationDirectory().getAbsolutePath();
        TemplateLoader.INSTANCE.initializeAndExportDefaults(path);
    }

    @EventHandler
    public void init(FMLInitializationEvent evt) {
        BlockDataManager.INSTANCE.load();
        AWStructureCrafting.loadRecipes();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent evt) {
        statics.loadPostInitValues();//needs to be called prior to worldgen biome loading, as biome aliases are loaded in this stage
        StructurePluginManager.INSTANCE.loadPlugins();
        WorldGenStructureManager.INSTANCE.loadBiomeList();
        TemplateLoader.INSTANCE.loadTemplates();
        if (config.hasChanged())
            config.save();
    }

    @SubscribeEvent
    public void onLogin(PlayerEvent.PlayerLoggedInEvent evt) {
        if (!evt.player.worldObj.isRemote) {
            StructureTemplateManager.INSTANCE.onPlayerConnect((EntityPlayerMP) evt.player);
        }
    }

    @EventHandler
    public void serverStart(FMLServerStartingEvent evt) {
        evt.registerServerCommand(new CommandStructure());
    }

}
