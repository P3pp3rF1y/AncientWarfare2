package net.shadowmage.ancientwarfare.npc;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.compat.CompatLoader;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.core.gamedata.WorldData;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.network.PacketBase;
import net.shadowmage.ancientwarfare.core.registry.RegistryLoader;
import net.shadowmage.ancientwarfare.npc.command.CommandDebugAI;
import net.shadowmage.ancientwarfare.npc.command.CommandFaction;
import net.shadowmage.ancientwarfare.npc.compat.EpicSiegeCompat;
import net.shadowmage.ancientwarfare.npc.compat.TwilightForestCompat;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;
import net.shadowmage.ancientwarfare.npc.container.ContainerCombatOrder;
import net.shadowmage.ancientwarfare.npc.container.ContainerNpcBard;
import net.shadowmage.ancientwarfare.npc.container.ContainerNpcCreativeControls;
import net.shadowmage.ancientwarfare.npc.container.ContainerNpcFactionBard;
import net.shadowmage.ancientwarfare.npc.container.ContainerNpcFactionTradeSetup;
import net.shadowmage.ancientwarfare.npc.container.ContainerNpcFactionTradeView;
import net.shadowmage.ancientwarfare.npc.container.ContainerNpcInventory;
import net.shadowmage.ancientwarfare.npc.container.ContainerNpcPlayerOwnedTrade;
import net.shadowmage.ancientwarfare.npc.container.ContainerRoutingOrder;
import net.shadowmage.ancientwarfare.npc.container.ContainerTownHall;
import net.shadowmage.ancientwarfare.npc.container.ContainerTradeOrder;
import net.shadowmage.ancientwarfare.npc.container.ContainerUpkeepOrder;
import net.shadowmage.ancientwarfare.npc.container.ContainerWorkOrder;
import net.shadowmage.ancientwarfare.npc.faction.FactionTracker;
import net.shadowmage.ancientwarfare.npc.init.AWNPCEntities;
import net.shadowmage.ancientwarfare.npc.init.AWNPCItems;
import net.shadowmage.ancientwarfare.npc.network.PacketExtendedReachAttack;
import net.shadowmage.ancientwarfare.npc.network.PacketFactionUpdate;
import net.shadowmage.ancientwarfare.npc.network.PacketNpcCommand;
import net.shadowmage.ancientwarfare.npc.proxy.NpcCommonProxy;
import net.shadowmage.ancientwarfare.npc.registry.FactionRegistry;
import net.shadowmage.ancientwarfare.npc.registry.FactionTradeListRegistry;
import net.shadowmage.ancientwarfare.npc.registry.NpcDefaultsRegistry;
import net.shadowmage.ancientwarfare.npc.registry.TargetRegistry;
import net.shadowmage.ancientwarfare.structure.network.PacketStructureEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(name = "Ancient Warfare NPCs", modid = AncientWarfareNPC.MOD_ID, version = "@VERSION@", dependencies = "required-after:ancientwarfare")

public class AncientWarfareNPC {
	public static final String MOD_ID = "ancientwarfarenpc";
	public static final String MOD_PREFIX = MOD_ID + ":";

	public static final AWNPCTab TAB = new AWNPCTab();

	@Instance(value = MOD_ID)
	public static AncientWarfareNPC instance;

	@SidedProxy(clientSide = "net.shadowmage.ancientwarfare.npc.proxy.NpcClientProxy", serverSide = "net.shadowmage.ancientwarfare.npc.proxy.NpcCommonProxy")
	public static NpcCommonProxy proxy;

	public static final Logger LOG = LogManager.getLogger(MOD_ID);
	public static AWNPCStatics statics;

