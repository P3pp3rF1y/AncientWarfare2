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
package shadowmage.ancient_framework.common.inventory;

import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import shadowmage.ancient_framework.common.utils.InventoryTools;

public class SlotResourceOnly extends Slot
{

List<ItemStack> itemFilters;

/**
 * 1=nbt
 * 2=damage
 * 3=both
 */
int ignoreType = 0;
int maxStackSize = 64;

/**
 * @param par1iInventory
 * @param par2
 * @param par3
 * @param par4
 */
public SlotResourceOnly(IInventory par1iInventory, int par2, int par3, int par4, List<ItemStack> filters)
  {
  super(par1iInventory, par2, par3, par4);
  this.itemFilters = filters;
  }

public SlotResourceOnly setIgnoreType(int type)
  {
  this.ignoreType = type;
  return this;
  }

public SlotResourceOnly setMaxStackSize(int size)
  {
  this.maxStackSize = size;
  return this;
  }

@Override
public int getSlotStackLimit()
  {
  return maxStackSize;
  }

@Override
public boolean isItemValid(ItemStack par1ItemStack)
  {
  if(par1ItemStack==null){return true;}
  for(ItemStack item : this.itemFilters)
    {
    if(item==null){continue;}
    if(this.ignoreType==0)
      {
      if(InventoryTools.doItemsMatch(item, par1ItemStack))
        {
        return true;
        }
      }
    else if(this.ignoreType==1)
      {
      if(item.itemID==par1ItemStack.itemID && item.getItemDamage()==par1ItemStack.getItemDamage())
        {
        return true;
        }
      }
    else if(this.ignoreType==2)
      {
      if(item.itemID==par1ItemStack.itemID && ItemStack.areItemStackTagsEqual(item, par1ItemStack))
        {
        return true;
        }
      }
    else if(this.ignoreType==3)
      {
      if(item.itemID==par1ItemStack.itemID)
        {
        return true;
        }
      }
    }
  return false;
  }

}
