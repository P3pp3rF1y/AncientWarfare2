package net.shadowmage.ancientwarfare.automation.tile;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.inventory.InventoryBasic;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.WorldTools;

public abstract class TileWarehouseStorageBase extends TileEntity implements IInteractableTile, IWarehouseStorageTile, IControlledTile
{

String inventoryName = "";

InventoryBasic inventory;

BlockPosition controllerPosition = null;

private List<WarehouseItemFilter> itemFilters = new ArrayList<WarehouseItemFilter>();
private boolean init;

/**
 * implementing sub-classes must create their inventory in their constructor, or things will NPE
 * on load/save
 */
public TileWarehouseStorageBase()
  {
  
  }

@Override
public String toString()
  {
  return "Storage tile, size: "+inventory.getSizeInventory()+ " name: "+getInventoryName()+" location: "+xCoord+","+yCoord+","+zCoord;
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
public void setControllerPosition(BlockPosition position)
  {
  this.controllerPosition = position;
  this.init = this.controllerPosition!=null;
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

/*****************************************NETWORK HANDLING METHODS*******************************************/
@Override
public void readFromNBT(NBTTagCompound tag)
  {
  super.readFromNBT(tag);
  readFilterList(tag);
  inventory.readFromNBT(tag.getCompoundTag("inventory"));
  inventoryName = tag.getString("name");
  }

@Override
public void writeToNBT(NBTTagCompound tag)
  {
  super.writeToNBT(tag);
  writeFilterList(tag);
  tag.setTag("inventory", inventory.writeToNBT(new NBTTagCompound()));
  tag.setString("name", inventoryName);
  }

private void writeFilterList(NBTTagCompound tag)
  {
  NBTTagList filterList = new NBTTagList();
  for(WarehouseItemFilter filter : this.itemFilters)
    {
    filterList.appendTag(filter.writeToNBT(new NBTTagCompound()));
    }
  tag.setTag("filterList", filterList);
  }

private void readFilterList(NBTTagCompound tag)
  {
  itemFilters.clear();
  NBTTagList filterList = tag.getTagList("filterList", Constants.NBT.TAG_COMPOUND);
  WarehouseItemFilter filter;
  for(int i = 0; i < filterList.tagCount(); i++)
    {
    filter = new WarehouseItemFilter();
    filter.readFromNBT(filterList.getCompoundTagAt(i));
    itemFilters.add(filter);    
    }
  }

@Override
public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
  {  
  if(pkt.func_148857_g().hasKey("controllerPosition"))
    {
    controllerPosition = new BlockPosition(pkt.func_148857_g().getCompoundTag("controllerPosition"));
    }
  readFilterList(pkt.func_148857_g());
  inventoryName = pkt.func_148857_g().getString("name");
  }

@Override
public Packet getDescriptionPacket()
  {  
  NBTTagCompound tag = new NBTTagCompound();
  if(controllerPosition!=null)
    {
    tag.setTag("controllerPosition", controllerPosition.writeToNBT(new NBTTagCompound()));
    }
  tag.setString("name", inventoryName);
  writeFilterList(tag);
  S35PacketUpdateTileEntity pkt = new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, tag);
  return pkt;
  }

@Override
public boolean onBlockClicked(EntityPlayer player)
  {
  if(!player.worldObj.isRemote)
    {
    NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WAREHOUSE_STORAGE, xCoord, yCoord, zCoord);
    }
  return true;
  }

@Override
public boolean receiveClientEvent(int a, int b)
  {
  if(!worldObj.isRemote)
    {
    return true;
    }
  AWLog.logDebug("receiving client event: "+a+"::"+b+" client: "+worldObj.isRemote);
  updateFilterCount(a, b);
  this.informControllerOfClientUpdate();
  return false;
  }

/*****************************************FILTER LIST METHODS*******************************************/

@Override
public void setFilterList(List<WarehouseItemFilter> filters)
  {
  List<WarehouseItemFilter> filters1 = new ArrayList<WarehouseItemFilter>();
  filters1.addAll(itemFilters);
  itemFilters.clear();
  itemFilters.addAll(filters);
  if(!worldObj.isRemote)
    { 
    if(controllerPosition!=null)
      {
      TileEntity te = worldObj.getTileEntity(controllerPosition.x, controllerPosition.y, controllerPosition.z);
      if(te instanceof WorkSiteWarehouse)
        {
        ((WorkSiteWarehouse)te).updateStorageBlockFilters(this, filters1, itemFilters);
        }
      }
    this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);  
    }
  this.recountFilters();
  this.informControllerOfClientUpdate();
  } 

