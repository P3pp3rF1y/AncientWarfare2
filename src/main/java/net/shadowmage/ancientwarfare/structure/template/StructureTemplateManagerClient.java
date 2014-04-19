package net.shadowmage.ancientwarfare.structure.template;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.TextureFileBased;
import net.shadowmage.ancientwarfare.core.util.TextureImageBased;
import net.shadowmage.ancientwarfare.structure.network.PacketStructureImageList;

public class StructureTemplateManagerClient
{
private StructureTemplateManagerClient(){}
private static StructureTemplateManagerClient instance = new StructureTemplateManagerClient(){};
public static StructureTemplateManagerClient instance(){return instance;}

private HashMap<String,ResourceLocation> clientTemplateImages = new HashMap<String, ResourceLocation>();
private HashMap<String,StructureTemplateClient> clientTemplates = new HashMap<String,StructureTemplateClient>();

public void onTemplateData(NBTTagCompound tag)
  {
  if(tag.hasKey("singleStructure"))
    {
    NBTTagCompound structureTag = tag.getCompoundTag("singleStructure");
    readClientStructure(structureTag);
    }
  else
    {
    clientTemplateImages.clear();
    clientTemplates.clear();
    NBTTagList list = tag.getTagList("structureList", Constants.NBT.TAG_COMPOUND);
    NBTTagCompound structureTag;
    for(int i = 0; i < list.tagCount(); i++)
      {
      structureTag = (NBTTagCompound) list.getCompoundTagAt(i);
      readClientStructure(structureTag);
      }    
    }
  }

private void readClientStructure(NBTTagCompound tag)
  {
  StructureTemplateClient template = StructureTemplateClient.readFromNBT(tag);
  addTemplate(template);
  }

public Collection<StructureTemplateClient> getClientStructures()
  {
  return clientTemplates.values();
  }

public StructureTemplateClient getClientTemplate(String name)
  {
  return clientTemplates.get(name);
  }

public void addTemplate(StructureTemplateClient template)
  {
  clientTemplates.put(template.name, template);
  loadTemplateImage(template.name+".png");
  }

public ResourceLocation getImageFor(String templateName)
  {
  return clientTemplateImages.get(templateName+".png");
  }

public void handleStructureImageNameList(List<String> imageNames)
  {  
  AWLog.logDebug("receiving image names list of: "+imageNames);
  
  String pathBase = "config/AWConfig/structures/image_cache/";
  File dirBase = new File(pathBase);
  dirBase.mkdirs();
  List<String> neededFiles = new ArrayList<String>();
  File testFile;
  for(String name : imageNames)
    {
    testFile = new File(pathBase+name);
    if(!clientTemplateImages.containsKey(name) && !testFile.exists())
      {
      AWLog.logDebug("adding: "+name+ " to needed structure images list");
      neededFiles.add(name);
      }
    }  
  
  AWLog.logDebug("sending needed image list of: "+neededFiles);
  PacketStructureImageList pkt = new PacketStructureImageList(neededFiles);
  NetworkHandler.sendToServer(pkt);
  }

private void loadTemplateImage(String imageName)
  {
  BufferedImage image = StructureTemplateManager.instance().getTemplateImage(imageName);
  String pathBase = "config/AWConfig/structures/image_cache/";
  ResourceLocation loc = new ResourceLocation("ancientwarfare", pathBase+imageName);
  if(image!=null)
    {
    AWLog.logDebug("Loading template image from server-image cache: "+imageName);
    Minecraft.getMinecraft().renderEngine.loadTexture(loc, new TextureImageBased(loc, image));
    clientTemplateImages.put(imageName, loc);
    }
  else
    {
    File file = new File(pathBase+imageName);
    if(file.exists())
      {    
      AWLog.logDebug("Loading template image from file: "+imageName);
      Minecraft.getMinecraft().renderEngine.loadTexture(loc, new TextureFileBased(loc, file));
      clientTemplateImages.put(imageName, loc);    
      }
    }  
  }

public void addStructureImage(String imageName, BufferedImage image)
  {
  AWLog.logDebug("Received client side image of: "+imageName+" saving to image cache, and loading texture");
  String pathBase = "config/AWConfig/structures/image_cache/";
  File file;
  try
    {
    file = new File(pathBase+imageName);
    ImageIO.write(image, "png", file);    
    } 
  catch (IOException e)
    {
    e.printStackTrace();
    }
  loadTemplateImage(imageName);
  }

}
