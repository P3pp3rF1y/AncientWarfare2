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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class AWInventoryMapped extends AWInventoryBase
{

HashMap<Integer, ItemStack> items = new HashMap<Integer, ItemStack>();

/**
 * @param size
 */
public AWInventoryMapped(int size)
  {
  super(size);
  }

public void setInventorySize(int size)
  {
  this.storageSize = size;
  }

public List<ItemStack> getInaccessibleItems()
  {
  return Collections.emptyList();
  }

@Override
public ItemStack getStackInSlot(int slot)
  {
  if(slot>=0 && slot<this.storageSize)
    {
    return items.get(slot);
    }
  return null;
  }

@Override
public void setInventorySlotContents(int slot, ItemStack newContents)
  {
  if(slot>=0 && slot<this.storageSize)
    {
    if(newContents==null)
      {
      this.items.remove(slot);
      }
    else
      {
      if(newContents.stackSize > this.getInventoryStackLimit())
        {
        newContents.stackSize = this.getInventoryStackLimit();
        }
      this.items.put(slot, newContents);
      }
    }
  }


/**
 * return {@link NBTTagCompound} describing this inventory
 * @return
 */
@Override
public NBTTagCompound getNBTTag()
  {  
  NBTTagCompound tag = new NBTTagCompound();
  NBTTagList itemList = new NBTTagList();
  NBTTagCompound itemEntryTag;
  ItemStack stack = null;
  for(Integer key : this.items.keySet())
    {
    stack = this.items.get(key);
    if(stack!=null)
      {
      itemEntryTag = new NBTTagCompound();
      itemEntryTag.setInteger("Slot", key);
      stack.writeToNBT(itemEntryTag);
      itemList.appendTag(itemEntryTag);
      }
    }  
  tag.setTag("Items", itemList);
  return tag;
  }

/**
 * read the inventory from an NBT tag
 * @param tag
 */
@Override
public void readFromNBT(NBTTagCompound tag)
  {
  NBTTagList itemList = tag.getTagList("Items");  
  for (int tagIndex = 0; tagIndex < itemList.tagCount(); ++tagIndex)
    {
    NBTTagCompound itemStackTag = (NBTTagCompound)itemList.tagAt(tagIndex);
    int slotForItem = itemStackTag.getInteger("Slot");
    if(slotForItem >= 0 )
      {
      this.items.put(slotForItem, ItemStack.loadItemStackFromNBT(itemStackTag));
      }
    }
  }


}
