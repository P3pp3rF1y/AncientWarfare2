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
package shadowmage.ancient_structures.common.utils;

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class BlockMetaInfo
{
private static HashMap<String, Block> blockNameMap = new HashMap<String, Block>();
private static HashMap<Block, BlockMetaInfo> infoByBlock = new HashMap<Block, BlockMetaInfo>();

int id;
String name;
byte[] rotationMatrix = new byte[16];
int buildPriority = 0;

public BlockMetaInfo(Block block)
  {
  id = block.blockID;
  name = block.getUnlocalizedName();
  blockNameMap.put(name, block);
  infoByBlock.put(block, this);
  }

public int getRotatedMetaFor(int meta, int turns)
  {  
  for(int i = 0; i <turns; i++)
    {
    meta = getRotatedMetaFor(meta);
    }
  return meta;
  }

public int getRotatedMetaFor(int meta)
  {
  return rotationMatrix[meta];
  }

public void setRotationMap(int... metaMap)
  {
  for(int i =0; i < metaMap.length && i < 16; i++)
    {
    rotationMatrix[i] = (byte) metaMap[i];
    }
  }

public ItemStack getItemToPlace(int meta)
  {
  return null;
  }

public static Block getBlockForName(String name)
  {
  return blockNameMap.get(name);
  }

public static int getRotatedMetaFor(Block block, int meta, int turns)
  {
  if(infoByBlock.containsKey(block))
    {
    return infoByBlock.get(block).getRotatedMetaFor(meta, turns);
    }
  return meta;
  }

}
