package net.shadowmage.ancientwarfare.structure.town;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.shadowmage.ancientwarfare.automation.registry.TreeFarmRegistry;
import net.shadowmage.ancientwarfare.automation.tile.worksite.treefarm.ITree;
import net.shadowmage.ancientwarfare.automation.tile.worksite.treefarm.ITreeScanner;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;
import net.shadowmage.ancientwarfare.structure.worldgen.WorldStructureGenerator;

import java.util.Optional;

public class TownGeneratorBorders {
	private static final int CLEAR_TREE_MAX_BORDER_DISTANCE = 10;

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
				handleBorderBlock(world, px, pz, fillBase - step, fillBase + step, getFillBlock(world, px, pz, false), getFillBlock(world, px, pz, true));
			}
		}

		minX = walls.max.getX() + 1;
		maxX = max.max.getX();
		for (int px = minX; px <= maxX; px++) {
			for (int pz = max.min.getZ(); pz <= max.max.getZ(); pz++) {
				step = WorldStructureGenerator.getStepNumber(px, pz, eminx, emaxx, eminz, emaxz);
				handleBorderBlock(world, px, pz, fillBase - step, fillBase + step, getFillBlock(world, px, pz, false), getFillBlock(world, px, pz, true));
			}
		}

		minZ = max.min.getZ();
		maxZ = walls.min.getZ() - 1;
		for (int pz = minZ; pz <= maxZ; pz++) {
			for (int px = max.min.getX(); px <= max.max.getX(); px++) {
				step = WorldStructureGenerator.getStepNumber(px, pz, eminx, emaxx, eminz, emaxz);
				handleBorderBlock(world, px, pz, fillBase - step, fillBase + step, getFillBlock(world, px, pz, false), getFillBlock(world, px, pz, true));
			}
		}

		minZ = walls.max.getZ() + 1;
		maxZ = max.max.getZ();
		for (int pz = minZ; pz <= maxZ; pz++) {
			for (int px = max.min.getX(); px <= max.max.getX(); px++) {
				step = WorldStructureGenerator.getStepNumber(px, pz, eminx, emaxx, eminz, emaxz);
				handleBorderBlock(world, px, pz, fillBase - step, fillBase + step, getFillBlock(world, px, pz, false), getFillBlock(world, px, pz, true));
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
				handleBorderBlock(world, x, z, desiredTopBlockHeight, desiredTopBlockHeight, getFillBlock(world, x, z, false), getFillBlock(world, x, z, true));
				world.setBlockState(new BlockPos(x, desiredTopBlockHeight - 5, z), Blocks.COBBLESTONE.getDefaultState());
			}
		}
	}

	private static void handleBorderBlock(World world, int x, int z, int fillLevel, int cutLevel, IBlockState fillBlock, IBlockState topBlock) {
		int y = BlockTools.getTopFilledHeight(world.getChunkFromBlockCoords(new BlockPos(x, 1, z)), x, z, false);
		int topSolidY = BlockTools.getTopFilledHeight(world.getChunkFromBlockCoords(new BlockPos(x, 1, z)), x, z, true);
		if (y >= cutLevel) {
			for (int py = y; py > Math.min(topSolidY, cutLevel); py--) {
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
			Optional<ITreeScanner> treeScanner = TreeFarmRegistry.getRegisteredTreeScanner(state);
			if (!treeScanner.isPresent()) {
				world.setBlockToAir(clearPos);
				return;
			}
			ITree tree = treeScanner.get().scanTree(world, clearPos, CLEAR_TREE_MAX_BORDER_DISTANCE);
			tree.getLeafPositions().forEach(world::setBlockToAir);
			tree.getTrunkPositions().forEach(world::setBlockToAir);
		}
	}

	private static IBlockState getFillBlock(World world, int x, int z, boolean surface) {
		Biome biome = world.provider.getBiomeForCoords(new BlockPos(x, 1, z));
		if (surface) {
			return biome.topBlock;
		} else {
			return biome.fillerBlock;
		}
	}
}
