package net.shadowmage.ancientwarfare.core.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;

import java.io.IOException;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.interfaces.IEntityPacketHandler;

public class PacketEntity extends PacketBase
{

int entityId;
public NBTTagCompound packetData = new NBTTagCompound();

public PacketEntity()
  {
  }

public PacketEntity(Entity e)
  {
  this.entityId = e.getEntityId();
  }

@Override
protected void writeToStream(ByteBuf data)
  {
  data.writeInt(entityId);
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
  entityId = data.readInt();
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
  Entity e = player.worldObj.getEntityByID(entityId);
  if(e instanceof IEntityPacketHandler)
    {
    ((IEntityPacketHandler)e).handlePacketData(packetData);
    }
  }

}
