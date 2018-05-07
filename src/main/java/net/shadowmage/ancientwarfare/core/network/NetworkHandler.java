package net.shadowmage.ancientwarfare.core.network;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.compat.jei.PacketTransferRecipe;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;

import java.util.HashMap;

public final class NetworkHandler implements IGuiHandler {

	//TODO split this into separate GUI handlers for separate mods. Also rename to something that says it's guihandler

	public static final String CHANNELNAME = "AWCORE";
	public static final NetworkHandler INSTANCE = new NetworkHandler();

	//public static final int PACKET_TEST = 0;//unused id
	public static final int PACKET_GUI = 1;
	public static final int PACKET_STRUCTURE = 2;
	public static final int PACKET_ITEM_KEY_INTERFACE = 3;
	public static final int PACKET_ENTITY = 5;
	public static final int PACKET_RESEARCH_INIT = 6;
	public static final int PACKET_RESEARCH_ADD = 7;
	public static final int PACKET_RESEARCH_START = 8;
	//public static final int PACKET_STRUCTURE_IMAGE_LIST = 9;//unused
	//public static final int PACKET_STRUCTURE_IMAGE_DATA = 10;//unused
	public static final int PACKET_STRUCTURE_REMOVE = 11;
	public static final int PACKET_NPC_COMMAND = 12;
	public static final int PACKET_FACTION_UPDATE = 13;
	public static final int PACKET_BLOCK_EVENT = 14;

	public static final int PACKET_AIM_UPDATE = 15;
	public static final int PACKET_AMMO_SELECT = 16;
	public static final int PACKET_AMMO_UPDATE = 17;
	public static final int PACKET_FIRE_UPDATE = 18;
	public static final int PACKET_PACK_COMMAND = 19;
	public static final int PACKET_SINGLE_AMMO_UPDATE = 20;
	public static final int PACKET_TURRET_ANGLES_UPDATE = 21;
	public static final int PACKET_UPGRADE_UPDATE = 22;
	public static final int PACKET_VEHICLE_INPUT = 23;
	public static final int PACKET_VEHICLE_MOVE = 24;

	public static final int PACKET_JEI_TRANSFER_RECIPE = 25;

	public static final int GUI_CRAFTING = 0;
	public static final int GUI_SCANNER = 1;
	public static final int GUI_BUILDER = 2;
	public static final int GUI_SPAWNER = 3;
	public static final int GUI_NPC_INVENTORY = 4;
	public static final int GUI_WORKSITE_INVENTORY_SIDE_ADJUST = 5;
	public static final int GUI_NPC_TRADE_ORDER = 6;
	public static final int GUI_SPAWNER_ADVANCED = 7;
	public static final int GUI_SPAWNER_ADVANCED_BLOCK = 8;
	public static final int GUI_SPAWNER_ADVANCED_INVENTORY = 9;
	public static final int GUI_SPAWNER_ADVANCED_BLOCK_INVENTORY = 10;
	public static final int GUI_GATE_CONTROL = 11;
	public static final int GUI_RESEARCH_STATION = 12;
	public static final int GUI_DRAFTING_STATION = 13;
	public static final int GUI_WORKSITE_ANIMAL_CONTROL = 14;
	public static final int GUI_WORKSITE_AUTO_CRAFT = 15;
	public static final int GUI_WORKSITE_FISH_CONTROL = 16;
	public static final int GUI_MAILBOX_INVENTORY = 17;
	public static final int GUI_WAREHOUSE_CONTROL = 18;
	public static final int GUI_WAREHOUSE_STORAGE = 19;
	public static final int GUI_WAREHOUSE_STOCK = 20;
	public static final int GUI_WAREHOUSE_OUTPUT = 21;
	public static final int GUI_WAREHOUSE_CRAFTING = 22;
	public static final int GUI_CHUNK_LOADER_DELUXE = 23;
	public static final int GUI_WORKSITE_QUARRY = 24;
	public static final int GUI_WORKSITE_TREE_FARM = 25;
	public static final int GUI_WORKSITE_ANIMAL_FARM = 26;
	public static final int GUI_WORKSITE_CROP_FARM = 27;
	public static final int GUI_WORKSITE_MUSHROOM_FARM = 28;
	public static final int GUI_WORKSITE_FISH_FARM = 29;
	public static final int GUI_WORKSITE_REED_FARM = 30;
	public static final int GUI_STIRLING_GENERATOR = 31;
	public static final int GUI_TORQUE_GENERATOR_WATERWHEEL = 32;
	public static final int GUI_TORQUE_STORAGE_FLYWHEEL = 33;
	public static final int GUI_NPC_WORK_ORDER = 34;
	public static final int GUI_NPC_UPKEEP_ORDER = 35;
	public static final int GUI_NPC_COMBAT_ORDER = 36;
	public static final int GUI_NPC_ROUTING_ORDER = 37;
	public static final int GUI_NPC_COMMAND_BATON = 38;
	public static final int GUI_NPC_FACTION_TRADE_SETUP = 39;
	public static final int GUI_BACKPACK = 40;
	public static final int GUI_NPC_TOWN_HALL = 41;
	public static final int GUI_NPC_FACTION_TRADE_VIEW = 42;
	public static final int GUI_NPC_BARD = 43;
	public static final int GUI_NPC_CREATIVE = 44;
	public static final int GUI_RESEARCH_BOOK = 45;
	public static final int GUI_WORKSITE_BOUNDS = 46;
	public static final int GUI_NPC_PLAYER_OWNED_TRADE = 47;
	public static final int GUI_SOUND_BLOCK = 48;
	public static final int GUI_NPC_FACTION_BARD = 49;

