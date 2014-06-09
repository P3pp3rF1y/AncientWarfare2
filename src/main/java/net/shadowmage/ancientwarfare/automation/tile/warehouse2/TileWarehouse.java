package net.shadowmage.ancientwarfare.automation.tile.warehouse2;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;


public class TileWarehouse extends TileWarehouseBase
{

public TileWarehouse()
  {
  
  }

@Override
public void handleSlotClick(EntityPlayer player, ItemStack filter)
  {
  if(filter!=null && player.inventory.getItemStack()==null)
    {
    tryGetItem(player, filter);    
    }
  else if(player.inventory.getItemStack()!=null)
    {
    tryAddItem(player, player.inventory.getItemStack());
    }
  }

private void tryAddItem(EntityPlayer player, ItemStack cursorStack)
  {
  List<IWarehouseStorageTile> destinations = new ArrayList<IWarehouseStorageTile>();
  storageMap.getDestinations(cursorStack, destinations);
  int stackSize = cursorStack.stackSize;
  int moved;
  for(IWarehouseStorageTile tile : destinations)
    {
    moved = tile.insertItem(cursorStack, cursorStack.stackSize);
    cursorStack.stackSize-=moved;    
    cachedItemMap.addCount(cursorStack, moved);
    updateViewers();
    if(cursorStack.stackSize<=0){break;}
    }
  if(cursorStack.stackSize<=0)
    {
    player.inventory.setItemStack(null);
    }
  if(stackSize!=cursorStack.stackSize)
    {
    EntityPlayerMP playerMP = (EntityPlayerMP)player;
    playerMP.updateHeldItem();
    }
  }

private void tryGetItem(EntityPlayer player, ItemStack filter)
  {
  List<IWarehouseStorageTile> destinations = new ArrayList<IWarehouseStorageTile>();
  ItemStack newCursorStack = filter.copy();
  newCursorStack.stackSize=0;
  storageMap.getDestinations(filter, destinations);
  int count;
  int toMove;
  for(IWarehouseStorageTile tile : destinations)
    {
    count = tile.getQuantityStored(filter);
    toMove = newCursorStack.getMaxStackSize()-newCursorStack.stackSize;
    toMove = toMove>count? count : toMove;
    if(toMove>0)
      {
      newCursorStack.stackSize+=toMove;
      tile.extractItem(filter, toMove);
      cachedItemMap.decreaseCount(filter, toMove);
      updateViewers();
      }      
    if(newCursorStack.stackSize>=newCursorStack.getMaxStackSize())
      {
      break;
      }
    }
  if(newCursorStack.stackSize>0)
    {
    player.inventory.setItemStack(newCursorStack);
    EntityPlayerMP playerMP = (EntityPlayerMP)player;
    playerMP.updateHeldItem();
    }
  }


}
