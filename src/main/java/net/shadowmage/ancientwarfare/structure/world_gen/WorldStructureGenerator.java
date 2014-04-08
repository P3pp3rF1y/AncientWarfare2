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
package net.shadowmage.ancientwarfare.structure.world_gen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.config.Statics;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.structure.block.BlockDataManager;
import net.shadowmage.ancientwarfare.structure.config.AWStructureStatics;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.WorldGenStructureManager;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;
import net.shadowmage.ancientwarfare.structure.template.build.validation.StructureBuilderWorldGen;
import cpw.mods.fml.common.IWorldGenerator;

public class WorldStructureGenerator implements IWorldGenerator
{

private static WorldStructureGenerator instance = new WorldStructureGenerator();
private WorldStructureGenerator(){}
public static WorldStructureGenerator instance(){return instance;}

public static HashSet<String> defaultTargetBlocks = new HashSet<String>();

static
{


defaultTargetBlocks.add(BlockDataManager.instance().getNameForBlock(Blocks.dirt));
defaultTargetBlocks.add(BlockDataManager.instance().getNameForBlock(Blocks.grass));
defaultTargetBlocks.add(BlockDataManager.instance().getNameForBlock(Blocks.stone));
defaultTargetBlocks.add(BlockDataManager.instance().getNameForBlock(Blocks.sand));
defaultTargetBlocks.add(BlockDataManager.instance().getNameForBlock(Blocks.gravel));
defaultTargetBlocks.add(BlockDataManager.instance().getNameForBlock(Blocks.sandstone));
defaultTargetBlocks.add(BlockDataManager.instance().getNameForBlock(Blocks.clay));
defaultTargetBlocks.add(BlockDataManager.instance().getNameForBlock(Blocks.iron_ore));
defaultTargetBlocks.add(BlockDataManager.instance().getNameForBlock(Blocks.coal_ore));
}


private boolean isGenerating = false;
private LinkedList<DelayedGenerationEntry> delayedChunks = new LinkedList<DelayedGenerationEntry>();
private Random rng = new Random();

@Override
public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider)
  {
  ChunkCoordinates cc = world.getSpawnPoint();
  float distSq = cc.getDistanceSquared(chunkX*16, 70, chunkZ*16);
  if(distSq < (AWStructureStatics.spawnProtectionRange*16)*(AWStructureStatics.spawnProtectionRange*16))
    {
    return;
    }
  int rand = rng.nextInt(AWStructureStatics.randomRange);
  if(rand>AWStructureStatics.randomChance)
    {
    return;
    }
  if(!AWStructureStatics.enableStructureGeneration){return;}
//  if(rng.nextInt(10)>0){return;}
  if(isGenerating)
    {
    delayedChunks.add(new DelayedGenerationEntry(chunkX, chunkZ, world, chunkGenerator, chunkProvider));
    return;
    }
  else
    {
    isGenerating = true;
    generateAt(chunkX, chunkZ, world, chunkGenerator, chunkProvider);
    }
  while(!delayedChunks.isEmpty())
    {    
    DelayedGenerationEntry entry = delayedChunks.poll();
    generateAt(entry.chunkX, entry.chunkZ, entry.world, entry.generator, entry.provider);
    }
  isGenerating = false;
  }

private void generateAt(int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider)
  {
  long t1 = System.currentTimeMillis();
  long seed = (((long)chunkX)<< 32) | (((long)chunkZ) & 0xffffffffl);
  rng.setSeed(seed);
  int x = chunkX*16 + rng.nextInt(16);  
  int z = chunkZ*16 + rng.nextInt(16); 
  int y = getTargetY(world, x, z, false)+1;  
  if(y<=0){return;}
  int face = rng.nextInt(4);
  StructureTemplate template = WorldGenStructureManager.instance().selectTemplateForGeneration(world, rng, x, y, z, face, AWStructureStatics.chunkSearchRadius);  
  int remainingClusterValue = WorldGenStructureManager.instance().getRemainingValue();//TODO use this to alter the random chance/range values to favor generating in clusters  
  if(Statics.DEBUG)
    {
    AWLog.logError("Template selection took: "+(System.currentTimeMillis()-t1)+" ms.");
    }
  if(template==null){return;} 
  StructureMap map = AWGameData.INSTANCE.getData("AWStructureMap", world, StructureMap.class);
  if(attemptStructureGenerationAt(world, x, y, z, face, template, map))
    {
    AWLog.log(String.format("Generated structure: %s at %s, %s, %s", template.name, x, y, z));
    } 
  }

