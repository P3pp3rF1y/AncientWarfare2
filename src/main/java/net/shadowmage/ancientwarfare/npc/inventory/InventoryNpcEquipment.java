package net.shadowmage.ancientwarfare.npc.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class InventoryNpcEquipment implements IInventory
{

NpcBase npc;

ItemStack[] inventory;

public InventoryNpcEquipment(NpcBase npc)
  {
  this.npc = npc;
  inventory = new ItemStack[8];
  for(int i = 0; i < 5; i++)
    {
    inventory[i] = npc.getEquipmentInSlot(i)==null? null : npc.getEquipmentInSlot(i).copy();
    }
  inventory[5]=npc.ordersStack==null ? null : npc.ordersStack.copy();
  inventory[6]=npc.upkeepStack==null ? null : npc.upkeepStack.copy();
  inventory[7]=npc.getShieldStack()==null ? null : npc.getShieldStack().copy();      
  }

@Override
public int getSizeInventory()
  {  
  return 8;
  }

@Override
public ItemStack getStackInSlot(int var1)
  {
  return inventory[var1];
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
  inventory[var1]=var2;
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
  return true;
  }
  }

/**
 * should be called server-side when npc-inventory container closes
 */
public void writeToNpc()
  {
  ItemStack a, b;
  for(int i = 0; i < 5; i++)
    {
    a = inventory[i];
    b = npc.getEquipmentInSlot(i);
    if(!ItemStack.areItemStacksEqual(a, b))
      {
      npc.setCurrentItemOrArmor(i, a);
      if(i==0){npc.onWeaponInventoryChanged();}
      }
    }
  b = npc.ordersStack;
  a = inventory[5];
  if(!ItemStack.areItemStacksEqual(a, b))
    {
    npc.ordersStack = a;
    }
  b = npc.upkeepStack;
  a = inventory[6];
  if(!ItemStack.areItemStacksEqual(a, b))
    {
    npc.ordersStack = a;
    }
  a = inventory[7];
  b = npc.getShieldStack();
  if(!ItemStack.areItemStacksEqual(a, b))
    {
    npc.setShieldStack(a);
    }
  }

}
