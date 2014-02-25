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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class TemplateRuleBlockLogic extends TemplateRuleVanillaBlocks
{

public NBTTagCompound tag = new NBTTagCompound();

public TemplateRuleBlockLogic(World world, int x, int y, int z, Block block, int meta, int turns)
  {
  super(world, x, y, z, block, meta, turns);
  TileEntity te = world.getBlockTileEntity(x, y, z);
  te.writeToNBT(tag);  
  }

public TemplateRuleBlockLogic()
  {
  }

@Override
public void handlePlacement(World world, int turns, int x, int y, int z)
  {
  super.handlePlacement(world, turns, x, y, z);
  tag.setInteger("x", x);
  tag.setInteger("y", y);
  tag.setInteger("z", z);
  TileEntity te = world.getBlockTileEntity(x, y, z);
  te.readFromNBT(tag);
  world.markBlockForUpdate(x, y, z);
  }

@Override
public boolean shouldReuseRule(World world, Block block, int meta, int turns, TileEntity te, int x, int y, int z)
  {
  return false;
  }

@Override
public void writeRuleData(NBTTagCompound tag)
  {
  super.writeRuleData(tag);
  tag.setTag("teData", this.tag);  
  }

@Override
public void parseRuleData(NBTTagCompound tag)
  {
  super.parseRuleData(tag);  
  this.tag = tag.getCompoundTag("teData");
  }
}
