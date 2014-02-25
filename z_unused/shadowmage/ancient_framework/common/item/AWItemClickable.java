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
package shadowmage.ancient_framework.common.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import shadowmage.ancient_framework.common.utils.BlockPosition;

public abstract class AWItemClickable extends AWItemBase
{

public boolean hasLeftClick = false;

/**
 * @param itemID
 * @param hasSubTypes
 */
public AWItemClickable(Configuration config, String itemName)
  {
  super(config, itemName);
  }

@Override
public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float xOff, float yOff, float zOff)
  {
  return onUsedFinal(world, player, stack, new BlockPosition(x,y,z), side);
  }

@Override
public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World,EntityPlayer par3EntityPlayer)
  {
  onUsedFinal(par2World, par3EntityPlayer, par1ItemStack, null, -1);  
  return par1ItemStack;
  }

@Override
public boolean onBlockStartBreak(ItemStack stack, int x, int y, int z, EntityPlayer player)
  { 
  if(!hasLeftClick)
    {    
    return super.onBlockStartBreak(stack, x, y, z, player);
    }
  return true;
  }

public abstract boolean onUsedFinal(World world, EntityPlayer player, ItemStack stack, BlockPosition hit, int side);

public abstract boolean onUsedFinalLeft(World world, EntityPlayer player, ItemStack stack, BlockPosition hit, int side);

}
