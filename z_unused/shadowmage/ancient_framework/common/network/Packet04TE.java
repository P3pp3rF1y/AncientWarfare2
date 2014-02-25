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

import net.minecraft.tileentity.TileEntity;
import shadowmage.ancient_framework.common.interfaces.IHandlePacketData;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

public class Packet04TE extends PacketBase
{

int x;
int y;
int z;
/**
 * 
 */
public Packet04TE()
  {
  
  }

@Override
public String getChannel()
  {
  return "AW_tile";
  }

@Override
public int getPacketType()
  {
  return PacketHandler.TILE;
  }

public void setParams(TileEntity te)
  {
  this.x = te.xCoord;
  this.y = te.yCoord;
  this.z = te.zCoord;
  }

@Override
public void writeDataToStream(ByteArrayDataOutput data)
  {
  data.writeInt(x);
  data.writeInt(y);
  data.writeInt(z);
  }

@Override
public void readDataStream(ByteArrayDataInput data)
  {
  x = data.readInt();
  y = data.readInt();
  z = data.readInt();
  }

@Override
public void execute()
  {
  TileEntity te = world.getBlockTileEntity(x, y, z);
  if(te instanceof IHandlePacketData)
    {
    ((IHandlePacketData)te).handlePacketData(packetData);
    }
  }

}
