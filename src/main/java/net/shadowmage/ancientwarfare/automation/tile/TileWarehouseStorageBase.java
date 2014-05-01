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
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

public abstract class TileWarehouseStorageBase extends TileEntity implements IInventory, IInteractableTile
{

String inventoryName = "";

InventoryBasic inventory;

private List<WarehouseItemFilter> itemFilters = new ArrayList<WarehouseItemFilter>();

/**
 * implementing sub-classes must create their inventory in their constructor, or things will NPE
 * on load/save
 */
public TileWarehouseStorageBase()
  {
  
  }

public List<WarehouseItemFilter> getValidItemFilters()
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
  if(this.itemFilters.isEmpty()){return true;}
  for(WarehouseItemFilter filter : this.itemFilters)
    {
    if(filter.isItemValid(var2))
      {
      AWLog.logDebug("item validated by filter: "+var2+ " :: "+filter);
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
