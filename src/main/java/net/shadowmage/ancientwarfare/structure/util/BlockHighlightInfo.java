package net.shadowmage.ancientwarfare.structure.util;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;

public class BlockHighlightInfo {
	public static final BlockHighlightInfo EXPIRED = new BlockHighlightInfo(BlockPos.ORIGIN, 0);
	private BlockPos pos;
	private long expirationTime;

	public BlockHighlightInfo(BlockPos pos, long expirationTime) {
		this.pos = pos;
		this.expirationTime = expirationTime;
	}

	public long getExpirationTime() {
		return expirationTime;
	}

	public BlockPos getPos() {
		return pos;
	}

	public void serializeToBuffer(ByteBuf data) {
		data.writeLong(pos.toLong());
		data.writeLong(expirationTime);
	}

	public static BlockHighlightInfo deserializeFromBuffer(ByteBuf data) {
		return new BlockHighlightInfo(BlockPos.fromLong(data.readLong()), data.readLong());
	}
}
