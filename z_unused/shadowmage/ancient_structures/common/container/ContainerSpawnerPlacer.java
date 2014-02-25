/**
   Copyright 2012-2013 John Cummens (aka Shadowmage, Shadowmage4513)
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
package shadowmage.ancient_structures.common.container;

import java.util.Collections;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import shadowmage.ancient_framework.common.container.ContainerBase;
import shadowmage.ancient_structures.common.item.AWStructuresItemLoader;

public class ContainerSpawnerPlacer extends ContainerBase
{
public String mobID = "Pig";
/**
 * @param openingPlayer
 * @param synch
 */
public ContainerSpawnerPlacer(EntityPlayer openingPlayer, int x, int y, int z)
  {
  super(openingPlayer, x, y, z);
  ItemStack builderItem = player.inventory.getCurrentItem(); 
  if(builderItem.hasTagCompound() && builderItem.getTagCompound().hasKey("spawnData"))
    {
    this.mobID = builderItem.getTagCompound().getCompoundTag("spawnData").getString("mobID");
    }
  }

@Override
public void handlePacketData(NBTTagCompound tag)
  {
  if(tag.hasKey("mobID"))
    {
    this.mobID = tag.getString("mobID");
    }
  }

@Override
public void handleInitData(NBTTagCompound tag)
  {
  this.mobID = tag.getString("mobID");
  }

@Override
public List<NBTTagCompound> getInitData()
  {
  return Collections.emptyList();
  }

@Override
public void onContainerClosed(EntityPlayer par1EntityPlayer)
  {
  super.onContainerClosed(par1EntityPlayer);
  if(par1EntityPlayer.worldObj.isRemote)
    {
    return;
    }
  ItemStack builderItem = player.inventory.getCurrentItem();  
  if(builderItem==null || builderItem.getItem()==null || builderItem.getItem()!=AWStructuresItemLoader.spawnerPlacer)
    {
    return;
    }
  if(!builderItem.hasTagCompound() || !builderItem.getTagCompound().hasKey("spawnData"))
    {
    builderItem.setTagInfo("spawnData", new NBTTagCompound());
    }
  builderItem.getTagCompound().getCompoundTag("spawnData").setString("mobID", mobID);
  }

}
