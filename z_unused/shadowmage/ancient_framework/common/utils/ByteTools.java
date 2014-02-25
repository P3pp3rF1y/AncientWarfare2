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
package shadowmage.ancient_framework.common.utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import shadowmage.ancient_framework.AWFramework;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

public class ByteTools
{

/**
 * return a list of byte[] of max size (packetSize)
 * @param allBytes
 * @param packetSize
 * @return
 */
public static List<byte[]> getByteChunks(byte[] allBytes, int packetSize)
  {
  int numOfChunks = (allBytes.length/packetSize)+1;
//  Config.logDebug("bytesLength: "+allBytes.length+"  numOfCunks: "+numOfChunks);
  List<byte[]> byteChunks = new ArrayList<byte[]>(numOfChunks);  
  int totalToWrite = allBytes.length;
  for(int i = 0; i < numOfChunks; i++)
    {
    int pkSize = totalToWrite>packetSize? packetSize : totalToWrite;
    byte[] chunk = new byte[pkSize];
    for(int k = 0; k < pkSize ; k++)
      {
      if(k + i*packetSize < allBytes.length)
        {
        chunk[k] = allBytes[k+i*packetSize];
        }
      }
    totalToWrite -= chunk.length;
    byteChunks.add(chunk);
    }
  return byteChunks;
  }

/**
 * return a single byte[] containing the data in chunks list, of the same
 * size as the sum of all arrays in chunk list
 * @param chunks
 * @param packetSize
 * @return
 */
public static byte[] compositeByteChunks(List<byte[]> chunks, int packetSize)
  {
  int totalLen = 0;
  for(byte[] ck : chunks)
    {
    totalLen += ck.length;
    }
  byte[] fullFile = new byte[totalLen];   
  
  int currentPos = 0;
  for(int i = 0; i < chunks.size(); i++)
    {
    for(int k = 0; k < chunks.get(i).length; k++, currentPos++)
      {
      fullFile[currentPos] = chunks.get(i)[k];
      }
    }
  return fullFile;
  }

/**
 * return a byte [] containing the data from all byte arrays
 * @param bytes
 * @return
 */
public static byte[] compositeByteChunks(byte[][] bytes)
  {
  byte [] allBytes;
  int totalLen = 0;
  for(int i = 0; i < bytes.length; i++)
    {
    totalLen += bytes[i].length;
    }
  allBytes = new byte[totalLen];
  
  int currentPos = 0;
  for(int i = 0; i < bytes.length; i++)
    {
    byte[] chunk = bytes[i];
    for(int k = 0; k < chunk.length; k++)
      {
      allBytes[currentPos]=chunk[k];
      currentPos++;
      }
    }
  
  return allBytes;
  }

/**
 * Writes a compressed NBTTagCompound to the OutputStream
 */
public static void writeNBTTagCompound(NBTTagCompound par0NBTTagCompound, DataOutputStream par1DataOutputStream) throws IOException
  {
  if (par0NBTTagCompound == null)
    {
    par1DataOutputStream.writeShort(-1);
    }
  else
    {
    byte[] var2 = CompressedStreamTools.compress(par0NBTTagCompound);
    par1DataOutputStream.writeShort((short)var2.length);
    par1DataOutputStream.write(var2);
    }
  }

/**
 * Reads a compressed NBTTagCompound from the InputStream
 */
public static NBTTagCompound readNBTTagCompound(DataInputStream par0DataInputStream) throws IOException
  {
  short var1 = par0DataInputStream.readShort();
  
  if (var1 < 0)
    {
    return null;
    }
  else
    {
    byte[] var2 = new byte[var1];
    par0DataInputStream.readFully(var2);
    return CompressedStreamTools.decompress(var2);
    }
  }

/**
 * Writes a compressed NBTTagCompound to the OutputStream
 */
public static void writeNBTTagCompound(NBTTagCompound par0NBTTagCompound, ByteArrayDataOutput data)
  {
  if (par0NBTTagCompound == null)
    {
    data.writeShort(-1);
    }
  else
    {
   
    byte[] var2;
    try
      {
      var2 = CompressedStreamTools.compress(par0NBTTagCompound);
      data.writeShort((short)var2.length);
      data.write(var2);
      } 
    catch (IOException e)
      {
      AWFramework.instance.logError("Severe error writing NBTTagCompound to dataStream");
      e.printStackTrace();
      } 
    }
  }

/**
 * Reads a compressed NBTTagCompound from the InputStream
 */
public static NBTTagCompound readNBTTagCompound(ByteArrayDataInput data)
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
      AWFramework.instance.logError("Severe error reading NBTTagCompound to dataStream");
      e.printStackTrace();
      }
    }
  NBTTagCompound tag = new NBTTagCompound();
  return tag;
  }

}
