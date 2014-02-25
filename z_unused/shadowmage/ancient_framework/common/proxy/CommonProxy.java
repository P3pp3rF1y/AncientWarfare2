/**
   Copyright 2012 John Cummens (aka Shadowmage, Shadowmage4513)
   This software is distributed under the terms of the GNU General Public License.
   Please see COPYING for precise license information.

   This file is part of Ancient Warfare.

   Ancient Warfare is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   Ancient Warfare is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Ancient Warfare.  If not, see <http://www.gnu.org/licenses/>.


 */
package shadowmage.ancient_framework.common.proxy;

import java.util.Arrays;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.WorldServer;
import shadowmage.ancient_framework.common.network.Packet00MultiPart;
import shadowmage.ancient_framework.common.network.PacketBase;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class CommonProxy
{

public void registerClientData()
  {
  //NOOP server side
  }

public EntityPlayer getClientPlayer()
  {
  //NOOP server side
  return null;
  }

public void sendPacketToServer(PacketBase pkt)
  {
  //NOOP server side
  }

public final void sendPacketToAllClientsTracking(Entity ent, PacketBase packet)
  {
  PacketBase[] packets = getPackets(packet);
  WorldServer world = (WorldServer)ent.worldObj;
  for(PacketBase pkt : packets)
    {
    world.getEntityTracker().sendPacketToAllPlayersTrackingEntity(ent, pkt.get250Packet());
    }
  }

public final void sendPacketToPlayer(EntityPlayer player, PacketBase packet)
  {
  PacketBase[] packets = getPackets(packet);
  for(PacketBase pkt : packets)
    {
    PacketDispatcher.sendPacketToPlayer(pkt.get250Packet(), (Player)player);
    }
  }

public final void sendPacketToAllPlayers(PacketBase packet)
  {
  PacketBase[] packets = getPackets(packet);
  for(PacketBase pkt : packets)
    {
    PacketDispatcher.sendPacketToAllPlayers(pkt.get250Packet());
    }  
  }

protected final PacketBase[] getPackets(PacketBase packet)
  {
  Packet250CustomPayload customPacket = packet.get250Packet();
  PacketBase[] packetArray;
  int totalLength = customPacket.data.length;
   
  if(totalLength<=32000)
    {
    return new PacketBase[]{packet};
    }
  else
    {
    Packet00MultiPart partPacket;
    int numOfPackets =  (totalLength/32000) + 1;
    packetArray = new PacketBase[numOfPackets];
    int startIndex;
    int length;
    int packetID = Packet00MultiPart.getNextPacketID();
    for(int i = 0; i < numOfPackets; i++)
      {      
      startIndex = 32000 * i;      
      length = totalLength - startIndex;
      if(length>32000)
        {
        length = 32000;
        }              
      partPacket = new Packet00MultiPart();
      partPacket.uniquePacketID = packetID;
      partPacket.sourcePacketType = packet.getPacketType();
      partPacket.datas = Arrays.copyOfRange(customPacket.data, startIndex, startIndex+length);   ;
      partPacket.totalChunks = numOfPackets;
      partPacket.chunkLength = length;
      partPacket.chunkNumber = i;
      partPacket.startIndex = startIndex;
      partPacket.totalLength = totalLength;
      packetArray[i] = partPacket;
      }
    }  
  return packetArray;
  }


}
