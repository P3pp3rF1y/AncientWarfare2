package net.shadowmage.ancientwarfare.core.network;

import io.netty.buffer.ByteBuf;

public class PacketTileEvent extends PacketBase
{

int x, y, z;
short a, b;

public PacketTileEvent()
  {
  // TODO Auto-generated constructor stub
  }

public void setParams(int x, int y, int z, short a, short b)
  {
  this.x = x;
  this.y = y;
  this.z = z;
  this.a = a;
  this.b = b;
  }

@Override
protected void writeToStream(ByteBuf data)
  {
  data.writeInt(x);
  data.writeShort(y);
  data.writeInt(z);
  data.writeShort(a);
  data.writeShort(b);
  }

@Override
protected void readFromStream(ByteBuf data)
  {
  x = data.readInt();
  y = data.readShort();
  z = data.readInt();
  a = data.readShort();
  b = data.readShort();
  }

@Override
protected void execute()
  {
  player.worldObj.addBlockEvent(x, y, z, player.worldObj.getBlock(x, y, z), a, b);
  }

}
