package net.shadowmage.ancientwarfare.core.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.inventory.InventoryBackpack;
import net.shadowmage.ancientwarfare.core.item.ItemBackpack;

public class ContainerBackpack extends ContainerBase
{

public int backpackSlotIndex;

InventoryBackpack inventory;

public int guiHeight;
public ContainerBackpack(EntityPlayer player, int x, int y, int z)
  {
  super(player, x, y, z);
  
  ItemStack stack = player.getCurrentEquippedItem();
  backpackSlotIndex = player.inventory.currentItem;
  
  inventory = ItemBackpack.getInventoryFor(stack);
  int xPos, yPos;
  for(int i = 0; i < inventory.getSizeInventory(); i++)
    {
    xPos = (i%9)*18+8;
    yPos = (i/9)*18+8;
    addSlotToContainer(new Slot(inventory, i, xPos, yPos));
    }
  int height = (stack.getItemDamage()+1)*18 + 8;
  guiHeight = addPlayerSlots(player, 8, height+8, 4)+8;  
  }

@Override
public void onContainerClosed(EntityPlayer par1EntityPlayer)
  {  
  super.onContainerClosed(par1EntityPlayer);
  if(!par1EntityPlayer.worldObj.isRemote)
    {
    ItemBackpack.writeBackpackToItem(inventory, par1EntityPlayer.getCurrentEquippedItem());
    }
  }

@Override
protected int addPlayerSlots(EntityPlayer player, int tx, int ty, int gap)
  {  
  int y;
  int x;
  int slotNum;
  int xPos; 
  int yPos;
  for (x = 0; x < 9; ++x)//add player hotbar slots
    {
    slotNum = x;
    if(slotNum==backpackSlotIndex){continue;}//TODO add fake slot in gui
    xPos = tx + x *18;
    yPos = ty+gap + 3*18;
    this.addSlotToContainer(new Slot(player.inventory, x, xPos, yPos));
    }
  for (y = 0; y < 3; ++y)
    {
    for (x = 0; x < 9; ++x)
      {
      slotNum = y*9 + x + 9;// +9 is to increment past hotbar slots
      xPos = tx + x * 18;
      yPos = ty + y * 18;
      this.addSlotToContainer(new Slot(player.inventory, slotNum, xPos, yPos));
      }
    }
  return ty + (4*18) + gap;
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
    if(slotClickedIndex < inventory.getSizeInventory())//book slot
      {      
      if(!this.mergeItemStack(slotStack, inventory.getSizeInventory(), inventory.getSizeInventory()+36, false))//merge into player inventory
        {
        return null;
        }
      }
    else
      {
      if(!this.mergeItemStack(slotStack, 0, inventory.getSizeInventory(), false))//merge into player inventory
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
