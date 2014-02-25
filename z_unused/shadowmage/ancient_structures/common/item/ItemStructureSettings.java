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
package shadowmage.ancient_structures.common.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import shadowmage.ancient_framework.common.utils.BlockPosition;

public class ItemStructureSettings
{

boolean[] setKeys = new boolean[4];
BlockPosition pos1;
BlockPosition pos2;
BlockPosition key;
int buildFace;
String name;

public ItemStructureSettings()
  {
  pos1 = new BlockPosition();
  pos2 = new BlockPosition();
  key = new BlockPosition();
  }

/**
 * @param item
 * @return
 */
public static ItemStructureSettings getSettingsFor(ItemStack stack, ItemStructureSettings settings)
  {
  NBTTagCompound tag;
  if(stack.hasTagCompound() && stack.getTagCompound().hasKey("structData"))
    {
    tag = stack.getTagCompound().getCompoundTag("structData");
    }
  else
    {
    tag = new NBTTagCompound();
    }
  for(int i = 0; i < 4; i++)
    {
    settings.setKeys[i] = false;
    }
  if(tag.hasKey("pos1"))
    {
    settings.pos1.read(tag.getCompoundTag("pos1"));
    settings.setKeys[0] = true;
    }
  if(tag.hasKey("pos2"))      
    {
    settings.pos2.read(tag.getCompoundTag("pos2"));
    settings.setKeys[1] = true;
    }
  if(tag.hasKey("buildKey"))
    {
    settings.key.read(tag.getCompoundTag("buildKey"));
    settings.setKeys[2] = true;
    settings.buildFace = tag.getCompoundTag("buildKey").getInteger("face");
    }
  if(tag.hasKey("name"))
    {
    settings.name = tag.getString("name");
    settings.setKeys[3] = true;
    }
  return settings;
  }

public static void setSettingsFor(ItemStack item, ItemStructureSettings settings)
  {
  NBTTagCompound tag = new NBTTagCompound();  
  if(settings.setKeys[0])
    {
    NBTTagCompound tag1 = new NBTTagCompound();
    settings.pos1.writeToNBT(tag1);
    tag.setTag("pos1", tag1);
    }  
  if(settings.setKeys[1])
    {
    NBTTagCompound tag1 = new NBTTagCompound();
    settings.pos2.writeToNBT(tag1);
    tag.setTag("pos2", tag1);
    }
  if(settings.setKeys[2])
    {
    NBTTagCompound tag1 = new NBTTagCompound();
    settings.key.writeToNBT(tag1);
    tag1.setInteger("face", settings.buildFace);
    tag.setTag("buildKey", tag1);
    }  
  if(settings.setKeys[3])
    {
    tag.setString("name", settings.name);
    }
  item.setTagInfo("structData", tag);
  }

public void setPos1(int x, int y, int z)
  {
  pos1.reassign(x, y, z);
  setKeys[0] = true;
  }

public void setPos2(int x, int y, int z)
  {
  pos2.reassign(x, y, z);
  setKeys[1] = true;
  }

public void setBuildKey(int x, int y, int z, int face)
  {
  key.reassign(x, y, z);
  buildFace = face;
  setKeys[2] = true;
  }

public void setName(String name)
  {
  this.name = name;
  setKeys[3] = true;
  }

public boolean hasPos1()
  {
  return setKeys[0];
  }

public boolean hasPos2()
  {
  return setKeys[1];
  }

public boolean hasBuildKey()
  {
  return setKeys[2];
  }

public boolean hasName()
  {
  return setKeys[3];
  }

public BlockPosition pos1()
  {
  return pos1;
  }

public BlockPosition pos2()
  {
  return pos2;
  }

public BlockPosition buildKey()
  {
  return key;
  }

public int face()
  {
  return buildFace;
  }

public String name()
  {
  return name;
  }

public void clearSettings()
  {
  for(int i = 0; i<3; i++)
    {
    this.setKeys[i] = false;
    }
  }

}
