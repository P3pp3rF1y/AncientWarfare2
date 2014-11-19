package net.shadowmage.ancientwarfare.core.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;

public class PacketBlockEvent extends PacketBase
{

int x, y, z;
short id, a, b;

public PacketBlockEvent()
  {
  // TODO Auto-generated constructor stub
  }

/**
 * @param x coordinate of block in the world (written as int)
 * @param y coordinate of block in the world (written as short)
 * @param z coordinate of block in the world (written as int)
 * @param block type to validate on client-side prior to reading event (id written as short)
 * @param a data part a - (written as a unsigned byte)
 * @param b data part b - (written as a unsigned byte)
 */
public void setParams(int x, int y, int z, Block block, short a, short b)
  {
  this.x = x;
  this.y = y;
  this.z = z;
  this.id = (short)Block.getIdFromBlock(block);
  this.a = a;
  this.b = b;
  }

@Override
protected void writeToStream(ByteBuf data)
  {
  data.writeInt(x);
  data.writeShort(y);
  data.writeInt(z);
  data.writeShort(id);
  data.writeByte(a&0xff);
  data.writeByte(b&0xff);
  }

@Override
protected void readFromStream(ByteBuf data)
  {
  x = data.readInt();
  y = data.readShort();
  z = data.readInt();
  id = data.readShort();
  a = data.readUnsignedByte();
  b = data.readUnsignedByte();
  }

@Override
protected void execute()
  {
  player.worldObj.addBlockEvent(x, y, z, Block.getBlockById(id), a, b);
  }

}
