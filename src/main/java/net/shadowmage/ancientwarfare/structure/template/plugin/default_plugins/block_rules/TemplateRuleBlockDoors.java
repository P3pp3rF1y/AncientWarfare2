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
import net.shadowmage.ancientwarfare.structure.block.BlockDataManager;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBuilder;

public class TemplateRuleBlockDoors extends TemplateRuleVanillaBlocks
{

byte sideFlag = 0;
boolean isTop = false;

public TemplateRuleBlockDoors(World world, int x, int y, int z, Block block, int meta, int turns)
  {
  super(world, x, y, z, block, meta, turns);
  }

public TemplateRuleBlockDoors()
  {
  }

@Override
public void handlePlacement(World world, int turns, int x, int y, int z, StructureBuilder builder)
  {
  Block block = BlockDataManager.instance().getBlockForName(blockName);
  int localMeta = BlockDataManager.instance().getRotatedMeta(block, this.meta, turns); 
  if(world.getBlock(x, y-1, z)!=block)//this is the bottom door block, call placeDoor from our block...
    {     
    world.setBlock(x, y, z, block, localMeta, 2);
    world.setBlock(x, y+1, z, block, sideFlag==0? 8 : sideFlag, 2);    
    }  
  }

@Override
public void writeRuleData(NBTTagCompound tag)
  {
  tag.setString("blockName", blockName);
  tag.setInteger("meta", meta);
  tag.setInteger("buildPass", buildPass);
  tag.setByte("sideFlag", sideFlag);
  }

@Override
public void parseRuleData(NBTTagCompound tag)
  {
  this.blockName = tag.getString("blockName");
  this.meta = tag.getInteger("meta");
  this.buildPass = tag.getInteger("buildPass");   
  this.sideFlag = tag.getByte("sideFlag"); 
  }

@Override
public boolean shouldReuseRule(World world, Block block, int meta, int turns, TileEntity te, int x, int y, int z)
  {  
  Block block1 = world.getBlock(x, y+1, z);
  return super.shouldReuseRule(world, block, meta, turns, te, x, y, z) && block1!=null && blockName.equals(BlockDataManager.instance().getNameForBlock(block1)) && world.getBlockMetadata(x, y+1, z)==sideFlag;
  }

@Override
public void addResources(List<ItemStack> resources)
  {
  if(sideFlag>0)
    {
    super.addResources(resources);    
    }
  }

}
