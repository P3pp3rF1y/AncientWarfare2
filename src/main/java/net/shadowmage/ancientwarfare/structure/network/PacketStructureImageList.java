package net.shadowmage.ancientwarfare.structure.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.shadowmage.ancientwarfare.core.network.PacketBase;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateManager;

public class PacketStructureImageList extends PacketBase
{

List<String> imageNames = new ArrayList<String>();

public PacketStructureImageList(Collection<String> imageNames)
  {
  this.imageNames.addAll(imageNames);
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
    for(String line : imageNames)
      {
      writer.write(line);
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
  try
    {
    while((line = reader.readLine())!=null)
      {
      imageNames.add(line);
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
  StructureTemplateManager.instance().onStructureImageNameList(imageNames);
  }

}
