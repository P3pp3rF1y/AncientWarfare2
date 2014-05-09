package net.shadowmage.ancientwarfare.automation.tile;

import java.util.ArrayList;
import java.util.Collections;
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


ItemStack slot1;
ItemStack inputSlotStack;

int quantity = 0;
ItemStack filterStack;

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
  if(var1==0){return slot1;}
  else if(var1==1){return inputSlotStack;}
  return null;
  }

@Override
public ItemStack decrStackSize(int slotIndex, int amount)
  {
//  if(slotIndex==0)
//    {
//    if(slot1==null || quantity<=0){return null;}
//    int qty = amount;
//    if(qty>quantity){qty=quantity;}
//    }
//  else if(slotIndex==1)
//    {
//    
//    }
  
  ItemStack slotStack = getStackInSlot(slotIndex);
  if(slotStack!=null)
    {
    if(amount>slotStack.stackSize){amount = slotStack.stackSize;}
    if(amount>slotStack.getMaxStackSize()){amount = slotStack.getMaxStackSize();}
    ItemStack returnStack = slotStack.copy();
    slotStack.stackSize-=amount;
    returnStack.stackSize = amount;  
    if(slotStack.stackSize<=0)
      {
      setInventorySlotContents(slotIndex, null);
      }
    validateSlot(slotIndex);
    return returnStack;
    }
  return null;
  }

@Override
public ItemStack getStackInSlotOnClosing(int slotIndex)
  {
  ItemStack slotStack = getStackInSlot(slotIndex);
  setInventorySlotContents(slotIndex, null);
  validateSlot(slotIndex);
  return slotStack;
  }

@Override
public void setInventorySlotContents(int slotIndex, ItemStack var2)
  {
  if(slotIndex==0)
    {
    slot1=var2;
    validateOutputSlot();
    }
  else if(slotIndex==1)
    {
    inputSlotStack=var2;
    validateInputSlot();
    }
  }

private void validateSlot(int slotIndex)
  {
  if(slotIndex==0){validateOutputSlot();}
  else if(slotIndex==1){validateInputSlot();}
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
  validateOutputSlot();
  validateInputSlot();
  if(!worldObj.isRemote)
    {
    informControllerOfClientUpdate();    
    }
  }

private void informControllerOfClientUpdate()
  {
  if(controllerPosition!=null)
    {
    AWLog.logDebug("informing controller of updated information...");
//    new Exception().printStackTrace();
    WorkSiteWarehouse tile = (WorkSiteWarehouse) worldObj.getTileEntity(controllerPosition.x, controllerPosition.y, controllerPosition.z);
    tile.updateViewers();
    }
  }

private void validateOutputSlot()
  {
  if(filterStack==null)
    {
    slot1=null;
    quantity=0;
    AWLog.logDebug("deep storage quantity updated to: "+quantity + " slot1: "+(slot1==null? 0 : slot1.stackSize));
    new Exception().printStackTrace();
    return;
    }
  if(slot1==null)//item was either removed, or fresh from input
    {
    slot1=filterStack.copy();
    slot1.stackSize = 0;
    }
  else if(slot1!=null)
    {
    if(!InventoryTools.doItemStacksMatch(slot1, filterStack))
      {
      InventoryTools.dropItemInWorld(worldObj, slot1, xCoord, yCoord, zCoord);
      slot1=filterStack.copy();
      slot1.stackSize = 0;
      }
    }
  if(slot1.stackSize<=0 && quantity<=0)
    {
    filterStack=null;
    slot1=null;
    return;
    }
  if(slot1.stackSize<slot1.getMaxStackSize())
    {
    int qty = slot1.getMaxStackSize()-slot1.stackSize;
    if(qty>quantity){qty=quantity;}
    quantity-=qty;
    slot1.stackSize+=qty;
    }  
  AWLog.logDebug("deep storage quantity updated to: "+quantity + " slot1: "+slot1.stackSize);
  new Exception().printStackTrace();
  }

int prev;

private void validateInputSlot()
  { 
  if(inputSlotStack==null)
    {
    return;
    }
  if(filterStack==null)
    {
    filterStack = inputSlotStack.copy();
    filterStack.stackSize=1;
    quantity = inputSlotStack.stackSize;
    AWLog.logDebug("deep storage quantity updated to: "+quantity + " slot1: "+(slot1==null? 0 : slot1.stackSize));
    new Exception().printStackTrace();
    validateOutputSlot();
    }
  else if(filterStack!=null)
    {
    if(InventoryTools.doItemStacksMatch(filterStack, inputSlotStack))
      {
      quantity+=inputSlotStack.stackSize;
      AWLog.logDebug("deep storage quantity updated to: "+quantity + " slot1: "+(slot1==null? 0 : slot1.stackSize));
      new Exception().printStackTrace();
      validateOutputSlot();  
      }
    else
      {
      InventoryTools.dropItemInWorld(worldObj, inputSlotStack, xCoord, yCoord, zCoord);
      }
    }
  inputSlotStack=null;    
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
  if(var1==0){return false;}
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
  if(filter==null){return Collections.emptyList();}
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
