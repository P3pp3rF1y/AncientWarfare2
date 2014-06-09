package net.shadowmage.ancientwarfare.automation.tile.warehouse;

import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.util.ItemQuantityMap;



public class TileWarehouseStorageSmall extends TileWarehouseStorageBase
{

public TileWarehouseStorageSmall()
  {
  this.storageAdditionSize = 27*64;
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
