package net.shadowmage.ancientwarfare.automation.tile;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

public class WarehouseItemMap
{

Set<IWarehouseStorageTile> generalStorage = new HashSet<IWarehouseStorageTile>();
Map<Item, ItemEntry> filteredStorageSpecific = new HashMap<Item, ItemEntry>();
Map<Item, ItemEntry> filteredStorageIgnoreNBT = new HashMap<Item, ItemEntry>();
Map<Item, ItemEntry> filteredStorageIgnoreDmg = new HashMap<Item, ItemEntry>();
Map<Item, ItemEntry> filteredStorageIgnoreDmgNBT = new HashMap<Item, ItemEntry>();

public void addStorageTile(IWarehouseStorageTile tile)
  {
  List<WarehouseItemFilter> filters = tile.getFilters();
  if(filters.isEmpty())
    {
    generalStorage.add(tile);    
    return;
    }
  
  Item item;
  Map<Item, ItemEntry> entryMap;
  for(WarehouseItemFilter filter : filters)
    {
    if(filter.getFilterItem()==null)
      {
      generalStorage.add(tile);
      continue;
      }
    item = filter.getFilterItem().getItem();
    if(item==null){continue;}
    if(filter.isIgnoreDamage() && filter.isIgnoreNBT())
      {
      entryMap = filteredStorageIgnoreDmgNBT;
      }
    else if(filter.isIgnoreDamage())
      {
      entryMap = filteredStorageIgnoreDmg;
      }
    else if(filter.isIgnoreNBT())
      {
      entryMap = filteredStorageIgnoreNBT;
      }
    else
      {
      entryMap = filteredStorageSpecific;
      }
    if(!entryMap.containsKey(item))
      {
      entryMap.put(item, new ItemEntry());
      }
    entryMap.get(item).addTile(tile);
    }
  }

public void removeStorageTile(IWarehouseStorageTile tile, List<WarehouseItemFilter> filters)
  {
  if(filters.isEmpty())
    {
    generalStorage.remove(tile); 
    return;
    }
  
  Item item;
  Map<Item, ItemEntry> entryMap;
  for(WarehouseItemFilter filter : filters)
    {
    if(filter.getFilterItem()==null)
      {
      generalStorage.remove(tile);
      continue;
      }
    item = filter.getFilterItem().getItem();
    if(item==null){continue;}
    if(filter.isIgnoreDamage() && filter.isIgnoreNBT())
      {
      entryMap = filteredStorageIgnoreDmgNBT;
      }
    else if(filter.isIgnoreDamage())
      {
      entryMap = filteredStorageIgnoreDmg;
      }
    else if(filter.isIgnoreNBT())
      {
      entryMap = filteredStorageIgnoreNBT;
      }
    else
      {
      entryMap = filteredStorageSpecific;
      }
    if(entryMap.containsKey(item))
      {
      entryMap.get(item).removeTile(tile);
      }
    }
  }

public void updateStorageFilters(IWarehouseStorageTile tile, List<WarehouseItemFilter> oldFilters, List<WarehouseItemFilter> newFilters)
  {
  removeStorageTile(tile, oldFilters);
  addStorageTile(tile);
  }

public ItemStack mergeItem(ItemStack stack)
  {
  if(stack==null || stack.getItem()==null)
    {
    return stack;
    }
  Item item = stack.getItem();
  if(filteredStorageSpecific.containsKey(item))
    {
    stack = filteredStorageSpecific.get(item).mergeStack(stack);
    if(stack==null){return null;}
    }
  if(filteredStorageIgnoreNBT.containsKey(item))
    {
    stack = filteredStorageIgnoreNBT.get(item).mergeStack(stack);
    if(stack==null){return null;}
    }
  if(filteredStorageIgnoreDmg.containsKey(item))
    {
    stack = filteredStorageIgnoreDmg.get(item).mergeStack(stack);
    if(stack==null){return null;}
    }
  if(filteredStorageIgnoreDmgNBT.containsKey(item))
    {
    stack = filteredStorageIgnoreDmgNBT.get(item).mergeStack(stack);
    if(stack==null){return null;}
    }
  
  for(IWarehouseStorageTile tile : generalStorage)
    {
    stack = tile.addItem(stack);
    if(stack==null)
      {
      break;
      }
    }
  return stack;
  }

private static final class ItemEntry
{

private Set<IWarehouseStorageTile> generalStorage = new HashSet<IWarehouseStorageTile>();

private void addTile(IWarehouseStorageTile tile)
  {
  generalStorage.add(tile);
  }

private void removeTile(IWarehouseStorageTile tile)
  {
  generalStorage.remove(tile);
  }

private ItemStack mergeStack(ItemStack stack)
  {
  for(IWarehouseStorageTile tile : generalStorage)
    {
    if(tile.isItemValid(stack))
      {
      stack = tile.addItem(stack);
      if(stack==null)
        {
        break;
        }
      }
    }
  return stack;
  }

}
}
