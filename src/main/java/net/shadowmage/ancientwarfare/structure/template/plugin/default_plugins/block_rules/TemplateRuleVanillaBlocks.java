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
package net.shadowmage.ancientwarfare.structure.template.plugin.default_plugins.block_rules;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.structure.block.BlockDataManager;
import net.shadowmage.ancientwarfare.structure.template.rule.TemplateRuleBlock;

public class TemplateRuleVanillaBlocks extends TemplateRuleBlock
{

public String blockName;
public int meta;
public int buildPass = 0;

/**
 * constructor for dynamic construction.  passed world and coords so that the rule can handle its own logic internally
 * @param world
 * @param x
 * @param y
 * @param z
 * @param block
 * @param meta
 */
public TemplateRuleVanillaBlocks(World world, int x, int y, int z, Block block, int meta, int turns)
  {
  super(world, x, y, z, block, meta, turns);
  this.blockName = BlockDataManager.instance().getNameForBlock(block);
  this.meta = BlockDataManager.instance().getRotatedMeta(block, meta, turns);
  this.buildPass = BlockDataManager.instance().getPriorityForBlock(block);
  }

public TemplateRuleVanillaBlocks()
  {  
  
  }

@Override
public void handlePlacement(World world, int turns, int x, int y, int z)
  {
  Block block = BlockDataManager.instance().getBlockForName(blockName);
  int localMeta = BlockDataManager.instance().getRotatedMeta(block, this.meta, turns);  
  world.setBlock(x, y, z, block);//using flag=2 -- no block update, but send still send to clients (should help with issues of things popping off)
  world.setBlockMetadataWithNotify(x, y, z, localMeta, 2);
  }
  
@Override
public boolean shouldReuseRule(World world, Block block, int meta, int turns, TileEntity te, int x, int y, int z)
  {
  return block!=null && blockName.equals(BlockDataManager.instance().getNameForBlock(block)) && BlockDataManager.instance().getRotatedMeta(block, meta, turns) == this.meta;
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
  return buildPass == this.buildPass;
  }

@Override
public String toString()
  {
  return String.format("Vanilla Block Rule id: %s meta: %s buildPass: %s", blockName, meta, buildPass);
  }

@Override
public void writeRuleData(NBTTagCompound tag)
  {
  tag.setString("blockName", blockName);
  tag.setInteger("meta", meta);
  tag.setInteger("buildPass", buildPass);
  }

@Override
public void parseRuleData(NBTTagCompound tag)
  {
  this.blockName = tag.getString("blockName");
  this.meta = tag.getInteger("meta");
  this.buildPass = tag.getInteger("buildPass");      
  }

}
