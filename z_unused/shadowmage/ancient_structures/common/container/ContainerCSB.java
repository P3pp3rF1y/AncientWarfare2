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
package shadowmage.ancient_structures.common.container;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import shadowmage.ancient_framework.common.config.AWLog;
import shadowmage.ancient_framework.common.container.ContainerBase;
import shadowmage.ancient_structures.common.item.AWStructuresItemLoader;
import shadowmage.ancient_structures.common.item.ItemStructureSettings;

public class ContainerCSB extends ContainerBase
{

public String structureName = "";

ItemStructureSettings settings = new ItemStructureSettings();

/**
 * @param openingPlayer
 * @param synch
 */
public ContainerCSB(EntityPlayer openingPlayer, int x, int y, int z) 
  {
  super(openingPlayer, x, y, z);
  if(player.worldObj.isRemote)
    {
    return;
    }
  ItemStack builderItem = player.inventory.getCurrentItem();
  if(builderItem==null || builderItem.getItem()==null || (builderItem.getItem()!=AWStructuresItemLoader.structureBuilderCreative && builderItem.getItem()!=AWStructuresItemLoader.structureGenerator))
    {
    return;
    } 
  ItemStructureSettings.getSettingsFor(builderItem, settings);
  }

@Override
public void handlePacketData(NBTTagCompound tag)
  {
  AWLog.logDebug("rec info...");
  if(tag.hasKey("name"))
    {
    this.settings.setName(tag.getString("name")); 
    }
  }

@Override
public void handleInitData(NBTTagCompound tag)
  {
  if(tag.hasKey("name"))
    {
    this.structureName = tag.getString("name");
    }
  this.refreshGui();
  }

@Override
public List<NBTTagCompound> getInitData()
  {  
  ItemStack builderItem = player.inventory.getCurrentItem();  
  if(builderItem!=null && (builderItem.getItem() == AWStructuresItemLoader.structureBuilderCreative || builderItem.getItem()==AWStructuresItemLoader.structureGenerator) && builderItem.hasTagCompound() && builderItem.getTagCompound().hasKey("structData") && builderItem.getTagCompound().getCompoundTag("structData").hasKey("name"))
    {
    NBTTagCompound tag = new NBTTagCompound();
    tag.setString("name", builderItem.getTagCompound().getCompoundTag("structData").getString("name"));    
    ArrayList<NBTTagCompound> initList = new ArrayList<NBTTagCompound>();    
    initList.add(tag);
    return initList;
    } 
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
  if(builderItem==null || builderItem.getItem()==null || (builderItem.getItem()!=AWStructuresItemLoader.structureBuilderCreative &&builderItem.getItem()!=AWStructuresItemLoader.structureGenerator))
    {
    return;
    }
  ItemStructureSettings.setSettingsFor(builderItem, settings);  
  }

}
