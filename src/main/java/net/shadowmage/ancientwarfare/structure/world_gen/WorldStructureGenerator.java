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
package net.shadowmage.ancientwarfare.structure.world_gen;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
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
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class WorldStructureGenerator implements IWorldGenerator {
    private static final Set<StructureChunkGenTicket> structureTickets = Sets.newHashSet();
    public static final HashSet<String> defaultTargetBlocks = new HashSet<>();

    static {
        defaultTargetBlocks.add(BlockDataManager.INSTANCE.getNameForBlock(Blocks.DIRT));
        defaultTargetBlocks.add(BlockDataManager.INSTANCE.getNameForBlock(Blocks.GRASS));
        defaultTargetBlocks.add(BlockDataManager.INSTANCE.getNameForBlock(Blocks.STONE));
        defaultTargetBlocks.add(BlockDataManager.INSTANCE.getNameForBlock(Blocks.SAND));
        defaultTargetBlocks.add(BlockDataManager.INSTANCE.getNameForBlock(Blocks.GRAVEL));
        defaultTargetBlocks.add(BlockDataManager.INSTANCE.getNameForBlock(Blocks.SANDSTONE));
        defaultTargetBlocks.add(BlockDataManager.INSTANCE.getNameForBlock(Blocks.CLAY));
        defaultTargetBlocks.add(BlockDataManager.INSTANCE.getNameForBlock(Blocks.IRON_ORE));
        defaultTargetBlocks.add(BlockDataManager.INSTANCE.getNameForBlock(Blocks.COAL_ORE));
    }

    public static final WorldStructureGenerator INSTANCE = new WorldStructureGenerator();

    private final Random rng;

    private WorldStructureGenerator() {
        rng = new Random();
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        BlockPos cc = world.getSpawnPoint();
        double distSq = cc.distanceSq(chunkX * 16, cc.getY(), chunkZ * 16);
        if (AWStructureStatics.withinProtectionRange(distSq)) {
            return;
        }
        if (rng.nextFloat() < AWStructureStatics.randomGenerationChance) {
            if (world == null) {
                return;
            }
            long t1 = System.currentTimeMillis();
            long seed = (((long) chunkX) << 32) | (((long) chunkZ) & 0xffffffffl);
            rng.setSeed(seed);
            int x = chunkX * 16 + rng.nextInt(16);
            int z = chunkZ * 16 + rng.nextInt(16);
            int y = getTargetY(world, x, z, false) + 1;
            if (y <= 0) {
                return;
            }

            EnumFacing face = EnumFacing.HORIZONTALS[rng.nextInt(4)];
            world.profiler.startSection("AWTemplateSelection");
            StructureTemplate template = WorldGenStructureManager.INSTANCE.selectTemplateForGeneration(world, rng, x, y, z, face);
            world.profiler.endSection();
            AWLog.logDebug("Template selection took: " + (System.currentTimeMillis() - t1) + " ms.");
            if (template == null) {
                return;
            }
            StructureMap map = AWGameData.INSTANCE.getData(world, StructureMap.class);
            if (map == null) {
                return;
            }
            BlockPos pos = new BlockPos(x, y, z);
            if (isLocationValidForTemplate(new NoGenWorld((WorldServer) world), pos, face, template, map)) {
                markStructureGenerated(world, pos, face, template, map);
                StructureChunkGenTicket ticket = new StructureChunkGenTicket(world, template, face, pos);
                structureTickets.add(ticket);
            }
        }
        generateStructures(random, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
    }

    private void generateStructures(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        generateStructuresInChunk(chunkX, chunkZ, world);
        generateStructuresInChunk(chunkX + 1, chunkZ, world);
        generateStructuresInChunk(chunkX - 1, chunkZ, world);
        generateStructuresInChunk(chunkX, chunkZ + 1, world);
        generateStructuresInChunk(chunkX, chunkZ - 1, world);
    }

    private void generateStructuresInChunk(int chunkX, int chunkZ, World world) {
        Iterator<StructureChunkGenTicket> it = structureTickets.iterator();
        while(it.hasNext()) {
            StructureChunkGenTicket ticket = it.next();
            if (ticket.dimension == world.provider.getDimension()) {
                ticket.generateInChunk(world, chunkX, chunkZ);
            }
            if (ticket.isFullyGenerated()) {
                it.remove();
            }
        }
    }

    public static int getTargetY(NoGenWorld world, BlockPos pos, boolean skipWater) {
        Block block;
        for (BlockPos groundPos = world.getGroundPos(pos); groundPos.getY() > 0; groundPos = groundPos.down()) {
            block = world.getBlockState(groundPos).getBlock();
            if (AWStructureStatics.skippableBlocksContains(block)) {
                continue;
            }
            if (skipWater && (block == Blocks.WATER || block == Blocks.FLOWING_WATER)) {
                continue;
            }
            return groundPos.getY();
        }
        return -1;
    }

    public static int getTargetY(World world, int x, int z, boolean skipWater) {
        Block block;
        for (int y = world.getActualHeight(); y > 0; y--) {
            block = world.getBlockState(new BlockPos(x, y, z)).getBlock();
            if (AWStructureStatics.skippableBlocksContains(block)) {
                continue;
            }
            if (skipWater && (block == Blocks.WATER || block == Blocks.FLOWING_WATER)) {
                continue;
            }
            return y;
        }
        return -1;
    }

    public static void sprinkleSnow(World world, StructureBB bb, int border) {
        sprinkleSnow(world, bb.min.add(- border, 0, -border), bb.max.add(border, 0, border));
    }

    public static void sprinkleSnow(World world, int minX, int minZ, int maxX, int maxZ) {
        BlockPos p1 = new BlockPos(minX, 0, minZ);
        BlockPos p2 = new BlockPos(maxX, 0, maxZ);
        sprinkleSnow(world, p1, p2);
    }

    public static void sprinkleSnow(World world, BlockPos min, BlockPos max) {
        for (int x = min.getX(); x <= max.getX(); x++) {
            for (int z = min.getZ(); z <= max.getZ(); z++) {
                int y = world.getPrecipitationHeight(new BlockPos(x, 1, z)).getY() - 1;
                BlockPos pos = new BlockPos(x, y, z);
                if(max.getY() >= y && y > 0 && world.canSnowAtBody(pos.up(), true)) {
                    IBlockState state = world.getBlockState(pos);
                    Block block = state.getBlock();
                    if (block != Blocks.AIR && state.getBlockFaceShape(world, pos, EnumFacing.UP) == BlockFaceShape.SOLID) {
                        world.setBlockState(pos.up(), Blocks.SNOW_LAYER.getDefaultState());
                    }
                }
            }
        }
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

    public final boolean attemptStructureGenerationAt(WorldServer world, BlockPos pos, EnumFacing face, StructureTemplate template, StructureMap map) {
        if (isLocationValidForTemplate(new NoGenWorld(world), pos, face, template, map)) {
            markStructureGenerated(world, pos, face, template, map);
            generateStructureAt(world, pos, face, template, map);
            return true;
        }

        return false;
    }

    private boolean isLocationValidForTemplate(NoGenWorld world, BlockPos pos, EnumFacing face, StructureTemplate template, StructureMap map) {
        long t1 = System.currentTimeMillis();
        int prevY = pos.getY();
        StructureBB bb = new StructureBB(pos, face, template.xSize, template.ySize, template.zSize, template.xOffset, template.yOffset, template.zOffset);
        int y = template.getValidationSettings().getAdjustedSpawnY(world,pos.getX(), pos.getY(), pos.getZ(), face, template, bb);
        pos = new BlockPos(pos.getX(), y, pos.getZ());
        bb.min = bb.min.up(y - prevY);
        bb.max = bb.max.up(y - prevY);
        int xs = bb.getXSize();
        int zs = bb.getZSize();
        int size = ((xs > zs ? xs : zs) / 16) + 3;
        if(map!=null) {
            Collection<StructureEntry> bbCheckList = map.getEntriesNear(world.getDimension(), pos.getX(), pos.getZ(), size, true, new ArrayList<>());
            for (StructureEntry entry : bbCheckList) {
                if (bb.crossWith(entry.getBB())) {
                    return false;
                }
            }
        }

        TownMap townMap = AWGameData.INSTANCE.getPerWorldData(world.getPerWorldStorage(), TownMap.class);
        if (townMap!=null && townMap.intersectsWithTown(bb)) {
            AWLog.logDebug("Skipping structure generation: " + template.name + " at: " + bb + " for intersection with existing town");
            return false;
        }
        if (!template.getValidationSettings().validatePlacement(world, pos.getX(), pos.getY(), pos.getZ(), face, template, bb)) {
            return false;
        }
        AWLog.logDebug("Validation took: " + (System.currentTimeMillis() - t1 + " ms"));
        return true;
    }

    private void markStructureGenerated(World world, BlockPos pos, EnumFacing face, StructureTemplate template, StructureMap map) {
        if(map!=null) {
            map.setGeneratedAt(world, pos.getX(), pos.getY(), pos.getZ(), face, new StructureEntry(pos.getX(), pos.getY(), pos.getZ(), face, template), template.getValidationSettings().isUnique());
        }
    }

    private void generateStructureAt(World world, BlockPos pos, EnumFacing face, StructureTemplate template, StructureMap map) {
        WorldGenTickHandler.INSTANCE.addStructureForGeneration(new StructureBuilderWorldGen(world, template, face, pos));
    }

    private static class StructureChunkGenTicket {
        int dimension;
        private Map<ChunkPos, StructureBuilderWorldGen> chunkParts = Maps.newHashMap();

        public StructureChunkGenTicket(World world, StructureTemplate template, EnumFacing face, BlockPos origin) {
            this.dimension = world.provider.getDimension();

            StructureBB bb = new StructureBB(origin, face, template);

            int turns = ((face.getHorizontalIndex() + 2) % 4);
            BlockPos corner1 = BlockTools.rotateInArea(new BlockPos(template.xSize - 1, 0, template.zSize - 1), template.xSize, template.zSize, turns).add(bb.min);
            BlockPos corner2 = BlockTools.rotateInArea(new BlockPos(0, 0, 0), template.xSize, template.zSize, turns).add(bb.min);

            BlockPos min = new BlockPos(Math.min(corner1.getX(), corner2.getX()), 0, Math.min(corner1.getZ(), corner2.getZ()));
            BlockPos max = new BlockPos(Math.max(corner1.getX(), corner2.getX()), 0, Math.max(corner1.getZ(), corner2.getZ()));

            int border = template.getValidationSettings().getBorderSize();
            int minX = min.getX() - border;
            int minZ = min.getZ() - border;
            int maxX = max.getX() + border;
            int maxZ = max.getZ() + border;

            for(int chunkX = minX >> 4; chunkX <= maxX >> 4; chunkX++) {
                for(int chunkZ = minZ >> 4; chunkZ <= maxZ >> 4; chunkZ++) {
                    int stMinX = Math.max(minX, chunkX << 4) - min.getX();
                    int stMinZ = Math.max(minZ, chunkZ << 4) - min.getZ();
                    int stMaxX = Math.min(maxX, (chunkX << 4) + 15) - min.getX();
                    int stMaxZ = Math.min(maxZ, (chunkZ << 4) + 15) - min.getZ();

                    int xSize = max.getX() - min.getX() + 1;
                    int zSize = max.getZ() - min.getZ() + 1;

                    BlockPos chunkCorner1 = BlockTools.rotateInAreaCW(new BlockPos(stMinX, 0, stMinZ), xSize, zSize, turns);
                    BlockPos chunkCorner2 = BlockTools.rotateInAreaCW(new BlockPos(stMaxX, 0, stMaxZ), xSize, zSize, turns);

                    chunkParts.put(new ChunkPos(chunkX, chunkZ), new StructureBuilderWorldGen(world, template, face, origin, bb,
                            Math.min(chunkCorner1.getX(), chunkCorner2.getX()), Math.min(chunkCorner1.getZ(), chunkCorner2.getZ()),
                            Math.max(chunkCorner1.getX(), chunkCorner2.getX()), Math.max(chunkCorner1.getZ(), chunkCorner2.getZ())));
                }
            }
        }

        public void generateInChunk(World world, int chunkX, int chunkZ) {
            long chunkId = ChunkPos.asLong(chunkX, chunkZ);
            Iterator<Map.Entry<ChunkPos, StructureBuilderWorldGen>> it = chunkParts.entrySet().iterator();
            while(it.hasNext()) {
                Map.Entry<ChunkPos, StructureBuilderWorldGen> chunkPart = it.next();
                ChunkPos chunkPos = chunkPart.getKey();
                if (ChunkPos.asLong(chunkPos.x, chunkPos.z) == chunkId) {
                    if (areNeededChunksGenerated(world, chunkX, chunkZ)) {
                        StructureBuilderWorldGen builder = chunkPart.getValue();
                        int pass = builder.getPass();

                        boolean generated = false;

                        while (!builder.isFinished() && (builder.getPass() < 0 || areNeighborsAtPass(chunkX, chunkZ, pass))) {
                            builder.incrementPass();
                            builder.constructCurrentPass();
                            generated = true;
                        }
                        if (builder.isFinished()) {
                            it.remove();
                        }
                        if (generated) {
                            generateInChunk(world, chunkX + 1, chunkZ);
                            generateInChunk(world, chunkX - 1, chunkZ);
                            generateInChunk(world, chunkX, chunkZ + 1);
                            generateInChunk(world, chunkX, chunkZ - 1);
                        }
                    }
                    break;
                }
            }
        }

        private boolean areNeighborsAtPass(int chunkX, int chunkZ, int pass) {
            return isChunkAtPassOrEmpty(new ChunkPos(chunkX + 1, chunkZ), pass)
                    && isChunkAtPassOrEmpty(new ChunkPos(chunkX - 1, chunkZ), pass)
                    && isChunkAtPassOrEmpty(new ChunkPos(chunkX, chunkZ + 1), pass)
                    && isChunkAtPassOrEmpty(new ChunkPos(chunkX, chunkZ - 1), pass);
        }

        private boolean isChunkAtPassOrEmpty(ChunkPos chunkPos, int pass) {
            return !chunkParts.containsKey(chunkPos) || chunkParts.get(chunkPos).getPass() >= pass;
        }

        private boolean areNeededChunksGenerated(World world, int chunkX, int chunkZ) {
            return world.isChunkGeneratedAt(chunkX, chunkZ)
                    && world.isChunkGeneratedAt(chunkX + 1, chunkZ)
                    && world.isChunkGeneratedAt(chunkX - 1, chunkZ)
                    && world.isChunkGeneratedAt(chunkX, chunkZ + 1)
                    && world.isChunkGeneratedAt(chunkX, chunkZ - 1)
                    && world.isChunkGeneratedAt(chunkX -1, chunkZ - 1)
                    && world.isChunkGeneratedAt(chunkX + 1, chunkZ - 1)
                    && world.isChunkGeneratedAt(chunkX - 1, chunkZ + 1)
                    && world.isChunkGeneratedAt(chunkX + 1, chunkZ + 1);
        }

        public boolean isFullyGenerated() {
            return chunkParts.isEmpty();
        }
    }
}
