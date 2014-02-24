package net.shadowmage.ancientwarfare.core.network;

import java.io.IOException;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;

public class PacketGui extends PacketBase
{

public NBTTagCompound dataTag;

public PacketGui()
  {
  
  }

@Override
protected void writeToStream(ByteBuf data)
  {
  if(dataTag!=null)
    {
    ByteBufOutputStream bbos = new ByteBufOutputStream(data);
    try
      {
      CompressedStreamTools.writeCompressed(dataTag, bbos);
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
    dataTag = CompressedStreamTools.readCompressed(new ByteBufInputStream(data));
    } 
  catch (IOException e)
    {
    e.printStackTrace();
    }
  }

@Override
protected void execute()
  {
  AWLog.logDebug("executing gui packet");
  if(player.openContainer instanceof ContainerBase)
    {
    ((ContainerBase)player.openContainer).onPacketData(dataTag);
    }
  }

}
