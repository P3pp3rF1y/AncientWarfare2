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

import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import shadowmage.ancient_framework.common.utils.BlockPosition;
import shadowmage.ancient_structures.common.manager.BlockDataManager;
import shadowmage.ancient_structures.common.template.StructureTemplate;
import shadowmage.ancient_structures.common.template.build.StructureBB;
import shadowmage.ancient_structures.common.world_gen.WorldStructureGenerator;

public class StructureValidatorHarbor extends StructureValidator
{

BlockPosition testMin = new BlockPosition();
BlockPosition testMax = new BlockPosition();

Set<String> validTargetBlocks;
Set<String> validTargetBlocksSide;
Set<String> validTargetBlocksRear;

public StructureValidatorHarbor()
  {
  super(StructureValidationType.HARBOR);
  validTargetBlocks = new HashSet<String>();
  validTargetBlocksSide = new HashSet<String>();
  validTargetBlocksRear = new HashSet<String>();
  validTargetBlocks.addAll(WorldStructureGenerator.defaultTargetBlocks);
  validTargetBlocksSide.addAll(WorldStructureGenerator.defaultTargetBlocks);
  validTargetBlocksRear.add(BlockDataManager.getBlockName(Block.waterStill));  
  validTargetBlocksRear.add(BlockDataManager.getBlockName(Block.waterMoving));
  validTargetBlocksSide.add(BlockDataManager.getBlockName(Block.waterMoving));
  validTargetBlocksSide.add(BlockDataManager.getBlockName(Block.waterStill));
  }

@Override
protected void setDefaultSettings(StructureTemplate template)
  {
  
  }

@Override
public boolean shouldIncludeForSelection(World world, int x, int y, int z, int face, StructureTemplate template)
  {
  /**
   * testing that front target position is valid block
   * then test back target position to ensure that it has water at same level
   * or at an acceptable level difference
   */
  Block block = Block.blocksList[world.getBlockId(x, y-1, z)];  
  if(block!=null && validTargetBlocks.contains(BlockDataManager.getBlockName(block)))
    {
    testMin.reassign(x, y, z);
    testMin.moveForward(face, template.zOffset);
    int by = WorldStructureGenerator.getTargetY(world, testMin.x, testMin.z, false);
    if(y - by >maxFill)
      {
      return false;
      }
    block = Block.blocksList[world.getBlockId(testMin.x, by, testMin.z)];
    if(block==Block.waterStill || block==Block.waterMoving)
      {
      return true;
      }
    }
  return false;
  }

@Override
public int getAdjustedSpawnY(World world, int x, int y, int z, int face, StructureTemplate template, StructureBB bb)
  {
  testMin.reassign(x, y, z);
  testMin.moveForward(face, template.zOffset);
  return WorldStructureGenerator.getTargetY(world, testMin.x, testMin.z, false)+1;
  }

@Override
public boolean validatePlacement(World world, int x, int y, int z, int face,  StructureTemplate template, StructureBB bb)
  { 
  int bx, bz;
  
  int minY = getMinY(template, bb);
  int maxY = getMaxY(template, bb);
  
  bb.getFrontCorners(face, testMin, testMax);
  for(bx = testMin.x; bx<=testMax.x; bx++)
    {
    for(bz = testMin.z; bz<=testMax.z; bz++)
      {      
      if(!validateBlockHeightAndType(world, bx, bz, minY, maxY, false, validTargetBlocks))
        {
        return false;
        }
      }
    }
  
  bb.getRearCorners(face, testMin, testMax);
  for(bx = testMin.x; bx<=testMax.x; bx++)
    {
    for(bz = testMin.z; bz<=testMax.z; bz++)
      {      
      if(!validateBlockHeightAndType(world, bx, bz, minY, maxY, false, validTargetBlocksRear))
        {
        return false;
        }
      }
    }
  
  bb.getRightCorners(face, testMin, testMax);
  for(bx = testMin.x; bx<=testMax.x; bx++)
    {
    for(bz = testMin.z; bz<=testMax.z; bz++)
      {      
      if(!validateBlockHeightAndType(world, bx, bz, minY, maxY, false, validTargetBlocksSide))
        {
        return false;
        }
      }
    }
  
  bb.getLeftCorners(face, testMin, testMax);
  for(bx = testMin.x; bx<=testMax.x; bx++)
    {
    for(bz = testMin.z; bz<=testMax.z; bz++)
      {      
      if(!validateBlockHeightAndType(world, bx, bz, minY, maxY, false, validTargetBlocksSide))
        {
        return false;
        }
      }
    }
  
   
  return true;
  }

@Override
public void preGeneration(World world, int x, int y, int z, int face, StructureTemplate template, StructureBB bb)
  {
  prePlacementBorder(world, template, bb);
  }

@Override
public void handleClearAction(World world, int x, int y, int z, StructureTemplate template, StructureBB bb)
  {
  if(y>=bb.min.y+template.yOffset)
    {
    world.setBlock(x, y, z, 0);    
    }
  else
    {
    world.setBlock(x, y, z, Block.waterStill.blockID);
    }
  }

}
