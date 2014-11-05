package net.shadowmage.ancientwarfare.structure.town;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.structure.config.AWStructureStatics;

public class TownPlacementValidator
{

/**
 * input a single X, Y, Z coordinate to examine the nearby area for potential town generation.<br>
 * First examines the chunk that was at the input coordinates, and expands out from there attempting
 * to create the largest town-bounding area that it can.
 * 
 * @param world
 * @param x
 * @param y
 * @param z
 * @return bounding area for the town, or null if no acceptable area was found starting in the specified chunk
 */
public static TownBoundingArea findGenerationPosition(World world, int x, int y, int z)
  {     
  int cx = x >> 4;
  int cz = z >> 4;
   
  int height = getTopFilledHeight(world.getChunkFromChunkCoords(cx, cz), x & 15, z & 15);
  AWLog.logDebug("Found initial avg height of: "+height);
  if(height<=0){return null;}
  
  TownBoundingArea area = new TownBoundingArea();
  area.minY = Math.max(0, height-8);
  area.maxY = Math.min(256, height+7);
  area.chunkMinX = cx;
  area.chunkMaxX = cx;
  area.chunkMinZ = cz;
  area.chunkMaxZ = cz;  
  expandBoundingArea(world, area);
    
  if(area.getChunkWidth()>=1 && area.getChunkLength()>=1)
    {
    return area;
    }
  return null;
  }

private static void expandBoundingArea(World world, TownBoundingArea area)
  {
  boolean xneg = true, xpos = true, zneg = true, zpos = true;//if should try and expand on this direction next pass, once set to false it never checks that direction again
  boolean didExpand = false;//set to true if any expansion occurred on that pass.  if false at end of pass, will break out of loop as no more expansion is possible
  do
    {
    didExpand = false;
    if(xneg && area.getChunkWidth() <= 2)
      {
      xneg = tryExpandXNeg(world, area);
      didExpand = didExpand || xneg; 
      }
    if(xpos && area.getChunkWidth() <= 2)
      {
      xpos = tryExpandXPos(world, area);  
      didExpand = didExpand || xpos;
      } 
    if(zneg && area.getChunkLength() <= 2)
      {
      zneg = tryExpandZNeg(world, area);
      didExpand = didExpand || zneg;
      } 
    if(zpos &&area.getChunkLength() <= 2)
      {
      zpos = tryExpandZPos(world, area);
      didExpand = didExpand || zpos;
      }  
    }
  while(didExpand && (area.getChunkWidth() <= 2 || area.getChunkLength() <= 2));
  }

private static boolean tryExpandXNeg(World world, TownBoundingArea area)
  {
  AWLog.logDebug("attempting expand x-");
  int cx = area.chunkMinX - 1;
  int minZ = area.chunkMinZ;
  int maxZ = area.chunkMaxZ;
  boolean valid = true;
  int top = 0;
  for(int z = minZ; z <= maxZ; z++)
    {
    top = findAverageTopHeight(world, cx, z);
    if(top < area.minY || top > area.maxY)
      {
      valid = false;
      break;
      }
    }
  if(valid)
    {
    AWLog.logDebug("exp x-");
    area.chunkMinX = cx;
    }
  return valid;
  }

private static boolean tryExpandXPos(World world, TownBoundingArea area)
  {
  AWLog.logDebug("attempting expand x+");
  int cx = area.chunkMaxX + 1;
  int minZ = area.chunkMinZ;
  int maxZ = area.chunkMaxZ;
  boolean valid = true;
  int top = 0;
  for(int z = minZ; z <= maxZ; z++)
    {
    top = findAverageTopHeight(world, cx, z);
    if(top < area.minY || top > area.maxY)
      {
      valid = false;
      break;
      }
    }
  if(valid)
    {
    AWLog.logDebug("exp x+");
    area.chunkMaxX = cx;
    }
  return valid;
  }

private static boolean tryExpandZNeg(World world, TownBoundingArea area)
  {
  AWLog.logDebug("attempting expand z-");
  int cz = area.chunkMinZ - 1;
  int minX = area.chunkMinX;
  int maxX = area.chunkMaxX;
  boolean valid = true;
  int top = 0;
  for(int x = minX; x <= maxX; x++)
    {
    top = findAverageTopHeight(world, x, cz);
    if(top < area.minY || top > area.maxY)
      {
      valid = false;
      break;
      }
    }
  if(valid)
    {
    AWLog.logDebug("exp z-");
    area.chunkMinZ = cz;
    }
  return valid;
  }

private static boolean tryExpandZPos(World world, TownBoundingArea area)
  {
  AWLog.logDebug("attempting expand z+");
  int cz = area.chunkMaxZ + 1;
  int minX = area.chunkMinX;
  int maxX = area.chunkMaxX;
  boolean valid = true;
  int top = 0;
  for(int x = minX; x <= maxX; x++)
    {
    top = findAverageTopHeight(world, x, cz);
    if(top < area.minY || top > area.maxY)
      {
      valid = false;
      break;
      }
    }
  if(valid)
    {
    AWLog.logDebug("exp z+");
    area.chunkMaxZ = cz;
    }
  return valid;
  }

private static int findAverageTopHeight(World world, int cx, int cz)
  {
  Chunk chunk = world.getChunkFromChunkCoords(cx, cz);
  int total = 0;
  int val = 0;
  for(int x = 0; x<16; x++)
    {
    for(int z = 0; z<16; z++)
      {
      val = getTopFilledHeight(chunk, x, z);
      if(val >= 0)
        {
        total += val;        
        }
      else
        {
        //invalid chunk, return -1??
        return -1;
        }
      }
    }
  total /= 256;
  return total;
  }

private static int getTopFilledHeight(Chunk chunk, int xInChunk, int zInChunk)
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

public static final class TownBoundingArea
{

int chunkMinX;
int chunkMaxX;
int chunkMinZ;
int chunkMaxZ;
int minY;
int maxY;

public int getChunkWidth(){return (chunkMaxX-chunkMinX)+1;}
public int getChunkLength(){return (chunkMaxZ-chunkMinZ)+1;}
public int getChunkMinX(){return chunkMinX;}
public int getChunkMaxX(){return chunkMaxX;}
public int getChunkMinZ(){return chunkMinZ;}
public int getChunkMaxZ(){return chunkMaxZ;}
public int getBlockMinX(){return chunkMinX*16;}
public int getBlockMaxX(){return chunkMaxX*16+15;}
public int getBlockMinZ(){return chunkMinZ*16;}
public int getBlockMaxZ(){return chunkMaxZ*16+15;}
public int getBlockWidth(){return getBlockMaxX()-getBlockMinX()+1;}
public int getBlockLength(){return getBlockMaxZ()-getBlockMinZ()+1;}

@Override
public String toString()
  {
  int minX = chunkMinX * 16;
  int maxX = chunkMaxX * 16 + 15;
  int minZ = chunkMinZ * 16;
  int maxZ = chunkMaxZ * 16 + 15;
  return "TownArea: "+minX+"  :"+minZ+" :: "+maxX+" : "+maxZ +" size: "+getBlockWidth()+" : "+getBlockLength();
  }

}

}
