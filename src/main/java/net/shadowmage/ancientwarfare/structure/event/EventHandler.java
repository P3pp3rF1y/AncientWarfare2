package net.shadowmage.ancientwarfare.structure.event;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.structure.gamedata.StructureMap;
import net.shadowmage.ancientwarfare.structure.network.PacketStructureMap;

public class EventHandler {
	private EventHandler() {}

	public static final EventHandler INSTANCE = new EventHandler();

	@SubscribeEvent
	public void onPlayerLogin(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent evt) {
		NetworkHandler.sendToAllPlayers(new PacketStructureMap(AWGameData.INSTANCE.getData(evt.player.world, StructureMap.class).writeToNBT(new NBTTagCompound()).getCompoundTag("map")));
	}
}
