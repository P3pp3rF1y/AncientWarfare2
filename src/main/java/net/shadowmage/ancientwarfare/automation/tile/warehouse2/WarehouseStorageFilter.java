package net.shadowmage.ancientwarfare.automation.tile.warehouse2;

import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.inventory.ItemQuantityMap.ItemHashEntry;

public class WarehouseStorageFilter
{

ItemHashEntry hashKey;
ItemStack item;

public WarehouseStorageFilter(ItemStack filter)
  {
  item = filter;
  hashKey = filter==null ? null : new ItemHashEntry(filter);
  }

public ItemStack getFilterItem()
  {
  return item;
  }

public void setFilterItem(ItemStack itemStack)
  {
  item = itemStack;
  hashKey = item==null ? null : new ItemHashEntry(item);
  }

}
