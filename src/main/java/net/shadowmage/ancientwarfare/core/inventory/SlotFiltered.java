package net.shadowmage.ancientwarfare.core.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.inventory.InventorySided.SlotItemFilter;

public class SlotFiltered extends Slot
{

SlotItemFilter filter;

public SlotFiltered(IInventory par1iInventory, int par2, int par3, int par4, SlotItemFilter filter)
  {
  super(par1iInventory, par2, par3, par4);
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

}
