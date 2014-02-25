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

import net.minecraft.nbt.NBTTagCompound;
import shadowmage.ancient_framework.common.config.AWLog;
import shadowmage.ancient_framework.common.interfaces.IHandlePacketData;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

public class Packet03GuiComs extends PacketBase
{

@Override
public String getChannel()
  {  
  return "AW_gui";
  }

@Override
public int getPacketType()
  {
  return PacketHandler.GUI_COMS;
  }

@Override
public void writeDataToStream(ByteArrayDataOutput data)
  {
  
  }

@Override
public void readDataStream(ByteArrayDataInput data)
  {
  
  }

public void setGuiToOpen(byte id, int x, int y, int z)
  {
  NBTTagCompound tag = new NBTTagCompound();
  tag.setByte("id", id);
  tag.setInteger("x", x);
  tag.setInteger("y", y);
  tag.setInteger("z", z);
  this.packetData.setTag("openGUI", tag);
  }

public void setData(NBTTagCompound tag)
  {
  this.packetData.setCompoundTag("data", tag);
  }

public void setInitData(NBTTagCompound tag)
  {
  this.packetData.setCompoundTag("init", tag);
  }

@Override
public void execute()
  {
  if(packetData.hasKey("openGUI"))
    {  
    if(world.isRemote)
      {
      AWLog.logError("Opening GUI on client-side only from openGUI packet.  This is not proper gui handling.");
      return;
      }
    NBTTagCompound tag = packetData.getCompoundTag("openGUI");
    int id = tag.getByte("id");
    int x = tag.getInteger("x");
    int y = tag.getInteger("y");
    int z = tag.getInteger("z");
    GUIHandler.instance().openGUI(id, player, x, y, z);  
    }
  if(player.openContainer instanceof IHandlePacketData)
    {
    if(packetData.hasKey("data"))
      {
      ((IHandlePacketData)player.openContainer).handlePacketData(packetData.getCompoundTag("data"));
      return;
      }
    if(packetData.hasKey("init") && world.isRemote)
      {
      ((IHandlePacketData)player.openContainer).handleInitData(packetData.getCompoundTag("init"));
      return;      
      }
    }  
  }

}
