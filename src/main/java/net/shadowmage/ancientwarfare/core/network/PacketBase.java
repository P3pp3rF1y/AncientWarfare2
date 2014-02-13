package net.shadowmage.ancientwarfare.core.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;

public abstract class PacketBase
{

private static HashMap<Integer, Class<? extends PacketBase>> packetTypes = new HashMap<Integer, Class<? extends PacketBase>>();
private static HashMap<Class<? extends PacketBase>, Integer> packetIDs = new HashMap<Class<? extends PacketBase>, Integer>();

protected EntityPlayer player;

public static void registerPacketType(int typeNum, Class <? extends PacketBase> packetClz)
  {
  packetTypes.put(typeNum, packetClz);
  packetIDs.put(packetClz, typeNum);
  }

public PacketBase()
  {  
  }

protected void setPlayer(EntityPlayer player)
  {
  this.player = player;
  }

protected void writeHeaderToStream(ByteBuf data)
  {
  data.writeByte(packetIDs.get(this.getClass()));
  }

protected static PacketBase readHeaderFromStream(ByteBuf data)
  {
  int typeNum = data.readByte();
  try
    {
    PacketBase pkt = packetTypes.get(typeNum).newInstance();
    return pkt;
    } 
  catch (InstantiationException e)
    {    
    e.printStackTrace();
    } 
  catch (IllegalAccessException e)
    {  
    e.printStackTrace();
    }
  return null;
  }

protected abstract void writeToStream(ByteBuf data);
protected abstract void readFromStream(ByteBuf data);
protected abstract void execute();

public static PacketBase readPacket(ByteBuf data)
  {
  PacketBase pkt = readHeaderFromStream(data);
  pkt.readFromStream(data);
  return pkt;
  }

public final FMLProxyPacket getFMLPacket()
  {
  ByteBuf buf = Unpooled.buffer();
  writeHeaderToStream(buf);
  writeToStream(buf);
  return new FMLProxyPacket(buf, NetworkHandler.CHANNELNAME);
  }

}
