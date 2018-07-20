package net.shadowmage.ancientwarfare.structure.town;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.shadowmage.ancientwarfare.automation.registry.TreeFarmRegistry;
import net.shadowmage.ancientwarfare.automation.tile.worksite.treefarm.ITree;
import net.shadowmage.ancientwarfare.automation.tile.worksite.treefarm.ITreeScanner;
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

		int eminx = exterior.min.getX();
		int eminz = exterior.min.getZ();
		int emaxx = exterior.max.getX();
		int emaxz = exterior.max.getZ();

		minX = max.min.getX();
		maxX = walls.min.getX() - 1;
		for (int px = minX; px <= maxX; px++) {
			for (int pz = max.min.getZ(); pz <= max.max.getZ(); pz++) {
				step = WorldStructureGenerator.getStepNumber(px, pz, eminx, emaxx, eminz, emaxz);
				handleBorderBlock(world, px, pz, fillBase - step, fillBase + step, getFillBlock(world, px, pz, false, Blocks.DIRT.getDefaultState()), getFillBlock(world, px, pz, true, Blocks.GRASS.getDefaultState()));
			}
		}

		minX = walls.max.getX() + 1;
		maxX = max.max.getX();
		for (int px = minX; px <= maxX; px++) {
			for (int pz = max.min.getZ(); pz <= max.max.getZ(); pz++) {
				step = WorldStructureGenerator.getStepNumber(px, pz, eminx, emaxx, eminz, emaxz);
				handleBorderBlock(world, px, pz, fillBase - step, fillBase + step, getFillBlock(world, px, pz, false, Blocks.DIRT.getDefaultState()), getFillBlock(world, px, pz, true, Blocks.GRASS.getDefaultState()));
			}
		}

		minZ = max.min.getZ();
		maxZ = walls.min.getZ() - 1;
		for (int pz = minZ; pz <= maxZ; pz++) {
			for (int px = max.min.getX(); px <= max.max.getX(); px++) {
				step = WorldStructureGenerator.getStepNumber(px, pz, eminx, emaxx, eminz, emaxz);
				handleBorderBlock(world, px, pz, fillBase - step, fillBase + step, getFillBlock(world, px, pz, false, Blocks.DIRT.getDefaultState()), getFillBlock(world, px, pz, true, Blocks.GRASS.getDefaultState()));
			}
		}

		minZ = walls.max.getZ() + 1;
		maxZ = max.max.getZ();
		for (int pz = minZ; pz <= maxZ; pz++) {
			for (int px = max.min.getX(); px <= max.max.getX(); px++) {
				step = WorldStructureGenerator.getStepNumber(px, pz, eminx, emaxx, eminz, emaxz);
				handleBorderBlock(world, px, pz, fillBase - step, fillBase + step, getFillBlock(world, px, pz, false, Blocks.DIRT.getDefaultState()), getFillBlock(world, px, pz, true, Blocks.GRASS.getDefaultState()));
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
				handleBorderBlock(world, x, z, desiredTopBlockHeight, desiredTopBlockHeight, getFillBlock(world, x, z, false, Blocks.GRASS.getDefaultState()), getFillBlock(world, x, z, true, Blocks.GRASS.getDefaultState()));
				world.setBlockState(new BlockPos(x, desiredTopBlockHeight - 5, z), Blocks.COBBLESTONE.getDefaultState());
			}
		}
	}

	private static void handleBorderBlock(World world, int x, int z, int fillLevel, int cutLevel, IBlockState fillBlock, IBlockState topBlock) {
		int y = getTopFilledHeight(world.getChunkFromBlockCoords(new BlockPos(x, 1, z)), x, z, false);
		int topSolidY = getTopFilledHeight(world.getChunkFromBlockCoords(new BlockPos(x, 1, z)), x, z, true);
		if (y >= cutLevel) {
			for (int py = Math.min(topSolidY, cutLevel) + 1; py <= y; py++) {
				BlockPos clearPos = new BlockPos(x, py, z);
				handleClearing(world, clearPos);
			}
			if (topSolidY > cutLevel) {
				world.setBlockState(new BlockPos(x, cutLevel, z), topBlock);
			}
		}
		if (topSolidY <= fillLevel) {
			for (int py = topSolidY + 1; py < fillLevel; py++) {
				world.setBlockState(new BlockPos(x, py, z), fillBlock);
			}
			world.setBlockState(new BlockPos(x, fillLevel, z), topBlock);
		}
	}

	private static void handleClearing(World world, BlockPos clearPos) {
		IBlockState state = world.getBlockState(clearPos);
		if (state.getMaterial() != Material.AIR) {
			ITreeScanner treeScanner = TreeFarmRegistry.getTreeScanner(state);
			if (!treeScanner.matches(state)) {
				world.setBlockToAir(clearPos);
				return;
			}
			ITree tree = treeScanner.scanTree(world, clearPos);
			tree.getLeafPositions().forEach(world::setBlockToAir);
			tree.getTrunkPositions().forEach(world::setBlockToAir);
		}
	}

	private static int getTopFilledHeight(Chunk chunk, int x, int z, boolean skippables) {
		int maxY = chunk.getTopFilledSegment() + 16;
		Block block;
		for (int y = maxY; y > 0; y--) {
			IBlockState state = chunk.getBlockState(new BlockPos(x, y, z));
			block = state.getBlock();
			if (block == Blocks.AIR || (skippables && AWStructureStatics.isSkippable(state))) {
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
