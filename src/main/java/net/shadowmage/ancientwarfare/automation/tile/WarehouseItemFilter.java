package net.shadowmage.ancientwarfare.automation.tile;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

public final class WarehouseItemFilter
{

private ItemStack filterItem;
private boolean ignoreDamage;
private boolean ignoreNBT;
private int itemCount;

public WarehouseItemFilter(){}

public boolean isItemValid(ItemStack item)
  {
  if(item==null){return false;}
  if(filterItem==null){return true;}//null filter item, use for 'match all'
  if(item.getItem()!=filterItem.getItem()){return false;}//item not equivalent, obvious mis-match
  if(ignoreDamage && ignoreNBT){return true;}//item was equal, and ignore all else, return true
  else if(ignoreDamage){return ItemStack.areItemStackTagsEqual(item, filterItem);}//item was equal, ignore damage..return true if nbt-tags match
  else if(ignoreNBT){return item.getItemDamage()==filterItem.getItemDamage();}//item was equal, ignore nbt, check if item damages are equal 
  return InventoryTools.doItemStacksMatch(item, filterItem);//finally, items were equal, no ignores' -- check both dmg and tag
  }

public void readFromNBT(NBTTagCompound tag)
  {
  itemCount = tag.getInteger("count");
  ignoreDamage = tag.getBoolean("dmg");
  ignoreNBT = tag.getBoolean("nbt"); 
  if(tag.hasKey("filter")){filterItem = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("filter"));}
  }

public NBTTagCompound writeToNBT(NBTTagCompound tag)
  {
  tag.setInteger("count", itemCount);
  tag.setBoolean("dmg", ignoreDamage);
  tag.setBoolean("nbt", ignoreNBT);
  if(filterItem!=null){tag.setTag("filter", filterItem.writeToNBT(new NBTTagCompound()));}  
  return tag;
  }

public final int getItemCount()
  {
  return itemCount;
  }

public final void setItemCount(int itemCount)
  {
  this.itemCount = itemCount;
  }

public final ItemStack getFilterItem()
  {
  return filterItem;
  }

public final void setFilterItem(ItemStack item)
  {
  this.filterItem = item;
  }

public final boolean isIgnoreDamage()
  {
  return ignoreDamage;
  }

public final void setIgnoreDamage(boolean ignoreDamage)
  {
  this.ignoreDamage = ignoreDamage;
  }

public final boolean isIgnoreNBT()
  {
  return ignoreNBT;
  }

public final void setIgnoreNBT(boolean ignoreNBT)
  {
  this.ignoreNBT = ignoreNBT;
  }

@Override
public String toString()
  {
  return "Filter item: "+filterItem + " ignore dmg/nbt:"+ignoreDamage+":"+ignoreNBT;
  }

}
