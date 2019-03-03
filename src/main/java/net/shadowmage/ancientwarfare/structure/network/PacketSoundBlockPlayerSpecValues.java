package net.shadowmage.ancientwarfare.structure.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.shadowmage.ancientwarfare.core.network.PacketBase;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.structure.tile.TileSoundBlock;

import java.io.IOException;

public class PacketSoundBlockPlayerSpecValues extends PacketBase {
	private BlockPos tilePos;
	private boolean stopped;
	private long lastTimePlayerNear;
	private int numberOfTimesRepeated;

	public PacketSoundBlockPlayerSpecValues() {}

	public PacketSoundBlockPlayerSpecValues(BlockPos tilePos, TileSoundBlock.PersistentValues values) {
		this.tilePos = tilePos;
		this.stopped = values.isStopped();
		this.lastTimePlayerNear = values.getLastTimePlayerNear();
		this.numberOfTimesRepeated = values.getNumberOfTimesRepeated();
	}

	@Override
	protected void writeToStream(ByteBuf data) {
		data.writeLong(tilePos.toLong());
		data.writeBoolean(stopped);
		data.writeLong(lastTimePlayerNear);
		data.writeInt(numberOfTimesRepeated);
	}

	@Override
	protected void readFromStream(ByteBuf data) throws IOException {
		tilePos = BlockPos.fromLong(data.readLong());
		stopped = data.readBoolean();
		lastTimePlayerNear = data.readLong();
		numberOfTimesRepeated = data.readInt();
	}

	@Override
	protected void execute(EntityPlayer player) {
		WorldTools.getTile(player.world, tilePos, TileSoundBlock.class).ifPresent(tile ->
				tile.updatePlayerSpecValues(player.getUniqueID(), stopped, lastTimePlayerNear, numberOfTimesRepeated));

	}

}
