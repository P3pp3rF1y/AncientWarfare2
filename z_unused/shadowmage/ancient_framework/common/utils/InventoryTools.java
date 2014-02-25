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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

public class InventoryTools
{

static Random random = new Random();

public static NBTTagCompound getTagForInventory(IInventory inventory)
  {
  NBTTagCompound tag = new NBTTagCompound();
  NBTTagList itemList = new NBTTagList();
  for (int slotIndex = 0; slotIndex < inventory.getSizeInventory(); ++slotIndex)
    {
    if (inventory.getStackInSlot(slotIndex) != null)
      {
      NBTTagCompound itemEntryTag = new NBTTagCompound();
      itemEntryTag.setByte("Slot", (byte)slotIndex);
      inventory.getStackInSlot(slotIndex).writeToNBT(itemEntryTag);
      itemList.appendTag(itemEntryTag);
      }
    }
  tag.setTag("Items", itemList);
  return tag;
  }

public static void readInventoryFromTag(IInventory inventory, NBTTagCompound tag)
  {
  NBTTagList itemList = tag.getTagList("Items");  
  for (int tagIndex = 0; tagIndex < itemList.tagCount(); ++tagIndex)
    {
    NBTTagCompound itemStackTag = (NBTTagCompound)itemList.tagAt(tagIndex);
    int slotForItem = itemStackTag.getByte("Slot") & 255;
    if (slotForItem >= 0 && slotForItem < inventory.getSizeInventory())
      {
      inventory.setInventorySlotContents(slotForItem, ItemStack.loadItemStackFromNBT(itemStackTag));
      }
    }
  }

public static List<ItemStackWrapper> getCompactInventoryFromTag(NBTTagCompound tag)
  {
  ArrayList<ItemStackWrapper> stacks = new ArrayList<ItemStackWrapper>();
  NBTTagList list = tag.getTagList("items");  
  for(int i = 0; i < list.tagCount(); i++)
    {
    stacks.add(new ItemStackWrapper((NBTTagCompound)list.tagAt(i)));
    }  
  return stacks;
  }

public static NBTTagCompound getTagForCompactInventory(List<ItemStackWrapper> items)
  {
  NBTTagCompound tag = new NBTTagCompound();
  NBTTagList list = new NBTTagList();
  for(ItemStackWrapper wrap : items)
    {
    list.appendTag(wrap.writeToNBT(new NBTTagCompound()));
    }
  tag.setTag("items", list);
  return tag;
  }

public static List<ItemStackWrapper> getCompactedInventory(IInventory inv, Comparator<ItemStackWrapper> sorter)
  {
  ArrayList<ItemStackWrapper> stacks = new ArrayList<ItemStackWrapper>();
  ItemStack fromInv;
  ItemStackWrapper fromList;  
  for(int i = 0; i < inv.getSizeInventory(); i++)
    {
    fromInv = inv.getStackInSlot(i);
    if(fromInv==null){continue;}
    boolean found = false;
    for(int k = 0; k < stacks.size();k ++)
      {
      fromList = stacks.get(k);
      if(fromList==null){continue;}
      if(fromList.equals(fromInv))
        {
        found = true;
        fromList.getFilter().stackSize+=fromInv.stackSize;
        //found item, increment counts
        break;
        }
      }
    if(!found)
      {
      stacks.add(new ItemStackWrapper(fromInv));
      }
    }  
  if(sorter!=null)
    {
    Collections.sort(stacks, sorter);
    }
  return stacks;
  }

public static ItemStack loadStackFromTag(NBTTagCompound tag)
  {
  int itemID = tag.getShort("id");
  int stackSize = tag.getInteger("ICount");
  int itemDamage = tag.getShort("Damage");
  if(stackSize==0)
    {
    stackSize=1;
    }  
  if ( itemDamage < 0)
    {
    itemDamage = 0;
    }
  ItemStack stack = new ItemStack(itemID, stackSize, itemDamage);  
  if (tag.hasKey("tag"))
    {
    stack.setTagCompound(tag.getCompoundTag("tag"));
    }
  return stack;
  }

public static NBTTagCompound writeItemStackToTag(ItemStack stack, NBTTagCompound tag)
  {
  tag.setShort("id", (short) stack.itemID);
  tag.setShort("Damage", (short) stack.getItemDamage());
  tag.setInteger("ICount", stack.stackSize);
  if(stack.hasTagCompound())
    {
    tag.setCompoundTag("tag", stack.getTagCompound());
    }  
  return tag;
  }

public static boolean isSlotPresentInIndices(int slot, int[] indices)
  {
  for(int k : indices)
    {
    if(slot==k)
      {
      return true;
      }
    }
  return false;
  }

public static int getFoodValue(IInventory inv, int firstSlot, int lastSlot)
  {
  ItemStack fromSlot = null;
  int foodValue = 0;
  for(int i = firstSlot; i < inv.getSizeInventory() && i <=lastSlot; i++)
    {
    fromSlot = inv.getStackInSlot(i);
    if(fromSlot==null){continue;}
    if(fromSlot.getItem() instanceof ItemFood && fromSlot.itemID != Item.rottenFlesh.itemID)
      {
      foodValue += ((ItemFood)fromSlot.getItem()).getHealAmount() * fromSlot.stackSize;
      }
    }
  return foodValue;
  }

public static void tryRemoveFoodValue(IInventory inv, int firstSlot, int lastSlot, int foodValue)
  {
  ItemStack fromSlot = null;
  int stackValue;
  int perItem;
  for(int i = firstSlot; i < inv.getSizeInventory() && i <=lastSlot; i++)
    {
    fromSlot = inv.getStackInSlot(i);
    if(fromSlot==null){continue;}
    if(fromSlot.getItem() instanceof ItemFood && fromSlot.itemID!=Item.rottenFlesh.itemID)
      {
      perItem = ((ItemFood)fromSlot.getItem()).getHealAmount();
      if(perItem<=0){continue;}
      stackValue = perItem * fromSlot.stackSize;
      if(stackValue > foodValue)
        {
        int remaining = stackValue - foodValue;
        fromSlot.stackSize = remaining/perItem;
        foodValue = 0;
        }
      else
        {
        foodValue-=stackValue;
        inv.setInventorySlotContents(i, null);
        }
      if(fromSlot.stackSize==0)
        {
        inv.setInventorySlotContents(i, null);
        }
      }
    if(foodValue<=0)
      {
      break;
      }
    }
  }

public static int getCountOf(IInventory inv, ItemStack filter, int[] slotIndices)
  {
  if(inv.getSizeInventory()==0)
    {
    return 0;
    }
  ItemStack fromSlot = null;
  int qtyFound = 0;
  for(int i = 0; i < slotIndices.length; i++)
    {
    fromSlot = inv.getStackInSlot(slotIndices[i]);
    if(fromSlot==null){continue;}
    if(doItemsMatch(fromSlot, filter))
      {
      qtyFound += fromSlot.stackSize;
      }
    }
  return qtyFound;
  }

public static int getCountOf(IInventory inv, ItemStack filter, int firstSlot, int lastSlot)
  {
  if(inv.getSizeInventory()==0)
    {
    return 0;
    }
  ItemStack fromSlot = null;
  int qtyFound = 0;
  firstSlot = firstSlot < 0 ? 0 : firstSlot >= inv.getSizeInventory() ? inv.getSizeInventory() - 1 : firstSlot;
  lastSlot = lastSlot<0 ? 0 : lastSlot>=inv.getSizeInventory() ? inv.getSizeInventory() - 1 : lastSlot;
  for(int i = firstSlot; i <= lastSlot; i++)
    {
    fromSlot = inv.getStackInSlot(i);
    if(fromSlot==null){continue;}
    if(doItemsMatch(fromSlot, filter))
      {
      qtyFound += fromSlot.stackSize;
      }
    }
  return qtyFound;
  }

public static int tryRemoveItems(IInventory inv, ItemStack filter, int qty, int firstSlot, int lastSlot)
  {
  if(filter==null || inv.getSizeInventory()==0)
    {
    return 0;
    }
  ItemStack fromSlot = null;
  int qtyLeft = qty;
  firstSlot = firstSlot < 0 ? 0 : firstSlot >= inv.getSizeInventory() ? inv.getSizeInventory() - 1 : firstSlot;
  lastSlot = lastSlot<0 ? 0 : lastSlot>=inv.getSizeInventory() ? inv.getSizeInventory() - 1 : lastSlot;
  for(int i = firstSlot; i <= lastSlot; i++)
    {
    fromSlot = inv.getStackInSlot(i);
    if(fromSlot==null){continue;}
    if(fromSlot.itemID==filter.itemID && fromSlot.getItemDamage()==filter.getItemDamage() && ItemStack.areItemStackTagsEqual(fromSlot, filter))
      {
      int howMany = fromSlot.stackSize > qty? qty : fromSlot.stackSize;
      qtyLeft -= howMany;
      fromSlot.stackSize-= howMany;
      if(fromSlot.stackSize<=0)
        {
        inv.setInventorySlotContents(i, null);
        }
      if(qtyLeft<=0)
        {
        return 0;
        }
      }    
    }  
  return qtyLeft;
  }

public static int tryRemoveItems(IInventory inv, ItemStack filter, int qty, int [] slots)
  {
  if(filter==null || inv.getSizeInventory()==0)
    {
    return 0;
    }
  ItemStack fromSlot = null;
  int qtyLeft = qty;  
  int slot;
  for(int i = 0; i < slots.length; i++)
    {
    slot = slots[i];
    fromSlot = inv.getStackInSlot(slot);
    if(fromSlot==null){continue;}
    if(fromSlot.itemID==filter.itemID && fromSlot.getItemDamage()==filter.getItemDamage() && ItemStack.areItemStackTagsEqual(fromSlot, filter))
      {
      int howMany = fromSlot.stackSize > qty? qty : fromSlot.stackSize;
      qtyLeft -= howMany;
      fromSlot.stackSize-= howMany;
      if(fromSlot.stackSize<=0)
        {
        inv.setInventorySlotContents(slot, null);
        }
      if(qtyLeft<=0)
        {
        return 0;
        }
      }    
    }  
  return qtyLeft;
  }

public static ItemStack getItems(IInventory inv, ItemStack filter, int max, int firstSlot, int lastSlot)
  {
  if(filter==null|| inv.getSizeInventory()==0){ return null;}
  ItemStack toReturn = null;
  ItemStack fromSlot = null;
  max = max > filter.getMaxStackSize() ? filter.getMaxStackSize() : max;
  firstSlot = firstSlot < 0 ? 0 : firstSlot >= inv.getSizeInventory() ? inv.getSizeInventory() - 1 : firstSlot;
  lastSlot = lastSlot<0 ? 0 : lastSlot>=inv.getSizeInventory() ? inv.getSizeInventory() - 1 : lastSlot;
  for(int i = firstSlot; i <= lastSlot; i ++)
    {
    fromSlot = inv.getStackInSlot(i);
    if(fromSlot!=null)
      {
      if(fromSlot.itemID==filter.itemID && fromSlot.getItemDamage()==filter.getItemDamage() && ItemStack.areItemStackTagsEqual(fromSlot, filter))
        {
        if(toReturn==null)
          {
          toReturn = ItemStack.copyItemStack(fromSlot);
          toReturn.stackSize = 0;
          }
        int howMany = max - toReturn.stackSize;
        howMany = toReturn.stackSize + howMany > toReturn.getMaxStackSize() ? toReturn.getMaxStackSize()-toReturn.stackSize : howMany;
        howMany = howMany > fromSlot.stackSize? fromSlot.stackSize : howMany;
        if(howMany==0)
          {
          continue;
          }
        fromSlot.stackSize-=howMany;
        toReturn.stackSize+=howMany;
        if(fromSlot.stackSize==0)
          {
          inv.setInventorySlotContents(i, null);
          }
        }
      }
    if(toReturn!=null && (toReturn.stackSize>=max || toReturn.stackSize>=toReturn.getMaxStackSize()))//found 'enough', return
      {
      break;
      }
    }
  return toReturn;
  }

public static int getOccupiedSlots(IInventory inv, int firstSlot, int lastSlot)  
  {
  int count = 0;
  firstSlot = firstSlot < 0 ? 0 : firstSlot >= inv.getSizeInventory() ? inv.getSizeInventory() - 1 : firstSlot;
  lastSlot = lastSlot<0 ? 0 : lastSlot>=inv.getSizeInventory() ? inv.getSizeInventory() - 1 : lastSlot;
  for(int i = firstSlot; i <=lastSlot; i++)
    {
    if(inv.getStackInSlot(i)!=null)
      {
      count++;
      }
    }
  return count;
  }

public static int getEmptySlots(IInventory inv, int firstSlot, int lastSlot)
  {
  int count = 0;
  firstSlot = firstSlot < 0 ? 0 : firstSlot >= inv.getSizeInventory() ? inv.getSizeInventory() - 1 : firstSlot;
  lastSlot = lastSlot<0 ? 0 : lastSlot>=inv.getSizeInventory() ? inv.getSizeInventory() - 1 : lastSlot;
  for(int i = firstSlot; i <= lastSlot; i++)
    {
    if(inv.getStackInSlot(i)==null)
      {
      count++;
      }
    }
  return count;
  }

public static ItemStack tryMergeStack(IInventory inv, ItemStack toMerge, int side)
  {
  if(side<0 || side>5)
    {    
    return tryMergeStack(inv, toMerge, getSlotIndexMap(inv));
    }
  if(inv instanceof ISidedInventory)
    {
    return tryMergeStack(inv, toMerge, ((ISidedInventory)inv).getAccessibleSlotsFromSide(side));
    }
  return tryMergeStack(inv, toMerge, getSlotIndexMap(inv));
  }

public static int[] getSlotIndexMap(IInventory inv)
  {
  int[] indices = new int[inv.getSizeInventory()];
  for(int i = 0; i < indices.length; i++)
    {
    indices[i] = i;
    }
  return indices;
  }

public static ItemStack tryMergeStack(IInventory inv, ItemStack toMerge, int[] slotIndices)
  {
  if(slotIndices==null)
    {
    return toMerge;
    } 
  if(toMerge==null)
    {
    return null;
    }
  if(inv.getSizeInventory()==0)
    {
    return toMerge;
    }
  ItemStack fromSlot = null;
  int slot = 0;
  for(int i = 0; i<slotIndices.length; i++)
    {
    slot = slotIndices[i];
    fromSlot = inv.getStackInSlot(slot);
    if(fromSlot==null || !inv.isItemValidForSlot(slot, toMerge))//skip emtpy slots this pass, we're trying to merge partial stacks first
      {
      continue;
      }
    else if(fromSlot.itemID==toMerge.itemID && fromSlot.getItemDamage()==toMerge.getItemDamage() && ItemStack.areItemStackTagsEqual(fromSlot, toMerge))
      {
      int decrAmt = fromSlot.getMaxStackSize() - fromSlot.stackSize;
      decrAmt = decrAmt > toMerge.stackSize ? toMerge.stackSize : decrAmt;
      toMerge.stackSize -= decrAmt;
      fromSlot.stackSize +=decrAmt;
      }
    if(toMerge.stackSize<=0)
      {  
      return null;
      }
    }
  for(int i = 0; i<slotIndices.length; i++)
    {
    slot = slotIndices[i];
    fromSlot = inv.getStackInSlot(slot);
    if(fromSlot==null && inv.isItemValidForSlot(slot, toMerge))//place in slot
      {      
      inv.setInventorySlotContents(slot, toMerge);
      toMerge = null;
      return null;
      }
    }
  if(toMerge!=null && toMerge.stackSize<=0)
    {  
    return null;
    }
  return toMerge;
  }

public static boolean canHoldItem(IInventory inv, ItemStack filter, int qty, int firstSlot, int lastSlot)
  {
  if(filter==null){return false;}
  int qtyLeft = qty;
  ItemStack fromSlot = null;
  firstSlot = firstSlot < 0 ? 0 : firstSlot >= inv.getSizeInventory() ? inv.getSizeInventory() - 1 : firstSlot;
  lastSlot = lastSlot<0 ? 0 : lastSlot>=inv.getSizeInventory() ? inv.getSizeInventory() - 1 : lastSlot;
  for(int i = firstSlot ; i <= lastSlot; i ++)
    {
    if(!inv.isItemValidForSlot(i, filter)){continue;}
    fromSlot = inv.getStackInSlot(i);
    if(fromSlot==null)//emtpy slot, decr by entire stack size
      {
      qtyLeft -= filter.getMaxStackSize();
      }
    else
      {
      if(fromSlot.itemID==filter.itemID && fromSlot.getItemDamage()==filter.getItemDamage() && ItemStack.areItemStackTagsEqual(fromSlot, filter))
        {
        qtyLeft -= fromSlot.getMaxStackSize()-fromSlot.stackSize;
        }
      }
    if(qtyLeft<=0)
      {
      return true;
      }
    }
  return false;
  }

public static boolean canHoldItem(IInventory inv, ItemStack filter, int qty, int [] slots)
  {
  if(filter==null){return false;}
  int qtyLeft = qty;
  ItemStack fromSlot = null;
  int slot;
  for(int i = 0 ; i < slots.length; i ++)
    {
    slot = slots[i];
    if(!inv.isItemValidForSlot(slot, filter)){continue;}
    fromSlot = inv.getStackInSlot(slot);
    if(fromSlot==null)//emtpy slot, decr by entire stack size
      {
      qtyLeft -= filter.getMaxStackSize();
      }
    else
      {
      if(fromSlot.itemID==filter.itemID && fromSlot.getItemDamage()==filter.getItemDamage() && ItemStack.areItemStackTagsEqual(fromSlot, filter))
        {
        qtyLeft -= fromSlot.getMaxStackSize()-fromSlot.stackSize;
        }
      }
    if(qtyLeft<=0)
      {
      return true;
      }
    }
  return false;
  }

public static boolean containsAtLeast(IInventory inv, ItemStack filter, int qty, int firstSlot, int lastSlot)
  {
  if(filter==null|| inv.getSizeInventory()==0)
    {
    return false;
    }
  ItemStack fromSlot = null;
  int foundQty = 0;
  firstSlot = firstSlot < 0 ? 0 : firstSlot >= inv.getSizeInventory() ? inv.getSizeInventory() - 1 : firstSlot;
  lastSlot = lastSlot<0 ? 0 : lastSlot>=inv.getSizeInventory() ? inv.getSizeInventory() - 1 : lastSlot;
  for(int i = firstSlot; i <= lastSlot; i++)
    {
    fromSlot = inv.getStackInSlot(i);
    if(fromSlot==null)
      {
      continue;
      }
    if(fromSlot.itemID==filter.itemID && fromSlot.getItemDamage()==filter.getItemDamage() && ItemStack.areItemStackTagsEqual(filter, fromSlot))
      {
      foundQty += fromSlot.stackSize;
      if(foundQty>=qty)
        {
        return true;
        }
      }
    }
  return false;
  }

public static boolean containsAtLeast(IInventory inv, ItemStack filter, int qty, int[] slotIndices)
  {
  if(filter==null|| inv.getSizeInventory()==0)
    {
    return false;
    }
  ItemStack fromSlot = null;
  int foundQty = 0;  
  int slot = 0;
  for(int i = 0; i < slotIndices.length; i++)
    {
    slot = slotIndices[i];
    fromSlot = inv.getStackInSlot(slot);
    if(fromSlot==null)
      {
      continue;
      }
    if(fromSlot.itemID==filter.itemID && fromSlot.getItemDamage()==filter.getItemDamage() && ItemStack.areItemStackTagsEqual(filter, fromSlot))
      {
      foundQty += fromSlot.stackSize;
      if(foundQty>=qty)
        {
        return true;
        }
      }
    }
  return false;
  }

public static int canHoldMore(IInventory inv, ItemStack filter, int firstSlot, int lastSlot)
  {
  if(filter==null || inv.getSizeInventory()==0)
    {
    return 0;
    }
  firstSlot = firstSlot < 0 ? 0 : firstSlot >= inv.getSizeInventory() ? inv.getSizeInventory() - 1 : firstSlot;
  lastSlot = lastSlot<0 ? 0 : lastSlot>=inv.getSizeInventory() ? inv.getSizeInventory() - 1 : lastSlot;
  int availCount = 0;
  int emptySlots = getEmptySlots(inv, firstSlot, lastSlot);
  if(emptySlots>0)
    {
    availCount = filter.getMaxStackSize() * emptySlots;
    }
  if(filter.getMaxStackSize()>1)
    {
    ItemStack fromSlot;
    for(int i = firstSlot; i <= lastSlot; i++)
      {
      fromSlot = inv.getStackInSlot(i);
      if(fromSlot!=null)
        {
        if(fromSlot.itemID==filter.itemID && fromSlot.getItemDamage()==filter.getItemDamage() && ItemStack.areItemStackTagsEqual(filter, fromSlot))
          {
          availCount += filter.getMaxStackSize() - fromSlot.stackSize;
          }
        }
      }
    }
  return availCount;
  }

public static boolean doItemsMatch(ItemStack a, ItemStack b)
  {
  if(a==null || b == null)
    {
    return false;
    }
  if(a.itemID==b.itemID && a.getItemDamage()==b.getItemDamage() && ItemStack.areItemStackTagsEqual(a, b))
    {
    return true;
    }  
  return false;
  }

public static void dropItemInWorld(World world, ItemStack item, double x, double y, double z)
  {
  if(item==null || world==null )
    {
    return;
    }
  EntityItem entityToSpawn;
  x += random.nextFloat() * 0.6f - 0.3f;
  y += random.nextFloat() * 0.6f + 1 - 0.3f;
  z += random.nextFloat() * 0.6f - 0.3f;
  entityToSpawn = new EntityItem(world, x, y, z, item);
  entityToSpawn.setPosition(x, y, z);
  world.spawnEntityInWorld(entityToSpawn);      
  }

public static void dropInventoryInWorld(World world, IInventory localInventory, double x, double y, double z)
  {
  if(world.isRemote)
    {
    return;
    }
  if (localInventory != null)
    {
    ItemStack stack;
    for(int i = 0; i < localInventory.getSizeInventory(); i++)
      {      
      stack = localInventory.getStackInSlotOnClosing(i);      
      if(stack==null)
        {
        continue;
        }
      dropItemInWorld(world, stack, x, y, z);      
      }
    }
  }

public static List<ItemStack> getCompactResourcesForRecipe(ShapedRecipes recipe)
  {
  return getCompactedItemList(Arrays.asList(recipe.recipeItems));
  }

public static List<ItemStack> getCompactResourcesForRecipe(ShapelessRecipes recipe)
  {
  return getCompactedItemList(recipe.recipeItems);
  }

public static List<ItemStack> getCompactedItemList(Collection<ItemStack> items)
  {
  ArrayList<ItemStack> foundStacks = new ArrayList<ItemStack>();
  for(ItemStack stack : items)
    {
    if(stack==null){continue;}
    boolean found = false;
    for(ItemStack test : foundStacks)
      {
      if(stack.itemID==test.itemID && stack.getItemDamage()==test.getItemDamage() && ItemStack.areItemStackTagsEqual(stack, test))
        {        
        test.stackSize += stack.stackSize;
        found = true;
        break;
        }
      }
    if(!found)
      {
      foundStacks.add(stack.copy());
      }
    }  
  return foundStacks;
  }

}
