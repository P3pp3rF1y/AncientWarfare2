/*
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

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.structure.network.PacketStructure;
import net.shadowmage.ancientwarfare.structure.network.PacketStructureRemove;

import java.awt.image.BufferedImage;
import java.util.HashMap;

public class StructureTemplateManager {
    private HashMap<String, StructureTemplateClient> clientTemplates = new HashMap<>();//server-side client-templates
    private HashMap<String, BufferedImage> templateImages = new HashMap<>();//server-side images
    //private HashMap<String,String> imageMD5s = new HashMap<>();
    private HashMap<String, StructureTemplate> loadedTemplates = new HashMap<>();

    private StructureTemplateManager() {
    }

    public static final StructureTemplateManager INSTANCE = new StructureTemplateManager();

    public void addTemplate(StructureTemplate template) {
        if (template.getValidationSettings() == null) {
            return;
        }
        if (template.getValidationSettings().isWorldGenEnabled()) {
            WorldGenStructureManager.INSTANCE.registerWorldGenStructure(template);
        }
        loadedTemplates.put(template.name, template);
        StructureTemplateClient cl = new StructureTemplateClient(template);
        clientTemplates.put(template.name, cl);

        MinecraftServer server = MinecraftServer.getServer();
        if (server != null && server.isServerRunning() && server.getConfigurationManager() != null) {
            NBTTagCompound tag = new NBTTagCompound();
            cl.writeToNBT(tag);
            PacketStructure pkt = new PacketStructure();
            pkt.packetData.setTag("singleStructure", tag);
            NetworkHandler.sendToAllPlayers(pkt);
        }
    }

    public void onPlayerConnect(EntityPlayerMP player) {
        NBTTagList list = new NBTTagList();
        for (StructureTemplateClient cl : clientTemplates.values()) {
            NBTTagCompound tag = new NBTTagCompound();
            cl.writeToNBT(tag);
            list.appendTag(tag);
        }
        PacketStructure pkt = new PacketStructure();
        pkt.packetData.setTag("structureList", list);
        NetworkHandler.sendToPlayer(player, pkt);

//  PacketStructureImageList pkt2 = new PacketStructureImageList(this.imageMD5s);
//  NetworkHandler.sendToPlayer(player, pkt2);
    }

    public boolean removeTemplate(String name) {
        if (this.loadedTemplates.containsKey(name)) {
            this.loadedTemplates.remove(name);
            this.clientTemplates.remove(name);
            this.templateImages.remove(name);
            NetworkHandler.sendToAllPlayers(new PacketStructureRemove(name));
            return true;
        }
        return false;
    }

    public StructureTemplate getTemplate(String name) {
        return this.loadedTemplates.get(name);
    }

    public void addTemplateImage(String imageName, BufferedImage image) {
        this.templateImages.put(imageName, image);
    }

    public BufferedImage getTemplateImage(String imageName) {
        return templateImages.get(imageName);
    }

}
