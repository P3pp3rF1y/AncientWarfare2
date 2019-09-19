package net.shadowmage.ancientwarfare.structure.template.build.validation;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.structure.AncientWarfareStructure;
import net.shadowmage.ancientwarfare.structure.config.AWStructureStatics;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;
import net.shadowmage.ancientwarfare.structure.template.build.validation.border.SmoothingMatrixBuilder;
import net.shadowmage.ancientwarfare.structure.worldgen.WorldStructureGenerator;

public class StructureValidatorGround extends StructureValidator {

	public StructureValidatorGround() {
		super(StructureValidationType.GROUND);
	}

	@Override
	public boolean shouldIncludeForSelection(World world, int x, int y, int z, EnumFacing face, StructureTemplate template) {
		IBlockState state = world.getBlockState(new BlockPos(x, y - 1, z));
		Block block = state.getBlock();
		if (!AWStructureStatics.isValidTargetBlock(state)) {
			//noinspection ConstantConditions
			AncientWarfareStructure.LOG.debug("Rejecting due to target block mismatch of: {} at: {},{},{}", () -> block.getRegistryName().toString(), () -> x, () -> y, () -> z);
			return false;
		}
		return true;
	}

	@Override
	public boolean validatePlacement(World world, int x, int y, int z, EnumFacing face, StructureTemplate template, StructureBB bb) {
		if (y - template.offset.getY() <= 0) {
			AncientWarfareStructure.LOG.debug("Ground isn't deep enough for the structure- required: {}, found: {}", Math.abs(bb.min.getY()), y);
			return false;
		}
		int minY = getMinY(template, bb);
		int maxY = getMaxY(template, bb);
		return validateBorderBlocks(world, bb, minY, maxY, false);
	}

	@Override
	public void preGeneration(World world, BlockPos pos, EnumFacing face, StructureTemplate template, StructureBB bb) {
		if (!isPreserveBlocks()) {
			clearBB(world, template, bb);
			prePlacementUnderfill(world, bb);
		}
	}

	private void smoothoutBorder(World world, StructureBB bb, StructureTemplate template) {
		int borderSize = getBorderSize();
		if (borderSize > 0) {
			new SmoothingMatrixBuilder(world, bb, borderSize, bb.min.getY() + template.getOffset().getY() - 1).build()
					.apply(world, pos -> handleClearAction(world, pos, template, bb));
		}
	}

	private void clearBB(World world, StructureTemplate template, StructureBB bb) {
		BlockTools.getAllInBoxTopDown(bb.min, bb.max.add(0, 10 + getMaxLeveling(), 0)).forEach(pos -> handleClearAction(world, pos, template, bb));
	}

	@Override
	public void postGeneration(World world, BlockPos origin, StructureBB bb, StructureTemplate template) {
		if (!isPreserveBlocks()) {
			smoothoutBorder(world, bb, template);
		}
		if (world.canSnowAt(origin.up(), false)) {
			WorldStructureGenerator.sprinkleSnow(world, bb, getBorderSize());
		}
	}
}
