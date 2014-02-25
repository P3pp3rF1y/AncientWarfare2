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
package shadowmage.ancient_structures.common.template.plugin.default_plugins.entity_rules;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import shadowmage.ancient_framework.common.utils.BlockTools;

public class TemplateRuleEntityLogic extends TemplateRuleVanillaEntity
{

public NBTTagCompound tag = new NBTTagCompound();

public TemplateRuleEntityLogic(){}

public TemplateRuleEntityLogic(World world, Entity entity, int turns, int x, int y, int z)
  {
  super(world, entity, turns, x, y, z);
  entity.writeToNBT(tag);
  tag.removeTag("UUIDMost");
  tag.removeTag("UUIDLeast");
  }

@Override
public void handlePlacement(World world, int turns, int x, int y, int z)
  {
  Entity e = EntityList.createEntityByName(mobID, world);
  NBTTagList list = tag.getTagList("Pos");
  if(list.tagCount()>=3)
    {
    ((NBTTagDouble)list.tagAt(0)).data = x + BlockTools.rotateFloatX(xOffset, zOffset, turns);
    ((NBTTagDouble)list.tagAt(1)).data = y;
    ((NBTTagDouble)list.tagAt(2)).data = z + BlockTools.rotateFloatZ(xOffset, zOffset, turns);
    e.readFromNBT(tag);
    }
  else
    {
    e.setPosition(x+0.5d, y, z+0.5d);
    }
  float yaw = (rotation + 90.f * turns)%360.f;
  e.rotationYaw = yaw;
  world.spawnEntityInWorld(e);
  }

@Override
public void writeRuleData(NBTTagCompound tag)
  {
  super.writeRuleData(tag);
  tag.setTag("entityData", this.tag);
  }

@Override
public void parseRuleData(NBTTagCompound tag)
  {
  super.parseRuleData(tag);
  this.tag = tag.getCompoundTag("entityData");
  }

}
