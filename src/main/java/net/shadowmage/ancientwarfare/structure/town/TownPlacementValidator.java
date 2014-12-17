package net.shadowmage.ancientwarfare.structure.town;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.structure.config.AWStructureStatics;
import net.shadowmage.ancientwarfare.structure.gamedata.StructureMap;
import net.shadowmage.ancientwarfare.structure.gamedata.TownMap;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;
import net.shadowmage.ancientwarfare.structure.world_gen.StructureEntry;

public class TownPlacementValidator
{

private static int maxSize = 21;

/**
 * input a single X, Y, Z coordinate to examine the nearby area for potential town generation.<br>
 * First examines the chunk that was at the input coordinates, and expands out from there attempting
 * to create the largest town-bounding area that it can.
 * 
 * 
 * 
 * @param world
 * @param x world x to search from
 * @param z world z to search from
 * @return maximal bounding area for a town, or null if no acceptable area was found starting in the specified chunk
 */
public static TownBoundingArea findGenerationPosition(World world, int x, int z)
  {     
  int cx = x >> 4;
  int cz = z >> 4;  
  
  TownMap tm = AWGameData.INSTANCE.getData(TownMap.NAME, world, TownMap.class);
  int minDist = AWStructureStatics.townClosestDistance * 16;
  float dist = tm.getClosestTown(world, x, z, minDist * 2);
  if(dist<minDist){AWLog.logDebug("Skipping generation for existing town too close!"); return null;}
   
  int height = getTopFilledHeight(world.getChunkFromChunkCoords(cx, cz), x & 15, z & 15);
  if(height <= 0){return null;}
  
  TownBoundingArea area = new TownBoundingArea();
  area.minY = Math.max(0, height-7);
  area.maxY = Math.min(255, area.minY+15);
  area.chunkMinX = cx;
  area.chunkMaxX = cx;
  area.chunkMinZ = cz;
  area.chunkMaxZ = cz;  
  expandBoundingArea(world, area);
  
  /**
   * Shrink town along x or z axis if ratio of sizes is > 2:1 
   */
  int cw = area.getChunkWidth();  
  int cl = area.getChunkLength();  
  if(cw > cl * 2)
    {
    int diff = cw - (cl * 2);
    while(diff>0)
      {
      area.chunkMaxX--;
      diff--;
      if(diff>0)
        {
        area.chunkMinX++;
        diff--;
        }
      }
    }
  if(cl > cw * 2)
    {
    int diff = cl - (cw * 2);
    while(diff>0)
      {
      area.chunkMaxZ--;
      diff--;
      if(diff>0)
        {
        area.chunkMinZ++;
        diff--;
        }
      }
    }
  return area;
  }

public static boolean validateAreaForPlacement(World world, TownBoundingArea area)
  {
  if(!validateStructureCollision(world, area)){return false;} 
  return true;
  }

private static boolean validateStructureCollision(World world, TownBoundingArea area)
  {
  StructureMap map = AWGameData.INSTANCE.getData("AWStructureMap", world, StructureMap.class);
  if(map==null){return false;}
  StructureBB bb = new StructureBB(new BlockPosition(area.getBlockMinX(), area.getMinY(), area.getBlockMaxX()), new BlockPosition(area.getBlockMaxX(), area.getMaxY(), area.getBlockMaxZ()));
  int size = Math.max(area.getChunkWidth(), area.getChunkLength());
  List<StructureEntry> entries = new ArrayList<StructureEntry>();
  map.getEntriesNear(world, area.getCenterX(), area.getCenterZ(), size, true, entries);
  for(StructureEntry e : entries)
    {
    if(e.getBB().collidesWith(bb)){return false;}
    }
  return true;
  }

private static void expandBoundingArea(World world, TownBoundingArea area)
  {
  boolean xneg = true, xpos = true, zneg = true, zpos = true;//if should try and expand on this direction next pass, once set to false it never checks that direction again
  boolean didExpand = false;//set to true if any expansion occurred on that pass.  if false at end of pass, will break out of loop as no more expansion is possible
  do
    {
    didExpand = false;
    if(xneg && area.getChunkWidth() <= maxSize)
      {
      xneg = tryExpandXNeg(world, area);
      didExpand = didExpand || xneg; 
      }
    if(xpos && area.getChunkWidth() <= maxSize)
      {
      xpos = tryExpandXPos(world, area);  
      didExpand = didExpand || xpos;
      } 
    if(zneg && area.getChunkLength() <= maxSize)
      {
      zneg = tryExpandZNeg(world, area);
      didExpand = didExpand || zneg;
      } 
    if(zpos &&area.getChunkLength() <= maxSize)
      {
      zpos = tryExpandZPos(world, area);
      didExpand = didExpand || zpos;
      }  
    }
  while(didExpand && (area.getChunkWidth() <= maxSize || area.getChunkLength() <= maxSize));
  }

private static boolean tryExpandXNeg(World world, TownBoundingArea area)
  {
  int cx = area.chunkMinX - 1;
  int minZ = area.chunkMinZ;
  int maxZ = area.chunkMaxZ;
  boolean valid = true;
  for(int z = minZ; z <= maxZ; z++)
    {
    if(!isAverageHeightWithin(world, cx, z, area.minY, area.maxY))
      {
      valid = false;
      break;      
      }
    }
  if(valid)
    {
    area.chunkMinX = cx;
    }
  return valid;
  }

private static boolean tryExpandXPos(World world, TownBoundingArea area)
  {
  int cx = area.chunkMaxX + 1;
  int minZ = area.chunkMinZ;
  int maxZ = area.chunkMaxZ;
  boolean valid = true;
  for(int z = minZ; z <= maxZ; z++)
    {
    if(!isAverageHeightWithin(world, cx, z, area.minY, area.maxY))
      {
      valid = false;
      break;      
      }
    }
  if(valid)
    {
    area.chunkMaxX = cx;
    }
  return valid;
  }

private static boolean tryExpandZNeg(World world, TownBoundingArea area)
  {
  int cz = area.chunkMinZ - 1;
  int minX = area.chunkMinX;
  int maxX = area.chunkMaxX;
  boolean valid = true;
  for(int x = minX; x <= maxX; x++)
    {
    if(!isAverageHeightWithin(world, x, cz, area.minY, area.maxY))
      {
      valid = false;
      break;      
      }
    }
  if(valid)
    {
    area.chunkMinZ = cz;
    }
  return valid;
  }

private static boolean tryExpandZPos(World world, TownBoundingArea area)
  {
  int cz = area.chunkMaxZ + 1;
  int minX = area.chunkMinX;
  int maxX = area.chunkMaxX;
  boolean valid = true;
  for(int x = minX; x <= maxX; x++)
    {
    if(!isAverageHeightWithin(world, x, cz, area.minY, area.maxY))
      {
      valid = false;
      break;      
      }
    }
  if(valid)
    {
    area.chunkMaxZ = cz;
    }
  return valid;
  }

private static boolean isAverageHeightWithin(World world, int cx, int cz, int min, int max)
  {
  Chunk chunk = world.getChunkFromChunkCoords(cx, cz);
  int val;
  int total = 0;
  for(int x = 0; x<16; x++)
    {
    for(int z = 0; z<16; z++)
      {
      val = getTopFilledHeight(chunk, x, z);      
      total += val;
      }
    }
  total /= 256;//make it the average top-height
//  AWLog.logDebug("found top average height of: "+total+" for min/max of: "+min+ " : "+max);
  return total >= min && total<=max;
  }

/**
 * return the highest Y that has a solid non-skipped block in it<br>
 * This implementation skips water, air, and any blocks on the world-gen skippable blocks list (trees, plants, etc)
 * @param chunk
 * @param xInChunk
 * @param zInChunk
 * @return top solid block height, or -1 for invalid top block or no top block found (void, bedrock...)
 */
private static int getTopFilledHeight(Chunk chunk, int xInChunk, int zInChunk)
  {
  int maxY = chunk.getTopFilledSegment()+15;
  Block block = null;
  for(int y = maxY; y>0; y--)
    {
    block = chunk.getBlock(xInChunk, y, zInChunk);
    if(block==null || block==Blocks.air || AWStructureStatics.skippableBlocksContains(block)){continue;}
    if(block.getMaterial().isLiquid())
      {
      if(y>=56){continue;}// >=56 is fillable through underfill/border settings.  below that is too deep for a proper gradient on the border.
      return -1;//return invalid Y if liquid block is too low
      }
    if(!AWStructureStatics.isValidTownTargetBlock(block)){return -1;}    
    return y;//if not skippable and is valid target block, return that y-level
    }
  return -1;
  }

}
