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
package shadowmage.ancient_structures.common.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import shadowmage.ancient_framework.common.gamedata.AWGameData;
import shadowmage.ancient_framework.common.utils.BlockPosition;
import shadowmage.ancient_structures.common.config.AWStructureStatics;
import shadowmage.ancient_structures.common.template.StructureTemplate;
import shadowmage.ancient_structures.common.template.build.validation.StructureValidator;
import shadowmage.ancient_structures.common.world_gen.StructureEntry;
import shadowmage.ancient_structures.common.world_gen.StructureMap;

public class WorldGenStructureManager
{

private HashMap<String, Set<StructureTemplate>> templatesByBiome = new HashMap<String, Set<StructureTemplate>>();

private static WorldGenStructureManager instance = new WorldGenStructureManager();
private WorldGenStructureManager(){}
public static WorldGenStructureManager instance(){return instance;}

public void loadBiomeList()
  {
  BiomeGenBase biome;
  for(int i = 0; i < BiomeGenBase.biomeList.length; i++)
    {
    biome = BiomeGenBase.biomeList[i];
    if(biome==null){continue;}
    templatesByBiome.put(biome.biomeName.toLowerCase(), new HashSet<StructureTemplate>());
    }
  }

public void registerWorldGenStructure(StructureTemplate template)
  {
  StructureValidator validation = template.getValidationSettings();
  Set<String> biomes = validation.getBiomeList();
  if(validation.isBiomeWhiteList())
    {    
    for(String biome : biomes)
      {
      if(templatesByBiome.containsKey(biome.toLowerCase()))
        {
        templatesByBiome.get(biome.toLowerCase()).add(template);
        }      
      }
    }
  else//blacklist, skip template-biomes
    {
    for(String biome : templatesByBiome.keySet())
      {
      if(!biomes.isEmpty() && biomes.contains(biome.toLowerCase())){continue;}
      templatesByBiome.get(biome).add(template);
      }
    }
  }

/**
 * cached list objects, used for temp searching, as to not allocate new lists for every chunk-generated....
 */
List<StructureEntry> searchCache = new ArrayList<StructureEntry>();
List<StructureTemplate> trimmedPotentialStructures = new ArrayList<StructureTemplate>();
HashMap<String, Integer> distancesFound = new HashMap<String, Integer>();
int remainingValueCache;
BlockPosition rearBorderPos = new BlockPosition(0,0,0);

/**
 * returns the remaining value from cluster-value check from the last selected structure/attempt at structure selection
 * @return
 */
public int getRemainingValue()
  {
  return remainingValueCache;
  }

public StructureTemplate selectTemplateForGeneration(World world, Random rng, int x, int y, int z, int face, int chunkSearchRange)
  {
  remainingValueCache = 0;
  searchCache.clear();
  trimmedPotentialStructures.clear();
  distancesFound.clear();
  StructureMap map = AWGameData.get(world, "AWStructureMap", StructureMap.class);
  if(map==null){return null;}
  int foundValue, chunkDistance;
  float foundDistance, mx, mz;
   
  String biomeName = world.getBiomeGenForCoords(x, z).biomeName.toLowerCase();
  Collection<StructureEntry> genEntries = map.getEntriesNear(world, x, z, chunkSearchRange, false, searchCache);
  
  foundValue = 0;
  for(StructureEntry entry : genEntries)
    {
    foundValue += entry.getValue();
    mx = entry.getBB().getCenterX() - x;
    mz = entry.getBB().getCenterZ() - z;
    foundDistance = MathHelper.sqrt_float(mx * mx + mz * mz);
    chunkDistance = (int)(foundDistance/16.f);
    if(distancesFound.containsKey(entry.getName()))
      {
      int dist = distancesFound.get(entry.getName());
      if(chunkDistance<dist)
        {
        distancesFound.put(entry.getName(), chunkDistance);
        }
      }
    else
      {
      distancesFound.put(entry.getName(), chunkDistance);
      }
    }
  remainingValueCache = AWStructureStatics.maxClusterValue - foundValue;
  Collection<String> generatedUniques = map.getGeneratedUniques();
  Set<StructureTemplate> potentialStructures = templatesByBiome.get(biomeName);
  if(potentialStructures==null || potentialStructures.isEmpty()){return null;}
  StructureValidator settings;
  int dim = world.provider.dimensionId;
  for(StructureTemplate template : potentialStructures)//loop through initial structures, only adding to 2nd list those which meet biome, unique, value, and minDuplicate distance settings
    {
    settings = template.getValidationSettings();
     
    boolean dimensionMatch = !settings.isDimensionWhiteList();
    for(int i = 0; i < settings.getAcceptedDimensions().length; i++)
      {
      int dimTest = settings.getAcceptedDimensions()[i];
      if(dimTest == dim)
        {
        dimensionMatch = !dimensionMatch;        
        break;
        }      
      }    
    if(!dimensionMatch)//skip if dimension is blacklisted, or not present on whitelist
      {
      continue;
      }
    if(generatedUniques.contains(template.name))
      {
      continue;
      }//skip already generated uniques
    if(settings.getClusterValue()>remainingValueCache)
      {
      continue;
      }//skip if cluster value is to high to place in given area
    if(distancesFound.containsKey(template.name))
      {
      int dist = distancesFound.get(template.name);
      if(dist<settings.getMinDuplicateDistance())
        {
        continue;
        }//skip if minDuplicate distance is not met
      }
    if(!settings.shouldIncludeForSelection(world, x, y, z, face, template))
      {
      continue;
      }   
    trimmedPotentialStructures.add(template);
    }  
  if(trimmedPotentialStructures.isEmpty()){return null;}
  int totalWeight = 0;
  for(StructureTemplate t : trimmedPotentialStructures)
    {
    totalWeight += t.getValidationSettings().getSelectionWeight();
    }
  totalWeight -= rng.nextInt(totalWeight+1);
  StructureTemplate toReturn = null;
  for(StructureTemplate t : trimmedPotentialStructures)
    {
    if(totalWeight<=0)
      {
      toReturn = t;
      break;
      }   
    totalWeight -= t.getValidationSettings().getSelectionWeight();     
    }
  distancesFound.clear();
  trimmedPotentialStructures.clear();
  return toReturn;
  }

}
