package net.shadowmage.ancientwarfare.automation.tile;

import java.util.List;

import net.shadowmage.ancientwarfare.automation.tile.worksite.WorkSiteWarehouse;


public interface IWarehouseStorageTile
{

public int getStorageAdditionSize();

public void onWarehouseInventoryUpdated(WorkSiteWarehouse warehouse);

public List<WarehouseItemFilter> getFilters();

public void setFilters(List<WarehouseItemFilter> filters);

}
