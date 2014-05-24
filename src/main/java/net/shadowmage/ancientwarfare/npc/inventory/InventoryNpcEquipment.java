package net.shadowmage.ancientwarfare.npc.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemArmor;
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
  return 7;
  }

@Override
public ItemStack getStackInSlot(int var1)
  {
  if(var1==5){return npc.ordersStack;}
  if(var1==6){return npc.upkeepStack;}
  return npc.getEquipmentInSlot(var1);
  }

@Override
public ItemStack decrStackSize(int var1, int var2)
  {
  ItemStack item = getStackInSlot(var1);
  if(item!=null)
    {
    if(var2>item.stackSize){var2=item.stackSize;}
    ItemStack copy = item.copy();
    copy.stackSize=var2;
    item.stackSize-=var2;
    if(item.stackSize<=0)
      {
      setInventorySlotContents(var1, null);      
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
  if(var1==5)
    {
    npc.ordersStack =var2;
    npc.onOrdersInventoryChanged();
    }
  else if(var1==6)
    {
    npc.upkeepStack = var2;
    npc.onUpkeepInventoryChanged();
    }
  else
    {
    npc.setCurrentItemOrArmor(var1, var2);
    if(var1==0){npc.onWeaponInventoryChanged();}  
    }
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
  if(var2==null || var2.getItem()==null){return false;}
  switch(var1)
  {
  case 0://weapon/tool, open
  // TODO validate input for weapon slot?
  return true;
  case 1://head
  return (var2.getItem() instanceof ItemArmor) && ((ItemArmor)var2.getItem()).armorType==3;
  case 2://chest
  return (var2.getItem() instanceof ItemArmor) && ((ItemArmor)var2.getItem()).armorType==2;
  case 3://legs
  return (var2.getItem() instanceof ItemArmor) && ((ItemArmor)var2.getItem()).armorType==1;
  case 4://boots
  return (var2.getItem() instanceof ItemArmor) && ((ItemArmor)var2.getItem()).armorType==0;
  case 5:
  return npc.isValidOrdersStack(var2);
  default:
  return false;
  }
  }

}
