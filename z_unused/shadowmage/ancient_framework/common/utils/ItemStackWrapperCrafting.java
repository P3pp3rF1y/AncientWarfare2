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
package shadowmage.ancient_framework.common.utils;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

public class ItemStackWrapperCrafting extends ItemStackWrapper
{

int remainingNeeded = 0;
public boolean ignoreDamage = false;
public boolean ignoreTag = false;

public ItemStackWrapperCrafting(ItemStack stack, int qty, boolean dmg, boolean tag)
  {
  super(stack, qty);
  this.remainingNeeded = qty;
  this.ignoreDamage = dmg;
  this.ignoreTag = tag;
  }

public ItemStackWrapperCrafting(ItemStack stack, boolean dmg, boolean tag)
  {
  this(stack, stack.stackSize, dmg, tag);
  }

public ItemStackWrapperCrafting(Item item, int qty, int meta, boolean tag){this(new ItemStack(item,qty, meta),false,tag);}
public ItemStackWrapperCrafting(Block block, int qty, int meta, boolean tag){this(new ItemStack(block,qty),false,tag);}
public ItemStackWrapperCrafting(Item item, int qty, boolean dmg, boolean tag){this(new ItemStack(item,qty), qty, dmg, tag);}
public ItemStackWrapperCrafting(Block block, int qty, boolean dmg, boolean tag){this(new ItemStack(block,qty), qty, dmg, tag);}
public ItemStackWrapperCrafting(Item item, int qty){this(new ItemStack(item), qty, true, true);}
public ItemStackWrapperCrafting(Block block, int qty){this(new ItemStack(block), qty, true, true);}

public ItemStackWrapperCrafting(NBTTagCompound tag)
  {
  super(tag);
  this.remainingNeeded = tag.getInteger("rem");
  this.ignoreDamage = tag.getBoolean("igdmg");
  this.ignoreTag = tag.getBoolean("igtg");
  }

public int getRemainingNeeded()
  {
  return this.remainingNeeded;
  }

public void setRemainingNeeded(int val)
  {
  this.remainingNeeded = val;
  if(val<0)
    {
    val = 0;
    }  
  }

public NBTTagCompound writeToNBT(NBTTagCompound tag)
  {
  super.writeToNBT(tag);
  tag.setInteger("rem", remainingNeeded);
  if(this.ignoreDamage){tag.setBoolean("igdmg", true);}
  if(this.ignoreTag){tag.setBoolean("igtg", true);}
  return tag;
  }

@Override
public boolean matches(ItemStack stack)
  {
  if(!this.ignoreDamage && !this.ignoreTag)
    {
    return super.matches(stack);    
    }
  int oreID = OreDictionary.getOreID(filter);
  boolean idMeta = false;
  if(oreID>=0)
    {
    List<ItemStack> targets = OreDictionary.getOres(oreID);
    for(ItemStack target : targets)
      {
      if(OreDictionary.itemMatches(target, stack, !ignoreDamage))
        {
        idMeta = true;
        break;
        }
      }
    }
  else
    {
    idMeta = stack.itemID == filter.itemID && (ignoreDamage || stack.getItemDamage() == filter.getItemDamage());
    }  
  boolean tag = ItemStack.areItemStackTagsEqual(stack, filter);
  if(idMeta)
    {    
    if(!ignoreTag && !tag)
      {
      return false;
      }
    return true;
    }
  return false;
  }

@Override
public boolean matches(ItemStackWrapper wrap)
  {
  return matches(wrap.filter);
  }


}
