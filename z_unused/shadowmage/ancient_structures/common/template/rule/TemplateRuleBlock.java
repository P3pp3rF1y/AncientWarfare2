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
package shadowmage.ancient_structures.common.template.rule;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public abstract class TemplateRuleBlock extends TemplateRule
{

public TemplateRuleBlock(World world, int x, int y, int z, Block block, int meta, int turns)
  {
  
  }

public TemplateRuleBlock()
  {
  
  }

/**
 * should this rule be re-used in the template for the passed in block/te parameters?
 * common things to check are simple block ID / meta combinations. 
 * keep in mind you must rotate the passed in meta if you wish to compare it with the meta stored in your rule (you did normalize to north-oriented on construction, right?)
 * more complex blocks may check the tile-entity for specific data  
 * @param world 
 * @param block
 * @param meta -- pure meta as from world.getblockMetaData
 * @param turns -- 90' clockwise turns needed for proper orientation from normalized template orientation 
 * @param te -- the tile entity from world.getblockTileEntity -- may be null if none
 * @param x
 * @param y
 * @param z
 * @return true if this rule can handle the input block
 */
public abstract boolean shouldReuseRule(World world, Block block, int meta, int turns, TileEntity te, int x, int y, int z);



}
