package net.shadowmage.ancientwarfare.automation.tile.warehouse2;

import net.minecraft.item.ItemStack;


public class TileWarehouse extends TileWarehouseBase
{

public TileWarehouse()
  {
  
  }

@Override
protected boolean tryCleanOutput()
  {
  // TODO Auto-generated method stub
  return false;
  }

@Override
protected boolean tryEmptyInput()
  {
  // TODO Auto-generated method stub
  return false;
  }

@Override
protected boolean tryFillOutput()
  {
  // TODO Auto-generated method stub
  return false;
  }

@Override
protected void processDirtyOutputTiles()
  {
  // TODO Auto-generated method stub
  /**
   * loop through output tiles in 'outputToUpdate'
   * if tile needs filled, add to toFill set
   * if tile has extra non-matching items, add to 'toClean' set
   */
  }

@Override
public ItemStack requestItem(ItemStack filter)
  {
  // TODO Auto-generated method stub
  return null;
  }

@Override
public ItemStack mergeItem(ItemStack item)
  {
  // TODO Auto-generated method stub
  return null;
  }

}
