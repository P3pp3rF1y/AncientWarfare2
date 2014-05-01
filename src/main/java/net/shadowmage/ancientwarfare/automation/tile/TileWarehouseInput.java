package net.shadowmage.ancientwarfare.automation.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class TileWarehouseInput extends TileEntity implements IInventory
{



public TileWarehouseInput()
  {
  
  }

@Override
public int getSizeInventory()
  {
  // TODO Auto-generated method stub
  return 0;
  }

@Override
public ItemStack getStackInSlot(int var1)
  {
  // TODO Auto-generated method stub
  return null;
  }

@Override
public ItemStack decrStackSize(int var1, int var2)
  {
  // TODO Auto-generated method stub
  return null;
  }

@Override
public ItemStack getStackInSlotOnClosing(int var1)
  {
  // TODO Auto-generated method stub
  return null;
  }

@Override
public void setInventorySlotContents(int var1, ItemStack var2)
  {
  // TODO Auto-generated method stub

  }

@Override
public String getInventoryName()
  {
  // TODO Auto-generated method stub
  return null;
  }

@Override
public boolean hasCustomInventoryName()
  {
  // TODO Auto-generated method stub
  return false;
  }

@Override
public int getInventoryStackLimit()
  {
  // TODO Auto-generated method stub
  return 0;
  }

@Override
public boolean isUseableByPlayer(EntityPlayer var1)
  {
  // TODO Auto-generated method stub
  return false;
  }

@Override
public void openInventory()
  {
  // TODO Auto-generated method stub

  }

@Override
public void closeInventory()
  {
  // TODO Auto-generated method stub

  }

@Override
public boolean isItemValidForSlot(int var1, ItemStack var2)
  {
  // TODO Auto-generated method stub
  return false;
  }

}
