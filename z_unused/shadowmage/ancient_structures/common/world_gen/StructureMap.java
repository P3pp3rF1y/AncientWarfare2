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
package shadowmage.ancient_structures.common.world_gen;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.world.World;
import shadowmage.ancient_framework.common.config.AWLog;
import shadowmage.ancient_framework.common.gamedata.GameData;
import shadowmage.ancient_structures.common.template.StructureTemplate;

public class StructureMap extends GameData
{

private StructureDimensionMap map;

public StructureMap(String name)
  {
  super(name);
  map = new StructureDimensionMap();
  }

public StructureMap()
  {
  super("AWStructureMap");
  map = new StructureDimensionMap();
  }

@Override
public void readFromNBT(NBTTagCompound nbttagcompound)
  {
  AWLog.logDebug("reading structure map from nbt...");
  NBTTagCompound mapTag = nbttagcompound.getCompoundTag("map");
  map.readFromNBT(mapTag);
  }

@Override
public void writeToNBT(NBTTagCompound nbttagcompound)
  {
  AWLog.logDebug("writing structure map to nbt...");
  NBTTagCompound mapTag = new NBTTagCompound();
  map.writeToNBT(mapTag);
  nbttagcompound.setTag("map", mapTag);
  }

public Collection<StructureEntry> getEntriesNear(World world, int worldX, int worldZ, int chunkRadius, boolean expandBySize, Collection<StructureEntry> list)
  {
  int cx = worldX >> 4;
  int cz = worldZ >> 4;
  return map.getEntriesNear(world.provider.dimensionId, cx, cz, chunkRadius, expandBySize, list);
  }

public void setGeneratedAt(World world, int worldX, int worldY, int worldZ, int face, StructureTemplate structure)
  {
  int cx = worldX >> 4;
  int cz = worldZ >> 4;
  StructureEntry entry = new StructureEntry(worldX, worldY, worldZ, face, structure);  
  map.setGeneratedAt(world.provider.dimensionId, cx, cz, entry, structure.getValidationSettings().isUnique());
//  AWLog.logDebug("marking generated at: "+cx+","+cz);
  this.markDirty();
  }

public Collection<String> getGeneratedUniques()
  {
  return this.map.generatedUniques;  
  }

private class StructureDimensionMap
{
private HashMap<Integer, StructureWorldMap> mapsByDimension = new HashMap<Integer, StructureWorldMap>();
Set<String> generatedUniques = new HashSet<String>();

public Collection<StructureEntry> getEntriesNear(int dimension, int chunkX, int chunkZ, int chunkRadius, boolean expandBySize, Collection<StructureEntry> list)
  {
  if(mapsByDimension.containsKey(dimension))
    {
    return mapsByDimension.get(dimension).getEntriesNear(chunkX, chunkZ, chunkRadius, expandBySize, list);
    }
  return Collections.emptyList();
  }

public void setGeneratedAt(int dimension, int chunkX, int chunkZ, StructureEntry entry, boolean unique)
  {
  if(!this.mapsByDimension.containsKey(dimension))
    {
    this.mapsByDimension.put(dimension, new StructureWorldMap());
    }
  this.mapsByDimension.get(dimension).setGeneratedAt(chunkX, chunkZ, entry);
  if(unique)
    {
    generatedUniques.add(entry.name);
    }
  }

public void readFromNBT(NBTTagCompound nbttagcompound)
  {
  NBTTagList uniquesList = nbttagcompound.getTagList("uniques");
  NBTTagList dimensionList = nbttagcompound.getTagList("dimensions");
  
  NBTTagCompound dimensionTag;
  int dim;
  for(int i = 0; i < dimensionList.tagCount(); i++)
    {
    dimensionTag = (NBTTagCompound) dimensionList.tagAt(i);
    dim = dimensionTag.getInteger("dim");     
    if(!this.mapsByDimension.containsKey(dim))
      {
      this.mapsByDimension.put(dim, new StructureWorldMap());
      }
    this.mapsByDimension.get(dim).readFromNBT(dimensionTag.getCompoundTag("data"));
    }
  
  NBTTagString uniqueTag;
  for(int i = 0; i < uniquesList.tagCount(); i++)
    {
    uniqueTag = (NBTTagString) uniquesList.tagAt(i);
    generatedUniques.add(uniqueTag.data);
    }
  }

public void writeToNBT(NBTTagCompound nbttagcompound)
  {
  NBTTagList dimensionsList = new NBTTagList();
  NBTTagList uniquesList = new NBTTagList();
  NBTTagCompound dimensionTag;
  NBTTagCompound dimensionData;
  for(Integer dim : this.mapsByDimension.keySet())
    {
    dimensionTag = new NBTTagCompound();
    dimensionData = new NBTTagCompound();
    dimensionTag.setInteger("dim", dim);
    mapsByDimension.get(dim).writeToNBT(dimensionData);
    dimensionTag.setTag("data", dimensionData);        
    dimensionsList.appendTag(dimensionTag);
    }  
  
  for(String name : this.generatedUniques)
    {
    uniquesList.appendTag(new NBTTagString("name", name));
    }
  nbttagcompound.setTag("dimensions", dimensionsList);
  nbttagcompound.setTag("uniques", uniquesList);
  }
}//end structure dimension map

private class StructureWorldMap
{

private HashMap<Integer, HashMap<Integer, StructureEntry>> worldMap = new HashMap<Integer, HashMap<Integer, StructureEntry>>();
private int largestGeneratedX;
private int largestGeneratedZ;

public Collection<StructureEntry> getEntriesNear(int chunkX, int chunkZ, int chunkRadius, boolean expandBySize, Collection<StructureEntry> list)
  {
  StructureEntry entry;
  int crx = chunkRadius;
  int crz = chunkRadius;
  if(expandBySize)
    {
    crx+=largestGeneratedX/16;
    crz+=largestGeneratedZ/16;
    }
//  AWLog.logDebug("checking radius for structures: "+(chunkX-crx)+","+(chunkZ-crz)+"::"+(chunkX+crx)+","+(chunkZ+crz));
  for(int x = chunkX-crx; x<=chunkX+crx; x++)
    {
    if(worldMap.containsKey(x))
      {
      for(int z = chunkZ-crz; z<=chunkZ+crz; z++)
        {
        entry = worldMap.get(x).get(z);
        if(entry!=null)
          {
          list.add(entry);
          }
        }
      }
    }
//  AWLog.logDebug("found: "+list.size()+" nearby entries");
  return list;
  }

public void setGeneratedAt(int chunkX, int chunkZ, StructureEntry entry)
  {
  if(!this.worldMap.containsKey(chunkX))
    {
    this.worldMap.put(chunkX, new HashMap<Integer, StructureEntry>());
    }
  this.worldMap.get(chunkX).put(chunkZ, entry);
  int x = entry.bb.getXSize();
  int z = entry.bb.getZSize();
  if(x>largestGeneratedX){largestGeneratedX = x;}
  if(z>largestGeneratedZ){largestGeneratedZ = z;}
  }

public void readFromNBT(NBTTagCompound nbttagcompound)
  {
  NBTTagList entryList = nbttagcompound.getTagList("entries");
  StructureEntry entry;
  NBTTagCompound entryTag;
  int x, z;
  for(int i = 0; i < entryList.tagCount(); i++)
    {
    entryTag = (NBTTagCompound) entryList.tagAt(i);
    x = entryTag.getInteger("x");
    z = entryTag.getInteger("z");
    entry = new StructureEntry();
    entry.readFromNBT(entryTag);
    if(!this.worldMap.containsKey(x))
      {
      this.worldMap.put(x, new HashMap<Integer, StructureEntry>());
      }
    this.worldMap.get(x).put(z, entry);
    }
  this.largestGeneratedX = nbttagcompound.getInteger("largestX");
  this.largestGeneratedZ = nbttagcompound.getInteger("largestZ");
  }

public void writeToNBT(NBTTagCompound nbttagcompound)
  {
  NBTTagList entryList = new NBTTagList();
  NBTTagCompound entryTag;
  for(Integer x : this.worldMap.keySet())
    {
    for(Integer z : this.worldMap.get(x).keySet())
      {
      entryTag = new NBTTagCompound();
      entryTag.setInteger("x", x);
      entryTag.setInteger("z", z);
      this.worldMap.get(x).get(z).writeToNBT(entryTag);
      entryList.appendTag(entryTag);
      }
    }
  nbttagcompound.setInteger("largestX", largestGeneratedX);
  nbttagcompound.setInteger("largestZ", largestGeneratedZ);
  nbttagcompound.setTag("entries", entryList);
  }
}//end structure X Map

@Override
public void handlePacketData(NBTTagCompound data)
  {
  /**
   * NOOP FOR STRUCTURE MAP -- not needed on client-side
   */
  }
}
