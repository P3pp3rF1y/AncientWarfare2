package net.shadowmage.ancientwarfare.structure.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.network.PacketBase;

public class PacketStructure extends PacketBase
{

public NBTTagCompound packetData = new NBTTagCompound();

public PacketStructure()
  {
  // TODO Auto-generated constructor stub
  }

@Override
protected void writeToStream(ByteBuf data)
  {
  // TODO Auto-generated method stub

  }

@Override
protected void readFromStream(ByteBuf data)
  {
  // TODO Auto-generated method stub

  }

@Override
protected void execute()
  {
  // TODO Auto-generated method stub

  }

}
