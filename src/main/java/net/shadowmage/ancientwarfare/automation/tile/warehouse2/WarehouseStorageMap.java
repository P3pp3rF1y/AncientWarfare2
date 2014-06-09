package net.shadowmage.ancientwarfare.automation.tile.warehouse2;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.automation.tile.warehouse.IWarehouseStorageTile;
import net.shadowmage.ancientwarfare.core.inventory.ItemQuantityMap.ItemHashEntry;

public class WarehouseStorageMap
{

Set<IWarehouseStorageTile> unfilteredStorage = new HashSet<IWarehouseStorageTile>();
Map<ItemHashEntry, Set<IWarehouseStorageTile>> storageMap = new HashMap<ItemHashEntry, Set<IWarehouseStorageTile>>();

public final void addStorageTile(IWarehouseStorageTile tile)
  {    
  addTileFilters(tile, tile.getFilters());
  }

public final void removeStorageTile(IWarehouseStorageTile tile)
  {
  removeTileFilters(tile, tile.getFilters());
  }

public final void updateTileFilters(IWarehouseStorageTile tile, List<WarehouseStorageFilter> oldFilters, List<WarehouseStorageFilter> newFilters)
  {
  removeTileFilters(tile, oldFilters);
  addTileFilters(tile, newFilters);
  }

public final Set<IWarehouseStorageTile> getFilterSetFor(ItemStack filter)
  {
  return getOrCreateStorageSet(new ItemHashEntry(filter));
  }

public final Set<IWarehouseStorageTile> getUnFilteredSet()
  {
  return unfilteredStorage;
  }

public final void getDestinations(ItemStack filter, List<IWarehouseStorageTile> out)
  {
  ItemHashEntry key = new ItemHashEntry(filter);
  Set<IWarehouseStorageTile> set = getOrCreateStorageSet(key);
  out.addAll(set);
  out.addAll(unfilteredStorage);
  }

private Set<IWarehouseStorageTile> getOrCreateStorageSet(ItemHashEntry key)
  {
  Set<IWarehouseStorageTile> set = storageMap.get(key);
  if(set==null)
    {
    set = new HashSet<IWarehouseStorageTile>();
    storageMap.put(key, set);
    }
  return set;
  } 

private void removeTileFilters(IWarehouseStorageTile tile, List<WarehouseStorageFilter> filters)
  {
  if(filters.isEmpty())
    {
    unfilteredStorage.remove(tile);
    }
  else
    {
    for(WarehouseStorageFilter filter : filters)
      {
      if(filter.hashKey==null){continue;}
      getOrCreateStorageSet(filter.hashKey).remove(tile);
      }
    }  
  }

private void addTileFilters(IWarehouseStorageTile tile, List<WarehouseStorageFilter> filters)
  {
  if(filters.isEmpty())
    {
    unfilteredStorage.add(tile);
    }
  else
    {
    for(WarehouseStorageFilter filter : filters)
      {
      if(filter.hashKey==null){continue;}
      getOrCreateStorageSet(filter.hashKey).add(tile);
      }
    }
  }

}
