package net.shadowmage.ancientwarfare.structure.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import net.shadowmage.ancientwarfare.core.network.PacketBase;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateManager;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateManagerClient;

public class PacketStructureImageList extends PacketBase
{

Map<String, String> imageNames = new HashMap<String, String>();

public PacketStructureImageList(Map<String, String> imageNames)
  {
  this.imageNames.putAll(imageNames);
  }

public PacketStructureImageList()
  {
  }

@Override
protected void writeToStream(ByteBuf data)
  {
  ByteBufOutputStream bbos = new ByteBufOutputStream(data); 
  OutputStreamWriter osw = new OutputStreamWriter(bbos);
  BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(bbos));
  try
    {
    for(String line : imageNames.keySet())
      {
      writer.write(line);
      writer.newLine();      
      writer.write(imageNames.get(line));
      writer.newLine();
      }
    } 
  catch (IOException e)
    {
    e.printStackTrace();
    }
  try
    {
    writer.close();
    } 
  catch (IOException e)
    {
    e.printStackTrace();
    }
  try
    {
    osw.close();
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
  ByteBufInputStream bbis = new ByteBufInputStream(data);
  InputStreamReader isr = new InputStreamReader(bbis);  
  BufferedReader reader = new BufferedReader(isr);
  
  String line = null;
  String line2;
  try
    {
    while((line = reader.readLine())!=null && (line2 = reader.readLine())!=null)
      {
      imageNames.put(line, line2);
      }
    } 
  catch (IOException e1)
    {
    e1.printStackTrace();
    }  
  try
    {
    reader.close();
    } 
  catch (IOException e)
    {
    e.printStackTrace();
    }
  try
    {
    isr.close();
    } 
  catch (IOException e)
    {
    e.printStackTrace();
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
  if(player.worldObj.isRemote)
    {
    StructureTemplateManagerClient.instance().handleStructureImageNameList(imageNames);    
    }
  else
    {
    StructureTemplateManager.instance().handleClientImageNameListRequest(player, imageNames);
    }
  }

}
