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
package shadowmage.ancient_framework.common.network;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.World;
import shadowmage.ancient_framework.AWFramework;
import shadowmage.ancient_framework.common.utils.NBTTools;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public abstract class PacketBase 
{

/**
 * world and player are populated only after receiving, by the packetHandler
 */
public World world;
public EntityPlayer player;

/**
 * static return channel
 * @return
 */
public abstract String getChannel();

/**
 * NBTTag containing the actual data for this packet
 */
public NBTTagCompound packetData = new NBTTagCompound();

/**
 * the actual transformed packet that will be written and sent
 */
private Packet250CustomPayload packet250;

/**
 * should be treated as chunk update packet?
 */
private boolean chunkPacket = false;

/**
 * return the numerical packet type, used to create new packet instances from a packetTypeMap in PacketHandler
 * @return packetType
 */
public abstract int getPacketType();

/**
 * write packet specific data to the stream
 * @param data
 */
public abstract void writeDataToStream(ByteArrayDataOutput data);

/**
 * read packet-specific data from the stream
 * @param data
 */
public abstract void readDataStream(ByteArrayDataInput data);

/**
 * called to execute the contents of the packet, whether executed by the packet or passed
 * on to another entity/class
 */
public abstract void execute();

/**
 * create the custom250packet from the current data in this packet.
 */
protected void constructPacket()
  {  
  ByteArrayDataOutput data = ByteStreams.newDataOutput();

  /**
   * write the packet type number to the stream, decoded in packetHandler to create a new packet
   */
  data.writeInt(this.getPacketType());
  
  /**
   * write default packet data NBTCompound to the stream
   */  
  NBTTools.writeTagToStream(packetData, data);    
  
  /**
   * write custom data to the output stream
   */
  this.writeDataToStream(data);
  this.packet250 = new Packet250CustomPayload();
  packet250.isChunkDataPacket = this.chunkPacket;
  packet250.channel = this.getChannel();
  packet250.data = data.toByteArray();
  packet250.length = packet250.data.length;
  }

public Packet250CustomPayload get250Packet()
  {
  if(this.packet250==null)
    {
    this.constructPacket();
    }
  return this.packet250;
  }

public void sendPacketToServer()
  {
  AWFramework.proxy.sendPacketToServer(this);
  }

public void sendPacketToAllTrackingClients(Entity ent)
  {
  AWFramework.proxy.sendPacketToAllClientsTracking(ent, this);
  }

public void sendPacketToPlayer(EntityPlayer player)
  {
  AWFramework.proxy.sendPacketToPlayer(player, this);
  }

public void sendPacketToAllPlayers()
  {
  AWFramework.proxy.sendPacketToAllPlayers(this);
  }
}
