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

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import shadowmage.ancient_structures.common.manager.BlockDataManager;
import shadowmage.ancient_structures.common.template.rule.TemplateRuleBlock;

public class TemplateRuleModBlocks extends TemplateRuleBlock
{

public String blockName;
public int meta;

public TemplateRuleModBlocks(World world, int x, int y, int z, Block block, int meta, int turns)
  {
  super(world, x, y, z, block, meta, turns);
  this.blockName = BlockDataManager.getBlockName(block);
  this.meta = meta;
  }

public TemplateRuleModBlocks()
  {

  }

@Override
public boolean shouldReuseRule(World world, Block block, int meta, int turns, TileEntity te, int x, int y, int z)
  {
  return BlockDataManager.getBlockName(block).equals(blockName) && meta==this.meta;
  }

@Override
public void handlePlacement(World world, int turns, int x, int y, int z)
  {
  Block block = BlockDataManager.getBlockByName(blockName);
  world.setBlock(x, y, z, block.blockID, meta, 3);
  }

@Override
public void writeRuleData(NBTTagCompound tag)
  {
  tag.setString("blockName", blockName);
  tag.setInteger("meta", meta);
  }

@Override
public void parseRuleData(NBTTagCompound tag)
  {
  blockName = tag.getString("blockName");
  meta = tag.getInteger("meta");
  }

@Override
public void addResources(List<ItemStack> resources)
  {
  /**
   * TODO
   */
  }

@Override
public boolean shouldPlaceOnBuildPass(World world, int turns, int x, int y, int z, int buildPass)
  {
  return buildPass==0;
  }

}
