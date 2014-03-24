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

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import shadowmage.ancient_structures.common.template.rule.TemplateRuleEntity;
import shadowmage.ancient_structures.common.utils.BlockTools;

public class TemplateRuleVanillaEntity extends TemplateRuleEntity
{

public String mobID;
public float xOffset;
public float yOffset;
public float zOffset;
public float rotation;

public TemplateRuleVanillaEntity(World world, Entity entity, int turns, int x, int y, int z)
  {
  this.mobID = EntityList.getStringFromID(EntityList.getEntityID(entity));
  rotation = (entity.rotationYaw + 90.f*turns)%360.f;
  float x1, z1;
  x1 = (float) (entity.posX%1.d);
  z1 = (float) (entity.posZ%1.d);
  if(x1<0){x1++;}
  if(z1<0){z1++;}
  xOffset = BlockTools.rotateFloatX(x1, z1, turns);
  zOffset = BlockTools.rotateFloatZ(x1, z1, turns);
  yOffset = (float)(entity.posY % 1.d);
  }

public TemplateRuleVanillaEntity()
  {
  
  }

@Override
public void handlePlacement(World world, int turns, int x, int y, int z)
  {
  Entity e = EntityList.createEntityByName(mobID, world);
  float x1 = BlockTools.rotateFloatX(xOffset, zOffset, turns);
  float z1 = BlockTools.rotateFloatZ(xOffset, zOffset, turns);
  float yaw = (rotation + 90.f * turns)%360.f;
  e.setPosition(x+x1, y+yOffset, z+z1);
  e.rotationYaw = yaw;
  world.spawnEntityInWorld(e);
  }

@Override
public void writeRuleData(NBTTagCompound tag)
  {
  tag.setString("mobID", mobID);
  tag.setFloat("xOffset", xOffset);
  tag.setFloat("zOffset", zOffset);
  tag.setFloat("yOffset", yOffset);
  tag.setFloat("rotation", rotation);
  }

@Override
public void parseRuleData(NBTTagCompound tag)
  {
  mobID = tag.getString("mobID");
  xOffset = tag.getFloat("xOffset");
  zOffset = tag.getFloat("zOffset");
  yOffset = tag.getFloat("yOffset");
  rotation = tag.getFloat("rotation");
  }

@Override
public boolean shouldPlaceOnBuildPass(World world, int turns, int x, int y, int z, int buildPass)
  {
  return buildPass==3;
  }

@Override
public void addResources(List<ItemStack> resources)
  {
  
  }

}
