package net.shadowmage.ancientwarfare.core.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.interfaces.IContainerGuiCallback;

public class ContainerBase extends Container
{

EntityPlayer player;
IContainerGuiCallback gui;
IInventory[] inventories;//sub-classes need to define this member with the proper size, it should NEVER be null

public ContainerBase(EntityPlayer player, int x, int y, int z)
  {
  this.player = player;
  }

public final void setGui(IContainerGuiCallback gui)
  {
  this.gui = gui;
  }

/**
 * server side method to send a data-packet to the client-side GUI attached to the client-side verison of this container
 * @param data
 */
protected final void sendDataToGui(NBTTagCompound data)
  {
  if(gui!=null)
    {
    gui.handlePacketData(data);
    }
  }

/**
 * client/server side method to receive packet data from PacketGui
 * @param data
 */
public final void onPacketData(NBTTagCompound data)
  {
  if(data.hasKey("slot"))
    {
    data = data.getCompoundTag("slot");
    int slot = data.getInteger("slotIndex");
    int invNumber = data.getInteger("inventory");
    int button = data.getInteger("button");
    IInventory inventory = this.inventories[invNumber];
    this.onSlotClicked(inventory, slot, button);
    }
  else if(data.hasKey("gui"))
    {
    
    }
  else
    {
    handlePacketData(data);
    }
  }

/**
 * sub-classes should override this method to handle any packet data they are expecting to receive.
 * packets destined to the GUI or for slot-click have already been filtered out
 * @param tag
 */
public void handlePacketData(NBTTagCompound tag)
  {
  
  }

@Override
public boolean canInteractWith(EntityPlayer var1)
  {
  return true;
  }

public final void onSlotClicked(IInventory inventory, int slotIndex, int button)
  {  
  ItemStack cursorStack = player.inventory.getItemStack();
  ItemStack inventoryStack = inventory.getStackInSlot(slotIndex);
  if(button==0)//left click
    {
    if(cursorStack==null)//place slot stack on cursor
      {
      player.inventory.setItemStack(inventory.getStackInSlot(slotIndex));
      inventory.setInventorySlotContents(slotIndex, null);
      }
    else if(cursorStack!=null && inventoryStack==null)//place cursor stack into slot
      {
      player.inventory.setItemStack(null);
      inventory.setInventorySlotContents(slotIndex, cursorStack);
      }
    else if(cursorStack!=null && inventoryStack!=null)//try to merge from cursor into slot
      {
      if(    cursorStack.getItem()==inventoryStack.getItem()
          && cursorStack.getItemDamage()== inventoryStack.getItemDamage()
          && inventoryStack.stackSize < inventoryStack.getMaxStackSize()
          && ItemStack.areItemStackTagsEqual(cursorStack, inventoryStack))
        {
        int moveMax = inventoryStack.getMaxStackSize() - inventoryStack.stackSize;
        if(moveMax > cursorStack.stackSize)
          {
          moveMax = cursorStack.stackSize;
          }
        cursorStack.stackSize-=moveMax;
        inventoryStack.stackSize+=moveMax;
        if(cursorStack.stackSize<=0)
          {
          player.inventory.setItemStack(null);
          }
        }
      }
    }
  else//right click
    {
    
    }
  
  if(player.worldObj.isRemote)
    {
    //send packet to server w/ changes
    //really only needs a ref to the inventory (? how?), and the slot clicked
    }
  else
    {
    this.detectAndSendChanges();
    }
  }

}
