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
package shadowmage.ancient_structures.common.world_gen;

import net.minecraft.nbt.NBTTagCompound;
import shadowmage.ancient_framework.common.utils.BlockPosition;
import shadowmage.ancient_structures.common.template.StructureTemplate;
import shadowmage.ancient_structures.common.template.build.StructureBB;

public class StructureEntry
{

String name;
int value;
StructureBB bb;

public StructureEntry(int x, int y, int z, int face, StructureTemplate template)
  {
  name = template.name;
  bb = new StructureBB(x, y, z, face, template.xSize, template.ySize, template.zSize, template.xOffset, template.yOffset, template.zOffset);
  value = template.getValidationSettings().getClusterValue();
  }

public StructureEntry()
  {
  bb = new StructureBB(new BlockPosition(), new BlockPosition());
  }//NBT constructor

public void writeToNBT(NBTTagCompound tag)
  {
  tag.setString("name", name);
  tag.setInteger("value", value);
  tag.setIntArray("bb", new int[]{bb.min.x, bb.min.y, bb.min.z, bb.max.x, bb.max.y, bb.max.z});
  }

public void readFromNBT(NBTTagCompound tag)
  {
  name = tag.getString("name");
  value = tag.getInteger("value");
  int[] datas = tag.getIntArray("bb");
  if(datas.length>=6)
    {
    bb.min.x = datas[0];
    bb.min.y = datas[1];
    bb.min.z = datas[2];
    bb.max.x = datas[3];
    bb.max.y = datas[4];
    bb.max.z = datas[5]; 
    }
  }

public String getName()
  {
  return name;
  }

public int getValue()
  {
  return value;
  }

public StructureBB getBB()
  {
  return bb;
  }


}
