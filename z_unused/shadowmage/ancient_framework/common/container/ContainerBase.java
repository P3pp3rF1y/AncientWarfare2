/**
   Copyright 2012 John Cummens (aka Shadowmage, Shadowmage4513)
   This software is distributed under the terms of the GNU General Public License.
   Please see COPYING for precise license information.

   This file is part of Ancient Warfare.

   Ancient Warfare is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   Ancient Warfare is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Ancient Warfare.  If not, see <http://www.gnu.org/licenses/>.
 */
package shadowmage.ancient_framework.common.container;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import shadowmage.ancient_framework.AWFramework;
import shadowmage.ancient_framework.common.config.AWLog;
import shadowmage.ancient_framework.common.interfaces.IContainerGuiCallback;
import shadowmage.ancient_framework.common.interfaces.IHandlePacketData;
import shadowmage.ancient_framework.common.network.Packet03GuiComs;
import shadowmage.ancient_framework.common.utils.InventoryTools;



/**
 * client-server synching container
 * @author Shadowmage
 *
 */
public abstract class ContainerBase extends Container implements IHandlePacketData
{

/**
 * the player who opened this container
 */
public final EntityPlayer player;

public IContainerGuiCallback gui;

public ContainerBase(EntityPlayer player, int x, int y, int z)
  {
  this.player = player;
  }

protected void addPlayerSlots(EntityPlayer player, int tx, int ty, int gap)
  {
  int y;
  int x;
  int slotNum;
  int xPos; 
  int yPos;
  for (x = 0; x < 9; ++x)//add player hotbar slots
    {
    slotNum = x;
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
    
  }

public void refreshGui()
  {
  if(this.gui!=null)
    {
    AWLog.logDebug("refreshing gui: "+this.gui);
    this.gui.refreshGui();
    }
  }

public void setGui(IContainerGuiCallback gui)
  {
  this.gui = gui;
  }

public void sendDataToGUI(NBTTagCompound tag)
  {
  NBTTagCompound baseTag = new NBTTagCompound();
  baseTag.setTag("guiData", tag);
  this.sendDataToPlayer(baseTag);
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
//    int storageSlots = npc.npcType.getInventorySize(npc.rank);    
//    if (slotClickedIndex < 36)//player slots...
//      {      
//      if (!this.mergeItemStack(slotStack, 36, 36+storageSlots, false))//merge into storage inventory
//        {
//        return null;
//        }
//      }
//    else if(slotClickedIndex >=36 &&slotClickedIndex < 36+storageSlots)//storage slots, merge to player inventory
//      {
//      if (!this.mergeItemStack(slotStack, 0, 36, true))//merge into player inventory
//        {
//        return null;
//        }
//      }
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

@Override
public boolean canInteractWith(EntityPlayer var1)
  {  
  return true;
  }

public void sendDataToServer(NBTTagCompound tag)
  {
  if(!player.worldObj.isRemote)
    {
    AWFramework.instance.logError("Attempt to send data to server FROM server");
    Exception e = new IllegalAccessException();
    e.printStackTrace();
    return;
    }
  Packet03GuiComs pkt = new Packet03GuiComs();
  pkt.setData(tag);
  AWFramework.proxy.sendPacketToServer(pkt);
  }

/**
 * send data from server to populate client-side container
 * @param tag
 */
public void sendDataToPlayer(NBTTagCompound tag)
  {
  if(player.worldObj.isRemote)
    {
    AWFramework.instance.logError("Attempt to send data to client FROM client");
    Exception e = new IllegalAccessException();
    e.printStackTrace();
    return;
    }
  Packet03GuiComs pkt = new Packet03GuiComs();
  pkt.setData(tag);
  AWFramework.proxy.sendPacketToPlayer(player, pkt);
  }

public abstract List<NBTTagCompound> getInitData();

@Override
protected boolean mergeItemStack(ItemStack inputStack, int startSlot, int stopSlot, boolean iterateBackwards)
  {
  boolean returnFlag = false;
  int k = startSlot;
  if(stopSlot < startSlot)//if some nubtard tried reversing indices//because who the fuck iterates backwards...
    {
    startSlot = stopSlot;
    stopSlot = k;
    }
  Slot slot;
  ItemStack stackFromSlot;
  if (inputStack.isStackable())
    {
    int numToMerge;
    for(int i =startSlot; i < stopSlot && inputStack.stackSize > 0 ; i++)
      {
      slot = (Slot)this.inventorySlots.get(i);
      if(slot==null || !slot.isItemValid(inputStack))
        {
        continue;
        }      
      stackFromSlot = slot.getStack();
      if(InventoryTools.doItemsMatch(inputStack, stackFromSlot))
        {
        numToMerge = slot.getSlotStackLimit() - stackFromSlot.stackSize;
        numToMerge = numToMerge > inputStack.stackSize ? inputStack.stackSize : numToMerge;
        numToMerge = numToMerge + stackFromSlot.stackSize > stackFromSlot.getMaxStackSize() ? stackFromSlot.getMaxStackSize() - stackFromSlot.stackSize : numToMerge; 
        if(numToMerge>0)
          {
          inputStack.stackSize -= numToMerge;
          stackFromSlot.stackSize += numToMerge;
          slot.onSlotChanged();
          returnFlag = true;          
          }
        }
      }
    }

  for(int i = startSlot; i < stopSlot && inputStack.stackSize > 0 ; i++)
    {
    slot = (Slot)this.inventorySlots.get(i);
    if(slot==null || !slot.isItemValid(inputStack))
      {
      continue;
      } 
    stackFromSlot = slot.getStack();
    if(stackFromSlot==null)
      {
      stackFromSlot = inputStack.copy();
      if(inputStack.stackSize <= slot.getSlotStackLimit())
        {
        stackFromSlot.stackSize = inputStack.stackSize;
        inputStack.stackSize = 0;
        slot.putStack(stackFromSlot);
        }
      else
        {
        inputStack.stackSize -= slot.getSlotStackLimit();
        stackFromSlot.stackSize = slot.getSlotStackLimit();
        slot.putStack(stackFromSlot);
        }
      slot.onSlotChanged();
      returnFlag = true;
      if(inputStack.stackSize<=0)
        {
        break;
        }
      }
    }
    
  return returnFlag;
  }

}
