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
package shadowmage.ancient_framework.common.container;

import java.util.Collections;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import shadowmage.ancient_framework.common.utils.ServerPerformanceMonitor;

public class ContainerPerformanceMonitor extends ContainerBase
{

public long memUse;
public long tickTime;
public long tickPerSecond;
public long pathTimeTickAverage;
public long civicTick;
public long npcTick;
public long vehicleTick;


/**
 * @param openingPlayer
 * @param synch
 */
public ContainerPerformanceMonitor(EntityPlayer openingPlayer, int x, int y, int z)
  {
  super(openingPlayer, x, y, z);
  }

@Override
public void handlePacketData(NBTTagCompound tag)
  {
  if(tag.hasKey("tick")){this.tickTime = tag.getLong("tick");}
  if(tag.hasKey("tps")){this.tickPerSecond = tag.getLong("tps");}
  if(tag.hasKey("pathTick")){this.pathTimeTickAverage = tag.getLong("pathTick");}
  if(tag.hasKey("mem")){this.memUse = tag.getLong("mem");}
  if(tag.hasKey("civTick")){this.civicTick = tag.getLong("civTick");}
  if(tag.hasKey("npcTick")){this.npcTick = tag.getLong("npcTick");}
  if(tag.hasKey("vehTick")){this.vehicleTick = tag.getLong("vehTick");}
  }

@Override
public void handleInitData(NBTTagCompound tag)
  {
  
  }

@Override
public List<NBTTagCompound> getInitData()
  {
  return Collections.emptyList();
  }

@Override
public void detectAndSendChanges()
  {
  super.detectAndSendChanges();
  if(player.worldObj.isRemote){return;}
  NBTTagCompound tag = new NBTTagCompound();
  tag.setLong("tick", ServerPerformanceMonitor.tickTime);
  tag.setLong("tps", ServerPerformanceMonitor.tickPerSecond);
  tag.setLong("pathTick", ServerPerformanceMonitor.pathTimeAverage);
  tag.setLong("mem", Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
  tag.setLong("civTick", ServerPerformanceMonitor.civicTimeAverage);
  tag.setLong("npcTick", ServerPerformanceMonitor.npcTimeAverage);
  tag.setLong("vehTick", ServerPerformanceMonitor.vehicleTimeAverage);
  this.sendDataToPlayer(tag);
  }

}
