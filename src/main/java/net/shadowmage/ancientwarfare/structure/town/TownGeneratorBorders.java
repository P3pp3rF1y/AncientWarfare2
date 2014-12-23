package net.shadowmage.ancientwarfare.structure.town;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.shadowmage.ancientwarfare.structure.config.AWStructureStatics;
import net.shadowmage.ancientwarfare.structure.world_gen.WorldStructureGenerator;

public class TownGeneratorBorders
{

public static void generateBorders(World world, TownGenerator gen)  
  {
  int minX, maxX, minZ, maxZ;  
  int step;
  int fillBase = gen.maximalBounds.min.y - 1;
  int levelBase = fillBase;
  
  int eminx = gen.exteriorBounds.min.x;
  int eminz = gen.exteriorBounds.min.z;
  int emaxx = gen.exteriorBounds.max.x;
  int emaxz = gen.exteriorBounds.max.z;
  
  minX = gen.maximalBounds.min.x;
  maxX = gen.wallsBounds.min.x - 1;
  for(int px = minX; px <= maxX; px++)
    {    
    for(int pz = gen.maximalBounds.min.z; pz <= gen.maximalBounds.max.z; pz++)
      {
      step = WorldStructureGenerator.getStepNumber(px, pz, eminx, emaxx, eminz, emaxz);
      handleBorderBlock(world, px, pz, fillBase-step, levelBase+step, getFillBlock(world, px, pz, false, Blocks.dirt), getFillBlock(world, px, pz, true, Blocks.grass), true);
      }
    }  
  
  minX = gen.wallsBounds.max.x + 1;
  maxX = gen.maximalBounds.max.x;  
  for(int px = minX; px <= maxX; px++)
    {    
    for(int pz = gen.maximalBounds.min.z; pz <= gen.maximalBounds.max.z; pz++)
      {
      step = WorldStructureGenerator.getStepNumber(px, pz, eminx, emaxx, eminz, emaxz);
      handleBorderBlock(world, px, pz, fillBase-step, levelBase+step, getFillBlock(world, px, pz, false, Blocks.dirt), getFillBlock(world, px, pz, true, Blocks.grass), true);
      }
    } 
  
  minZ = gen.maximalBounds.min.z;
  maxZ = gen.wallsBounds.min.z - 1;
  for(int pz = minZ; pz <= maxZ; pz++)
    {
    for(int px = gen.maximalBounds.min.x; px <= gen.maximalBounds.max.x; px++)
      {
      step = WorldStructureGenerator.getStepNumber(px, pz, eminx, emaxx, eminz, emaxz);
      handleBorderBlock(world, px, pz, fillBase-step, levelBase+step, getFillBlock(world, px, pz, false, Blocks.dirt), getFillBlock(world, px, pz, true, Blocks.grass), true);
      }
    }
  
  minZ = gen.wallsBounds.max.z + 1;
  maxZ = gen.maximalBounds.max.z;
  for(int pz = minZ; pz <= maxZ; pz++)
    {
    for(int px = gen.maximalBounds.min.x; px <= gen.maximalBounds.max.x; px++)
      {
      step = WorldStructureGenerator.getStepNumber(px, pz, eminx, emaxx, eminz, emaxz);
      handleBorderBlock(world, px, pz, fillBase-step, levelBase+step, getFillBlock(world, px, pz, false, Blocks.dirt), getFillBlock(world, px, pz, true, Blocks.grass), true);
      }
    }  
  }

public static void levelTownArea(World world, TownGenerator gen)
  {
  int minX = gen.wallsBounds.min.x;
  int minZ = gen.wallsBounds.min.z;
  int maxX = gen.wallsBounds.max.x;
  int maxZ = gen.wallsBounds.max.z; 
  int desiredTopBlockHeight = gen.wallsBounds.min.y-1;
  for(int x = minX; x<=maxX; x++)
    {
    for(int z = minZ; z<=maxZ; z++)
      {
      handleBorderBlock(world, x, z, desiredTopBlockHeight, desiredTopBlockHeight, getFillBlock(world, x, z, false, Blocks.grass), getFillBlock(world, x, z, true, Blocks.grass), false);
      world.setBlock(x, desiredTopBlockHeight-5, z, Blocks.cobblestone);
      }
    }
  }

private static void handleBorderBlock(World world, int x, int z, int fillLevel, int cutLevel, Block fillBlock, Block topBlock, boolean skippables)
  {  
  int y = getTopFilledHeight(world.getChunkFromBlockCoords(x, z), x&15, z&15, skippables);
  if(y >= cutLevel)
    {
    for(int py = world.provider.getActualHeight(); py > cutLevel; py--){world.setBlockToAir(x, py, z);}
    world.setBlock(x, cutLevel, z, topBlock);
    }
  if(y <= fillLevel)
    {
    for(int py = y+1; py < fillLevel; py++)
      {
      world.setBlock(x, py, z, fillBlock);
      }
    world.setBlock(x, fillLevel, z, topBlock);
    }
  }

private static int getTopFilledHeight(Chunk chunk, int xInChunk, int zInChunk, boolean skippables)
  {
  int maxY = chunk.getTopFilledSegment() + 16;
  Block block;
  for(int y = maxY; y > 0; y--)
    {
    block = chunk.getBlock(xInChunk, y, zInChunk);
    if(block==null || block==Blocks.air || (skippables && AWStructureStatics.skippableBlocksContains(block)) || block.getMaterial()==Material.water){continue;}
    return y;
    }
  return -1;
  }

private static Block getFillBlock(World world, int x, int z, boolean surface, Block defaultBlock)
  {
  Block block = defaultBlock;
  BiomeGenBase biome = world.getBiomeGenForCoordsBody(x, z);
  if(biome!=null && biome.topBlock!=null)
    {
    if(surface && biome.topBlock!=null){block = biome.topBlock;}
    else if(!surface && biome.fillerBlock!=null){block = biome.fillerBlock;}
    }
  return block;
  }

}