public static int getTargetY(World world, int x, int z, boolean skipWater)
  {  
  Block block;
  for(int y = world.provider.getActualHeight(); y >0 ; y--)
    {
    block = world.getBlock(x, y, z);
    if(block==null){continue;}
    if(AWStructureStatics.skippableBlocksContains(BlockDataManager.instance().getNameForBlock(block))){continue;}
    if(skipWater && (block==Blocks.water || block==Blocks.flowing_water)){continue;}
    return y;
    }
  return -1;
  }

public static int getStepNumber(int x, int z, int minX, int maxX, int minZ, int maxZ)
  {
  int steps = 0;
  if(x<minX-1)
    {
    steps += (minX-1) - x;
    }
  else if(x > maxX+1)
    {
    steps += x - (maxX+1);
    }  
  if(z<minZ-1)
    {
    steps += (minZ-1) - z;
    }
  else if(z > maxZ+1)
    {
    steps += z - (maxZ+1);
    }
  return steps;
  }

public boolean attemptStructureGenerationAt(World world, int x, int y, int z, int face, StructureTemplate template, StructureMap map)
  {
  boolean generate = false;
  long t1, t2;
  t1 = System.currentTimeMillis();
  int prevY = y;
  StructureBB bb = new StructureBB(x, y, z, face, template.xSize, template.ySize, template.zSize, template.xOffset, template.yOffset, template.zOffset);
  y = template.getValidationSettings().getAdjustedSpawnY(world, x, y, z, face, template, bb);
  bb.min.y -= prevY-y;
  bb.max.y -= prevY-y;
  int xs = bb.getXSize();
  int zs = bb.getZSize();
  int size = ((xs > zs ? xs : zs)/16)+3;
  Collection<StructureEntry> bbCheckList = map.getEntriesNear(world, x, z, size, true, new ArrayList<StructureEntry>());  
  for(StructureEntry entry : bbCheckList)
    {
    if(bb.collidesWith(entry.getBB()))
      {
      return false;
      }
    }  
  generate = template.getValidationSettings().validatePlacement(world, x, y, z, face, template, bb);
  if(Statics.DEBUG)
    {
    AWLog.logError("validation took: "+(System.currentTimeMillis()-t1+" ms"));   
    }
  if(generate)
    {    
    t2 = System.currentTimeMillis();
    generateStructureAt(world, x, y, z, face, template, map, bb);
    if(Statics.DEBUG)
      {
      AWLog.logError("generation took: "+(System.currentTimeMillis()-t2)+" ms");      
      }
    }  
  return generate;
  }

private void generateStructureAt(World world, int x, int y, int z, int face, StructureTemplate template, StructureMap map, StructureBB bb)
  {
  StructureBuilderWorldGen builder = new StructureBuilderWorldGen(world, template, face, x, y, z);
  builder.instantConstruction();
  map.setGeneratedAt(world, x, y, z, face, template);
  map.markDirty();
  }

private class DelayedGenerationEntry
{
int chunkX;
int chunkZ;
World world;
IChunkProvider generator;
IChunkProvider provider;

public DelayedGenerationEntry(int cx, int cz, World world, IChunkProvider gen, IChunkProvider prov)
  {
  this.chunkX = cx;
  this.chunkZ = cz;
  this.world = world;
  this.generator = gen;
  this.provider = prov;
  }
}

}
