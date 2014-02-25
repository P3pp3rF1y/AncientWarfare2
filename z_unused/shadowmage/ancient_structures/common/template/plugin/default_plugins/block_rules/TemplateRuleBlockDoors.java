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
package shadowmage.ancient_structures.common.template.plugin.default_plugins.block_rules;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import shadowmage.ancient_structures.common.manager.BlockDataManager;

public class TemplateRuleBlockDoors extends TemplateRuleVanillaBlocks
{

public TemplateRuleBlockDoors(World world, int x, int y, int z, Block block, int meta, int turns)
  {
  super(world, x, y, z, block, meta, turns);
  }

public TemplateRuleBlockDoors()
  {
  }

@Override
public void handlePlacement(World world, int turns, int x, int y, int z)
  {
  Block block = BlockDataManager.getBlockByName(blockName);
  int localMeta = BlockDataManager.getRotatedMeta(block, this.meta, turns); 
  if(world.getBlockId(x, y-1, z)!=block.blockID)//this is the bottom door block, call placeDoor from our block...
    {
    world.setBlock(x, y, z, block.blockID, meta, 0);    
    world.setBlock(x, y+1, z, block.blockID, 8, 2);
    world.setBlockMetadataWithNotify(x, y, z, localMeta, 2);
    }
  }

}
