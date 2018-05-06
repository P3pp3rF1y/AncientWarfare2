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
import net.shadowmage.ancientwarfare.core.api.ModuleStatus;
import net.shadowmage.ancientwarfare.core.compat.CompatLoader;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.core.gamedata.WorldData;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.network.PacketBase;
import net.shadowmage.ancientwarfare.npc.command.CommandDebugAI;
import net.shadowmage.ancientwarfare.npc.command.CommandFaction;
import net.shadowmage.ancientwarfare.npc.compat.EpicSiegeCompat;
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
import net.shadowmage.ancientwarfare.npc.entity.AWNPCEntityLoader;
import net.shadowmage.ancientwarfare.npc.faction.FactionTracker;
import net.shadowmage.ancientwarfare.npc.network.PacketFactionUpdate;
import net.shadowmage.ancientwarfare.npc.network.PacketNpcCommand;
import net.shadowmage.ancientwarfare.npc.proxy.NpcCommonProxy;

@Mod(name = "Ancient Warfare NPCs", modid = AncientWarfareNPC.modID, version = "@VERSION@", dependencies = "required-after:ancientwarfare")

public class AncientWarfareNPC {
	public static final String modID = "ancientwarfarenpc";

	@Instance(value = modID)
	public static AncientWarfareNPC instance;

	@SidedProxy(clientSide = "net.shadowmage.ancientwarfare.npc.proxy.NpcClientProxy", serverSide = "net.shadowmage.ancientwarfare.npc.proxy.NpcCommonProxy")
	public static NpcCommonProxy proxy;

	public static AWNPCStatics statics;

	@EventHandler
	public void preInit(FMLPreInitializationEvent evt) {
		ModuleStatus.npcsLoaded = true;

        /*
		 * setup module-owned config file and config-access class
         */
		statics = new AWNPCStatics("AncientWarfareNpc");
		proxy.preInit();//must be loaded after configs
		MinecraftForge.EVENT_BUS.register(FactionTracker.INSTANCE);
		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(net.shadowmage.ancientwarfare.npc.event.EventHandler.INSTANCE);

        /*
		 * register containers
         */
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
		PacketBase.registerPacketType(NetworkHandler.PACKET_NPC_COMMAND, PacketNpcCommand.class);
		PacketBase.registerPacketType(NetworkHandler.PACKET_FACTION_UPDATE, PacketFactionUpdate.class);

		CompatLoader.registerCompat(new EpicSiegeCompat());
	}

	@EventHandler
	public void init(FMLInitializationEvent evt) {
		proxy.init();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent evt) {
		/*
		 * save config for any changes that were made during loading stages
         */
		statics.postInitCallback();
		proxy.loadSkins();
		AWNPCEntityLoader.loadNpcSubtypeEquipment();
		statics.save();
	}

	@SubscribeEvent
	public void onConfigChanged(OnConfigChangedEvent evt) {
		if (AncientWarfareCore.modID.equals(evt.getModID())) {
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
