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
package shadowmage.ancient_structures.common.config;

import java.io.File;
import java.util.logging.Logger;

import shadowmage.ancient_framework.common.config.ModConfiguration;

public class AWStructureStatics extends ModConfiguration
{

public static String templateExtension = "aws";
public static boolean enableVillageGen = true;
public static boolean enableStructureGeneration = true;
public static int chunkSearchRadius = 16;
public static int maxClusterValue = 500;
public static int randomChance = 75;
public static int randomRange = 1000;
public static int spawnProtectionRange = 0;

private static String worldGenCategory = "a_world-gen_settings";

/**
 * @param configFile
 * @param log
 * @param version
 */
public AWStructureStatics(File configFile, Logger log, String version)
  {
  super(configFile, log, version);
  }

@Override
public void initializeCategories()
  {
  this.config.addCustomCategoryComment(worldGenCategory, "Settings that effect all world-structure-generation.");
  }

@Override
public void initializeValues()
  {
  templateExtension = config.get(worldGenCategory, "template_extension", "aws").getString();
  enableVillageGen = config.get(worldGenCategory, "enable_village_generation", enableVillageGen).getBoolean(enableVillageGen);
  enableStructureGeneration = config.get(worldGenCategory, "enable_structure_generation", enableStructureGeneration).getBoolean(enableStructureGeneration);
  chunkSearchRadius = config.get(worldGenCategory, "validation_chunk_radius", chunkSearchRadius).getInt(chunkSearchRadius);
  maxClusterValue = config.get(worldGenCategory, "max_cluster_value", maxClusterValue).getInt(maxClusterValue);
  randomChance = config.get(worldGenCategory, "random_chance", randomChance).getInt(randomChance);
  randomRange = config.get(worldGenCategory, "random_range", randomRange).getInt(randomRange);
  spawnProtectionRange = config.get(worldGenCategory, "spawn_protection_chunk_radius", spawnProtectionRange).getInt(spawnProtectionRange);  
  this.config.save();
  }

}
