package net.shadowmage.ancientwarfare.automation.tile;

import java.util.ArrayList;
import java.util.List;

import com.sun.org.apache.xalan.internal.xsltc.dom.FilterIterator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.core.util.WorldTools;

public class TileWarehouseStorageDeep extends TileEntity implements IInteractableTile, IWarehouseStorageTile, IControlledTile
{

String inventoryName = "";
int quantity = 0;
ItemStack filterStack;
ItemStack[] inventorySlots = new ItemStack[2];
WarehouseItemFilter filter;

BlockPosition controllerPosition;
boolean init = false;

public TileWarehouseStorageDeep()
  {
  
  }

@Override
public void updateEntity()
  {
  if(!init)
    {
    init = true;
    AWLog.logDebug("scanning for controller...");
    for(TileEntity te : (List<TileEntity>)WorldTools.getTileEntitiesInArea(worldObj, xCoord-16, yCoord-4, zCoord-16, xCoord+16, yCoord+4, zCoord+16))
      {
      if(te instanceof WorkSiteWarehouse)
        {
        WorkSiteWarehouse warehouse = (WorkSiteWarehouse)te;
        BlockPosition min = warehouse.getWorkBoundsMin();
        BlockPosition max = warehouse.getWorkBoundsMax();
        if(xCoord>=min.x && xCoord<=max.x && yCoord>=min.y && yCoord<=max.y && zCoord>=min.z && zCoord<=max.z)
          {
          warehouse.addStorageBlock(this);
          controllerPosition = new BlockPosition(warehouse.xCoord, warehouse.yCoord, warehouse.zCoord);
          break;
          }
        }
      } 
    }
  }

@Override
public void validate()
  {
  super.validate();
  init = false;
  }

@Override
public void invalidate()
  {  
  super.invalidate();
  this.init = false;
  if(controllerPosition!=null && worldObj.blockExists(controllerPosition.x, controllerPosition.y, controllerPosition.z))
    {
    TileEntity te = worldObj.getTileEntity(controllerPosition.x, controllerPosition.y, controllerPosition.z);
    if(te instanceof WorkSiteWarehouse)
      {
      WorkSiteWarehouse warehouse = (WorkSiteWarehouse)te;
      BlockPosition min = warehouse.getWorkBoundsMin();
      BlockPosition max = warehouse.getWorkBoundsMax();
      if(xCoord>=min.x && xCoord<=max.x && yCoord>=min.y && yCoord<=max.y && zCoord>=min.z && zCoord<=max.z)
        {
        warehouse.removeStorageBlock(this);
        }
      }
    }
  controllerPosition = null;
  }

@Override
public String toString()
  {
  return "Deep Storage tile. Filter: "+filterStack+" size: "+quantity+" name: "+inventoryName+" location: "+xCoord+","+yCoord+","+zCoord;
  }

@Override
public int getSizeInventory()
  {
  return 2;
  }

@Override
public ItemStack getStackInSlot(int var1)
  {
  return inventorySlots[var1];
  }

@Override
public ItemStack decrStackSize(int slotIndex, int amount)
  {
  ItemStack slotStack = inventorySlots[slotIndex];
  if(slotStack!=null)
    {
    if(amount>slotStack.stackSize){amount = slotStack.stackSize;}
    if(amount>slotStack.getMaxStackSize()){amount = slotStack.getMaxStackSize();}
    ItemStack returnStack = slotStack.copy();
    slotStack.stackSize-=amount;
    returnStack.stackSize = amount;  
    if(slotStack.stackSize<=0)
      {
      inventorySlots[slotIndex]=null;
      }
    markDirty();
    return returnStack;
    }
  return null;
  }

@Override
public ItemStack getStackInSlotOnClosing(int var1)
  {
  ItemStack slotStack = inventorySlots[var1];
  inventorySlots[var1] = null;
  markDirty();
  return slotStack;
  }

@Override
public void setInventorySlotContents(int var1, ItemStack var2)
  {
  inventorySlots[var1] = var2;
  markDirty();
  }

@Override
public String getInventoryName()
  {
  return inventoryName;
  }

@Override
public boolean hasCustomInventoryName()
  {
  return false;
  }

@Override
public int getInventoryStackLimit()
  {
  return 64;
  }

@Override
public boolean isUseableByPlayer(EntityPlayer var1)
  {
  return true;
  }

@Override
public void markDirty()
  {  
  super.markDirty(); 
  validateSlots();
  refillSlot1();
  emptySlot2();

  }

private void validateSlots()
  {
  ItemStack slot1 = inventorySlots[0];
  ItemStack slot2 = inventorySlots[1];
  if(filterStack==null && slot1!=null)//was empty, fresh assignment
    {
    filterStack = slot1.copy();
    quantity = filterStack.stackSize;
    }
  if(filterStack==null && slot2!=null)//was empty, fresh assignment
    {
    filterStack = slot2.copy();
    quantity = filterStack.stackSize;
    }
  }

private void refillSlot1()
  {
  ItemStack slot1 = inventorySlots[0];
  if(filterStack!=null && slot1!=null && InventoryTools.doItemStacksMatch(filterStack, slot1))
    {
    if(slot1.stackSize<slot1.getMaxStackSize())
      {
      int qty = slot1.getMaxStackSize()-slot1.stackSize;
      if(qty>quantity){qty = quantity;}
      slot1.stackSize+=qty;
      quantity-=qty;
      }
    }
  else if(filterStack!=null && slot1==null)
    {
    if(quantity<=0)
      {
      
      }
    else
      {
      slot1 = filterStack.copy();
      slot1.stackSize = 0;
      int qty = quantity;
      if(qty>slot1.getMaxStackSize()){qty = slot1.getMaxStackSize();}
      slot1.stackSize = qty;
      quantity -= qty;
      inventorySlots[0]=slot1;      
      }
    }
  }

private void emptySlot2()
  {
  ItemStack slot2 = inventorySlots[1];  
  if(slot2!=null && filterStack!=null && InventoryTools.doItemStacksMatch(slot2, filterStack))
    {
    quantity+=slot2.stackSize;
    inventorySlots[1] = null;
    }
  }

@Override
public void openInventory()
  {  
  }

@Override
public void closeInventory()
  {  
  }

@Override
public boolean isItemValidForSlot(int var1, ItemStack var2)
  {
  if(filter!=null)
    {
    return filter.isItemValid(var2);
    }
  return filterStack==null || InventoryTools.doItemStacksMatch(filterStack, var2);
  }

@Override
public void setControllerPosition(BlockPosition position)
  {
  this.controllerPosition = position;
  init = this.controllerPosition!=null;
  }

@Override
public List<WarehouseItemFilter> getFilters()
  {
  ArrayList<WarehouseItemFilter> filters = new ArrayList<WarehouseItemFilter>();
  filters.add(filter);
  return filters;
  }

@Override
public void setFilterList(List<WarehouseItemFilter> filters)
  {
  WarehouseItemFilter filter = filters.isEmpty() ? null : filters.get(0);
  this.filter = filter;
  }

@Override
public boolean isItemValid(ItemStack item)
  {
  if(filter!=null)
    {
    return filter.isItemValid(item);
    }
  return filterStack==null || InventoryTools.doItemStacksMatch(item, filterStack);
  }

@Override
public boolean onBlockClicked(EntityPlayer player)
  {
  return false;
  }

}
