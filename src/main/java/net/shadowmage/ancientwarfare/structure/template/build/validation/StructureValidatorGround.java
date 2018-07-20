package net.shadowmage.ancientwarfare.structure.template.build.validation;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.shadowmage.ancientwarfare.structure.AncientWarfareStructures;
import net.shadowmage.ancientwarfare.structure.config.AWStructureStatics;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;
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
			AncientWarfareStructures.log.info("Rejecting due to target block mismatch of: " + block.getRegistryName().toString() + " at: " + x + "," + y + "," + z);
			return false;
		}
		return true;
	}

	@Override
	public boolean validatePlacement(World world, int x, int y, int z, EnumFacing face, StructureTemplate template, StructureBB bb) {
		int minY = getMinY(template, bb);
		int maxY = getMaxY(template, bb);
		return validateBorderBlocks(world, template, bb, minY, maxY, false);
	}

	@Override
	public void preGeneration(World world, BlockPos pos, EnumFacing face, StructureTemplate template, StructureBB bb) {
		prePlacementBorder(world, template, bb);
		prePlacementUnderfill(world, template, bb);
	}

	@Override
	public void postGeneration(World world, BlockPos origin, StructureBB bb) {
		Biome biome = world.provider.getBiomeForCoords(origin);
		if (biome != null && biome.getEnableSnow()) {
			WorldStructureGenerator.sprinkleSnow(world, bb, getBorderSize());
		}
	}

	@Override
	protected void borderLeveling(World world, int x, int z, StructureTemplate template, StructureBB bb) {
		if (getMaxLeveling() <= 0) {
			return;
		}
		int topFilledY = WorldStructureGenerator.getTargetY(world, x, z, true);
		int topNonAirBlock = world.getTopSolidOrLiquidBlock(new BlockPos(x, 1, z)).getY();
		int step = WorldStructureGenerator.getStepNumber(x, z, bb.min.getX(), bb.max.getX(), bb.min.getZ(), bb.max.getZ());
		int startY = Math.min(bb.min.getY() + template.yOffset + step, topFilledY + 1);
		for (int y = startY; y <= topNonAirBlock; y++) {
			handleClearAction(world, new BlockPos(x, y, z), template, bb);
		}
		Biome biome = world.provider.getBiomeForCoords(new BlockPos(x, 1, z));
		IBlockState fillBlock = Blocks.GRASS.getDefaultState();
		if (biome != null && biome.topBlock != null) {
			fillBlock = biome.topBlock;
		}
		int y = bb.min.getY() + template.yOffset + step - 1;
		BlockPos pos = new BlockPos(x, y, z);
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		if (block != Blocks.FLOWING_WATER && block != Blocks.WATER && !AWStructureStatics.isSkippable(state)) {
			world.setBlockState(pos, fillBlock);
		}
	}
}
