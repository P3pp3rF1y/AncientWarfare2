package net.shadowmage.ancientwarfare.automation.tile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class WarehouseItemMap
{

private static ComparatorWarehouseItemFilter comparator = new ComparatorWarehouseItemFilter();

Set<IWarehouseStorageTile> generalStorage = new HashSet<IWarehouseStorageTile>();
Map<Item, ItemFilterEntry> filteredStorage = new HashMap<Item, ItemFilterEntry>();

public void addStorageTile(IWarehouseStorageTile tile)
  {
  List<WarehouseItemFilter> filters = tile.getFilters();
  if(filters.isEmpty() && tile.isGeneralStorage())
    {
    generalStorage.add(tile);    
    return;
    }
  
  Item item;
  for(WarehouseItemFilter filter : filters)
    {
    if(filter.getFilterItem()==null)
      {    
      continue;
      }
    item = filter.getFilterItem().getItem();    
    if(!filteredStorage.containsKey(item))
      {
      filteredStorage.put(item, new ItemFilterEntry());
      }
    filteredStorage.get(item).addFilter(tile, filter);
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
  for(WarehouseItemFilter filter : filters)
    {
    item = filter.getFilterItem().getItem();
    if(item==null){continue;}
    if(filteredStorage.containsKey(item))
      {
      filteredStorage.get(item).removeFilter(tile, filter);
      }
    }
  }

public void updateStorageFilters(IWarehouseStorageTile tile, List<WarehouseItemFilter> oldFilters, List<WarehouseItemFilter> newFilters)
  {
  removeStorageTile(tile, oldFilters);
  addStorageTile(tile);
  }

public IWarehouseStorageTile getDestinationFor(ItemStack stack)
  {
  if(stack==null || stack.getItem()==null){return null;}
  Item item = stack.getItem();  
  if(filteredStorage.containsKey(item))
    {
    IWarehouseStorageTile tile;
    tile = filteredStorage.get(item).getDestinationFor(stack);
    if(tile!=null){return tile;}
    }  
  for(IWarehouseStorageTile tile : generalStorage)
    {
    if(tile.canHoldMore(stack)){return tile;}
    }  
  return null;
  }

private static final class ComparatorWarehouseItemFilter implements Comparator<WarehouseItemFilter>
{
@Override
public int compare(WarehouseItemFilter o1, WarehouseItemFilter o2)
  {
  return o1.getFilterPriority()-o2.getFilterPriority();  
  }
}

private static final class ItemFilterEntry
{
private List<WarehouseItemFilter> filters = new ArrayList<WarehouseItemFilter>();
private Map<WarehouseItemFilter, IWarehouseStorageTile> map = new HashMap<WarehouseItemFilter, IWarehouseStorageTile>();

private IWarehouseStorageTile getDestinationFor(ItemStack stack)
  {
  IWarehouseStorageTile tile;
  for(WarehouseItemFilter filter : filters)//should be sorted by priority 0....10....max (0=highest priority...)
    {
    if(filter.isItemValid(stack))
      {
      tile = map.get(filter);
      if(tile.canHoldMore(stack))
        {
        return tile;
        }
      }
    }
  return null;
  }

private void addFilter(IWarehouseStorageTile tile, WarehouseItemFilter filter)
  {
  if(!filters.contains(filter))
    {
    filters.add(filter);
    }
  Collections.sort(filters, comparator);
  }

private void removeFilter(IWarehouseStorageTile tile, WarehouseItemFilter filter)
  {
  filters.remove(filter);
  map.remove(filter);  
  Collections.sort(filters, comparator);
  }

}


}
