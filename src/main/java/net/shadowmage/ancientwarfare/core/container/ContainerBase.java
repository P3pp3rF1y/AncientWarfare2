package net.shadowmage.ancientwarfare.core.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.interfaces.IContainerGuiCallback;
import net.shadowmage.ancientwarfare.core.interfaces.ISlotClickCallback;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.network.PacketGui;

public class ContainerBase extends Container implements ISlotClickCallback
{

public EntityPlayer player;
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
 * @param player the player to add hotbar from
 * @param tx the upper-left X coordinate of the 9x3 inventory block
 * @param ty the upper-left Y coordinate of the 9x3 inventory block
 * @param gap the gap size between upper (9x3) and lower(9x1) inventory blocks, in pixels 
 */
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
  return ty + (4*18) + gap + 24;//no clue why I need an extra 24...
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
    if(this.gui!=null)
      {
      this.gui.handlePacketData(data.getTag("gui"));
      }
    }
  else
    {
    handlePacketData(data);
    }
  }

public void sendInitData()
  {
  
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
    packet.packetData = tag;    
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

public void refreshGui()
  {
  if(this.gui!=null)
    {
    this.gui.refreshGui();
    }
  }

}
