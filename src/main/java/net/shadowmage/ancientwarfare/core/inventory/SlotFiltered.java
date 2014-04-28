package net.shadowmage.ancientwarfare.core.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.inventory.InventorySided.SlotItemFilter;

public class SlotFiltered extends Slot
{

SlotItemFilter filter;

public SlotFiltered(IInventory par1iInventory, int slotIndex, int xPos, int yPos, SlotItemFilter filter)
  {
  super(par1iInventory, slotIndex, xPos, yPos);
  this.filter = filter;
  }

@Override
public boolean isItemValid(ItemStack par1ItemStack)
  {
  if(filter!=null)
    {
    return filter.isItemValid(par1ItemStack);
    }
  return super.isItemValid(par1ItemStack);
  }

@Override
public String toString()
  {
  return "Filtered slot: "+filter;
  }

}
