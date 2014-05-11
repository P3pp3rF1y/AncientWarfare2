package net.shadowmage.ancientwarfare.automation.tile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.core.util.InventoryTools.ItemQuantityMap;

public class TileWarehouseStorageDeep extends TileEntity implements IInteractableTile, IWarehouseStorageTile, IControlledTile
{

String inventoryName = "";

ItemStack filterStack;
int storedQuantity;
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
public String getInventoryName()
  {
  return inventoryName;
  }

@Override
public void markDirty()
  {  
  super.markDirty(); 
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
    WorkSiteWarehouse tile = (WorkSiteWarehouse) worldObj.getTileEntity(controllerPosition.x, controllerPosition.y, controllerPosition.z);
    tile.updateViewers();
    }
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
  if(filterStack!=null)
    {
    return InventoryTools.doItemStacksMatch(item, filterStack);
    }
  if(filter!=null)
    {
    return filter.isItemValid(item);
    }
  return true;
  }

@Override
public boolean onBlockClicked(EntityPlayer player)
  {
  if(!player.worldObj.isRemote)
    {
    NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WAREHOUSE_STORAGE_DEEP, xCoord, yCoord, zCoord);
    }
  return false;
  }

@Override
public int getMaxFilterCount()
  {
  return 1;
  }

@Override
public void dropInventoryInWorld()
  {
  if(filterStack!=null)
    {
    int qty = storedQuantity;
    int q1;
    ItemStack stack;
    while(qty>0)
      {
      q1 = filterStack.getMaxStackSize();
      if(q1>qty){q1=qty;}
      qty-=q1;
      stack = filterStack.copy();
      stack.stackSize=q1;
      InventoryTools.dropItemInWorld(worldObj, stack, xCoord, yCoord, zCoord);
      }
    storedQuantity = 0;
    }
  }

@Override
public void addInventoryContentsToMap(ItemQuantityMap itemMap)
  {
  if(filterStack!=null)
    {
    itemMap.addItemStack(filterStack, storedQuantity);
    }
  }

@Override
public int removeItem(ItemStack filter, int quantity)
  {
  if(filterStack==null || !InventoryTools.doItemStacksMatch(filter, filterStack)){return 0;}
  if(quantity>storedQuantity)
    {
    quantity=storedQuantity;    
    }
  storedQuantity-=quantity;
  informControllerOfClientUpdate();
  if(storedQuantity<=0){filterStack=null;}
  return quantity;
  }

@Override
public ItemStack addItem(ItemStack item)
  {
  if(isItemValid(item))
    {
    if(filterStack==null)
      {
      filterStack=item.copy();
      storedQuantity = 0;
      }
    storedQuantity+=item.stackSize;
    informControllerOfClientUpdate();
    return null;
    }
  return item;
  }

@Override
public boolean receiveClientEvent(int a, int b)
  {
  if(a==0)//qty update
    {
    storedQuantity = b;
    }
  else if(a==1)//remove filter stack (is only set through description packet)
    {
    storedQuantity = 0;
    filterStack = null;
    }
  return super.receiveClientEvent(a, b);
  }

@Override
public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
  {  
  NBTTagCompound tag = pkt.func_148857_g();
  readNBTData(tag);
  }

@Override
public Packet getDescriptionPacket()
  {  
  NBTTagCompound tag = new NBTTagCompound();
  writeNBTData(tag);
  S35PacketUpdateTileEntity pkt = new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, tag);
  return pkt;
  }

@Override
public void readFromNBT(NBTTagCompound tag)
  {
  super.readFromNBT(tag);
  readNBTData(tag);
  }

private void readNBTData(NBTTagCompound tag)
  {
  if(tag.hasKey("controllerPosition"))
    {
    controllerPosition = new BlockPosition(tag.getCompoundTag("controllerPosition"));
    }
  if(tag.hasKey("filter"))
    {
    filter = new WarehouseItemFilter();
    filter.readFromNBT(tag.getCompoundTag("filter"));
    }
  inventoryName = tag.getString("name");
  storedQuantity = tag.getInteger("quantity");
  if(tag.hasKey("filterStack"))
    {    
    filterStack = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("filterStack"));
    }
  else
    {
    filterStack = null;
    }
  }

@Override
public void writeToNBT(NBTTagCompound tag)
  {  
  super.writeToNBT(tag);
  writeNBTData(tag);
  }

private void writeNBTData(NBTTagCompound tag)
  {
  if(controllerPosition!=null)
    {
    tag.setTag("controllerPosition", controllerPosition.writeToNBT(new NBTTagCompound()));
    }
  tag.setString("name", inventoryName);
  if(filter!=null){tag.setTag("filter", filter.writeToNBT(new NBTTagCompound()));}
  tag.setInteger("quantity", storedQuantity);
  if(filterStack!=null){tag.setTag("filterStack", filterStack.writeToNBT(new NBTTagCompound()));}  
  }

@Override
public boolean isGeneralStorage()
  {  
  return false;
  }

@Override
public int getCountOf(WarehouseItemFilter filter)
  {
  return filter == this.filter ? storedQuantity : 0;
  }

@Override
public boolean canHoldMore(ItemStack item)
  {
  if(isItemValid(item))
    {
    return true;
    }
  return false;
  }
}
