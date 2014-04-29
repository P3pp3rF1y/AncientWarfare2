package net.shadowmage.ancientwarfare.core.container;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.interfaces.IContainerGuiCallback;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.network.PacketGui;

public class ContainerBase extends Container
{

public EntityPlayer player;
IContainerGuiCallback gui;

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
  if(!player.worldObj.isRemote)
    {
    PacketGui pkt = new PacketGui();
    pkt.packetData.setTag("gui", data);
    NetworkHandler.sendToPlayer((EntityPlayerMP) player, pkt);    
    }
  }

/**
 * send data from client-container to server container
 * @param data
 */
protected void sendDataToServer(NBTTagCompound data)
  {
  if(player.worldObj.isRemote)
    {
    PacketGui pkt = new PacketGui();
    pkt.packetData =  data;
    NetworkHandler.sendToServer(pkt);    
    }
  }

/**
 * send data from server container to client container
 * @param data
 */
protected void sendDataToClient(NBTTagCompound data)
  {
  if(!player.worldObj.isRemote)
    {
    PacketGui pkt = new PacketGui();
    pkt.packetData =  data;
    NetworkHandler.sendToPlayer((EntityPlayerMP) player, pkt);    
    }
  }

/**
 * client/server side method to receive packet data from PacketGui
 * @param data
 */
public final void onPacketData(NBTTagCompound data)
  {
  if(data.hasKey("gui"))
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

/**
 * subclasses should override this method to send any data from server to the client-side container.
 * This method is called immediately after the container has been constructed and set as the active container.
 * The data is received client-side immediately after the GUI has been constructed, initialized, and opened.
 */
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

public void refreshGui()
  {
  if(this.gui!=null)
    {
    this.gui.refreshGui();
    }
  }

public void removeSlots()
  {
  for(Slot s : ((List<Slot>)this.inventorySlots))
    {
    if(s.yDisplayPosition>=0)
      {
      s.yDisplayPosition-=10000;
      }
    }
  }

public void addSlots()
  {
  for(Slot s : ((List<Slot>)this.inventorySlots))
    {
    if(s.yDisplayPosition < 0)
      {
      s.yDisplayPosition+=10000;
      }
    }
  }

}
