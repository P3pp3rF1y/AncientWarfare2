/**
   Copyright 2012-2013 John Cummens (aka Shadowmage, Shadowmage4513)
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
package net.shadowmage.ancientwarfare.structure.template;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.structure.network.PacketStructure;
import net.shadowmage.ancientwarfare.structure.network.PacketStructureImageData;
import net.shadowmage.ancientwarfare.structure.network.PacketStructureImageList;
import net.shadowmage.ancientwarfare.structure.network.PacketStructureRemove;

public class StructureTemplateManager
{
private StructureTemplateManager(){}
private static StructureTemplateManager instance = new StructureTemplateManager(){};
public static StructureTemplateManager instance(){return instance;}

private HashMap<String,StructureTemplateClient> clientTemplates = new HashMap<String,StructureTemplateClient>();//server-side client-templates
private HashMap<String,BufferedImage> templateImages = new HashMap<String,BufferedImage>();//server-side images
private HashMap<String,String> imageMD5s = new HashMap<String,String>();
private HashMap<String,StructureTemplate> loadedTemplates = new HashMap<String,StructureTemplate>();

public void addTemplate(StructureTemplate template)
  {
  if(template.getValidationSettings()==null)
    {
    AWLog.logError("Could not load template for: " + template.name + " no validation settings present.");
    return;
    }
  if(template.getValidationSettings().isWorldGenEnabled())
    {
    WorldGenStructureManager.instance().registerWorldGenStructure(template);    
    }
  loadedTemplates.put(template.name, template);
  StructureTemplateClient cl = new StructureTemplateClient(template);
  clientTemplates.put(template.name, cl);
  
  MinecraftServer server = MinecraftServer.getServer();
  if(server!=null && server.isServerRunning() && server.getConfigurationManager()!=null)
    {
    NBTTagCompound tag = new NBTTagCompound();
    cl.writeToNBT(tag);    
    PacketStructure pkt = new PacketStructure();
    pkt.packetData.setTag("singleStructure", tag);
    NetworkHandler.sendToAllPlayers(pkt);    
    }
  }

public void onPlayerConnect(EntityPlayerMP player)
  {
  NBTTagList list = new NBTTagList();
  for(StructureTemplateClient cl : clientTemplates.values())
    {
    NBTTagCompound tag = new NBTTagCompound();
    cl.writeToNBT(tag);
    list.appendTag(tag);
    }
  PacketStructure pkt = new PacketStructure();
  pkt.packetData.setTag("structureList", list);
  NetworkHandler.sendToPlayer(player, pkt);
  
  PacketStructureImageList pkt2 = new PacketStructureImageList(this.imageMD5s);
  NetworkHandler.sendToPlayer(player, pkt2);
  }

public boolean removeTemplate(String name)
  {  
  if(this.loadedTemplates.containsKey(name))
    {
    this.loadedTemplates.remove(name);
    this.imageMD5s.remove(name);
    this.clientTemplates.remove(name);
    this.templateImages.remove(name);    
    NetworkHandler.sendToAllPlayers(new PacketStructureRemove(name));
    return true;
    }
  return false;
  }

public StructureTemplate getTemplate(String name)
  {
  return this.loadedTemplates.get(name);
  }

public void addTemplateImage(String imageName, BufferedImage image, String md5)
  {
  this.templateImages.put(imageName, image);
  this.imageMD5s.put(imageName, md5);
  }

public void handleClientImageNameListRequest(EntityPlayer player, Map<String, String> imageNames)
  {
  PacketStructureImageData pkt;
  for(String imageName : imageNames.keySet())
    {
    if(!templateImages.containsKey(imageName)){continue;}
    pkt = new PacketStructureImageData(imageName, templateImages.get(imageName));
    NetworkHandler.sendToPlayer((EntityPlayerMP) player, pkt);
    }
  }

public BufferedImage getTemplateImage(String imageName)
  {
  return templateImages.get(imageName);
  }

public String getImageMD5(String imageName)
  {
  return imageMD5s.get(imageName);
  }

}
