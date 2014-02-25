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
package shadowmage.ancient_framework.common.block;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;


public abstract class AWBlockContainer extends AWBlockBase
{

/**
 * @param par1
 * @param par2Material
 * @param baseName
 */
public AWBlockContainer(int par1, Material par2Material, String baseName)
  {
  super(par1, par2Material, baseName);
  this.isBlockContainer = true;
  }

@Override
public boolean hasTileEntity(int meta)
  {
  return true;
  }

public TileEntity createNewTileEntity(World world)
  {
  return getNewTileEntity(world, 0);
  }

public abstract TileEntity getNewTileEntity(World world, int meta);

@Override
public TileEntity createTileEntity(World world, int meta)
  {
  return getNewTileEntity(world, meta);
  }

/**
 * ejects contained items into the world, and notifies neighbours of an update, as appropriate
 */
@Override
public void breakBlock(World par1World, int par2, int par3, int par4, int par5, int par6)
  {
  super.breakBlock(par1World, par2, par3, par4, par5, par6);
  par1World.removeBlockTileEntity(par2, par3, par4);
  }

/**
 * Called when the block receives a BlockEvent - see World.addBlockEvent. By default, passes it on to the tile
 * entity at this location. Args: world, x, y, z, blockID, EventID, event parameter
 */
@Override
public boolean onBlockEventReceived(World par1World, int par2, int par3, int par4, int par5, int par6)
  {
  super.onBlockEventReceived(par1World, par2, par3, par4, par5, par6);
  TileEntity tileentity = par1World.getBlockTileEntity(par2, par3, par4);
  return tileentity != null ? tileentity.receiveClientEvent(par5, par6) : false;
  }

@Override
public int idDropped(int par1, Random par2Random, int par3)
  {
  return this.blockID;
  }

@Override
public int damageDropped(int par1)
  {
  return par1;
  }

@Override
public int quantityDropped(Random par1Random)
  {
  return 1;
  }

@Override
protected ItemStack createStackedBlock(int par1)
  {
  return new ItemStack(this.blockID,1,par1);
  }

@Override
public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int metadata, int fortune)
  {
  ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
  ret.add(createStackedBlock(metadata));
  return ret;
  }

@Override
public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
  {
  return new ItemStack(this.blockID,1,world.getBlockMetadata(x, y, z));
  }
}
