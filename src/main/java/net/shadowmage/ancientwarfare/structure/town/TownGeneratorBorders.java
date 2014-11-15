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

public static void generateBorders(World world, TownBoundingArea area)  
  {
  int minX, maxX, minZ, maxZ;  
  int step;
  int fillBase = area.getSurfaceY();
  int levelBase = fillBase;
  
  minX = area.getBlockMinX();
  maxX = area.getWallMinX()-1;
  for(int px = minX; px <= maxX; px++)
    {    
    for(int pz = area.getBlockMinZ(); pz<=area.getBlockMaxZ(); pz++)
      {
      step = WorldStructureGenerator.getStepNumber(px, pz, area.getWallMinX(), area.getWallMaxX(), area.getWallMinZ(), area.getWallMaxZ());
      handleBorderBlock(world, px, pz, fillBase-step, levelBase+step, getFillBlock(world, px, pz, false, Blocks.dirt), getFillBlock(world, px, pz, true, Blocks.grass));
      }
    }  
  
  minX = area.getWallMaxX()+1;
  maxX = area.getBlockMaxX();  
  for(int px = minX; px <= maxX; px++)
    {    
    for(int pz = area.getBlockMinZ(); pz<=area.getBlockMaxZ(); pz++)
      {
      step = WorldStructureGenerator.getStepNumber(px, pz, area.getWallMinX(), area.getWallMaxX(), area.getWallMinZ(), area.getWallMaxZ()); 
      handleBorderBlock(world, px, pz, fillBase-step, levelBase+step, getFillBlock(world, px, pz, false, Blocks.dirt), getFillBlock(world, px, pz, true, Blocks.grass));
      }
    } 
  
  minZ = area.getBlockMinZ();
  maxZ = area.getWallMinZ()-1;
  for(int pz = minZ; pz <= maxZ; pz++)
    {
    for(int px = area.getBlockMinX(); px<=area.getBlockMaxX(); px++)
      {
      step = WorldStructureGenerator.getStepNumber(px, pz, area.getWallMinX(), area.getWallMaxX(), area.getWallMinZ(), area.getWallMaxZ());
      handleBorderBlock(world, px, pz, fillBase-step, levelBase+step, getFillBlock(world, px, pz, false, Blocks.dirt), getFillBlock(world, px, pz, true, Blocks.grass));
      }
    }
  
  minZ = area.getWallMaxZ()+1;
  maxZ = area.getBlockMaxZ();
  for(int pz = minZ; pz <= maxZ; pz++)
    {
    for(int px = area.getBlockMinX(); px<=area.getBlockMaxX(); px++)
      {
      step = WorldStructureGenerator.getStepNumber(px, pz, area.getWallMinX(), area.getWallMaxX(), area.getWallMinZ(), area.getWallMaxZ());
      handleBorderBlock(world, px, pz, fillBase-step, levelBase+step, getFillBlock(world, px, pz, false, Blocks.dirt), getFillBlock(world, px, pz, true, Blocks.grass));
      }
    }  
  }

public static void levelTownArea(World world, TownBoundingArea area)
  {
  int minX = area.getWallMinX();
  int minZ = area.getWallMinZ();
  int maxX = area.getWallMaxX();
  int maxZ = area.getWallMaxZ();  
  int desiredTopBlockHeight = area.getSurfaceY();
  for(int x = minX; x<=maxX; x++)
    {
    for(int z = minZ; z<=maxZ; z++)
      {
      handleBorderBlock(world, x, z, desiredTopBlockHeight, desiredTopBlockHeight, getFillBlock(world, x, z, false, Blocks.grass), getFillBlock(world, x, z, true, Blocks.grass));
      }
    }
  }

private static void handleBorderBlock(World world, int x, int z, int fillLevel, int cutLevel, Block fillBlock, Block topBlock)
  {  
  int y = getTopFilledHeight(world.getChunkFromBlockCoords(x, z), x&15, z&15, true);
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
