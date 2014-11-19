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
package net.shadowmage.ancientwarfare.structure.template.build;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.structure.api.IStructureBuilder;
import net.shadowmage.ancientwarfare.structure.api.TemplateRule;
import net.shadowmage.ancientwarfare.structure.api.TemplateRuleEntity;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;

public class StructureBuilder implements IStructureBuilder
{

protected StructureTemplate template;
protected World world;
protected BlockPosition buildOrigin;
protected int buildFace;
protected int turns;
protected int maxPriority = 4;
protected int currentPriority;//current build priority...may not be needed anymore?
protected int currentX, currentY, currentZ;//coords in template
protected int destXSize, destYSize, destZSize;
protected BlockPosition destination;

protected StructureBB bb;

private boolean isFinished = false;

public StructureBuilder(World world, StructureTemplate template, int face, int x, int y, int z)
  {
  this(world, template, face, new BlockPosition(x,y,z), new StructureBB(x, y, z, face, template)); 
  }

public StructureBuilder(World world, StructureTemplate template, int face, BlockPosition buildKey, StructureBB bb)
  {
  this.world = world;
  this.template = template;
  this.buildFace = face;
  this.bb = bb;
  buildOrigin = buildKey;
  destination = new BlockPosition();   
  currentX = currentY = currentZ = 0;
  destXSize = template.xSize;
  destYSize = template.ySize;
  destZSize = template.zSize;
  currentPriority = 0;
  
  turns = ((face+2)%4);
  int swap;
  for(int i = 0; i<turns; i++)
    {
    swap = destXSize;
    destXSize = destZSize;
    destZSize = swap;
    }
  /**
   * initialize the first target destination so that the structure is ready to start building when called on to build
   */
  incrementDestination();
  }

protected StructureBuilder()
  {
  destination = new BlockPosition();
  buildOrigin = new BlockPosition();
  }

public void instantConstruction()
  {
  try
    {
    while(!this.isFinished())
      {
      this.placeCurrentPosition();
      }
    }
  catch(Exception e)
    {
    TemplateRule rule = template.getRuleAt(currentX, currentY, currentZ);
    throw new RuntimeException("Caught exception while constructing template blocks: "+rule, e);
    }
  this.placeEntities();    
  }

protected void placeEntities()
  {   
  TemplateRuleEntity[] rules = template.getEntityRules();
  for(TemplateRuleEntity rule : rules)
    {
    if(rule==null){continue;}
    destination.x = rule.x;
    destination.y = rule.y;
    destination.z = rule.z;
    BlockTools.rotateInArea(destination, template.xSize, template.zSize, turns);
    destination.offsetBy(bb.min);
    try
      {
      rule.handlePlacement(world, turns, destination.x, destination.y, destination.z, this);
      }
    catch (StructureBuildingException e)
      {
      e.printStackTrace();
      }
    }
  }

/**
 * should be called by template-rules to handle block-placement in the world.
 * Handles village-block swapping during world-gen, and chunk-insert for blocks
 * with priority > 0
 * @param x
 * @param y
 * @param z
 * @param block
 * @param meta
 * @param priority
 */
@Override
public void placeBlock(int x, int y, int z, Block block, int meta, int priority)
  {
  if(y<=0 || y>=256){return;}
  if(priority==0)
    {
    world.setBlock(x, y, z, block, meta, 2);//using flag=2 -- no block update, but send still send to clients (should help with issues of things popping off)
    }
  else
    {    
    Chunk chunk = world.getChunkFromBlockCoords(x, z);
    int cx = x&15; //bitwise-and to scrub all bits above 15
    int cz = z&15; //bitwise-and to scrub all bits above 15
    ExtendedBlockStorage[] st = chunk.getBlockStorageArray();
    ExtendedBlockStorage stc = st[y>>4];    
    if (stc == null)
      {
      stc = st[y >> 4] = new ExtendedBlockStorage(y >> 4 << 4, !world.provider.hasNoSky);
      }
    world.removeTileEntity(x, y, z);
    stc.func_150818_a(cx, y&15, cz, block);
    stc.setExtBlockMetadata(cx, y&15, cz, meta);
    if(block.hasTileEntity(meta))
      {
      TileEntity te = block.createTileEntity(world, meta);
      chunk.func_150812_a(x & 15, y, z & 15, te);//set TE in chunk data
      world.addTileEntity(te);//add TE to world added/loaded TE list
      }
    world.markBlockForUpdate(x, y, z);       
    }
  }

protected void placeCurrentPosition()
  {
  TemplateRule rule = template.getRuleAt(currentX, currentY, currentZ);
  if(rule!=null)
    {
    placeRule(rule);
    }
  else
    {
    placeAir();
    }
  if(incrementPosition())
    {
    incrementDestination();
    }
  else
    {
    this.isFinished = true;
    }
  }

protected void placeAir()
  {
  if(!template.getValidationSettings().isPreserveBlocks())
    {
    template.getValidationSettings().handleClearAction(world, destination.x, destination.y, destination.z, template, bb);    
    }
  }

protected void placeRule(TemplateRule rule)
  {  
  if(destination.y<=0){return;}
  if(rule.shouldPlaceOnBuildPass(world, turns, destination.x, destination.y, destination.z, currentPriority))
    {
    try
      {
      rule.handlePlacement(world, turns, destination.x, destination.y, destination.z, this);
      }
    catch (StructureBuildingException e)
      {
      e.printStackTrace();
      }    
    }
  }

protected void incrementDestination()
  {
  destination.reassign(currentX, currentY, currentZ);
  BlockTools.rotateInArea(destination, template.xSize, template.zSize, turns);
  destination.offsetBy(bb.min);
  }

/**
 * return true if could increment position
 * return false if template is finished
 * @return
 */
protected boolean incrementPosition()
  {
  currentX++;
  if(currentX>=template.xSize)
    {
    currentX = 0;
    currentZ++;
    if(currentZ>=template.zSize)
      {
      currentZ = 0;
      currentY++;
      if(currentY>=template.ySize)
        {
        currentY = 0;
        currentPriority++;
        if(currentPriority>maxPriority)
          {
          currentPriority = 0;
          return false;
          }
        }
      }
    }
  return true;
  }

public boolean isFinished()
  {
  return isFinished;
  }

public float getPercentDoneWithPass()
  {
  float max = template.xSize * template.zSize * template.ySize;
  float current = currentY * (template.xSize * template.zSize);//add layers done
  current += currentZ * template.xSize;//add rows done
  current += currentX;//add blocks done
  return current/max;
  }

public int getPass()
  {
  return currentPriority;
  }

public int getMaxPasses()
  {
  return maxPriority;
  }

}
