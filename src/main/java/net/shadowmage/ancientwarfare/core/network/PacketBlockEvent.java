package net.shadowmage.ancientwarfare.core.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

public class PacketBlockEvent extends PacketBase {
	private BlockPos pos;
	private short id;
	private short a;
	private short b;

	public PacketBlockEvent() {}

	/*
	 * @param pos    coordinates of block in the world
	 * @param block type to validate on client-side prior to reading event (id written as short)
	 * @param a     data part a - (written as a unsigned byte)
	 * @param b     data part b - (written as a unsigned byte)
	 */
	public PacketBlockEvent(BlockPos pos, Block block, short a, short b) {
		this.pos = pos;
		this.id = (short) Block.getIdFromBlock(block);
		this.a = a;
		this.b = b;
	}

	@Override
	protected void writeToStream(ByteBuf data) {
		data.writeLong(pos.toLong());
		data.writeShort(id);
		data.writeByte(a & 0xff);
		data.writeByte(b & 0xff);
	}

	@Override
	protected void readFromStream(ByteBuf data) {
		pos = BlockPos.fromLong(data.readLong());
		id = data.readShort();
		a = data.readUnsignedByte();
		b = data.readUnsignedByte();
	}

	@Override
	protected void execute(EntityPlayer player) {
		player.world.addBlockEvent(pos, Block.getBlockById(id), a, b);
	}
}
