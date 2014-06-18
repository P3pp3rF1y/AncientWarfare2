package net.shadowmage.ancientwarfare.automation.tile.warehouse2;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.shadowmage.ancientwarfare.core.inventory.ItemSlotFilter;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

public final class WarehouseInterfaceFilter extends ItemSlotFilter
{

private ItemStack filterItem;
private int quantity;

public WarehouseInterfaceFilter(){}

@Override
public boolean isItemValid(ItemStack item)
  {
  if(item==null){return false;}
  if(filterItem==null){return false;}//null filter item, invalid filter
  if(item.getItem()!=filterItem.getItem()){return false;}//item not equivalent, obvious mis-match   
  return InventoryTools.doItemStacksMatch(item, filterItem);//finally, items were equal, no ignores' -- check both dmg and tag
  }

public void readFromNBT(NBTTagCompound tag)
  {
  quantity = tag.getInteger("quantity");
  if(tag.hasKey("filter")){filterItem = InventoryTools.readItemStack(tag.getCompoundTag("filter"));}
  }

public NBTTagCompound writeToNBT(NBTTagCompound tag)
  {
  tag.setInteger("quantity", quantity);
  if(filterItem!=null){tag.setTag("filter", InventoryTools.writeItemStack(filterItem, new NBTTagCompound()));}  
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

public final int getFilterQuantity()
  {
  return quantity;
  }

public final void setFilterQuantity(int filterQuantity)
  {
  this.quantity = filterQuantity;
  }

@Override
public String toString()
  {
  return "Filter item: "+filterItem + " quantity: "+quantity;
  }

public static NBTTagList writeFilterList(List<WarehouseInterfaceFilter> filters)
  {
  NBTTagList list = new NBTTagList();
  for(WarehouseInterfaceFilter filter : filters)
    {
    list.appendTag(filter.writeToNBT(new NBTTagCompound()));
    }
  return list;
  }

public static List<WarehouseInterfaceFilter> readFilterList(NBTTagList list, List<WarehouseInterfaceFilter> filters)
  {
  WarehouseInterfaceFilter filter;
  for(int i = 0; i < list.tagCount(); i++)
    {
    filter = new WarehouseInterfaceFilter();
    filter.readFromNBT(list.getCompoundTagAt(i));   
    filters.add(filter);    
    }
  return filters;
  }

public WarehouseInterfaceFilter copy()
  {
  WarehouseInterfaceFilter filter = new WarehouseInterfaceFilter();
  filter.filterItem = this.filterItem==null? null : this.filterItem.copy();
  filter.quantity = this.quantity;
  return filter;
  }

}
