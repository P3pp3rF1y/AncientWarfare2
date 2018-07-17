package net.shadowmage.ancientwarfare.structure.town;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.shadowmage.ancientwarfare.structure.config.AWStructureStatics;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;
import net.shadowmage.ancientwarfare.structure.worldgen.WorldStructureGenerator;

public class TownGeneratorBorders {
	private TownGeneratorBorders() {}

	public static void generateBorders(World world, StructureBB exterior, StructureBB walls, StructureBB max) {
		int minX;
		int maxX;
		int minZ;
		int maxZ;
		int step;
		int fillBase = max.min.getY() - 1;
		int levelBase = fillBase;

		int eminx = exterior.min.getX();
		int eminz = exterior.min.getZ();
		int emaxx = exterior.max.getX();
		int emaxz = exterior.max.getZ();

		minX = max.min.getX();
		maxX = walls.min.getX() - 1;
		for (int px = minX; px <= maxX; px++) {
			for (int pz = max.min.getZ(); pz <= max.max.getZ(); pz++) {
				step = WorldStructureGenerator.getStepNumber(px, pz, eminx, emaxx, eminz, emaxz);
				handleBorderBlock(world, px, pz, fillBase - step, levelBase + step, getFillBlock(world, px, pz, false, Blocks.DIRT.getDefaultState()), getFillBlock(world, px, pz, true, Blocks.GRASS.getDefaultState()), true);
			}
		}

		minX = walls.max.getX() + 1;
		maxX = max.max.getX();
		for (int px = minX; px <= maxX; px++) {
			for (int pz = max.min.getZ(); pz <= max.max.getZ(); pz++) {
				step = WorldStructureGenerator.getStepNumber(px, pz, eminx, emaxx, eminz, emaxz);
				handleBorderBlock(world, px, pz, fillBase - step, levelBase + step, getFillBlock(world, px, pz, false, Blocks.DIRT.getDefaultState()), getFillBlock(world, px, pz, true, Blocks.GRASS.getDefaultState()), true);
			}
		}

		minZ = max.min.getZ();
		maxZ = walls.min.getZ() - 1;
		for (int pz = minZ; pz <= maxZ; pz++) {
			for (int px = max.min.getX(); px <= max.max.getX(); px++) {
				step = WorldStructureGenerator.getStepNumber(px, pz, eminx, emaxx, eminz, emaxz);
				handleBorderBlock(world, px, pz, fillBase - step, levelBase + step, getFillBlock(world, px, pz, false, Blocks.DIRT.getDefaultState()), getFillBlock(world, px, pz, true, Blocks.GRASS.getDefaultState()), true);
			}
		}

		minZ = walls.max.getZ() + 1;
		maxZ = max.max.getZ();
		for (int pz = minZ; pz <= maxZ; pz++) {
			for (int px = max.min.getX(); px <= max.max.getX(); px++) {
				step = WorldStructureGenerator.getStepNumber(px, pz, eminx, emaxx, eminz, emaxz);
				handleBorderBlock(world, px, pz, fillBase - step, levelBase + step, getFillBlock(world, px, pz, false, Blocks.DIRT.getDefaultState()), getFillBlock(world, px, pz, true, Blocks.GRASS.getDefaultState()), true);
			}
		}
	}

	public static void levelTownArea(World world, StructureBB walls) {
		int minX = walls.min.getX();
		int minZ = walls.min.getZ();
		int maxX = walls.max.getX();
		int maxZ = walls.max.getZ();
		int desiredTopBlockHeight = walls.min.getY() - 1;
		for (int x = minX; x <= maxX; x++) {
			for (int z = minZ; z <= maxZ; z++) {
				handleBorderBlock(world, x, z, desiredTopBlockHeight, desiredTopBlockHeight, getFillBlock(world, x, z, false, Blocks.GRASS.getDefaultState()), getFillBlock(world, x, z, true, Blocks.GRASS.getDefaultState()), false);
				world.setBlockState(new BlockPos(x, desiredTopBlockHeight - 5, z), Blocks.COBBLESTONE.getDefaultState());
			}
		}
	}

	private static void handleBorderBlock(World world, int x, int z, int fillLevel, int cutLevel, IBlockState fillBlock, IBlockState topBlock, boolean skippables) {
		int y = getTopFilledHeight(world.getChunkFromBlockCoords(new BlockPos(x, 1, z)), x, z, skippables);
		if (y >= cutLevel) {
			for (int py = world.getActualHeight(); py > cutLevel; py--) {
				world.setBlockToAir(new BlockPos(x, py, z));
			}
			world.setBlockState(new BlockPos(x, cutLevel, z), topBlock);
		}
		if (y <= fillLevel) {
			for (int py = y + 1; py < fillLevel; py++) {
				world.setBlockState(new BlockPos(x, py, z), fillBlock);
			}
			world.setBlockState(new BlockPos(x, fillLevel, z), topBlock);
		}
	}

	private static int getTopFilledHeight(Chunk chunk, int x, int z, boolean skippables) {
		int maxY = chunk.getTopFilledSegment() + 16;
		Block block;
		for (int y = maxY; y > 0; y--) {
			IBlockState state = chunk.getBlockState(new BlockPos(x, y, z));
			block = state.getBlock();
			if (block == null || block == Blocks.AIR || (skippables && AWStructureStatics.isSkippable(state))) {
				continue;
			}
			return y;
		}
		return -1;
	}

	private static IBlockState getFillBlock(World world, int x, int z, boolean surface, IBlockState defaultBlock) {
		Biome biome = world.provider.getBiomeForCoords(new BlockPos(x, 1, z));
		if (biome != null) {
			if (surface && biome.topBlock != null) {
				return biome.topBlock;
			} else if (!surface && biome.fillerBlock != null) {
				return biome.fillerBlock;
			}
		}
		return defaultBlock;
	}

}
