package net.shadowmage.ancientwarfare.automation.tile.warehouse2;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.automation.tile.warehouse.IWarehouseStorageTile;
import net.shadowmage.ancientwarfare.automation.tile.warehouse.WorkSiteWarehouse;
import net.shadowmage.ancientwarfare.core.util.ItemQuantityMap;

public class TileWarehouseStorage extends TileControlled implements IWarehouseStorageTile
{

public TileWarehouseStorage()
  {
  
  }

@Override
protected void updateTile()
  {

  }

@Override
protected void searchForController()
  {

  }

@Override
protected boolean isValidController(IControllerTile tile)
  {
  
  return false;
  }

@Override
public int getStorageAdditionSize()
  {
  // TODO Auto-generated method stub
  return 0;
  }

@Override
public void onWarehouseInventoryUpdated(WorkSiteWarehouse warehouse)
  {
  // TODO Auto-generated method stub
  
  }

@Override
public List<WarehouseStorageFilter> getFilters()
  {
  // TODO Auto-generated method stub
  return null;
  }

@Override
public void setFilters(List<WarehouseStorageFilter> filters)
  {
  // TODO Auto-generated method stub
  
  }

@Override
public void addItems(ItemQuantityMap map)
  {
  // TODO Auto-generated method stub
  
  }

@Override
public int getQuantityStored(ItemStack filter)
  {
  // TODO Auto-generated method stub
  return 0;
  }

@Override
public int getAvailableSpaceFor(ItemStack filter)
  {
  // TODO Auto-generated method stub
  return 0;
  }

@Override
public int extractItem(ItemStack filter, int amount)
  {
  // TODO Auto-generated method stub
  return 0;
  }

@Override
public int insertItem(ItemStack filter, int amount)
  {
  // TODO Auto-generated method stub
  return 0;
  }

}
