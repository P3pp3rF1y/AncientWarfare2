package net.shadowmage.ancientwarfare.npc.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class InventoryNpcEquipment implements IInventory
{

NpcBase npc;
public InventoryNpcEquipment(NpcBase npc)
  {
  this.npc = npc;
  }

@Override
public int getSizeInventory()
  {
  return 5;
  }

@Override
public ItemStack getStackInSlot(int var1)
  {
  return npc.getEquipmentInSlot(var1);
  }

@Override
public ItemStack decrStackSize(int var1, int var2)
  {
  ItemStack item = npc.getEquipmentInSlot(var1);
  if(item!=null)
    {
    if(var2>item.stackSize){var2=item.stackSize;}
    ItemStack copy = item.copy();
    copy.stackSize=var2;
    item.stackSize-=var2;
    if(item.stackSize<=0)
      {
      npc.setCurrentItemOrArmor(var1, null);
      }
    return copy;
    }
  return null;
  }

@Override
public ItemStack getStackInSlotOnClosing(int var1)
  {
  ItemStack item = getStackInSlot(var1);
  this.setInventorySlotContents(var1, null);
  return item;
  }

@Override
public void setInventorySlotContents(int var1, ItemStack var2)
  {
  npc.setCurrentItemOrArmor(var1, var2);
  }

@Override
public String getInventoryName()
  {
  return "AWNpcInventoryWrapper";
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
public void markDirty()
  {
  // TODO Auto-generated method stub
  //TODO inform NPC to update work-types
  }

@Override
public boolean isUseableByPlayer(EntityPlayer var1)
  {
  return true;
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
  // TODO validate input for slot
  switch(var1)
  {
  case 0:
  case 1:
  case 2:
  case 3:
  case 4:
  default:
  return false;
  }
  }

}
