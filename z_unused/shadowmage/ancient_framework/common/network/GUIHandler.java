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
package shadowmage.ancient_framework.common.network;

import java.util.HashMap;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import shadowmage.ancient_framework.AWFramework;
import shadowmage.ancient_framework.client.gui.GuiContainerAdvanced;
import shadowmage.ancient_framework.common.config.AWLog;
import shadowmage.ancient_framework.common.container.ContainerBase;
import cpw.mods.fml.common.network.FMLNetworkHandler;
import cpw.mods.fml.common.network.IGuiHandler;

public class GUIHandler implements IGuiHandler
{

private static HashMap<Integer, Class <? extends GuiContainerAdvanced>> guiMap = new HashMap<Integer, Class<? extends GuiContainerAdvanced>>();
private static HashMap<Integer, Class <? extends ContainerBase>> containerMap = new HashMap<Integer, Class<? extends ContainerBase>>();

private static GUIHandler INSTANCE;
private GUIHandler(){}

public static GUIHandler instance()
  {
  if(INSTANCE==null)
    {
    INSTANCE= new GUIHandler();
    }
  return INSTANCE;
  }

@Override
public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
  {
  Class containerClass = containerMap.get(ID);
  if(containerClass!=null)
    {
    try
      {
      ContainerBase container = (ContainerBase) containerClass.getConstructor(EntityPlayer.class, int.class, int.class, int.class).newInstance(player, x, y, z);
      return container;
      }
    catch (Exception e)
      {
      e.printStackTrace();
      }
    }
  AWLog.logDebug("returned container was null for id: "+ID);
  return null;
  }

@Override
public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
  {  
  ContainerBase c = (ContainerBase) getServerGuiElement(ID, player, world, x, y, z);
  Class guiClass = guiMap.get(ID);
  if(guiClass!=null)
    {
    try
      {
      GuiContainerAdvanced gui = (GuiContainerAdvanced) guiClass.getConstructor(ContainerBase.class).newInstance(c);
      return gui;
      }
    catch (Exception e)
      {
      e.printStackTrace();
      } 
    }
  AWLog.logDebug("returned gui was null for id: "+ID);
  return null;
  }

/**
 * auto-wrapper for sending an openGUI packet from client-server to open a server side GUI without
 * special scripting in every damn entity/TE, also handles sending init data after the GUI is opened
 * all synched containers must openGUI through here, or they must handle synching manually
 * @param ID
 * @param player
 * @param world
 * @param x
 * @param y
 * @param z
 */
public void openGUI(int ID, EntityPlayer player, int x, int y, int z)
  {
  AWLog.logDebug("opening gui: "+ID + " onSide client: "+player.worldObj.isRemote);
  new Exception().printStackTrace();
  if(player.worldObj.isRemote)//send open GUI packet to server, let server relay actual open command
    {
    Packet03GuiComs pkt = new Packet03GuiComs();
    pkt.setGuiToOpen((byte)ID, x, y, z);
    AWFramework.proxy.sendPacketToServer(pkt);
    }
  else
    {
    FMLNetworkHandler.openGui(player, AWFramework.instance, ID, player.worldObj, x, y, z);
    if(player.openContainer instanceof ContainerBase)
      {
      List<NBTTagCompound> packetTags = ((ContainerBase)player.openContainer).getInitData();      
      for(NBTTagCompound tag : packetTags)
        {
        if(tag!=null)
          {
          Packet03GuiComs pkt = new Packet03GuiComs();
          pkt.setInitData(tag);
          AWFramework.proxy.sendPacketToPlayer(player, pkt);
          }
        }        
      }
    }  
  }

public void registerContainer(int id, Class<? extends ContainerBase> containerClz)
  {
  containerMap.put(id, containerClz); 
  }

public void registerGui(int id, Class<? extends GuiContainerAdvanced> guiClass)
  {
  guiMap.put(id, guiClass);  
  }

}
