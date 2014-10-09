package net.shadowmage.ancientwarfare.structure.item;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;

public class FloodFillPathfinder
{

private int maxDist = 40;//TODO set from config

World world;
int x, y, z;//starting position
Block block;//target flood fill block
int meta;
boolean searchUpwards;
boolean searchDownwards;

ArrayList<BlockPosition> openList = new ArrayList<BlockPosition>();
Set<BlockPosition> closedList = new HashSet<BlockPosition>();
Set<BlockPosition> neighborCache = new HashSet<BlockPosition>();
Set<BlockPosition> returnSet = new HashSet<BlockPosition>();

public FloodFillPathfinder(World world, int x, int y, int z, Block block, int meta, boolean up, boolean down)
  {
  this.world = world;
  this.x = x;
  this.y = y;
  this.z = z;
  this.block = block;
  this.meta = meta;
  this.searchUpwards = up;
  this.searchDownwards = down;
  }

public Set<BlockPosition> doFloodFill()
  {
  openList.add(new BlockPosition(x, y, z));  
  BlockPosition pos;
  while(!openList.isEmpty())
    {
    pos = openList.remove(0);
    returnSet.add(pos);
    addNeighbors(pos);    
    for(BlockPosition p1 : neighborCache)
      {
      if(returnSet.contains(p1) || closedList.contains(p1) || openList.contains(p1)){continue;}
      if(isValidPosition(p1)){openList.add(p1);}
      }
    neighborCache.clear();
    }
  return returnSet;
  }

private boolean isValidPosition(BlockPosition pos)
  {
  return isWithinDist(pos) && world.getBlock(pos.x, pos.y, pos.z)==block && world.getBlockMetadata(pos.x, pos.y, pos.z)==meta;
  }

private boolean isWithinDist(BlockPosition pos)
  {
  return pos.x>=x-maxDist && pos.x<=x+maxDist && pos.y>=y-maxDist && pos.y<=y+maxDist && pos.z >= z-maxDist && pos.z <= z+maxDist;
  }

private void addNeighbors(BlockPosition pos)
  {
  neighborCache.add(new BlockPosition(pos.x-1, pos.y, pos.z));
  neighborCache.add(new BlockPosition(pos.x+1, pos.y, pos.z));
  neighborCache.add(new BlockPosition(pos.x, pos.y, pos.z-1));
  neighborCache.add(new BlockPosition(pos.x, pos.y, pos.z+1));  
  if(searchUpwards)    
    {
    neighborCache.add(new BlockPosition(pos.x-1, pos.y+1, pos.z));
    neighborCache.add(new BlockPosition(pos.x+1, pos.y+1, pos.z));
    neighborCache.add(new BlockPosition(pos.x, pos.y+1, pos.z-1));
    neighborCache.add(new BlockPosition(pos.x, pos.y+1, pos.z+1));
    }
  if(searchDownwards)
    {
    neighborCache.add(new BlockPosition(pos.x-1, pos.y-1, pos.z));
    neighborCache.add(new BlockPosition(pos.x+1, pos.y-1, pos.z));
    neighborCache.add(new BlockPosition(pos.x, pos.y-1, pos.z-1));
    neighborCache.add(new BlockPosition(pos.x, pos.y-1, pos.z+1));
    }
  }

}
