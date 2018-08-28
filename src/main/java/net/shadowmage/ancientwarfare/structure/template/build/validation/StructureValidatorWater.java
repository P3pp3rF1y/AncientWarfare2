package net.shadowmage.ancientwarfare.structure.template.build.validation;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;

public class StructureValidatorWater extends StructureValidator {

	public StructureValidatorWater() {
		super(StructureValidationType.WATER);
	}

	@Override
	public boolean shouldIncludeForSelection(World world, int x, int y, int z, EnumFacing face, StructureTemplate template) {
		Block block = world.getBlockState(new BlockPos(x, y - 1, z)).getBlock();
		return block == Blocks.WATER || block == Blocks.FLOWING_WATER;
	}

	@Override
	public boolean validatePlacement(World world, int x, int y, int z, EnumFacing face, StructureTemplate template, StructureBB bb) {
		int minY = getMinY(template, bb);
		return validateBorderBlocks(world, bb, 0, minY, true);
	}

	@Override
	public void preGeneration(World world, BlockPos pos, EnumFacing face, StructureTemplate template, StructureBB bb) {
		//noop
	}

	@Override
	public void handleClearAction(World world, BlockPos pos, StructureTemplate template, StructureBB bb) {
		if (pos.getY() < bb.min.getY() + template.getOffset().getY()) {
			world.setBlockState(pos, Blocks.WATER.getDefaultState());
		} else {
			super.handleClearAction(world, pos, template, bb);
		}
	}
}
