package net.shadowmage.ancientwarfare.automation.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.automation.tile.warehouse.TileWarehouseInput;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;

public class ContainerWarehouseInput extends ContainerBase
{
public TileWarehouseInput storageTile;

public ContainerWarehouseInput(EntityPlayer player, int x, int y, int z)
  {
  super(player, x, y, z);
  storageTile = (TileWarehouseInput) player.worldObj.getTileEntity(x, y, z);
  int x1, y1;
  for(int i = 0; i < storageTile.getSizeInventory(); i++)
    {
    x1 = (i%9) * 18 + 8;
    y1 = (i/9) * 18 + 8;
    addSlotToContainer(new Slot(storageTile, i, x1, y1));
    }  
  int guiHeight = 8 + 8 + 18*(storageTile.getSizeInventory()/9);
  addPlayerSlots(player, 8, guiHeight, 4);    
  }

@Override
public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int slotClickedIndex)
  {
  ItemStack slotStackCopy = null;
  Slot theSlot = (Slot)this.inventorySlots.get(slotClickedIndex);
  int slots = this.storageTile.getSizeInventory();
  if (theSlot != null && theSlot.getHasStack())
    {
    ItemStack slotStack = theSlot.getStack();
    slotStackCopy = slotStack.copy();
    if (slotClickedIndex < slots)//storage
      {      
      if(!this.mergeItemStack(slotStack, slots, slots+36, false))//merge into player inventory
        {
        return null;
        }
      }
    else
      {      
      if(!this.mergeItemStack(slotStack, 0, slots, false))//merge into player inventory
        {
        return null;
        }
      }
    if (slotStack.stackSize == 0)
      {
      theSlot.putStack((ItemStack)null);
      }
    else
      {
      theSlot.onSlotChanged();
      }
    if (slotStack.stackSize == slotStackCopy.stackSize)
      {
      return null;
      }
    theSlot.onPickupFromSlot(par1EntityPlayer, slotStack);
    }
  return slotStackCopy;
  }

}
