package net.shadowmage.ancientwarfare.automation.tile.warehouse2;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;


public class TileWarehouse extends TileWarehouseBase
{

public TileWarehouse()
  {
  
  }

@Override
public ForgeDirection getOrientation()
  {
  return ForgeDirection.getOrientation(getBlockMetadata());
  }

@Override
public void handleSlotClick(EntityPlayer player, ItemStack filter, boolean shiftClick)
  {
  if(filter!=null && player.inventory.getItemStack()==null)
    {
    tryGetItem(player, filter, shiftClick);    
    }
  else if(filter==null && player.inventory.getItemStack()!=null)
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
    changeCachedQuantity(cursorStack, moved);
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

private void tryGetItem(EntityPlayer player, ItemStack filter, boolean shiftClick)
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
      changeCachedQuantity(filter, -toMove);
      updateViewers();
      }      
    if(newCursorStack.stackSize>=newCursorStack.getMaxStackSize())
      {
      break;
      }
    }  
  if(newCursorStack.stackSize>0)
    {
    if(shiftClick)
      {
      newCursorStack = InventoryTools.mergeItemStack(player.inventory, newCursorStack, -1);
      }
    if(newCursorStack!=null)
      {
      player.inventory.setItemStack(newCursorStack);
      EntityPlayerMP playerMP = (EntityPlayerMP)player;
      playerMP.updateHeldItem();      
      }
    }
  }

}
