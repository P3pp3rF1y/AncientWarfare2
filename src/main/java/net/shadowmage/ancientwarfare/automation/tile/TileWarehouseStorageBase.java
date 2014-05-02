package net.shadowmage.ancientwarfare.automation.tile;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.inventory.InventoryBasic;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.core.util.WorldTools;

public abstract class TileWarehouseStorageBase extends TileEntity implements IInventory, IInteractableTile, IWarehouseStorageTile, IControlledTile
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
          warehouse.removeStorageBlock(xCoord, yCoord, zCoord);
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
  AWLog.logDebug("set controller position to: "+position);
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
            warehouse.addStorageBlock(xCoord, yCoord, zCoord);
            controllerPosition = new BlockPosition(warehouse.xCoord, warehouse.yCoord, warehouse.zCoord);
            break;
            }
          }
        } 
      }
    }
  }

@Override
public List<WarehouseItemFilter> getFilters()
  {
  return itemFilters;
  }

public void removeFilter(WarehouseItemFilter filter)
  {
  itemFilters.remove(filter);
  }

public void addFilter(WarehouseItemFilter filter)
  {
  itemFilters.add(filter);
  }

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

@Override
public void readFromNBT(NBTTagCompound tag)
  {
  super.readFromNBT(tag);
  NBTTagList filterList = tag.getTagList("filterList", Constants.NBT.TAG_COMPOUND);
  WarehouseItemFilter filter;
  for(int i = 0; i < filterList.tagCount(); i++)
    {
    filter = new WarehouseItemFilter();
    filter.readFromNBT(filterList.getCompoundTagAt(i));
    itemFilters.add(filter);    
    }
  inventory.readFromNBT(tag.getCompoundTag("inventory"));
  inventoryName = tag.getString("name");
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

@Override
public void writeToNBT(NBTTagCompound tag)
  {
  super.writeToNBT(tag);
  writeFilterList(tag);
  NBTTagCompound inventoryTag = new NBTTagCompound();
  inventory.writeToNBT(inventoryTag);
  tag.setTag("inventory", inventoryTag);
  tag.setString("name", inventoryName);
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
  
public static final class WarehouseItemFilter
{
private ItemStack filterItem;
private boolean ignoreDamage;
private boolean ignoreNBT;

private WarehouseItemFilter(ItemStack item, boolean dmg, boolean nbt)
  {
  this.filterItem = item;
  this.ignoreDamage = dmg;
  this.ignoreNBT = nbt;
  }

public WarehouseItemFilter(){}//nbt-constructor

public boolean isItemValid(ItemStack item)
  {
  if(item==null){return false;}
  if(filterItem==null){return true;}//null filter item, use for 'match all'
  if(item.getItem()!=filterItem.getItem()){return false;}//item not equivalent, obvious mis-match
  if(ignoreDamage && ignoreNBT){return true;}//item was equal, and ignore all else, return true
  else if(ignoreDamage){return ItemStack.areItemStackTagsEqual(item, filterItem);}//item was equal, ignore damage..return true if nbt-tags match
  else if(ignoreNBT){return item.getItemDamage()==filterItem.getItemDamage();}//item was equal, ignore nbt, check if item damages are equal 
  return InventoryTools.doItemStacksMatch(item, filterItem);//finally, items were equal, no ignores' -- check both dmg and tag
  }

public void readFromNBT(NBTTagCompound tag)
  {
  ignoreDamage = tag.getBoolean("dmg");
  ignoreNBT = tag.getBoolean("nbt"); 
  if(tag.hasKey("filter")){filterItem = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("filter"));}
  }

public NBTTagCompound writeToNBT(NBTTagCompound tag)
  {
  tag.setBoolean("dmg", ignoreDamage);
  tag.setBoolean("nbt", ignoreNBT);
  if(filterItem!=null){tag.setTag("filter", filterItem.writeToNBT(new NBTTagCompound()));}  
  return tag;
  }

public ItemStack getFilterItem()
  {
  return filterItem;
  }

public void setFilterItem(ItemStack item)
  {
  this.filterItem = item;
  }

public final boolean isIgnoreDamage()
  {
  return ignoreDamage;
  }

public final void setIgnoreDamage(boolean ignoreDamage)
  {
  this.ignoreDamage = ignoreDamage;
  }

public final boolean isIgnoreNBT()
  {
  return ignoreNBT;
  }

public final void setIgnoreNBT(boolean ignoreNBT)
  {
  this.ignoreNBT = ignoreNBT;
  }

@Override
public String toString()
  {
  return "Filter item: "+filterItem + " ignore dmg/nbt:"+ignoreDamage+":"+ignoreNBT;
  }
}

}