@Override
public List<WarehouseItemFilter> getFilters()
  {
  return itemFilters;
  }

@Override
public boolean isItemValid(ItemStack item)
  {
  if(this.itemFilters.isEmpty()){return true;}
  for(WarehouseItemFilter filter : this.itemFilters)
    {
    if(filter.isItemValid(item))
      {
      return true;
      }
    }
  return false;
  }

private void recountFilters()
  {
  WarehouseItemFilter filter;
  ItemStack item;
  int count = 0;
  for(int i = 0; i < this.itemFilters.size(); i++)
    {
    count = 0;
    filter = this.itemFilters.get(i);
    for(int k = 0; k < this.getSizeInventory(); k++)
      {
      item = inventory.getStackInSlot(k);
      if(item==null){continue;}
      if(filter.isItemValid(item))
        {
        count+=item.stackSize;
        }
      }    
    setFilterCount(i, count);    
    }
  }

private void setFilterCount(int filterIndex, int count)
  {
  WarehouseItemFilter filter = this.itemFilters.get(filterIndex);
  if(filter.getItemCount()!=count)
    {
    filter.setItemCount(count);
    worldObj.addBlockEvent(xCoord, yCoord, zCoord, getBlockType(), filterIndex, count);
    } 
  }

private void updateFilterCount(int filterIndex, int count)
  {
  if(filterIndex>=itemFilters.size()){return;}
  WarehouseItemFilter filter = this.itemFilters.get(filterIndex);
  if(filter.getItemCount()!=count)
    {
    filter.setItemCount(count);
    }
  }

/*****************************************INVENTORY METHODS*******************************************/

@Override
public int getSizeInventory()
  {
  return inventory.getSizeInventory();
  }

@Override
public ItemStack getStackInSlot(int var1)
  {
  return inventory.getStackInSlot(var1);
  }

@Override
public ItemStack decrStackSize(int var1, int var2)
  {
  return inventory.decrStackSize(var1, var2);
  }

@Override
public ItemStack getStackInSlotOnClosing(int var1)
  {
  return inventory.getStackInSlotOnClosing(var1);
  }

@Override
public void setInventorySlotContents(int var1, ItemStack var2)
  {
  inventory.setInventorySlotContents(var1, var2);
  }

@Override
public String getInventoryName()
  {
  return inventoryName;
  }

public void setInventoryName(String name)
  {
  this.inventoryName = name;
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
  return isItemValid(var2);
  }

@Override
public void markDirty()
  {
  super.markDirty();  
  if(!worldObj.isRemote)
    {
    long t1 = System.nanoTime();
    recountFilters();
    long t2 = System.nanoTime();
    AWLog.logDebug("inv count took: "+(t2-t1)+"ns  at:");
    }
  informControllerOfClientUpdate();
  }

/**
 * inventory class for use by basic warehouse storage tiles
 *
 */
protected class WarehouseBasicInventory extends InventoryBasic
{

TileWarehouseStorageBase tile;
public WarehouseBasicInventory(int size, TileWarehouseStorageBase tile)
  {
  super(size);
  this.tile = tile;
  }

@Override
public void markDirty()
  {
  tile.markDirty();
  super.markDirty();
  }

}

}
