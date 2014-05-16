package net.shadowmage.ancientwarfare.automation.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.shadowmage.ancientwarfare.automation.tile.TileMechanicalWorker;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;

public class ContainerMechanicalWorker extends ContainerBase
{

public int guiHeight;

public ContainerMechanicalWorker(EntityPlayer player, int x, int y, int z)
  {
  super(player, x, y, z);
  TileMechanicalWorker worker = (TileMechanicalWorker)player.worldObj.getTileEntity(x,y,z);
  addSlotToContainer(new Slot(worker,0,8+4*18,8)
    {
    @Override
    public boolean isItemValid(ItemStack par1ItemStack)
      {
      return TileEntityFurnace.isItemFuel(par1ItemStack);
      }
    });
  addPlayerSlots(player, 8, 8+18+8, 4);
  guiHeight = 8+18+8 + 4*18 + 4 + 8;
  }

@Override
public ItemStack transferStackInSlot(EntityPlayer player, int slotClickedIndex)
  {
  ItemStack slotStackCopy = null;
  Slot theSlot = (Slot)this.inventorySlots.get(slotClickedIndex);
  int slots = 1;
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
    theSlot.onPickupFromSlot(player, slotStack);
    }
  return slotStackCopy;
  }

}
