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

import shadowmage.ancient_framework.common.gamedata.AWGameData;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

/**
 * client-server mod meta-data comms packet
 * used to send server-> client onLogin data
 * used to send server-> client team/playerEntry update data
 * used to send server-> client cooldown, research, and stats data
 * used to send client-> server input for non-GUI information (team changes,
 * @author Shadowmage
 *
 */
public class Packet01ModData extends PacketBase
{

@Override
public String getChannel()
  {  
  return "AW_mod";
  }

@Override
public int getPacketType()
  {  
  return PacketHandler.MOD_DATA;
  }

@Override
public void writeDataToStream(ByteArrayDataOutput data)
  {

  }

@Override
public void readDataStream(ByteArrayDataInput data)
  {

  }

@Override
public void execute()
  {  
  if(this.packetData.hasKey("gameData"))
    {
    String name = this.packetData.getString("gameData");
    AWGameData.handlePacketData(name, packetData);
    } 
  }


}
