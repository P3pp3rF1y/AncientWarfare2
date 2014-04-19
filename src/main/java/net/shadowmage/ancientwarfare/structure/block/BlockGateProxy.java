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
package net.shadowmage.ancientwarfare.structure.block;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.structure.tile.TEGateProxy;

public class BlockGateProxy extends Block
{

/**
 * @param par1
 * @param par2Material
 * @param baseName
 */
public BlockGateProxy(String regName)
  {
  super(Material.rock);
  this.setBlockName(regName);
  this.setBlockTextureName("ancientwarfare:gate/gateProxy");
  this.setCreativeTab(null);
  this.setResistance(2000.f);
  this.setHardness(5.f);
  }

@Override
public boolean hasTileEntity(int metadata)
  {
  return true;
  }

@Override
public TileEntity createTileEntity(World world, int metadata)
  {
  return new TEGateProxy();
  }

@Override
public ArrayList<ItemStack> getDrops(World world, int x, int y, int z,      int metadata, int fortune)
  {
  return new ArrayList<ItemStack>();
  }

@Override
public boolean renderAsNormalBlock()
  {
  return false;
  }

@Override
public boolean shouldSideBeRendered(IBlockAccess par1iBlockAccess, int par2, int par3, int par4, int par5)
  {
  return false;
  }

@Override
public boolean isBlockSolid(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
  {
  return false;
  }

@Override
public AxisAlignedBB getSelectedBoundingBoxFromPool(World par1World, int x,  int y, int z)
  {
  return AxisAlignedBB.getAABBPool().getAABB(x+0.5d,y+0.5d,z+0.5d,x+0.5d,y+0.5d,z+0.5d);
  }

@Override
public boolean isOpaqueCube()
  {
  return false;
  }

@Override
public ItemStack getPickBlock(MovingObjectPosition target, World world, int x,  int y, int z)
  {
  return null;
  }

@Override
public int quantityDropped(Random par1Random)
  {
  return 0;
  }


}
