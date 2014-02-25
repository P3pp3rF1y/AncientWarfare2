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
import shadowmage.ancient_framework.common.interfaces.IEntityPacketHandler;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

public class Packet02Entity extends PacketBase
{

public int entityID;

@Override
public String getChannel()
  {  
  return "AW_entity";
  }

public void setParams(Entity ent)
  {  
  this.entityID = ent.entityId;
  }

@Override
public int getPacketType()
  {  
  return PacketHandler.ENTITY;
  }

@Override
public void writeDataToStream(ByteArrayDataOutput data)
  {
  data.writeInt(entityID);  
  }

@Override
public void readDataStream(ByteArrayDataInput data)
  {
  this.entityID = data.readInt(); 
  }

@Override
public void execute()
  {
  Entity entity = world.getEntityByID(entityID);
  if(entity instanceof IEntityPacketHandler)
    {
    IEntityPacketHandler handle = (IEntityPacketHandler)entity;
    handle.onPacketDataReceived(packetData);
    }
  }


}
