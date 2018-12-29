package net.shadowmage.ancientwarfare.structure.template.build.validation;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.structure.config.AWStructureStatics;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;
import net.shadowmage.ancientwarfare.structure.worldgen.WorldStructureGenerator;

import java.util.Set;

public class StructureValidatorHarbor extends StructureValidator {
	//TODO get rid of the weird way these get values assigned through seemingly unrelated calls
	private BlockPos testMin = BlockPos.ORIGIN;
	private BlockPos testMax = BlockPos.ORIGIN;

	private Set<Block> validTargetBlocks = ImmutableSet.of(
			Blocks.DIRT,
			Blocks.GRASS,
			Blocks.STONE,
			Blocks.SAND,
			Blocks.GRAVEL,
			Blocks.SANDSTONE,
			Blocks.CLAY,
			Blocks.IRON_ORE,
			Blocks.COAL_ORE
	);

	public StructureValidatorHarbor() {
		super(StructureValidationType.HARBOR);
	}

	@Override
	protected void setDefaultSettings(StructureTemplate template) {
		//noop
	}

	@Override
	public boolean shouldIncludeForSelection(World world, int x, int y, int z, EnumFacing face, StructureTemplate template) {
		/*
		 * testing that front target position is valid block
         * then test back target position to ensure that it has water at same level
         * or at an acceptable level difference
         */
		Block block = world.getBlockState(new BlockPos(x, y - 1, z)).getBlock();
		if (validTargetBlocks.contains(block)) {
			testMin = new BlockPos(x, y, z).offset(face, template.getOffset().getZ());
			int by = WorldStructureGenerator.getTargetY(world, testMin.getX(), testMin.getZ(), false);
			if (y - by > getMaxFill()) {
				return false;
			}
			block = world.getBlockState(new BlockPos(testMin.getX(), by, testMin.getZ())).getBlock();
			if (block == Blocks.WATER || block == Blocks.FLOWING_WATER) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int getAdjustedSpawnY(World world, int x, int y, int z, EnumFacing face, StructureTemplate template, StructureBB bb) {
		testMin = new BlockPos(x, y, z).offset(face, template.getOffset().getZ());
		return WorldStructureGenerator.getTargetY(world, testMin.getX(), testMin.getZ(), false) + 1;
	}

	@Override
	public boolean validatePlacement(World world, int x, int y, int z, EnumFacing face, StructureTemplate template, StructureBB bb) {
		int minY = getMinY(template, bb);
		int maxY = getMaxY(template, bb);
		StructureBB temp = bb.getFrontCorners(face, testMin, testMax);
		testMin = temp.min;
		testMax = temp.max;
		for (int bx = testMin.getX(); bx <= testMax.getX(); bx++) {
			for (int bz = testMin.getZ(); bz <= testMax.getZ(); bz++) {
				if (!validateBlockHeightTypeAndBiome(world, bx, bz, minY, maxY, false)) {
					return false;
				}
			}
		}

		temp = bb.getRearCorners(face, testMin, testMax);
		testMin = temp.min;
		testMax = temp.max;
		for (int bx = testMin.getX(); bx <= testMax.getX(); bx++) {
			for (int bz = testMin.getZ(); bz <= testMax.getZ(); bz++) {
				if (!validateBlockHeightAndType(world, bx, bz, minY, maxY, false, state -> state.getMaterial() == Material.WATER)) {
					return false;
				}
			}
		}

		temp = bb.getRightCorners(face, testMin, testMax);
		testMin = temp.min;
		testMax = temp.max;
		for (int bx = testMin.getX(); bx <= testMax.getX(); bx++) {
			for (int bz = testMin.getZ(); bz <= testMax.getZ(); bz++) {
				if (!validateBlockHeightAndType(world, bx, bz, minY, maxY, false,
						state -> AWStructureStatics.isValidTargetBlock(state) || state.getMaterial() == Material.WATER)) {
					return false;
				}
			}
		}

		temp = bb.getLeftCorners(face, testMin, testMax);
		testMin = temp.min;
		testMax = temp.max;
		for (int bx = testMin.getX(); bx <= testMax.getX(); bx++) {
			for (int bz = testMin.getZ(); bz <= testMax.getZ(); bz++) {
				if (!validateBlockHeightAndType(world, bx, bz, minY, maxY, false,
						state -> AWStructureStatics.isValidTargetBlock(state) || state.getMaterial() == Material.WATER)) {
					return false;
				}
			}
		}

		return true;
	}

	@Override
	public void preGeneration(World world, BlockPos pos, EnumFacing face, StructureTemplate template, StructureBB bb) {
		prePlacementBorder(world, template, bb);
	}

	@Override
	public void handleClearAction(World world, BlockPos pos, StructureTemplate template, StructureBB bb) {
		if (pos.getY() >= bb.min.getY() + template.getOffset().getY()) {
			super.handleClearAction(world, pos, template, bb);
		} else {
			world.setBlockState(pos, Blocks.WATER.getDefaultState());
		}
	}

}
