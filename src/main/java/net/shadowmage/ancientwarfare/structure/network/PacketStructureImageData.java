package net.shadowmage.ancientwarfare.structure.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.network.PacketBase;
import net.shadowmage.ancientwarfare.core.util.StringTools;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateManagerClient;

public class PacketStructureImageData extends PacketBase
{

String imageName;
BufferedImage image;

public PacketStructureImageData(String imageName, BufferedImage image)
  {
  this.imageName = imageName;
  this.image = image;
  }

public PacketStructureImageData()
  {
  }

@Override
protected void writeToStream(ByteBuf data)
  { 
  StringTools.writeString(data, imageName);  
  ByteBufOutputStream bbos = new ByteBufOutputStream(data);    
  try
    {
    AWLog.logDebug("writing image of: "+image);
    ImageIO.write(image, "png", bbos);
    } 
  catch (IOException e1)
    {
    e1.printStackTrace();
    }
   
  try
    {
    bbos.close();
    } 
  catch (IOException e)
    {
    e.printStackTrace();
    }
  }

@Override
protected void readFromStream(ByteBuf data)
  {
  AWLog.logDebug("reading image packet from stream....");

  imageName = StringTools.readString(data);
  AWLog.logDebug("read image name of: "+imageName);
  
  ByteBufInputStream bbis = new ByteBufInputStream(data);
  try
    {
    image = ImageIO.read(bbis);
    AWLog.logDebug("read image of: "+image);
    } 
  catch (IOException e1)
    {
    e1.printStackTrace();
    }
   
  try
    {
    bbis.close();
    } 
  catch (IOException e)
    {
    e.printStackTrace();
    }
  }

@Override
protected void execute()
  {
  if(image!=null && imageName!=null)
    {
    StructureTemplateManagerClient.instance().addStructureImage(imageName, image);    
    }
  }

}
