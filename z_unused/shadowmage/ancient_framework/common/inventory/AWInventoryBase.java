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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import shadowmage.ancient_framework.common.interfaces.IInventoryCallback;
import shadowmage.ancient_framework.common.utils.InventoryTools;

public abstract class AWInventoryBase implements IInventory
{

int storageSize = 0;

List<IInventoryCallback> callbacks = new ArrayList<IInventoryCallback>();

public AWInventoryBase(int size)
  {
  storageSize = size;
  }

public AWInventoryBase(int size, int maxStackSize)
  {
  this(size);
  this.maxStackSize = maxStackSize;
  }

public AWInventoryBase setCallback(IInventoryCallback call)
  {
  this.callbacks.add(call);
  return this;
  }

/**
 * return qty left that could not be removed from inventory
 * does not call onInventoryChanged...only used by vehicle ammo helper
 * to decrease ammo counts (only known by itemID/damage) should deprecate
 * and create itemStack filters for ammo (might be available from registry registered stack cache)
 * @param id
 * @param dmg
 * @param qty
 * @return
 */
public int decreaseCountOf(int id, int dmg, int qty)
  {  
  for(int i = 0; i < this.getSizeInventory(); i++)
    {
    ItemStack stack = this.getStackInSlot(i);
    if(stack!=null && stack.itemID==id && stack.getItemDamage()==dmg)
      {
      if(stack.stackSize>=qty)
        {
        stack.stackSize-=qty;
        qty = 0;
        }
      else
        {
        qty-=stack.stackSize;
        stack.stackSize = 0;
        }
      if(stack.stackSize==0)
        {
        this.setInventorySlotContents(i, null);
        }
      if(qty<=0)
        {
        return 0;
        }
      }
    } 
  return qty;
  }

@Override
public int getSizeInventory()
  {
  return this.storageSize;
  }

@Override
public ItemStack decrStackSize(int slotNum, int decreaseBy)
  {
  ItemStack stack = this.getStackInSlot(slotNum);
  if (stack != null)
    {
    if (stack.stackSize <= decreaseBy)
      {      
      this.setInventorySlotContents(slotNum, null);
      return stack;
      }
    else
      {
      stack = this.getStackInSlot(slotNum).splitStack(decreaseBy);
      if (this.getStackInSlot(slotNum).stackSize == 0)
        {
        this.setInventorySlotContents(slotNum, null);
        }
      return stack;
      }
    }
  else
    {
    return null;
    }
  }


@Override
public ItemStack getStackInSlotOnClosing(int var1)
  {
  ItemStack var2 = this.getStackInSlot(var1);
  if (var2 != null)
    {
    this.setInventorySlotContents(var1, null);
    return var2;
    }
  else
    {
    return null;
    }
  }

public int getCountOf(ItemStack filter)
  {
  return InventoryTools.getCountOf(this, filter, 0, getSizeInventory()-1);
  }

public int tryRemoveItems(ItemStack filter, int qty)
  {
  return InventoryTools.tryRemoveItems(this, filter, qty, 0, this.getSizeInventory()-1);
  }

public ItemStack getItems(ItemStack filter, int max)
  {
  return InventoryTools.getItems(this, filter, max, 0, this.getSizeInventory()-1);
  }

public boolean canHoldItem(ItemStack filter, int qty)
  {
  return InventoryTools.canHoldItem(this, filter, qty, 0, this.getSizeInventory()-1);
  }

public boolean canHoldItem(ItemStack filter, int qty, int [] slots)
  {
  return InventoryTools.canHoldItem(this, filter, qty, slots);
  }

/**
 * returns the remainder of the items not merged, or null if completely successful 
 * @param toMerge
 * @return
 */
public ItemStack tryMergeItem(ItemStack toMerge)
  {
  return InventoryTools.tryMergeStack(this, toMerge, -1);
  }

public ItemStack tryMergeItem(ItemStack toMerge, int[] slots)
  {
  return InventoryTools.tryMergeStack(this, toMerge, slots);
  }

public boolean containsAtLeast(ItemStack filter, int qty)
  {
  return InventoryTools.containsAtLeast(this, filter, qty, 0, this.getSizeInventory()-1);
  }

public int getEmptySlotCount()
  {
  int emptySlots = 0;
  for(int i = 0; i < this.getSizeInventory(); i ++)
    {
    if(this.getStackInSlot(i)==null)
      {
      emptySlots++;
      }
    }
  return emptySlots;
  }

public int canHoldMore(ItemStack item)
  {
  return InventoryTools.canHoldMore(this, item, 0, this.getSizeInventory()-1);
  }

/**
 * percentage full by slot count
 * @return
 */
public float getPercentEmpty()
  {
  if(this.getSizeInventory()==0)
    {
    return 1.f;
    }
  return (float)((float)this.getEmptySlotCount()/(float)this.getSizeInventory());
  }

@Override
public String getInvName()
  {  
  return "DefaultInventory";
  }

protected int maxStackSize = 64;

@Override
public int getInventoryStackLimit()
  {  
  return maxStackSize;
  }

@Override
public void onInventoryChanged()
  {
  for(IInventoryCallback call : this.callbacks)
    {
    call.onInventoryChanged(this);
    }
  }

@Override
public boolean isUseableByPlayer(EntityPlayer var1)
  {
  return true;
  }

@Override
public void openChest()
  {
  
  }

@Override
public void closeChest()
  {
  
  }

/**
 * return {@link NBTTagCompound} describing this inventory
 * @return
 */
public NBTTagCompound getNBTTag()
  {  
  NBTTagCompound tag = new NBTTagCompound();
  NBTTagList itemList = new NBTTagList();
  for (int slotIndex = 0; slotIndex < this.getSizeInventory(); ++slotIndex)
    {
    if (this.getStackInSlot(slotIndex) != null)
      {
      NBTTagCompound itemEntryTag = new NBTTagCompound();
      itemEntryTag.setByte("Slot", (byte)slotIndex);
      this.getStackInSlot(slotIndex).writeToNBT(itemEntryTag);
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
public void readFromNBT(NBTTagCompound tag)
  {
  NBTTagList itemList = tag.getTagList("Items");  
  for (int tagIndex = 0; tagIndex < itemList.tagCount(); ++tagIndex)
    {
    NBTTagCompound itemStackTag = (NBTTagCompound)itemList.tagAt(tagIndex);
    int slotForItem = itemStackTag.getByte("Slot") & 255;
    if (slotForItem >= 0 && slotForItem < this.getSizeInventory())
      {
      this.setInventorySlotContents(slotForItem, ItemStack.loadItemStackFromNBT(itemStackTag));
      }
    }
  }

@Override
public boolean isInvNameLocalized()
  {
  return true;
  }

@Override
public boolean isItemValidForSlot(int i, ItemStack itemstack)
  {
  return true;
  }

}
