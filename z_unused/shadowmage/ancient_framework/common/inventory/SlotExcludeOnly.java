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

import java.util.ArrayList;
import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import shadowmage.ancient_framework.common.utils.InventoryTools;

public class SlotExcludeOnly extends Slot
{

List<ItemStack> itemFilters;

/**
 * @param par1iInventory
 * @param par2
 * @param par3
 * @param par4
 */
public SlotExcludeOnly(IInventory par1iInventory, int par2, int par3, int par4, List<ItemStack> itemFilters)
  {
  super(par1iInventory, par2, par3, par4);
  this.itemFilters = itemFilters;
  }

public SlotExcludeOnly(IInventory par1iInventory, int par2, int par3, int par4, ItemStack itemFilter)
  {
  super(par1iInventory, par2, par3, par4);
  this.itemFilters = new ArrayList<ItemStack>();
  this.itemFilters.add(itemFilter);
  }

@Override
public boolean isItemValid(ItemStack par1ItemStack)
  {
  if(par1ItemStack==null){return true;}
  for(ItemStack item : this.itemFilters)
    {
    if(item==null){continue;}
    if(InventoryTools.doItemsMatch(par1ItemStack, item))
      {
      return false;
      }
    }
  return true;
  }
}
