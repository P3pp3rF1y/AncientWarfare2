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

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import shadowmage.ancient_framework.common.utils.StringTools;
import shadowmage.ancient_structures.common.template.StructureTemplate;
import shadowmage.ancient_structures.common.template.build.StructureBB;

public class StructureValidatorSky extends StructureValidator
{

int minGenerationHeight;
int maxGenerationHeight;
int minFlyingHeight;

public StructureValidatorSky()
  {
  super(StructureValidationType.SKY);
  }

@Override
public void readFromTag(NBTTagCompound tag)
  {
  super.readFromTag(tag);
  minGenerationHeight = tag.getInteger("minGenHeight");
  maxGenerationHeight = tag.getInteger("maxGenHeight");
  minFlyingHeight = tag.getInteger("minFlyingHeight");
  }

@Override
protected void readFromLines(List<String> lines)
  {
  for(String line : lines)
    {
    if(line.toLowerCase().startsWith("mingenerationheight=")){minGenerationHeight=StringTools.safeParseInt("=", line);}
    else if(line.toLowerCase().startsWith("maxgenerationheight=")){maxGenerationHeight=StringTools.safeParseInt("=", line);}
    else if(line.toLowerCase().startsWith("minflyingheight=")){minFlyingHeight=StringTools.safeParseInt("=", line);}
    }
  }

@Override
protected void write(BufferedWriter out) throws IOException
  {
  out.write("minGenerationHeight="+minGenerationHeight);
  out.newLine();
  out.write("maxGenerationHeight="+maxGenerationHeight);
  out.newLine();
  out.write("minFlyingHeight="+minFlyingHeight);
  out.newLine();
  }

@Override
protected void setDefaultSettings(StructureTemplate template)
  {

  }

@Override
public boolean shouldIncludeForSelection(World world, int x, int y, int z, int face, StructureTemplate template)
  {  
  int remainingHeight = world.provider.getActualHeight() - minFlyingHeight - (template.ySize-template.yOffset);
  return y < remainingHeight;
  }

@Override
public int getAdjustedSpawnY(World world, int x, int y, int z, int face, StructureTemplate template, StructureBB bb)
  {
  int range = maxGenerationHeight-minGenerationHeight+1;
  return y + minFlyingHeight + world.rand.nextInt(range);
  }

@Override
public boolean validatePlacement(World world, int x, int y, int z, int face, StructureTemplate template, StructureBB bb)
  {     
  int maxY = minGenerationHeight - minFlyingHeight;
  return validateBorderBlocks(world, template, bb, 0, maxY, false);
  }

@Override
public void preGeneration(World world, int x, int y, int z, int face, StructureTemplate template, StructureBB bb)
  {
  
  }

@Override
public void handleClearAction(World world, int x, int y, int z, StructureTemplate template, StructureBB bb)
  {
  world.setBlock(x, y, z, 0);
  }

}
