package net.shadowmage.ancientwarfare.structure.event;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.world.ChunkWatchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.structure.gamedata.StructureMap;
import net.shadowmage.ancientwarfare.structure.network.PacketStructureEntry;

public class EventHandler {
	private EventHandler() {}

	public static final EventHandler INSTANCE = new EventHandler();

	@SubscribeEvent
	public void onChunkWatch(ChunkWatchEvent.Watch evt) {
		if (evt.getChunkInstance() == null) {
			return;
		}
		Chunk chunk = evt.getChunkInstance();
		EntityPlayerMP player = evt.getPlayer();

		AWGameData.INSTANCE.getData(player.world, StructureMap.class).getStructureAt(player.world, chunk.x, chunk.z)
				.ifPresent(structureEntry -> NetworkHandler.sendToPlayer(player,
						new PacketStructureEntry(player.world.provider.getDimension(), chunk.x, chunk.z, structureEntry)
				));
	}
}
