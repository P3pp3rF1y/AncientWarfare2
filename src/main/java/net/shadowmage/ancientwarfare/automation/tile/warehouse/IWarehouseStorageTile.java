package net.shadowmage.ancientwarfare.automation.tile.warehouse;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.WarehouseStorageFilter;
import net.shadowmage.ancientwarfare.core.inventory.ItemQuantityMap;



public interface IWarehouseStorageTile
{

public int getStorageAdditionSize();

public void onWarehouseInventoryUpdated(WorkSiteWarehouse warehouse);

public List<WarehouseStorageFilter> getFilters();

public void setFilters(List<WarehouseStorageFilter> filters);

public void addItems(ItemQuantityMap map);

int getQuantityStored(ItemStack filter);

int getAvailableSpaceFor(ItemStack filter);

int extractItem(ItemStack filter, int amount);

int insertItem(ItemStack filter, int amount);

}
