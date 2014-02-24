package net.shadowmage.ancientwarfare.core.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.interfaces.IContainerGuiCallback;
import net.shadowmage.ancientwarfare.core.interfaces.ISlotClickCallback;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.network.PacketGui;

public class ContainerBase extends Container implements ISlotClickCallback
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
  AWLog.logDebug("receiving gui packet to container...");
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

@Override
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
    else//swap stacks from slot and cursor
      {
      ItemStack stack = player.inventory.getItemStack();
      ItemStack stack1 = inventory.getStackInSlot(slotIndex);
      player.inventory.setItemStack(stack1);
      inventory.setInventorySlotContents(slotIndex, stack);
      }
    }
  else//right click
    {
    
    }  
  if(player.worldObj.isRemote)
    {
    PacketGui packet = new PacketGui();
    NBTTagCompound tag = new NBTTagCompound();
    packet.dataTag = tag;    
    NBTTagCompound dataTag = new NBTTagCompound();

    dataTag.setInteger("inventory", getInventoryNumber(inventory));
    dataTag.setInteger("slotIndex", slotIndex);
    dataTag.setInteger("button", button);
     
    tag.setTag("slot", dataTag);    
    NetworkHandler.sendToServer(packet);
    }
  else
    {
    this.detectAndSendChanges();
    }
  }

protected int getInventoryNumber(IInventory inventory)
  {
  int index = 0;
  for(IInventory inv : this.inventories)
    {
    if(inv==inventory)
      {
      return index;
      }
    index++;
    }
  return 0;
  }

}