	public static final int GUI_VEHICLE_AMMO_SELECTION = 50;
	public static final int GUI_VEHICLE_INVENTORY = 51;
	public static final int GUI_VEHICLE_STATS = 52;

	public static final int GUI_WORKSITE_FRUIT_FARM = 53;

	private FMLEventChannel channel;

	private HashMap<Integer, Class<? extends ContainerBase>> containerClasses = new HashMap<>();
	private HashMap<Integer, Class<?>> guiClasses = new HashMap<>();

	public final void registerNetwork() {
		channel = NetworkRegistry.INSTANCE.newEventDrivenChannel(CHANNELNAME);
		channel.register(new PacketHandlerServer());
		PacketBase.registerPacketType(PACKET_GUI, PacketGui.class);
		PacketBase.registerPacketType(PACKET_ITEM_KEY_INTERFACE, PacketItemInteraction.class);
		PacketBase.registerPacketType(PACKET_ENTITY, PacketEntity.class);
		PacketBase.registerPacketType(PACKET_RESEARCH_INIT, PacketResearchInit.class);
		PacketBase.registerPacketType(PACKET_RESEARCH_ADD, PacketResearchUpdate.class);
		PacketBase.registerPacketType(PACKET_RESEARCH_START, PacketResearchStart.class);
		PacketBase.registerPacketType(PACKET_BLOCK_EVENT, PacketBlockEvent.class);
		NetworkRegistry.INSTANCE.registerGuiHandler(AncientWarfareCore.instance, this);

		if (Loader.isModLoaded("jei")) {
			PacketBase.registerPacketType(PACKET_JEI_TRANSFER_RECIPE, PacketTransferRecipe.class);
		}
	}

	public static void sendToServer(PacketBase pkt) {
		INSTANCE.channel.sendToServer(pkt.getFMLPacket());
	}

	public static void sendToPlayer(EntityPlayerMP player, PacketBase pkt) {
		INSTANCE.channel.sendTo(pkt.getFMLPacket(), player);
	}

	public static void sendToAllPlayers(PacketBase pkt) {
		INSTANCE.channel.sendToAll(pkt.getFMLPacket());
	}

	public static void sendToAllTracking(Entity e, PacketBase pkt) {
		WorldServer server = (WorldServer) e.world;
		server.getEntityTracker().sendToTracking(e, pkt.getFMLPacket());
	}

	public static void sendToAllNear(World world, int x, int y, int z, double range, PacketBase pkt) {
		INSTANCE.channel.sendToAllAround(pkt.getFMLPacket(), new TargetPoint(world.provider.getDimension(), x, y, z, range));
	}

    /*
	 * @param world (must be instanceof be WorldServer)
     * @param cx    chunkX
     * @param cz    chunkZ
     * @param pkt   the packet
     */

	public static void sendToAllTrackingChunk(World world, int cx, int cz, PacketBase pkt) {
		WorldServer server = (WorldServer) world;
		for (EntityPlayer p : server.playerEntities) {
			if (server.getPlayerChunkMap().isPlayerWatchingChunk((EntityPlayerMP) p, cx, cz)) {
				sendToPlayer((EntityPlayerMP) p, pkt);
			}
		}
	}

	@Override
	public final Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		Class<? extends ContainerBase> clz = containerClasses.get(ID);
		if (clz != null) {
			try {
				return clz.getConstructor(EntityPlayer.class, int.class, int.class, int.class).newInstance(player, x, y, z);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public final Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		Class<?> clz = this.guiClasses.get(ID);
		if (clz != null) {
			Object container = getServerGuiElement(ID, player, world, x, y, z);
			try {
				if (container != null)
					return clz.getConstructor(ContainerBase.class).newInstance(container);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static void registerContainer(int id, Class<? extends ContainerBase> containerClazz) {
		INSTANCE.containerClasses.put(id, containerClazz);
	}

	public static void registerGui(int id, Class<?> guiClazz) {
		INSTANCE.guiClasses.put(id, guiClazz);
	}

	public final void openGui(EntityPlayer player, int id, BlockPos pos) {
		openGui(player, id, pos.getX(), pos.getY(), pos.getZ());
	}

	//TODO refactor stuff to use this entityId overload
	public final void openGui(EntityPlayer player, int guiId) {
		openGui(player, guiId, 0);
	}

	public final void openGui(EntityPlayer player, int guiId, int entityId) {
		openGui(player, guiId, entityId, 0, 0);
	}

	public final void openGui(EntityPlayer player, int id, int x, int y, int z) {
		if (player.world.isRemote) {
			PacketGui pkt = new PacketGui();
			pkt.setOpenGui(id, x, y, z);
			sendToServer(pkt);
		} else {
			FMLNetworkHandler.openGui(player, AncientWarfareCore.instance, id, player.world, x, y, z);
			if (player.openContainer instanceof ContainerBase) {
				((ContainerBase) player.openContainer).sendInitData();
			}
		}
	}
}
