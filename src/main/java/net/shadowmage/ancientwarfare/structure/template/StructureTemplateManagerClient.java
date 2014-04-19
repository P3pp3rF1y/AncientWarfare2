package net.shadowmage.ancientwarfare.structure.template;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
import net.shadowmage.ancientwarfare.structure.config.AWStructureStatics;
import net.shadowmage.ancientwarfare.structure.network.PacketStructureImageList;

public class StructureTemplateManagerClient
{
private StructureTemplateManagerClient(){}
private static StructureTemplateManagerClient instance = new StructureTemplateManagerClient(){};
public static StructureTemplateManagerClient instance(){return instance;}

private HashMap<String,String> clientImageMD5s = new HashMap<String, String>();
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

public void handleStructureImageNameList(Map<String, String> imageMap)
  {  
  AWLog.logDebug("receiving image names map of: "+imageMap);
  
  String pathBase = "config/AWConfig/structures/image_cache/";
  File dirBase = new File(pathBase);
  dirBase.mkdirs();
  Map<String, String> neededFiles = new HashMap<String, String>();
  File testFile;
  for(String name : imageMap.keySet())
    {
    testFile = new File(pathBase+name);
    if(!clientTemplateImages.containsKey(name) && !testFile.exists())
      {
      AWLog.logDebug("adding: "+name+ " to needed structure images list");
      neededFiles.put(name, name);
      }
    else if(clientImageMD5s.containsKey(name) && !clientImageMD5s.get(name).equals(imageMap.get(name)))
      {
      AWLog.logDebug("adding: "+name+ " to needed structure images list for md5 mismatch");
      neededFiles.put(name, name);      
      }
    }  
  
  AWLog.logDebug("sending needed image list of: "+neededFiles);
  PacketStructureImageList pkt = new PacketStructureImageList(neededFiles);
  NetworkHandler.sendToServer(pkt);
  }

private void loadTemplateImage(String imageName)
  {
  String pathBase = "config/AWConfig/structures/image_cache/";
  File file = new File(pathBase+imageName);
  ResourceLocation loc = new ResourceLocation("ancientwarfare", pathBase+imageName);
  
  if(!file.exists())
    {
    BufferedImage image = StructureTemplateManager.instance().getTemplateImage(imageName);
    if(image!=null)
      {
      AWLog.logDebug("Loading template image from server template images cache: "+imageName);
      Minecraft.getMinecraft().renderEngine.loadTexture(loc, new TextureImageBased(loc, image));
      String md5 = StructureTemplateManager.instance().getImageMD5(imageName);
      clientTemplateImages.put(imageName, loc);
      clientImageMD5s.put(imageName, md5);      
      }
    }
  else
    {
    String md5;
    try
      {
      BufferedImage image = ImageIO.read(file);
      if(image.getWidth()==AWStructureStatics.structureImageWidth && image.getHeight()==AWStructureStatics.structureImageHeight)
        {
        Minecraft.getMinecraft().renderEngine.loadTexture(loc, new TextureImageBased(loc, image));      
        md5 = getMD5(file);
        clientImageMD5s.put(imageName, md5);
        clientTemplateImages.put(imageName, loc);  
        AWLog.logDebug("Loading template image from file in image_cache: "+imageName);        
        }  
      else
        {
        AWLog.logError("Error parsing image: "+file.getName()+" image was not of correct size. Found: "+image.getWidth()+"x"+image.getHeight()+"  Needed: "+AWStructureStatics.structureImageWidth+"x"+AWStructureStatics.structureImageHeight);
        }
      } 
    catch (IOException e)
      {
      e.printStackTrace();
      }
    }  
  }

private String getMD5(File file) throws IOException
  {
  MessageDigest md;
  try
    {
    md = MessageDigest.getInstance("MD5");
    } 
  catch (NoSuchAlgorithmException e)
    {    
    e.printStackTrace();
    return null;
    }
  FileInputStream fis = new FileInputStream(file);
  byte[] buffer = new byte[1024];
  int read;
  while((read = fis.read(buffer))>=0)
    {
    md.update(buffer, 0, read);
    }
  byte[] data = md.digest();
  String md5 = "";
  StringBuilder sb = new StringBuilder(2*data.length);
  for(byte b : data)
    {
    sb.append(String.format("%02x", b&0xff));
    }
  md5 = sb.toString();
  return md5;
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
