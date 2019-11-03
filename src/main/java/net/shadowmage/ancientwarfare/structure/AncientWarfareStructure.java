package net.shadowmage.ancientwarfare.structure;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.world.WorldEvent;
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
import net.shadowmage.ancientwarfare.core.registry.RegistryLoader;
import net.shadowmage.ancientwarfare.structure.command.CommandStructure;
import net.shadowmage.ancientwarfare.structure.config.AWStructureStatics;
import net.shadowmage.ancientwarfare.structure.container.ContainerDraftingStation;
import net.shadowmage.ancientwarfare.structure.container.ContainerGateControl;
import net.shadowmage.ancientwarfare.structure.container.ContainerLootBasket;
import net.shadowmage.ancientwarfare.structure.container.ContainerLootChestPlacer;
import net.shadowmage.ancientwarfare.structure.container.ContainerSoundBlock;
import net.shadowmage.ancientwarfare.structure.container.ContainerSpawnerAdvanced;
import net.shadowmage.ancientwarfare.structure.container.ContainerSpawnerAdvancedBlock;
import net.shadowmage.ancientwarfare.structure.container.ContainerSpawnerAdvancedInventoryBlock;
import net.shadowmage.ancientwarfare.structure.container.ContainerSpawnerAdvancedInventoryItem;
import net.shadowmage.ancientwarfare.structure.container.ContainerStake;
import net.shadowmage.ancientwarfare.structure.container.ContainerStatue;
import net.shadowmage.ancientwarfare.structure.container.ContainerStructureScanner;
import net.shadowmage.ancientwarfare.structure.container.ContainerStructureSelection;
import net.shadowmage.ancientwarfare.structure.container.ContainerTownSelection;
import net.shadowmage.ancientwarfare.structure.datafixes.LootSettingsPotionRegistryNameFixer;
import net.shadowmage.ancientwarfare.structure.datafixes.TileLootFixer;
import net.shadowmage.ancientwarfare.structure.entity.EntityGate;
import net.shadowmage.ancientwarfare.structure.entity.EntitySeat;
import net.shadowmage.ancientwarfare.structure.event.OneShotEntityDespawnListener;
import net.shadowmage.ancientwarfare.structure.network.PacketSoundBlockPlayerSpecValues;
import net.shadowmage.ancientwarfare.structure.network.PacketStructure;
import net.shadowmage.ancientwarfare.structure.network.PacketStructureRemove;
import net.shadowmage.ancientwarfare.structure.proxy.CommonProxyStructure;
import net.shadowmage.ancientwarfare.structure.registry.BiomeGroupRegistry;
import net.shadowmage.ancientwarfare.structure.registry.EntitySpawnNBTRegistry;
import net.shadowmage.ancientwarfare.structure.registry.StructureBlockRegistry;
import net.shadowmage.ancientwarfare.structure.template.StructurePluginManager;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateManager;
import net.shadowmage.ancientwarfare.structure.template.WorldGenStructureManager;
import net.shadowmage.ancientwarfare.structure.template.datafixes.DataFixManager;
import net.shadowmage.ancientwarfare.structure.template.datafixes.fixers.BlockMetaToBlockStateFixer;
import net.shadowmage.ancientwarfare.structure.template.datafixes.fixers.EntityEquipmentFixer;
import net.shadowmage.ancientwarfare.structure.template.datafixes.fixers.EntityPositionToNBTFixer;
import net.shadowmage.ancientwarfare.structure.template.datafixes.fixers.EntityRuleNameFixer;
import net.shadowmage.ancientwarfare.structure.template.datafixes.fixers.FactionExpansionFixer;
import net.shadowmage.ancientwarfare.structure.template.datafixes.fixers.RuleNameConsolidationFixer;
import net.shadowmage.ancientwarfare.structure.template.datafixes.fixers.json.JsonSimplificationFixer;
import net.shadowmage.ancientwarfare.structure.template.load.TemplateLoader;
import net.shadowmage.ancientwarfare.structure.town.WorldTownGenerator;
import net.shadowmage.ancientwarfare.structure.util.CapabilityRespawnData;
import net.shadowmage.ancientwarfare.structure.worldgen.WorldGenTickHandler;
import net.shadowmage.ancientwarfare.structure.worldgen.WorldStructureGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(name = "Ancient Warfare Structures", modid = AncientWarfareStructure.MOD_ID, version = "@VERSION@", dependencies = "required-after:ancientwarfare")

public class AncientWarfareStructure {
	public static final String MOD_ID = "ancientwarfarestructure";

	public static final CreativeTabs TAB = new AWStructureTab();

	@Instance(value = MOD_ID)
	public static AncientWarfareStructure instance;

	public static final Logger LOG = LogManager.getLogger(MOD_ID);

	@SidedProxy(clientSide = "net.shadowmage.ancientwarfare.structure.proxy.ClientProxyStructure", serverSide = "net.shadowmage.ancientwarfare.structure.proxy.CommonProxyStructure")
	@SuppressWarnings("squid:S1444")
	public static CommonProxyStructure proxy;

	private AWStructureStatics statics;

