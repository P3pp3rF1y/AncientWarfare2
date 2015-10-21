package net.shadowmage.ancientwarfare.npc;

import cpw.mods.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
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
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.api.ModuleStatus;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.core.gamedata.WorldData;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.network.PacketBase;
import net.shadowmage.ancientwarfare.npc.block.AWNPCBlockLoader;
import net.shadowmage.ancientwarfare.npc.command.CommandDebugAI;
import net.shadowmage.ancientwarfare.npc.command.CommandFaction;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;
import net.shadowmage.ancientwarfare.npc.container.*;
import net.shadowmage.ancientwarfare.npc.crafting.AWNpcCrafting;
import net.shadowmage.ancientwarfare.npc.entity.AWNPCEntityLoader;
import net.shadowmage.ancientwarfare.npc.faction.FactionTracker;
import net.shadowmage.ancientwarfare.npc.item.AWNpcItemLoader;
import net.shadowmage.ancientwarfare.npc.network.PacketFactionUpdate;
import net.shadowmage.ancientwarfare.npc.network.PacketNpcCommand;
import net.shadowmage.ancientwarfare.npc.proxy.NpcCommonProxy;

@Mod
        (
                name = "Ancient Warfare NPCs",
                modid = "AncientWarfareNpc",
                version = "@VERSION@",
                dependencies = "required-after:AncientWarfare"
        )

public class AncientWarfareNPC {

    @Instance(value = "AncientWarfareNpc")
    public static AncientWarfareNPC instance;

    @SidedProxy
            (
                    clientSide = "net.shadowmage.ancientwarfare.npc.proxy.NpcClientProxy",
                    serverSide = "net.shadowmage.ancientwarfare.npc.proxy.NpcCommonProxy"
            )
    public static NpcCommonProxy proxy;

    public static AWNPCStatics statics;

    @EventHandler
    public void preInit(FMLPreInitializationEvent evt) {
        ModuleStatus.npcsLoaded = true;

        /**
         * setup module-owned config file and config-access class
         */
        statics = new AWNPCStatics("AncientWarfareNpc");
        proxy.registerClient();//must be loaded after configs
        FMLCommonHandler.instance().bus().register(FactionTracker.INSTANCE);
        FMLCommonHandler.instance().bus().register(this);
        MinecraftForge.EVENT_BUS.register(net.shadowmage.ancientwarfare.npc.event.EventHandler.INSTANCE);

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
    }

    @EventHandler
    public void init(FMLInitializationEvent evt) {

        /**
         * construct recipes, load plugins
         */
        AWNpcCrafting.loadRecipes();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent evt) {
        /**
         * save config for any changes that were made during loading stages
         */
        statics.postInitCallback();
        proxy.loadSkins();
        AWNPCEntityLoader.loadNpcSubtypeEquipment();
        statics.save();
    }

    @SubscribeEvent
    public void onConfigChanged(OnConfigChangedEvent evt) {
        if (AncientWarfareCore.modID.equals(evt.modID)) {
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
        if (!evt.world.isRemote) {
            WorldData d = AWGameData.INSTANCE.getPerWorldData(evt.world, WorldData.class);
            if (d != null) {
                AWNPCStatics.npcAIDebugMode = d.get("NpcAIDebugMode");
            }
        }
    }

}
