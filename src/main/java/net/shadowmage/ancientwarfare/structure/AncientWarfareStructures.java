package net.shadowmage.ancientwarfare.structure;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.network.PacketBase;
import net.shadowmage.ancientwarfare.core.proxy.CommonProxyBase;
import net.shadowmage.ancientwarfare.structure.block.BlockDataManager;
import net.shadowmage.ancientwarfare.structure.command.CommandStructure;
import net.shadowmage.ancientwarfare.structure.config.AWStructureStatics;
import net.shadowmage.ancientwarfare.structure.container.ContainerDraftingStation;
import net.shadowmage.ancientwarfare.structure.container.ContainerGateControl;
import net.shadowmage.ancientwarfare.structure.container.ContainerLootChestPlacer;
import net.shadowmage.ancientwarfare.structure.container.ContainerSoundBlock;
import net.shadowmage.ancientwarfare.structure.container.ContainerSpawnerAdvanced;
import net.shadowmage.ancientwarfare.structure.container.ContainerSpawnerAdvancedBlock;
import net.shadowmage.ancientwarfare.structure.container.ContainerSpawnerAdvancedInventoryBlock;
import net.shadowmage.ancientwarfare.structure.container.ContainerSpawnerAdvancedInventoryItem;
import net.shadowmage.ancientwarfare.structure.container.ContainerStructureScanner;
import net.shadowmage.ancientwarfare.structure.container.ContainerStructureSelection;
import net.shadowmage.ancientwarfare.structure.container.ContainerTownSelection;
import net.shadowmage.ancientwarfare.structure.entity.EntityGate;
import net.shadowmage.ancientwarfare.structure.network.PacketStructure;
import net.shadowmage.ancientwarfare.structure.network.PacketStructureRemove;
import net.shadowmage.ancientwarfare.structure.template.StructurePluginManager;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateManager;
import net.shadowmage.ancientwarfare.structure.template.WorldGenStructureManager;
import net.shadowmage.ancientwarfare.structure.template.load.TemplateLoader;
import net.shadowmage.ancientwarfare.structure.town.WorldTownGenerator;
import net.shadowmage.ancientwarfare.structure.world_gen.WorldGenTickHandler;
import net.shadowmage.ancientwarfare.structure.world_gen.WorldStructureGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(name = "Ancient Warfare Structures", modid = AncientWarfareStructures.MOD_ID, version = "@VERSION@", dependencies = "required-after:ancientwarfare")

public class AncientWarfareStructures {
	public static final String MOD_ID = "ancientwarfarestructure";
	public static final String MOD_PREFIX = MOD_ID + ":";

	@Instance(value = MOD_ID)
	public static AncientWarfareStructures instance;

	public static final Logger LOG = LogManager.getLogger(MOD_ID);

	@SidedProxy(clientSide = "net.shadowmage.ancientwarfare.structure.proxy.ClientProxyStructures", serverSide = "net.shadowmage.ancientwarfare.core.proxy.CommonProxy")
	@SuppressWarnings("squid:S1444")
	public static CommonProxyBase proxy;

	private AWStructureStatics statics;

	@EventHandler
	public void preInit(FMLPreInitializationEvent evt) {
		statics = new AWStructureStatics("AncientWarfareStructures");

		/*
		 * Forge/FML registry
         */
		MinecraftForge.EVENT_BUS.register(this);
		if (AWStructureStatics.enableWorldGen) {
			MinecraftForge.EVENT_BUS.register(WorldGenTickHandler.INSTANCE);
			if (AWStructureStatics.enableStructureGeneration)
				GameRegistry.registerWorldGenerator(WorldStructureGenerator.INSTANCE, 1);
			if (AWStructureStatics.enableTownGeneration)
				GameRegistry.registerWorldGenerator(WorldTownGenerator.INSTANCE, 2);
		}
		EntityRegistry.registerModEntity(new ResourceLocation(AncientWarfareStructures.MOD_ID, "aw_gate"), EntityGate.class, "aw_gate", 0, this, 250, 200, false);
		/*
		 * internal registry
         */
		PacketBase.registerPacketType(NetworkHandler.PACKET_STRUCTURE, PacketStructure.class);
		PacketBase.registerPacketType(NetworkHandler.PACKET_STRUCTURE_REMOVE, PacketStructureRemove.class);
		NetworkHandler.registerContainer(NetworkHandler.GUI_SCANNER, ContainerStructureScanner.class);
		NetworkHandler.registerContainer(NetworkHandler.GUI_BUILDER, ContainerStructureSelection.class);
		NetworkHandler.registerContainer(NetworkHandler.GUI_TOWN_BUILDER, ContainerTownSelection.class);
		NetworkHandler.registerContainer(NetworkHandler.GUI_SPAWNER_ADVANCED, ContainerSpawnerAdvanced.class);
		NetworkHandler.registerContainer(NetworkHandler.GUI_SPAWNER_ADVANCED_BLOCK, ContainerSpawnerAdvancedBlock.class);
		NetworkHandler.registerContainer(NetworkHandler.GUI_SPAWNER_ADVANCED_INVENTORY, ContainerSpawnerAdvancedInventoryItem.class);
		NetworkHandler.registerContainer(NetworkHandler.GUI_SPAWNER_ADVANCED_BLOCK_INVENTORY, ContainerSpawnerAdvancedInventoryBlock.class);
		NetworkHandler.registerContainer(NetworkHandler.GUI_GATE_CONTROL, ContainerGateControl.class);
		NetworkHandler.registerContainer(NetworkHandler.GUI_DRAFTING_STATION, ContainerDraftingStation.class);
		NetworkHandler.registerContainer(NetworkHandler.GUI_SOUND_BLOCK, ContainerSoundBlock.class);
		NetworkHandler.registerContainer(NetworkHandler.GUI_LOOT_CHEST_PLACER, ContainerLootChestPlacer.class);
		proxy.preInit();

		TemplateLoader.INSTANCE.initializeAndExportDefaults();
	}

	@EventHandler
	public void init(FMLInitializationEvent evt) {
		proxy.init();

		BlockDataManager.INSTANCE.load();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent evt) {
		StructurePluginManager.INSTANCE.loadPlugins();
		WorldGenStructureManager.INSTANCE.loadBiomeList();
		TemplateLoader.INSTANCE.loadTemplates();
		statics.save();
		AWStructureStatics.logSkippableBlocksCoveredByMaterial();
	}

	@SubscribeEvent
	public void onLogin(PlayerEvent.PlayerLoggedInEvent evt) {
		if (!evt.player.world.isRemote) {
			StructureTemplateManager.INSTANCE.onPlayerConnect((EntityPlayerMP) evt.player);
		}
	}

	@EventHandler
	public void serverStart(FMLServerStartingEvent evt) {
		evt.registerServerCommand(new CommandStructure());
	}

	@EventHandler
	public void serverStop(FMLServerStoppingEvent evt) {
		if (AWStructureStatics.enableWorldGen)
			WorldGenTickHandler.INSTANCE.finalTick();
	}

}
