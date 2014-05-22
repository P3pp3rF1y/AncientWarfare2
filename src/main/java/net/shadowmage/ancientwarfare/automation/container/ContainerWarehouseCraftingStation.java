package net.shadowmage.ancientwarfare.automation.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.automation.tile.worksite.TileWarehouseCraftingStation;
import net.shadowmage.ancientwarfare.core.api.AWItems;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.item.ItemResearchBook;

public class ContainerWarehouseCraftingStation extends ContainerBase
{

public TileWarehouseCraftingStation station;

public ContainerWarehouseCraftingStation(EntityPlayer player, int x, int y, int z)
  {
  super(player, x, y, z);
  station = (TileWarehouseCraftingStation) player.worldObj.getTileEntity(x, y, z);
  IInventory inventory = station.layoutMatrix;
  
  Slot slot;
  
  slot = new SlotCrafting(player, inventory, station.result, 0, 3*18 + 3*18 + 8 + 18, 1*18+8)
    {
    @Override
    public void onPickupFromSlot(EntityPlayer par1EntityPlayer, ItemStack par2ItemStack)
      {
      station.preItemCrafted();
      super.onPickupFromSlot(par1EntityPlayer, par2ItemStack);
      station.onItemCrafted();
      }
    };
  addSlotToContainer(slot);
  
  slot = new Slot(station.bookInventory, 0, 8, 18+8)
    {
    @Override
    public boolean isItemValid(ItemStack par1ItemStack)
      {
      return par1ItemStack!=null && par1ItemStack.getItem()==AWItems.researchBook && ItemResearchBook.getResearcherName(par1ItemStack)!=null;
      }
    };
  addSlotToContainer(slot);
  
  int x2, y2, slotNum = 0;
  for(int y1 = 0; y1 <3; y1++)
    {
    y2 = y1*18 + 8 ;
    for(int x1 = 0; x1 <3; x1++)
      {
      x2 = x1*18 + 8 + 3*18;
      slotNum = y1*3 + x1;
      slot = new Slot(inventory, slotNum, x2, y2);
      addSlotToContainer(slot);
      }
    }
  
  int y1 = 8+3*18+8;
  y1 = this.addPlayerSlots(player, 8, y1, 4);
  }

@Override
public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int slotClickedIndex)
  {
  ItemStack slotStackCopy = null;
  Slot theSlot = (Slot)this.inventorySlots.get(slotClickedIndex);
  if (theSlot != null && theSlot.getHasStack())
    {
    ItemStack slotStack = theSlot.getStack();
    slotStackCopy = slotStack.copy();
    
    int playerSlotStart = 1+1+9;
    if (slotClickedIndex==0)//result slot
      {      
      if(!this.mergeItemStack(slotStack, playerSlotStart, playerSlotStart+36, false))//merge into player inventory
        {
        return null;
        }
      }
    else if(slotClickedIndex==1)//book slot
      {      
      if(!this.mergeItemStack(slotStack, playerSlotStart, playerSlotStart+36, false))//merge into player inventory
        {
        return null;
        }
      }
    else if(slotClickedIndex >=2 && slotClickedIndex < 2+9)//craft matrix
      {
      if (!this.mergeItemStack(slotStack, playerSlotStart, playerSlotStart+36, false))//merge into storage
        {
        return null;
        }
      }
    else if(slotClickedIndex >=playerSlotStart &&slotClickedIndex < 36+playerSlotStart)//player slots NOOP
      {

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
