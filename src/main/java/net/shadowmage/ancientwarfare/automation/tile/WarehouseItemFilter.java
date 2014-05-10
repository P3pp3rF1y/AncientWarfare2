package net.shadowmage.ancientwarfare.automation.tile;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

public final class WarehouseItemFilter
{

private ItemStack filterItem;
private boolean ignoreDamage;
private boolean ignoreNBT;
private int filterPriority;

public WarehouseItemFilter(){}

public boolean isItemValid(ItemStack item)
  {
  if(item==null){return false;}
  if(filterItem==null){return false;}//null filter item, invalid filter
  if(item.getItem()!=filterItem.getItem()){return false;}//item not equivalent, obvious mis-match
  if(ignoreDamage && ignoreNBT){return true;}//item was equal, and ignore all else, return true
  else if(ignoreDamage){return ItemStack.areItemStackTagsEqual(item, filterItem);}//item was equal, ignore damage..return true if nbt-tags match
  else if(ignoreNBT){return item.getItemDamage()==filterItem.getItemDamage();}//item was equal, ignore nbt, check if item damages are equal 
  return InventoryTools.doItemStacksMatch(item, filterItem);//finally, items were equal, no ignores' -- check both dmg and tag
  }

public void readFromNBT(NBTTagCompound tag)
  {
  filterPriority = tag.getInteger("priority");
  ignoreDamage = tag.getBoolean("dmg");
  ignoreNBT = tag.getBoolean("nbt"); 
  if(tag.hasKey("filter")){filterItem = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("filter"));}
  }

public NBTTagCompound writeToNBT(NBTTagCompound tag)
  {
  tag.setInteger("priority", filterPriority);
  tag.setBoolean("dmg", ignoreDamage);
  tag.setBoolean("nbt", ignoreNBT);  
  if(filterItem!=null){tag.setTag("filter", filterItem.writeToNBT(new NBTTagCompound()));}  
  return tag;
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

public final int getFilterPriority()
  {
  return filterPriority;
  }

public final void setFilterPriority(int filterPriority)
  {
  this.filterPriority = filterPriority;
  }

@Override
public String toString()
  {
  return "Filter item: "+filterItem + " ignore dmg/nbt:"+ignoreDamage+":"+ignoreNBT;
  }

}
