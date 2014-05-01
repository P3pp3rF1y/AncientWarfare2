package net.shadowmage.ancientwarfare.automation.tile;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.shadowmage.ancientwarfare.core.inventory.InventoryBasic;

public abstract class TileWarehouseStorageBase extends TileEntity implements IInventory
{

InventoryBasic inventory;

private List<WarehouseItemFilter> itemFilters = new ArrayList<WarehouseItemFilter>();

public TileWarehouseStorageBase()
  {
  
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
  return inventory.getInventoryName();
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
  /**
   * TODO add validity filters based on input filters from GUI
   */
  return true;
  }
  
private static class WarehouseItemFilter
{
private ItemStack filterItem;
boolean ignoreDamage;
boolean ignoreNBT;
private WarehouseItemFilter(ItemStack item, boolean dmg, boolean nbt)
  {
  this.filterItem = item;
  this.ignoreDamage = dmg;
  this.ignoreNBT = nbt;
  }

private boolean isItemValid(ItemStack item)
  {
  
  return false;
  }
}

}
