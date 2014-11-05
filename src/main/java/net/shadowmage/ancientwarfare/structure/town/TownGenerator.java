package net.shadowmage.ancientwarfare.structure.town;

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.structure.config.AWStructureStatics;
import net.shadowmage.ancientwarfare.structure.town.TownPlacementValidator.TownBoundingArea;
import net.shadowmage.ancientwarfare.structure.town.TownTemplate.TownStructureEntry;

/**
 * Responsible for constructing the town -- leveling the area, placing the structures, constructing walls
 * @author Shadowmage
 *
 */
public class TownGenerator
{

int remainingGenerationValue;
TownTemplate template;
private HashMap<String, TownGeneratedEntry> generatedStructureMap = new HashMap<String, TownGeneratedEntry>();

public TownGenerator(TownTemplate template)
  {
  this.template = template;
  this.remainingGenerationValue = template.maxValue;
  }

public void generateAt(World world, TownBoundingArea area)
  {
  testGeneration(world, area);
//  int height = area.minY+7;
//  int minX = area.getBlockMinX();
//  int maxX = area.getBlockMaxX();
//  int minZ = area.getBlockMinZ();
//  int maxZ = area.getBlockMaxZ();
//  for(int x = minX; x <= maxX; x++)
//    {
//    for(int z = minZ; z <= maxZ; z++)
//      {
//      for(int y = height; y<=height+8; y++)
//        {
//        world.setBlockToAir(x, y, z);
//        }
//      for(int y = area.minY; y < area.minY+7; y++)
//        {
//        if(world.isAirBlock(x, y, z))
//          {
//          world.setBlock(x, y, z, Blocks.dirt);
//          }
//        }
//      }
//    }
  }

private void testGeneration(World world, TownBoundingArea area)
  {
  doBorder(world, area);
  doLeveling(world, area);
  doWall(world, area);
  }

private void doBorder(World world, TownBoundingArea area)
  {
  int fillLevel, cutLevel;
  int minX, maxX, minZ, maxZ;
  
  fillLevel = area.minY-1;
  cutLevel = area.maxY+1;
  minX = area.getBlockMinX();
  maxX = minX + 8;
  AWLog.logDebug("border xneg: "+minX+" : "+maxX);
  for(int px = minX; px < maxX; px++)
    {
    for(int pz = area.getBlockMinZ(); pz<=area.getBlockMaxZ(); pz++)
      {
      handleBorderBlock(world, px, pz, fillLevel, cutLevel);
      }
    fillLevel++;
    cutLevel--;
    }  
  
  fillLevel = area.minY-1;
  cutLevel = area.maxY+1;
  minX = area.getBlockMaxX()-7;
  maxX = area.getBlockMaxX();  
  AWLog.logDebug("border xpos: "+minX+" : "+maxX);
  for(int px = area.getBlockMaxX(); px >= minX; px--)
    {
    for(int pz = area.getBlockMinZ(); pz<=area.getBlockMaxZ(); pz++)
      {
      handleBorderBlock(world, px, pz, fillLevel, cutLevel);
      }
    fillLevel++;
    cutLevel--;
    }  
  
  fillLevel = area.minY-1;
  cutLevel = area.maxY+1;
  minZ = area.getBlockMinZ();
  maxZ = minZ + 8;
  AWLog.logDebug("border zneg: "+minZ+" : "+maxZ);
  for(int pz = minZ; pz < maxZ; pz++)
    {
    for(int px = area.getBlockMinX(); px<=area.getBlockMaxX(); px++)
      {
      handleBorderBlock(world, px, pz, fillLevel, cutLevel);
      }
    fillLevel++;
    cutLevel--;
    }
  
  fillLevel = area.minY-1;
  cutLevel = area.maxY+1;
  minZ = area.getBlockMaxZ()-7;
  maxZ = area.getBlockMaxZ();
  AWLog.logDebug("border zpos: "+minZ+" : "+maxZ);
  for(int pz = maxZ; pz >= minZ; pz--)
    {
    for(int px = area.getBlockMinX(); px<=area.getBlockMaxX(); px++)
      {
      handleBorderBlock(world, px, pz, fillLevel, cutLevel);
      }
    fillLevel++;
    cutLevel--;
    }  
  }

private void handleBorderBlock(World world, int x, int z, int fillLevel, int cutLevel)
  {
  int y = getTopFilledHeight(world.getChunkFromBlockCoords(x, z), x&15, z&15);  
  if(y > cutLevel)
    {
    for(int py = y; y >= cutLevel; y--){world.setBlockToAir(x, py, z);}
    }
  else if(y < fillLevel)
    {
    for(int py = y+1; py <= fillLevel; py++){world.setBlock(x, py, z, Blocks.dirt);}
    }
  }

private void doLeveling(World world, TownBoundingArea area)
  {
  int minX = area.getBlockMinX()+8;
  int minZ = area.getBlockMinZ()+8;
  int maxX = area.getBlockMaxX()-8;
  int maxZ = area.getBlockMaxZ()-8;
  
  int desiredTopBlockHeight = area.minY + 7;
  int height;
  for(int x = minX; x<=maxX; x++)
    {
    for(int z = minZ; z<=maxZ; z++)
      {
      height = getTopFilledHeight(world.getChunkFromBlockCoords(x, z), x&15, z&15);
      if(height<desiredTopBlockHeight)
        {
        for(int y = height; y<=desiredTopBlockHeight; y++){world.setBlock(x, y, z, Blocks.dirt);}
        }
      else if(height>desiredTopBlockHeight)
        {
        for(int y = height; y>desiredTopBlockHeight; y--){world.setBlockToAir(x, y, z);}
        }
      }
    }
  }

private void doWall(World world, TownBoundingArea area)
  {
  
  }

private int getTopFilledHeight(Chunk chunk, int xInChunk, int zInChunk)
  {
  int maxY = chunk.getTopFilledSegment()+15;
  Block block;
  for(int y = maxY; y>0; y--)
    {
    block = chunk.getBlock(xInChunk, y, zInChunk);
    if(block==null || block==Blocks.air || AWStructureStatics.skippableBlocksContains(block) || block.getMaterial()==Material.water){continue;}
    return y;
    }
  return -1;
  }

public static final class TownGeneratedEntry
{
int numGenerated;
TownStructureEntry generatedEntry;
}

}