	@EventHandler
	public void preInit(FMLPreInitializationEvent evt) {
		statics = new AWStructureStatics("AncientWarfareStructures");

		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(net.shadowmage.ancientwarfare.structure.event.EventHandler.INSTANCE);

		if (AWStructureStatics.enableWorldGen) {
			MinecraftForge.EVENT_BUS.register(WorldGenTickHandler.INSTANCE);
			if (AWStructureStatics.enableStructureGeneration)
				GameRegistry.registerWorldGenerator(WorldStructureGenerator.INSTANCE, 1);
			if (AWStructureStatics.enableTownGeneration)
				GameRegistry.registerWorldGenerator(WorldTownGenerator.INSTANCE, 2);
		}
		EntityRegistry.registerModEntity(new ResourceLocation(AncientWarfareStructure.MOD_ID, "aw_gate"), EntityGate.class, "aw_gate", 0, this, 250, 200, false);
		EntityRegistry.registerModEntity(new ResourceLocation(AncientWarfareStructure.MOD_ID, "seat"), EntitySeat.class, "AWSeat", 1, this, 20, 10, false);

		PacketBase.registerPacketType(NetworkHandler.PACKET_STRUCTURE, PacketStructure.class, PacketStructure::new);
		PacketBase.registerPacketType(NetworkHandler.PACKET_STRUCTURE_REMOVE, PacketStructureRemove.class, PacketStructureRemove::new);
		PacketBase.registerPacketType(NetworkHandler.PACKET_SOUND_BLOCK_PLAYER_SPEC_VALUES, PacketSoundBlockPlayerSpecValues.class, PacketSoundBlockPlayerSpecValues::new);
		NetworkHandler.registerContainer(NetworkHandler.GUI_SCANNER, ContainerStructureScanner.class);
		NetworkHandler.registerContainer(NetworkHandler.GUI_BUILDER, ContainerStructureSelection.class);
		NetworkHandler.registerContainer(NetworkHandler.GUI_TOWN_BUILDER, ContainerTownSelection.class);
		NetworkHandler.registerContainer(NetworkHandler.GUI_SPAWNER_ADVANCED, ContainerSpawnerAdvanced.class);
		NetworkHandler.registerContainer(NetworkHandler.GUI_SPAWNER_ADVANCED_BLOCK, ContainerSpawnerAdvancedBlock.class);
		NetworkHandler.registerContainer(NetworkHandler.GUI_SPAWNER_ADVANCED_INVENTORY, ContainerSpawnerAdvancedInventoryItem.class);
		NetworkHandler.registerContainer(NetworkHandler.GUI_SPAWNER_ADVANCED_BLOCK_INVENTORY, ContainerSpawnerAdvancedInventoryBlock.class);
		NetworkHandler.registerContainer(NetworkHandler.GUI_GATE_CONTROL, ContainerGateControl.class);
		NetworkHandler.registerContainer(NetworkHandler.GUI_GATE_CONTROL_CREATIVE, ContainerGateControl.class);
		NetworkHandler.registerContainer(NetworkHandler.GUI_DRAFTING_STATION, ContainerDraftingStation.class);
		NetworkHandler.registerContainer(NetworkHandler.GUI_SOUND_BLOCK, ContainerSoundBlock.class);
		NetworkHandler.registerContainer(NetworkHandler.GUI_LOOT_CHEST_PLACER, ContainerLootChestPlacer.class);
		NetworkHandler.registerContainer(NetworkHandler.GUI_LOOT_BASKET, ContainerLootBasket.class);
		NetworkHandler.registerContainer(NetworkHandler.GUI_STAKE, ContainerStake.class);
		NetworkHandler.registerContainer(NetworkHandler.GUI_STATUE, ContainerStatue.class);

		proxy.preInit();

		TemplateLoader.INSTANCE.initializeAndExportDefaults();

		RegistryLoader.registerParser(new EntitySpawnNBTRegistry.Parser());
		RegistryLoader.registerParser(new BiomeGroupRegistry.Parser());
		RegistryLoader.registerParser(new StructureBlockRegistry.Parser());

		CapabilityRespawnData.register();
	}

	@EventHandler
	public void init(FMLInitializationEvent evt) {
		proxy.init();

		DataFixManager.registerRuleFixer(new FactionExpansionFixer());
		DataFixManager.registerRuleFixer(new JsonSimplificationFixer());
		DataFixManager.registerRuleFixer(new BlockMetaToBlockStateFixer());
		DataFixManager.registerRuleFixer(new EntityPositionToNBTFixer());
		DataFixManager.registerRuleFixer(new RuleNameConsolidationFixer());
		DataFixManager.registerRuleFixer(new EntityRuleNameFixer());
		DataFixManager.registerRuleFixer(new EntityEquipmentFixer());
		DataFixManager.registerRuleFixer(new TileLootFixer());
		DataFixManager.registerRuleFixer(new LootSettingsPotionRegistryNameFixer());
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
			StructureTemplateManager.onPlayerConnect((EntityPlayerMP) evt.player);
		}
	}

	@SubscribeEvent
	public void onEntityCapabilityAttach(AttachCapabilitiesEvent<Entity> event) {
		CapabilityRespawnData.onAttach(event);
	}

	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load event) {
		event.getWorld().addEventListener(OneShotEntityDespawnListener.INSTANCE);
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
