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
package shadowmage.ancient_structures.common.template.build.validation;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import shadowmage.ancient_framework.common.utils.StringTools;
import shadowmage.ancient_structures.common.template.StructureTemplate;
import shadowmage.ancient_structures.common.template.build.StructureBB;
import shadowmage.ancient_structures.common.world_gen.WorldStructureGenerator;

public class StructureValidatorUnderwater extends StructureValidator
{

int minWaterDepth;
int maxWaterDepth;

public StructureValidatorUnderwater()
  {
  super(StructureValidationType.UNDERWATER);
  }

@Override
public void readFromTag(NBTTagCompound tag)
  {
  super.readFromTag(tag);
  minWaterDepth = tag.getInteger("minWaterDepth");
  maxWaterDepth = tag.getInteger("maxWaterDepth");
  }

@Override
protected void readFromLines(List<String> lines)
  {
  for(String line : lines)
    {
    if(line.toLowerCase().startsWith("minwaterdepth=")){minWaterDepth = StringTools.safeParseInt("=", line);}
    else if(line.toLowerCase().startsWith("maxwaterdepth=")){maxWaterDepth = StringTools.safeParseInt("=", line);}
    }
  }

@Override
protected void write(BufferedWriter out) throws IOException
  {
  out.write("minWaterDepth="+minWaterDepth);
  out.newLine();
  out.write("minWaterDepth="+maxWaterDepth);
  out.newLine(); 
  }

@Override
public boolean shouldIncludeForSelection(World world, int x, int y, int z, int face, StructureTemplate template)
  {
  int water = 0;
  int startY = y;
  y = WorldStructureGenerator.getTargetY(world, x, z, true)+1;
  water = startY - y;
  if(water<minWaterDepth || water>maxWaterDepth)
    {  
    return false;
    }
  return true;
  }

@Override
public int getAdjustedSpawnY(World world, int x, int y, int z, int face,  StructureTemplate template, StructureBB bb)
  {
  return WorldStructureGenerator.getTargetY(world, x, z, true)+1;
  }

@Override
public boolean validatePlacement(World world, int x, int y, int z, int face,  StructureTemplate template, StructureBB bb)
  {
  int minY = getMinY(template, bb);
  int maxY = getMaxY(template, bb);
  return validateBorderBlocks(world, template, bb, minY, maxY, true);
  }

@Override
public void preGeneration(World world, int x, int y, int z, int face,  StructureTemplate template, StructureBB bb)
  {
  prePlacementBorder(world, template, bb);
  prePlacementUnderfill(world, template, bb);
  }

@Override
public void handleClearAction(World world, int x, int y, int z, StructureTemplate template, StructureBB bb)
  {
  world.setBlock(x, y, z, Block.waterStill.blockID);
  }

}
