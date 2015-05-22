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
package net.shadowmage.ancientwarfare.structure.world_gen;

import cpw.mods.fml.common.IWorldGenerator;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.structure.block.BlockDataManager;
import net.shadowmage.ancientwarfare.structure.config.AWStructureStatics;
import net.shadowmage.ancientwarfare.structure.gamedata.StructureMap;
import net.shadowmage.ancientwarfare.structure.gamedata.TownMap;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.WorldGenStructureManager;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBuilderWorldGen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Random;

public class WorldStructureGenerator implements IWorldGenerator {

    public static final HashSet<String> defaultTargetBlocks = new HashSet<String>();

    static {
        defaultTargetBlocks.add(BlockDataManager.INSTANCE.getNameForBlock(Blocks.dirt));
        defaultTargetBlocks.add(BlockDataManager.INSTANCE.getNameForBlock(Blocks.grass));
        defaultTargetBlocks.add(BlockDataManager.INSTANCE.getNameForBlock(Blocks.stone));
        defaultTargetBlocks.add(BlockDataManager.INSTANCE.getNameForBlock(Blocks.sand));
        defaultTargetBlocks.add(BlockDataManager.INSTANCE.getNameForBlock(Blocks.gravel));
        defaultTargetBlocks.add(BlockDataManager.INSTANCE.getNameForBlock(Blocks.sandstone));
        defaultTargetBlocks.add(BlockDataManager.INSTANCE.getNameForBlock(Blocks.clay));
        defaultTargetBlocks.add(BlockDataManager.INSTANCE.getNameForBlock(Blocks.iron_ore));
        defaultTargetBlocks.add(BlockDataManager.INSTANCE.getNameForBlock(Blocks.coal_ore));
    }

    public static final WorldStructureGenerator INSTANCE = new WorldStructureGenerator();

    private final Random rng;

    private WorldStructureGenerator() {
        rng = new Random();
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
        ChunkCoordinates cc = world.getSpawnPoint();
        float distSq = cc.getDistanceSquared(chunkX * 16, cc.posY, chunkZ * 16);
        if (AWStructureStatics.withinProtectionRange(distSq)) {
            return;
        }
        if (rng.nextFloat() < AWStructureStatics.randomGenerationChance)
            WorldGenTickHandler.INSTANCE.addChunkForGeneration(world, chunkX, chunkZ);
    }

    public void generateAt(int chunkX, int chunkZ, World world) {
        long t1 = System.currentTimeMillis();
        long seed = (((long) chunkX) << 32) | (((long) chunkZ) & 0xffffffffl);
        rng.setSeed(seed);
        int x = chunkX * 16 + rng.nextInt(16);
        int z = chunkZ * 16 + rng.nextInt(16);
        int y = getTargetY(world, x, z, false) + 1;
        if (y <= 0) {
            return;
        }


        int face = rng.nextInt(4);
        world.theProfiler.startSection("AWTemplateSelection");
        StructureTemplate template = WorldGenStructureManager.INSTANCE.selectTemplateForGeneration(world, rng, x, y, z, face);
        world.theProfiler.endSection();
        AWLog.logDebug("Template selection took: " + (System.currentTimeMillis() - t1) + " ms.");
        if (template == null) {
            return;
        }
        StructureMap map = AWGameData.INSTANCE.getData(world, StructureMap.class);
        if(map == null){
            return;
        }
        world.theProfiler.startSection("AWTemplateGeneration");
        if (attemptStructureGenerationAt(world, x, y, z, face, template, map)) {
            AWLog.log(String.format("Generated structure: %s at %s, %s, %s, time: %sms", template.name, x, y, z, (System.currentTimeMillis() - t1)));
        }
        world.theProfiler.endSection();
    }

    public static int getTargetY(World world, int x, int z, boolean skipWater) {
        Block block;
        for (int y = world.getActualHeight(); y > 0; y--) {
            block = world.getBlock(x, y, z);
            if (AWStructureStatics.skippableBlocksContains(block)) {
                continue;
            }
            if (skipWater && (block == Blocks.water || block == Blocks.flowing_water)) {
                continue;
            }
            return y;
        }
        return -1;
    }

    public static int getStepNumber(int x, int z, int minX, int maxX, int minZ, int maxZ) {
        int steps = 0;
        if (x < minX - 1) {
            steps += (minX - 1) - x;
        } else if (x > maxX + 1) {
            steps += x - (maxX + 1);
        }
        if (z < minZ - 1) {
            steps += (minZ - 1) - z;
        } else if (z > maxZ + 1) {
            steps += z - (maxZ + 1);
        }
        return steps;
    }

    public final boolean attemptStructureGenerationAt(World world, int x, int y, int z, int face, StructureTemplate template, StructureMap map) {
        long t1 = System.currentTimeMillis();
        int prevY = y;
        StructureBB bb = new StructureBB(x, y, z, face, template.xSize, template.ySize, template.zSize, template.xOffset, template.yOffset, template.zOffset);
        y = template.getValidationSettings().getAdjustedSpawnY(world, x, y, z, face, template, bb);
        bb.min.y -= prevY - y;
        bb.max.y -= prevY - y;
        int xs = bb.getXSize();
        int zs = bb.getZSize();
        int size = ((xs > zs ? xs : zs) / 16) + 3;
        if(map!=null) {
            Collection<StructureEntry> bbCheckList = map.getEntriesNear(world, x, z, size, true, new ArrayList<StructureEntry>());
            for (StructureEntry entry : bbCheckList) {
                if (bb.collidesWith(entry.getBB())) {
                    return false;
                }
            }
        }

        TownMap townMap = AWGameData.INSTANCE.getPerWorldData(world, TownMap.class);
        if (townMap!=null && townMap.intersectsWithTown(bb)) {
            AWLog.logDebug("Skipping structure generation: " + template.name + " at: " + bb + " for intersection with existing town");
            return false;
        }
        if (template.getValidationSettings().validatePlacement(world, x, y, z, face, template, bb)) {
            AWLog.logDebug("Validation took: " + (System.currentTimeMillis() - t1 + " ms"));
            generateStructureAt(world, x, y, z, face, template, map, bb);
            return true;
        }
        return false;
    }

    private void generateStructureAt(World world, int x, int y, int z, int face, StructureTemplate template, StructureMap map, StructureBB bb) {
        if(map!=null) {
            map.setGeneratedAt(world, x, y, z, face, new StructureEntry(x, y, z, face, template), template.getValidationSettings().isUnique());
        }
        WorldGenTickHandler.INSTANCE.addStructureForGeneration(new StructureBuilderWorldGen(world, template, face, x, y, z));
    }

}
