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

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.structure.api.IStructureBuilder;
import net.shadowmage.ancientwarfare.structure.block.BlockDataManager;

public class TemplateRuleBlockSign extends TemplateRuleVanillaBlocks
{

public String signContents[];
public boolean wall = true;

public TemplateRuleBlockSign(World world, int x, int y, int z, Block block, int meta, int turns)
  {
  super(world, x, y, z, block, meta, turns);
  TileEntitySign te = (TileEntitySign) world.getTileEntity(x, y, z);
  signContents = new String[te.signText.length];
  for(int i = 0; i < signContents.length; i++)
    {
    signContents[i] = te.signText[i];
    }
  if(block==Blocks.standing_sign)
    {
    wall = false;
    this.meta = (meta+4*turns)%16;
    } 
  }

public TemplateRuleBlockSign()
  {
  }

@Override
public void handlePlacement(World world, int turns, int x, int y, int z, IStructureBuilder builder)
  {
  Block block = wall? Blocks.wall_sign : Blocks.standing_sign;//BlockDataManager.getBlockByName(blockName);
  int meta = 0;
  if(block==Blocks.standing_sign)
    {
    meta = (this.meta+4*turns)%16; 
    }
  else
    {
    meta = BlockDataManager.instance().getRotatedMeta(block, this.meta, turns);
    }
  world.setBlock(x, y, z, block, meta, 2);
  TileEntitySign te = (TileEntitySign) world.getTileEntity(x, y, z);
  te.signText = new String[this.signContents.length];
  for(int i = 0; i < this.signContents.length; i++)
    {
    te.signText[i] = this.signContents[i];
    }
  world.markBlockForUpdate(x, y, z);
  }

@Override
public boolean shouldReuseRule(World world, Block block, int meta, int turns, TileEntity te, int x, int y, int z)
  {
  if(te instanceof TileEntitySign)
    {
    if(!signContents.equals(((TileEntitySign)te).signText))
      {
      return false;
      }
    }
  return ((block==Blocks.standing_sign && !this.wall && (meta+4*turns%16)==this.meta)||(block==Blocks.wall_sign && this.wall && BlockDataManager.instance().getRotatedMeta(block, meta, turns)==this.meta));
  }

@Override
public void writeRuleData(NBTTagCompound tag)
  {
  super.writeRuleData(tag);
  for(int i =0; i < 4; i++)
    {
    tag.setString("signContents"+i, signContents[i]);
    }
  }

@Override
public void parseRuleData(NBTTagCompound tag)
  {
  super.parseRuleData(tag);
  this.signContents = new String[4];
  for(int i = 0; i <4 ;i++)
    {
    this.signContents[i] = tag.getString("signContents"+i);
    }
  }

}
