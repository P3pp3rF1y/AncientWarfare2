package net.shadowmage.ancientwarfare.automation.tile.warehouse2;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.shadowmage.ancientwarfare.automation.tile.warehouse.WorkSiteWarehouse;
import net.shadowmage.ancientwarfare.core.inventory.InventorySlotlessBasic;
import net.shadowmage.ancientwarfare.core.inventory.ItemQuantityMap;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.WorldTools;

public class TileWarehouseStorage extends TileControlled implements IWarehouseStorageTile
{

InventorySlotlessBasic inventory;
List<WarehouseStorageFilter> filters = new ArrayList<WarehouseStorageFilter>();

public TileWarehouseStorage()
  {
  inventory = new InventorySlotlessBasic( 9 * 64 );
  }

@Override
protected void updateTile()
  {

  }

@Override
protected void searchForController()
  {
  BlockPosition pos = new BlockPosition(xCoord, yCoord, zCoord);
  BlockPosition min = pos.copy();
  BlockPosition max = min.copy();
  min.offset(-16, -4, -16);
  max.offset(16, 4, 16);
  for(TileEntity te : WorldTools.getTileEntitiesInArea(worldObj, min.x, min.y, min.z, max.x, max.y, max.z))
    {
    if(te instanceof TileWarehouseBase)
      {
      TileWarehouseBase twb = (TileWarehouseBase)te;
      if(BlockTools.isPositionWithinBounds(pos, twb.getWorkBoundsMin(), twb.getWorkBoundsMax()))
        {
        twb.addStorageTile(this);
        break;
        }
      }
    }
  }

@Override
protected boolean isValidController(IControllerTile tile)
  {
  return tile instanceof TileWarehouseBase;//TODO validate bounding area
  }

@Override
public int getStorageAdditionSize()
  {
  return 9*64;
  }

@Override
public void onWarehouseInventoryUpdated(WorkSiteWarehouse warehouse)
  {
  
  }

@Override
public List<WarehouseStorageFilter> getFilters()
  {
  return filters;
  }

@Override
public void setFilters(List<WarehouseStorageFilter> filters)
  {  
  List<WarehouseStorageFilter> old = new ArrayList<WarehouseStorageFilter>();
  old.addAll(this.filters);
  this.filters.clear();
  this.filters.addAll(filters);
  ((TileWarehouse)this.getController()).onStorageFilterChanged(this, old, this.filters);
  }

@Override
public void addItems(ItemQuantityMap map)
  {
  inventory.getItems(map);
  }

@Override
public int getQuantityStored(ItemStack filter)
  {
  return inventory.getQuantityStored(filter);
  }

@Override
public int getAvailableSpaceFor(ItemStack filter)
  {
  return inventory.getAvailableSpaceFor(filter);
  }

@Override
public int extractItem(ItemStack filter, int amount)
  {
  return inventory.extractItem(filter, amount);
  }

@Override
public int insertItem(ItemStack filter, int amount)
  {
  return inventory.insertItem(filter, amount);
  }

@Override
public void readFromNBT(NBTTagCompound tag)
  {
  super.readFromNBT(tag);
  inventory.readFromNBT(tag.getCompoundTag("inventory"));
  }

@Override
public void writeToNBT(NBTTagCompound tag)
  {
  super.writeToNBT(tag);
  tag.setTag("inventory", inventory.writeToNBT(new NBTTagCompound()));
  }

}
