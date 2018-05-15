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

import net.minecraft.block.Block;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;
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
		if (rng.nextFloat() < AWStructureStatics.randomGenerationChance)
			WorldGenTickHandler.INSTANCE.addChunkForGeneration(world, chunkX, chunkZ);
	}

	public void generateAt(int chunkX, int chunkZ, World world) {
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
		world.profiler.startSection("AWTemplateGeneration");
		if (attemptStructureGenerationAt(world, new BlockPos(x, y, z), face, template, map)) {
			AWLog.log(String.format("Generated structure: %s at %s, %s, %s, time: %sms", template.name, x, y, z, (System.currentTimeMillis() - t1)));
		}
		world.profiler.endSection();
	}

	public static int getTargetY(World world, int x, int z, boolean skipWater) {
		Block block;
		for (int y = world.getActualHeight(); y > 0; y--) {
			IBlockState state = world.getBlockState(new BlockPos(x, y, z));
			block = state.getBlock();
			if (AWStructureStatics.isSkippable(state)) {
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
		BlockPos p1 = bb.min.add(-border, 0, -border);
		BlockPos p2 = bb.max.add(border, 0, border);
		for (int x = p1.getX(); x <= p2.getX(); x++) {
			for (int z = p1.getZ(); z <= p2.getZ(); z++) {
				int y = world.getPrecipitationHeight(new BlockPos(x, 1, z)).getY() - 1;
				BlockPos pos = new BlockPos(x, y, z);
				if (p2.getY() >= y && y > 0 && world.canSnowAtBody(pos.up(), true)) {
					IBlockState state = world.getBlockState(pos);
					Block block = state.getBlock();
					if (block != Blocks.AIR && state.getBlockFaceShape(world, pos, EnumFacing.UP) == BlockFaceShape.SOLID) {
						world.setBlockState(pos.up(), Blocks.SNOW_LAYER.getDefaultState());
					}
				}
			}
		}
	}

	private static final int CLEARANCE_HEIGHT = 40;

	public static void clearAbove(World world, StructureBB bb, int border) {
		BlockPos minCorner = new BlockPos(bb.min.getX() - border, bb.max.getY() + 1, bb.min.getZ() - border);
		BlockPos maxCorner = new BlockPos(bb.max.getX() + border, bb.max.getY() + 1 + CLEARANCE_HEIGHT, bb.max.getZ() + border);
		for (BlockPos pos : BlockPos.getAllInBox(minCorner, maxCorner)) {
			world.setBlockToAir(pos);
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

	public final boolean attemptStructureGenerationAt(World world, BlockPos pos, EnumFacing face, StructureTemplate template, StructureMap map) {
		long t1 = System.currentTimeMillis();
		int prevY = pos.getY();
		StructureBB bb = new StructureBB(pos, face, template.xSize, template.ySize, template.zSize, template.xOffset, template.yOffset, template.zOffset);
		int y = template.getValidationSettings().getAdjustedSpawnY(world, pos.getX(), pos.getY(), pos.getZ(), face, template, bb);
		pos = new BlockPos(pos.getX(), y, pos.getZ());
		bb.min = bb.min.up(y - prevY);
		bb.max = bb.max.up(y - prevY);
		int xs = bb.getXSize();
		int zs = bb.getZSize();
		int size = ((xs > zs ? xs : zs) / 16) + 3;
		if (map != null) {
			Collection<StructureEntry> bbCheckList = map.getEntriesNear(world, pos.getX(), pos.getZ(), size, true, new ArrayList<>());
			for (StructureEntry entry : bbCheckList) {
				if (bb.crossWith(entry.getBB())) {
					return false;
				}
			}
		}

		TownMap townMap = AWGameData.INSTANCE.getPerWorldData(world, TownMap.class);
		if (townMap != null && townMap.intersectsWithTown(bb)) {
			AWLog.logDebug("Skipping structure generation: " + template.name + " at: " + bb + " for intersection with existing town");
			return false;
		}
		if (template.getValidationSettings().validatePlacement(world, pos.getX(), pos.getY(), pos.getZ(), face, template, bb)) {
			AWLog.logDebug("Validation took: " + (System.currentTimeMillis() - t1 + " ms"));
			generateStructureAt(world, pos, face, template, map);
			return true;
		}
		return false;
	}

	private void generateStructureAt(World world, BlockPos pos, EnumFacing face, StructureTemplate template, StructureMap map) {
		if (map != null) {
			map.setGeneratedAt(world, pos.getX(), pos.getY(), pos.getZ(), face, new StructureEntry(pos.getX(), pos.getY(), pos.getZ(), face, template), template.getValidationSettings().isUnique());
		}
		WorldGenTickHandler.INSTANCE.addStructureForGeneration(new StructureBuilderWorldGen(world, template, face, pos));
	}

}
