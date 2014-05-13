package net.shadowmage.ancientwarfare.automation.tile;

import java.util.List;


public interface IWarehouseStorageTile
{

public int getStorageAdditionSize();

public void onWarehouseInventoryUpdated(WorkSiteWarehouse warehouse);

public List<WarehouseItemFilter> getFilters();

public void setFilters(List<WarehouseItemFilter> filters);

}
