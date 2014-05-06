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
  if(!worldObj.isRemote)
    {
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
    } 
  controllerPosition = null;
  }

@Override
public void setControllerPosition(BlockPosition position)
  {
  this.controllerPosition = position;
  this.init = this.controllerPosition!=null;
  }

@Override
public void updateEntity()
  {
  if(!init)
    {
    init = true;
    if(!worldObj.isRemote)
      {
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
  readFilterList(pkt.func_148857_g());
  inventoryName = pkt.func_148857_g().getString("name");
  }

@Override
public Packet getDescriptionPacket()
  {  
  NBTTagCompound tag = new NBTTagCompound();
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
public boolean receiveClientEvent(int p_145842_1_, int p_145842_2_)
  {
  return super.receiveClientEvent(p_145842_1_, p_145842_2_);
  }

/*****************************************FILTER LIST METHODS*******************************************/

@Override
public void setFilterList(List<WarehouseItemFilter> filters)
  {
  List<WarehouseItemFilter> filters1 = new ArrayList<WarehouseItemFilter>();
  filters1.addAll(itemFilters);
  itemFilters.clear();
  itemFilters.addAll(filters);
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
      AWLog.logDebug("item validated by filter: "+item+ " :: "+filter);
      return true;
      }
    }
  return false;
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
  }

private void recountFilters()
  {
  for(int i = 0; i < this.itemFilters.size(); i++)
    {
    
    }
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
