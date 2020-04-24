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
import net.shadowmage.ancientwarfare.structure.template.build.validation.border.SmoothingMatrixBuilder;

import java.util.Optional;

public class TownGeneratorBorders {
	private static final int CLEAR_TREE_MAX_BORDER_DISTANCE = 10;
	public static final int MAX_BORDER_WIDTH = 20;

	private TownGeneratorBorders() {}

	public static void generateBorders(World world, StructureBB exterior) {
		BlockTools.getAllInBoxTopDown(exterior.min, exterior.max.add(0, 50, 0)).forEach(pos -> handleClearing(world, pos));

		new SmoothingMatrixBuilder(world, exterior, Math.min(Math.max(exterior.getXSize(), exterior.getZSize()) / 8, MAX_BORDER_WIDTH)).build()
				.apply(world, pos -> handleClearing(world, pos));
	}

	public static void levelTownArea(World world, StructureBB bb) {
		int minX = bb.min.getX();
		int minZ = bb.min.getZ();
		int maxX = bb.max.getX();
		int maxZ = bb.max.getZ();
		int desiredTopBlockHeight = bb.min.getY() - 1;
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
			ITree tree = treeScanner.get().scanTree(world, clearPos,
					clearPos.add(-CLEAR_TREE_MAX_BORDER_DISTANCE, 0, -CLEAR_TREE_MAX_BORDER_DISTANCE),
					clearPos.add(CLEAR_TREE_MAX_BORDER_DISTANCE, 0, CLEAR_TREE_MAX_BORDER_DISTANCE));
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
