package net.shadowmage.ancientwarfare.npc.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;

import java.io.IOException;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.network.PacketBase;
import net.shadowmage.ancientwarfare.npc.faction.FactionTracker;

public class PacketFactionUpdate extends PacketBase
{

NBTTagCompound packetData;

public PacketFactionUpdate(NBTTagCompound tag)
  {
  this.packetData = tag;
  }

public PacketFactionUpdate()
  {
  }

@Override
protected void writeToStream(ByteBuf data)
  {
  if(packetData!=null)
    {
    ByteBufOutputStream bbos = new ByteBufOutputStream(data);
    try
      {
      CompressedStreamTools.writeCompressed(packetData, bbos);
      } 
    catch (IOException e)
      {
      e.printStackTrace();
      }
    }
  }

@Override
protected void readFromStream(ByteBuf data)
  {
  try
    {
    packetData = CompressedStreamTools.readCompressed(new ByteBufInputStream(data));
    } 
  catch (IOException e)
    {
    e.printStackTrace();
    }
  }

@Override
protected void execute()
  {
  if(packetData!=null)
    {
    FactionTracker.INSTANCE.handlePacketData(packetData);
    }
  }

}
