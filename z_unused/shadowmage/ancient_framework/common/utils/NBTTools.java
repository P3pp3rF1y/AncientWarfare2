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
package shadowmage.ancient_framework.common.utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

public class NBTTools
{


/**
* 0-END
* 1-BYTE
* 2-SHORT
* 3-INT
* 4-LONG
* 5-FLOAT
* 6-DOUBLE
* 7-BYTE-ARRAY
* 8-STRING
* 9-TAG-LIST
* 10-TAG-COMPOUND
* 11-INT-ARRAY
*/

/************************************************************ NBT STRING READ ********************************************************************************/

public static NBTTagCompound readNBTFrom(List<String> lines)
  {
  Iterator<String> it = lines.iterator();  
  String leadLine = it.next();
  NBTTagCompound tag = parseCompoundTag(leadLine, it);
  return tag;  
  }

public static NBTTagCompound parseCompoundTag(String leadLine, Iterator<String> it)
  {
  NBTTagCompound tag = new NBTTagCompound(leadLine.split("=",-1)[2].split("\\{",-1)[0]); 
  NBTBase baseTag = null;
  String line;
  while(it.hasNext() && (line = it.next())!=null)
    {
    if(line.startsWith("}"))
      {
      break;
      }
    baseTag = parseBaseTag(line, it);
    if(baseTag!=null)
      {
      tag.setTag(baseTag.getName(), baseTag);
      }    
    }
  return tag;
  }

public static NBTTagList parseListTag(String leadLine, Iterator<String> it)
  {
  String line;
  NBTBase tag;
  NBTTagList list = new NBTTagList(leadLine.split("=",-1)[2].split("\\{", -1)[0]);  
  while(it.hasNext() && (line = it.next())!=null)
    {
    if(line.startsWith("}"))
      {
      break;
      }
    tag = parseBaseTag(line, it);
    if(tag!=null)
      {
      list.appendTag(tag);
      }
    }
  return list;
  }

public static NBTBase parseBaseTag(String leadLine, Iterator<String> it)
  {
  String line = leadLine;
  if(!line.startsWith("TAG=")){throw new IllegalArgumentException("Cannot parse tag, illegal input: "+line);}
  String[] split = line.split("=", -1);
  byte id = Byte.parseByte(split[1]);
  if(id==10)
    {
    return parseCompoundTag(leadLine, it);
    }
  else if(id==9)
    {
    return parseListTag(leadLine, it);
    }
  else
    {
    String name = split[2].split("\\{",-1)[0];
    String data = split[2];
    String data1 = data.split("\\{",-1)[1];
    String data2 = data1.split("\\}",-1)[0];
    switch(id)
    {
    case 0:
    case 1:
      {
      return new NBTTagByte(name, StringTools.safeParseByte(data2));
      }
    case 2:
      {
      return new NBTTagShort(name, StringTools.safeParseShort(data2));
      }
    case 3:
      {
      return new NBTTagInt(name, StringTools.safeParseInt(data2));
      }
    case 4:
      {
      return new NBTTagLong(name, StringTools.safeParseLong(data2));
      }
    case 5:
      {
      return new NBTTagFloat(name, StringTools.safeParseFloat(data2));
      }
    case 6:
      {
      return new NBTTagDouble(name, StringTools.safeParseDouble(data2));
      }
    case 7:
      {
      return new NBTTagByteArray(name, StringTools.parseByteArray(data2));
      }
    case 8:
      {
      return new NBTTagString(name, data2);
      }    
    case 11:
      {
      return new NBTTagIntArray(name, StringTools.parseIntArray(data2));
      }
    }
    }
  return null;
  }
/************************************************************ NBT STRING WRITE ********************************************************************************/

public static void writeNBTToLines(NBTTagCompound tag, List<String> lines)
  {  
  writeCompoundTag(tag, lines);
  }

private static void writeTagBase(NBTBase tag, List<String> lines)
  {
  byte id = tag.getId();
  String lead = getTagLead(id, tag.getName());
  switch(id)
  {
  case 0:
  case 1:
    {
    lines.add(lead+String.valueOf(((NBTTagByte)tag).data)+"}");
    }
    break;
  case 2:
    {
    lines.add(lead+String.valueOf(((NBTTagShort)tag).data)+"}");
    }
    break;
  case 3:
    {
    lines.add(lead+String.valueOf(((NBTTagInt)tag).data)+"}");
    }
    break;
  case 4:
    {
    lines.add(lead+String.valueOf(((NBTTagLong)tag).data)+"}");
    }
    break;
  case 5:
    {
    lines.add(lead+String.valueOf(((NBTTagFloat)tag).data)+"}");
    }
    break;
  case 6:
    {
    lines.add(lead+String.valueOf(((NBTTagDouble)tag).data)+"}");
    }
    break;
  case 7:
    {
    lines.add(lead+String.valueOf(StringTools.getCSVStringForArray(((NBTTagByteArray)tag).byteArray))+"}");
    }
    break;
  case 8:
    {
    lines.add(lead+String.valueOf(((NBTTagString)tag).data)+"}");
    }
    break;
  case 9:
    {
    writeTagList((NBTTagList)tag, lines);
    }
    break;
  case 10:
    {
    writeCompoundTag((NBTTagCompound)tag, lines);
    }
    break;
  case 11:
    {
    lines.add(lead+String.valueOf(StringTools.getCSVStringForArray(((NBTTagIntArray)tag).intArray))+"}");
    }
    break;
  }
  }

private static void writeTagList(NBTTagList list, List<String> lines)
  {
  lines.add(getTagLead(list.getId(), list.getName()));
  for(int i = 0; i < list.tagCount(); i++)
    {
    writeTagBase(list.tagAt(i), lines);
    }
  lines.add("}");
  }

private static String getTagLead(byte type, String name)
  {
  return "TAG="+type+"="+name+"{";
  }

private static void writeCompoundTag(NBTTagCompound tag, List<String> lines)
  {
  Collection<NBTBase> tags = tag.getTags();
  lines.add("TAG=10="+tag.getName()+"{");
  for(NBTBase baseTag : tags)
    {
    writeTagBase(baseTag, lines);    
    }
  lines.add("}");
  }


/************************************************************ NBT STREAM WRITE ********************************************************************************/

/**
 * Writes a compressed NBTTagCompound to the OutputStream
 */
public static void writeNBTTagCompound(NBTTagCompound tag, DataOutputStream data) throws IOException
  {
  if (tag == null)
    {
    data.writeShort(-1);
    }
  else
    {
    byte[] var2 = CompressedStreamTools.compress(tag);
    data.writeShort((short)var2.length);
    data.write(var2);
    }
  }


public static void writeTagToStream(NBTTagCompound tag, ByteArrayDataOutput data)
  {
  if (tag == null)
    {
    data.writeShort(-1);
    }
  else
    {
    byte[] var2;
    try
      {
      var2 = CompressedStreamTools.compress(tag);
      data.writeShort((short)var2.length);
      data.write(var2);
      } 
    catch (IOException e)
      {
      e.printStackTrace();      
      }
    }  
  }


/************************************************************ NBT STREAM READ ********************************************************************************/
/**
 * Reads a compressed NBTTagCompound from the InputStream
 */
public static NBTTagCompound readNBTTagCompound(DataInputStream data) throws IOException
  {
  short var1 = data.readShort();  
  if (var1 < 0)
    {
    return null;
    }
  else
    {
    byte[] var2 = new byte[var1];
    data.readFully(var2);
    return CompressedStreamTools.decompress(var2);
    }
  }

/**
 * read a tag from a datastream, using google iowrapper
 * @param data
 * @return
 */
public static NBTTagCompound readTagFromStream(ByteArrayDataInput data)
  {
  short var1 = data.readShort();  
  if (var1 < 0)
    {
    return null;
    }
  else
    {
    byte[] var2 = new byte[var1];
    data.readFully(var2);
    try
      {
      return CompressedStreamTools.decompress(var2);
      } 
    catch (IOException e)
      {
      e.printStackTrace();
      }
    }
  return new NBTTagCompound();
  }

}
