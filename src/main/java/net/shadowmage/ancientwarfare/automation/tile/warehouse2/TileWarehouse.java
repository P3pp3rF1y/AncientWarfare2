package net.shadowmage.ancientwarfare.automation.tile.warehouse2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.TileWarehouseInterface.InterfaceEmptyRequest;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.TileWarehouseInterface.InterfaceFillRequest;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;


public class TileWarehouse extends TileWarehouseBase
{

public TileWarehouse()
  {
  
  }

@Override
protected boolean tryEmptyInterfaces()
  {  
  List<TileWarehouseInterface> toEmpty = new ArrayList<TileWarehouseInterface>(interfacesToEmpty);  
  for(TileWarehouseInterface tile : toEmpty)
    {
    if(tryEmptyTile(tile))
      {
      tile.recalcRequests();
      return true;
      }
    }   
  return false;
  }

private boolean tryEmptyTile(TileWarehouseInterface tile)
  {
  List<InterfaceEmptyRequest> reqs = tile.getEmptyRequests();
  for(InterfaceEmptyRequest req : reqs)
    {
    if(tryRemoveFromRequest(tile, req)){return true;}   
    }
  return false;
  }

private boolean tryRemoveFromRequest(TileWarehouseInterface tile, InterfaceEmptyRequest request)
  {
  ItemStack stack = tile.getStackInSlot(request.slotNum);
  if(stack==null){return false;}
  int stackSize = stack.stackSize;
  int moved;
  List<IWarehouseStorageTile> potentialStorage = new ArrayList<IWarehouseStorageTile>();
  storageMap.getDestinations(stack, potentialStorage);
  for(IWarehouseStorageTile dest : potentialStorage)
    {
    moved = dest.insertItem(stack, stack.stackSize);
    if(moved>0)
      {
      cachedItemMap.addCount(stack, moved);
      updateViewers();
      }
    stack.stackSize -= moved;
    if(stack.stackSize!=stackSize)
      {
      if(stack.stackSize<=0)
        {
        tile.inventory.setInventorySlotContents(request.slotNum, null);
        }
      return true;
      }
    }  
  return false;
  }

@Override
protected boolean tryFillInterfaces()
  {
  List<TileWarehouseInterface> toFill = new ArrayList<TileWarehouseInterface>(interfacesToFill);
  for(TileWarehouseInterface tile : toFill)
    {
    if(tryFillTile(tile))
      {
      tile.recalcRequests();
      return true;
      }
    }
  return false;
  }

private boolean tryFillTile(TileWarehouseInterface tile)
  {
  List<InterfaceFillRequest> reqs = tile.getFillRequests();
  for(InterfaceFillRequest req : reqs)
    {
    if(tryFillFromRequest(tile, req)){return true;}
    }
  return false;
  }

private boolean tryFillFromRequest(TileWarehouseInterface tile, InterfaceFillRequest request)
  {  
  List<IWarehouseStorageTile> potentialStorage = new ArrayList<IWarehouseStorageTile>();
  storageMap.getDestinations(request.requestedItem, potentialStorage);
  int found, moved;
  ItemStack stack;
  int stackSize;
  for(IWarehouseStorageTile source : potentialStorage)
    {
    found = source.getQuantityStored(request.requestedItem);
    if(found>0)
      {
      stack = request.requestedItem.copy();
      stack.stackSize = found>stack.getMaxStackSize() ? stack.getMaxStackSize() : found;
      stackSize = stack.stackSize;
      stack = InventoryTools.mergeItemStack(tile.inventory, stack, -1);
      if(stack==null || stack.stackSize!=stackSize)
        {        
        moved = stack==null ? stackSize : stackSize-stack.stackSize;
        source.extractItem(request.requestedItem, moved);
        cachedItemMap.decreaseCount(request.requestedItem, moved);  
        updateViewers();      
        return true;
        }
      }
    }
  return false;
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
