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

import net.minecraft.block.Block;
import net.minecraft.world.World;
import shadowmage.ancient_structures.common.manager.BlockDataManager;
import shadowmage.ancient_structures.common.template.StructureTemplate;
import shadowmage.ancient_structures.common.template.build.StructureBB;

public class StructureValidatorGround extends StructureValidator
{

public StructureValidatorGround()
  {
  super(StructureValidationType.GROUND);
  }

@Override
public boolean shouldIncludeForSelection(World world, int x, int y, int z, int face, StructureTemplate template)
  {
  Block block = Block.blocksList[world.getBlockId(x, y-1, z)];
  if(block==null || !validTargetBlocks.contains(BlockDataManager.getBlockName(block))){return false;}
  return true;
  }

@Override
public boolean validatePlacement(World world, int x, int y, int z, int face, StructureTemplate template, StructureBB bb)
  {
  int minY = getMinY(template, bb);
  int maxY = getMaxY(template, bb);
  return validateBorderBlocks(world, template, bb, minY, maxY, false);
  }

@Override
public void preGeneration(World world, int x, int y, int z, int face, StructureTemplate template, StructureBB bb)
  {
  prePlacementBorder(world, template, bb);
  prePlacementUnderfill(world, template, bb);
  }

//private void doStructurePrePlacement(World world, int x, int y, int z, int face, StructureTemplate template)
//  {
//  StructureBB bb = new StructureBB(x, y, z, face, template);  
//  for(int bx = bb.min.x-borderSize; bx<= bb.max.x+borderSize; bx++)
//    {
//    for(int bz = bb.min.z-borderSize; bz<= bb.max.z+borderSize; bz++)
//      {
//      if(bx<bb.min.x || bx>bb.max.x || bz<bb.min.z || bz>bb.max.z)
//        {//is border block, do border clear/fill
//        doStructurePrePlacementBlockPlace(world, bx, bz, template, bb, true);
//        }
//      else
//        {//is structure block, do structure clear/fill
//        doStructurePrePlacementBlockPlace(world, bx, bz, template, bb, false);
//        }
//      }
//    }  
//  }

//private void doStructurePrePlacementBlockPlace(World world, int x, int z, StructureTemplate template, StructureBB bb, boolean border)
//  {
//  int leveling = maxLeveling;
//  int fill = maxFill;
//  
//  /**
//   * most of this is just to try and minimize the total Y range that is examined for clear/fill
//   */
//  int minFillY = getMinFillY(template, bb);
//  int maxFillY = getMaxFillY(template, bb);  
//  int minLevelY = getMinLevelingY(template, bb);
//  int maxLevelY = getMaxLevelingY(template, bb);
//  
//  int minY = minFillY < minLevelY ? minFillY : minLevelY;
//  if(!border)
//    {
//    if(fill>0)
//      {//for inside-structure bounds, we want to fill down to whatever is existing if fill is>0    
//      int topEmptyBlockY = WorldStructureGenerator.getTargetY(world, x, z, true)+1;
//      minY = minY< topEmptyBlockY ? minY : topEmptyBlockY;
//      }    
//    }  
//  else
//    {
//    int step = WorldStructureGenerator.getStepNumber(x, z, bb.min.x, bb.max.x, bb.min.z, bb.max.z);
//    int stepHeight = fill / borderSize;
//    maxFillY -= step*stepHeight;
//    minLevelY += step*stepHeight;
//    minY = minFillY < minLevelY ? minFillY : minLevelY;//reset minY from change to minLevelY
//    }
//  
//  minY = minY<=0 ? 1 : minY;
//  int maxY = maxFillY> maxLevelY ? maxFillY : maxLevelY;
//  
//  int xInChunk = x&15;
//  int zInChunk = z&15;  
//  Chunk chunk = world.getChunkFromBlockCoords(x, z);
//  
//  int id;
//  Block block;
//  BiomeGenBase biome = world.getBiomeGenForCoords(x, z);  
//  int fillBlockID = Block.grass.blockID;
//  if(biome!=null && biome.topBlock>=1)
//    {
//    fillBlockID = biome.topBlock;
//    }
//  for(int y = minY; y <=maxY; y++)
//    {    
//    id = world.getBlockId(x, y, z);
//    block = Block.blocksList[id];
//    if(leveling>0 && y >= minLevelY)
//      {
//      if(block!=null && !WorldStructureGenerator.skippableWorldGenBlocks.contains(BlockDataManager.getBlockName(block)) && validTargetBlocks.contains(BlockDataManager.getBlockName(block)))
//        {
//        chunk.setBlockIDWithMetadata(xInChunk, y, zInChunk, 0, 0);        
//        }
//      }
//    else if(leveling>0 && y==minLevelY-1)
//      {
//      if(block!=null && !WorldStructureGenerator.skippableWorldGenBlocks.contains(BlockDataManager.getBlockName(block)) && validTargetBlocks.contains(BlockDataManager.getBlockName(block)))
//        {
//        chunk.setBlockIDWithMetadata(xInChunk, y, zInChunk, fillBlockID, 0);        
//        }
//      }
//    if(fill>0 && y<=maxFillY)
//      {
//      if(block==null || !WorldStructureGenerator.skippableWorldGenBlocks.contains(BlockDataManager.getBlockName(block)))
//        {
//        chunk.setBlockIDWithMetadata(xInChunk, y, zInChunk, fillBlockID, 0);
//        }
//      }
//    }
//  }


@Override
public void handleClearAction(World world, int x, int y, int z, StructureTemplate template, StructureBB bb)
  {
  world.setBlock(x, y, z, 0);
  }

}
