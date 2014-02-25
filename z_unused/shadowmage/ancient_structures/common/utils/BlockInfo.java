/**
   Copyright 2012 John Cummens (aka Shadowmage, Shadowmage4513)
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
package shadowmage.ancient_structures.common.utils;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import shadowmage.ancient_framework.common.config.AWLog;
import shadowmage.ancient_framework.common.utils.IDPairCount;

/**
 * info wrapper for blocks which may be rotated dynamically
 * @author Shadowmage
 */
public class BlockInfo
{

/**
 * contains data on all _special_ blocks, that need build priority or rotation data
 * e.g. Air: priority 0, to build/clear air blocks first
 * e.g. Stone: priority 1, needs no special treatment
 * e.g. Sand: priority 2, needs to be placed after solid non-moving blocks
 * e.g  Ladder: priority 3, to build after all normal first/second tier blocks are placed
 */
public static BlockInfo[] blockList = new BlockInfo[4096];


/**
 * MC blockID for this block
 */
int blockID;

/**
 * name of the block
 */
String name = "";

/**
 * can be rotated through meta-data
 */
boolean rotatable = false;

/**
 * does this block with metadata represent itself as an inventory block (and vice-versa)?
 */
boolean isBasicSubTypeBlock = false;

boolean isBasicInventoryItem = false;
int basicInventoryItemID = -1;
int basicInventoryItemMeta = -1;
int basicInventoryItemCount = -1;

/**
 * the default build priority for this block, added to blockRules created for this block when templates are generated
 * may be overridden in template by user
 */
public byte buildOrder = 0;

/**
 * metadata rotation tables, one entry for each possible meta-data, broken into six tables.  Most blocks will only need
 * one or two tables. (pistons and levers need all six)
 * all rotations will fallback to meta-data 0 if no valid information is found in the table
 */
byte[][] metaRotations = new byte[8][4];


/**
 * list of meta id -> inventory id/meta
 * i.e. returns the itemstack itemid/meta needed to place this block given an input meta
 */
Map<Integer, IDPairCount> metaToBlocks = new HashMap<Integer, IDPairCount>();


public BlockInfo(int id, String name)
  {
  this.blockID = id;
  this.name = name;
  blockList[id]=this;
  }

public BlockInfo setMeta(int set, int a, int b, int c, int d)
  {
  if(set>=0 && set<metaRotations.length)
    {
    this.metaRotations[set][0]=(byte)a;
    this.metaRotations[set][1]=(byte)b;
    this.metaRotations[set][2]=(byte)c;
    this.metaRotations[set][3]=(byte)d;
    }
  return this;
  }

public BlockInfo setMeta1(int a, int b, int c, int d)
  {
  return setMeta(0, a, b, c, d);  
  }

public BlockInfo setMeta2(int a, int b, int c, int d)
  {
  return setMeta(1, a, b, c, d);
  }

public BlockInfo setMeta3(int a, int b, int c, int d)
  {
  return setMeta(2, a, b, c, d);
  }

public BlockInfo setMeta4(int a, int b, int c, int d)
  {
  return setMeta(3, a, b, c, d);
  }

public BlockInfo setRotatable()
  {
  this.rotatable = true;
  return this;
  }

public boolean isRotatable()
  {
  return this.rotatable;
  }

public BlockInfo setIsBasicSubtype()
  {
  this.isBasicSubTypeBlock = true;
  return this;
  }

/**
 * return rotated metadata for this block for one turn to the right
 * @param current
 * @return
 */
public int rotateRight(int current)
  {
  if(!this.rotatable)
    {
    return current;
    }
  byte cur = (byte)current;
  for(int i = 0; i <metaRotations.length; i++)
    {
    for(int j = 0; j <metaRotations[i].length; j++)
      {
      if(metaRotations[i][j]==cur)
        {
        if(j<3)
          {
          return metaRotations[i][j+1];
          }
        else
          {
          return metaRotations[i][0];
          }
        }
      }
    }  
  return 0;
  }

/**
 * return metadata for this block rotated specified number of turns to the right
 * @param current
 * @param turns
 * @return
 */
public int rotateRight(int current, int turns)
  {
  if(!this.rotatable)
    {
    return current;
    }
  for(int i = 0; i < turns; i++)
    {
    current = rotateRight(current);
    }
  return current;
  }


public BlockInfo setPriority(int p)
  {
  this.buildOrder = (byte)p;
  return this;
  }


public BlockInfo setInventoryBlock(int meta, IDPairCount data)
  {
  this.metaToBlocks.put(meta, data);
  return this;
  }

public BlockInfo setBasicInventoryItem(int id, int meta, int count)
  {
  this.isBasicInventoryItem = true;
  this.basicInventoryItemID = id;
  this.basicInventoryItemMeta = meta;
  this.basicInventoryItemCount = count;
  return this;
  }
  
  
/************************************** STATIC METHODS **************************************/
/**
 * create a BlockInfo entry for specified Block, and ensure it is added into
 * the BlockInfo.blockList[]
 * @param block
 * @param priority
 * @return
 */
public static BlockInfo createEntryFor(Block block)
  {
  return createEntryFor(block.blockID, block.getLocalizedName());
  }

public static BlockInfo createEntryFor(int id, String name)
  {
  BlockInfo info = new BlockInfo(id, name);
  if(blockList[id]==null)
    {
    blockList[id]=info;
    }
  return info;
  }

public static int getRotatedMeta(int id, int meta, int rotationAmt)  
  {
  if(blockList[id]==null)
    {
    return meta;
    }
  return blockList[id].rotateRight(meta, rotationAmt);
  }


/**
 * get the block or item id/meta combo needed in inventory in order to place this block
 * @param id of the block in the template
 * @param meta of the block in the template
 * @return id, meta, and count (double slab) making up the block necessary to place this block in the world returns NEW object
 */
public static IDPairCount getInventoryBlock(int id, int meta)
  {  
  BlockInfo data = blockList[id];
  if(data==null)
    {
    return new IDPairCount(id,0);
    }
  if(data.isBasicSubTypeBlock)
    {
    return new IDPairCount(id, meta);
    }
  else if(data.isBasicInventoryItem)
    {
    return new IDPairCount(data.basicInventoryItemID, data.basicInventoryItemMeta, data.basicInventoryItemCount);
    }
  IDPairCount info = data.metaToBlocks.get(meta);
  return info==null ? new IDPairCount(id,0) : info.copy();
  }

public static void setInventoryBlock(int id, int meta, int neededID, int neededMeta, int count)
  {
  if(blockList[id]!=null)
    {
    blockList[id].metaToBlocks.put(meta, new IDPairCount(neededID, neededMeta, count));
    } 
  else
    {
    AWLog.logError("Attempt to set inventory block for invalid block: "+id+","+meta);
    }
  }

public static void setInventoryBlock(Block block, int meta, int neededID, int neededMeta)
  {
  setInventoryBlock(block.blockID, meta, neededID, neededMeta, 1);
  }



}