	@EventHandler
	public void preInit(FMLPreInitializationEvent evt) {
		statics = new AWNPCStatics("AncientWarfareNpc");

		proxy.preInit();

		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(net.shadowmage.ancientwarfare.npc.event.EventHandler.INSTANCE);

		NetworkHandler.registerContainer(NetworkHandler.GUI_NPC_INVENTORY, ContainerNpcInventory.class);
		NetworkHandler.registerContainer(NetworkHandler.GUI_NPC_WORK_ORDER, ContainerWorkOrder.class);
		NetworkHandler.registerContainer(NetworkHandler.GUI_NPC_UPKEEP_ORDER, ContainerUpkeepOrder.class);
		NetworkHandler.registerContainer(NetworkHandler.GUI_NPC_COMBAT_ORDER, ContainerCombatOrder.class);
		NetworkHandler.registerContainer(NetworkHandler.GUI_NPC_FACTION_TRADE_SETUP, ContainerNpcFactionTradeSetup.class);
		NetworkHandler.registerContainer(NetworkHandler.GUI_NPC_FACTION_TRADE_VIEW, ContainerNpcFactionTradeView.class);
		NetworkHandler.registerContainer(NetworkHandler.GUI_NPC_ROUTING_ORDER, ContainerRoutingOrder.class);
		NetworkHandler.registerContainer(NetworkHandler.GUI_NPC_TOWN_HALL, ContainerTownHall.class);
		NetworkHandler.registerContainer(NetworkHandler.GUI_NPC_BARD, ContainerNpcBard.class);
		NetworkHandler.registerContainer(NetworkHandler.GUI_NPC_CREATIVE, ContainerNpcCreativeControls.class);
		NetworkHandler.registerContainer(NetworkHandler.GUI_NPC_TRADE_ORDER, ContainerTradeOrder.class);
		NetworkHandler.registerContainer(NetworkHandler.GUI_NPC_PLAYER_OWNED_TRADE, ContainerNpcPlayerOwnedTrade.class);
		NetworkHandler.registerContainer(NetworkHandler.GUI_NPC_FACTION_BARD, ContainerNpcFactionBard.class);
		PacketBase.registerPacketType(NetworkHandler.PACKET_NPC_COMMAND, PacketNpcCommand.class, PacketNpcCommand::new);
		PacketBase.registerPacketType(NetworkHandler.PACKET_FACTION_UPDATE, PacketFactionUpdate.class, PacketFactionUpdate::new);
		PacketBase.registerPacketType(NetworkHandler.PACKET_EXTENDED_REACH_ATTACK, PacketExtendedReachAttack.class, PacketExtendedReachAttack::new);
		PacketBase.registerPacketType(NetworkHandler.PACKET_STRUCTURE_ENTRY, PacketStructureEntry.class, PacketStructureEntry::new);

		CompatLoader.registerCompat(new EpicSiegeCompat());
		CompatLoader.registerCompat(new TwilightForestCompat());

		RegistryLoader.registerParser(new FactionRegistry.FactionParser());
		RegistryLoader.registerParser(new TargetRegistry.TargetListParser());
		RegistryLoader.registerParser(new NpcDefaultsRegistry.FactionNpcDefaultsParser());
		RegistryLoader.registerParser(new NpcDefaultsRegistry.OwnedNpcDefaultsParser());
		RegistryLoader.registerParser(new FactionTradeListRegistry.Parser());
	}

	@EventHandler
	public void init(FMLInitializationEvent evt) {
		proxy.init();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent evt) {
		proxy.loadSkins();
		AWNPCEntities.loadNpcSubtypeEquipment();
		MinecraftForge.EVENT_BUS.register(FactionTracker.INSTANCE);

		statics.save();

		AWNPCItems.addFactionBlocks();
	}

	@SubscribeEvent
	public void onConfigChanged(OnConfigChangedEvent evt) {
		if (AncientWarfareCore.MOD_ID.equals(evt.getModID())) {
			statics.save();
		}
	}

	@EventHandler
	public void serverStart(FMLServerStartingEvent evt) {
		evt.registerServerCommand(new CommandFaction());
		evt.registerServerCommand(new CommandDebugAI());
	}

	@SubscribeEvent
	public void worldLoaded(WorldEvent.Load evt) {
		if (!evt.getWorld().isRemote) {
			WorldData d = AWGameData.INSTANCE.getPerWorldData(evt.getWorld(), WorldData.class);
			if (d != null) {
				AWNPCStatics.npcAIDebugMode = d.get("NpcAIDebugMode");
			}
		}
	}

}
