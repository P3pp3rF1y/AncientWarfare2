/*
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
package net.shadowmage.ancientwarfare.structure.template;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.structure.config.AWStructureStatics;
import net.shadowmage.ancientwarfare.structure.gamedata.StructureMap;
import net.shadowmage.ancientwarfare.structure.template.build.validation.StructureValidator;
import net.shadowmage.ancientwarfare.structure.world_gen.StructureEntry;
import net.shadowmage.ancientwarfare.structure.world_gen.WorldGenManager;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

public class WorldGenStructureManager {

    private HashMap<String, Set<StructureTemplate>> templatesByBiome = new HashMap<>();
    /*
     * cached list objects, used for temp searching, as to not allocate new lists for every chunk-generated....
     */
    BlockPos rearBorderPos = BlockPos.ORIGIN;

    public static final WorldGenStructureManager INSTANCE = new WorldGenStructureManager();

    private WorldGenStructureManager() {
    }

    public void loadBiomeList() {
        for (Biome biome : Biome.REGISTRY) {
            if (biome == null) {
                continue;
            }
            String name = AWStructureStatics.getBiomeName(biome);
            templatesByBiome.put(name, new HashSet<>());
        }
    }

    public void registerWorldGenStructure(StructureTemplate template) {
        StructureValidator validation = template.getValidationSettings();
        Set<String> biomes = validation.getBiomeList();
        if (validation.isBiomeWhiteList()) {
            for (String biome : biomes) {
                if (templatesByBiome.containsKey(biome.toLowerCase(Locale.ENGLISH))) {
                    templatesByBiome.get(biome.toLowerCase(Locale.ENGLISH)).add(template);
                } else {
                    AWLog.logError("Could not locate biome: " + biome + " while registering template: " + template.name + " for world generation.");
                }
            }
        } else//blacklist, skip template-biomes
        {
            for (String biome : templatesByBiome.keySet()) {
                if (!biomes.isEmpty() && biomes.contains(biome.toLowerCase(Locale.ENGLISH))) {
                    continue;
                }
                templatesByBiome.get(biome).add(template);
            }
        }
    }

    @Nullable
    public StructureTemplate selectTemplateForGeneration(World world, Random rng, int x, int y, int z, EnumFacing face) {
        List<StructureTemplate> trimmedPotentialStructures = new ArrayList<>();
        List<StructureEntry> searchCache = new ArrayList<>();
        HashMap<String, Integer> distancesFound = new HashMap<>();
        StructureMap map = AWGameData.INSTANCE.getData(world, StructureMap.class);
        if (map == null) {
            return null;
        }
        int foundValue = 0, chunkDistance;
        float foundDistance, mx, mz;

        Biome biome = world.getBiome(new BlockPos(x, 1, z));
        String biomeName = AWStructureStatics.getBiomeName(biome);
        Collection<StructureEntry> duplicateSearchEntries = map.getEntriesNear(world, x, z, AWStructureStatics.duplicateStructureSearchRange, false, searchCache);
        for (StructureEntry entry : duplicateSearchEntries) {
            mx = entry.getBB().getCenterX() - x;
            mz = entry.getBB().getCenterZ() - z;
            foundDistance = MathHelper.sqrt(mx * mx + mz * mz);
            chunkDistance = (int) (foundDistance / 16.f);
            if (distancesFound.containsKey(entry.getName())) {
                int dist = distancesFound.get(entry.getName());
                if (chunkDistance < dist) {
                    distancesFound.put(entry.getName(), chunkDistance);
                }
            } else {
                distancesFound.put(entry.getName(), chunkDistance);
            }
        }

        Collection<StructureEntry> clusterValueSearchEntries = map.getEntriesNear(world, x, z, AWStructureStatics.clusterValueSearchRange, false, searchCache);
        for (StructureEntry entry : clusterValueSearchEntries) {
            foundValue += entry.getValue();
        }
        Set<StructureTemplate> potentialStructures = templatesByBiome.get(biomeName.toLowerCase(Locale.ENGLISH));
        if (potentialStructures == null || potentialStructures.isEmpty()) {
            return null;
        }

        int remainingValueCache = AWStructureStatics.maxClusterValue - foundValue;
        StructureValidator settings;
        int dim = world.provider.getDimension();
        for (StructureTemplate template : potentialStructures)//loop through initial structures, only adding to 2nd list those which meet biome, unique, value, and minDuplicate distance settings
        {
            settings = template.getValidationSettings();

            boolean dimensionMatch = !settings.isDimensionWhiteList();
            for (int i = 0; i < settings.getAcceptedDimensions().length; i++) {
                int dimTest = settings.getAcceptedDimensions()[i];
                if (dimTest == dim) {
                    dimensionMatch = !dimensionMatch;
                    break;
                }
            }
            if (!dimensionMatch)//skip if dimension is blacklisted, or not present on whitelist
            {
                continue;
            }
            if (settings.isUnique() && map.isGeneratedUnique(template.name)) {
                continue;
            }//skip already generated uniques
            if (settings.getClusterValue() > remainingValueCache) {
                continue;
            }//skip if cluster value is to high to place in given area
            if (distancesFound.containsKey(template.name)) {
                int dist = distancesFound.get(template.name);
                if (dist < settings.getMinDuplicateDistance()) {
                    continue;
                }//skip if minDuplicate distance is not met
            }
            if (!settings.shouldIncludeForSelection(WorldGenManager.getPreGenWorld((WorldServer) world), x, y, z, face, template)) {
                continue;
            }
            trimmedPotentialStructures.add(template);
        }
        if (trimmedPotentialStructures.isEmpty()) {
            return null;
        }
        int totalWeight = 0;
        for (StructureTemplate t : trimmedPotentialStructures) {
            totalWeight += t.getValidationSettings().getSelectionWeight();
        }
        totalWeight -= rng.nextInt(totalWeight + 1);
        StructureTemplate toReturn = null;
        for (StructureTemplate t : trimmedPotentialStructures) {
            toReturn = t;
            totalWeight -= t.getValidationSettings().getSelectionWeight();
            if (totalWeight <= 0) {
                break;
            }
        }
        return toReturn;
    }

}
