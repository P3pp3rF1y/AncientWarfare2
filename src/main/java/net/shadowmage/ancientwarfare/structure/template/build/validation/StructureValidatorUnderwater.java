package net.shadowmage.ancientwarfare.structure.template.build.validation;

import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;
import net.shadowmage.ancientwarfare.structure.worldgen.WorldStructureGenerator;

import static net.shadowmage.ancientwarfare.structure.template.build.validation.properties.StructureValidationProperties.MAX_WATER_DEPTH;
import static net.shadowmage.ancientwarfare.structure.template.build.validation.properties.StructureValidationProperties.MIN_WATER_DEPTH;

public class StructureValidatorUnderwater extends StructureValidator {

	public StructureValidatorUnderwater() {
		super(StructureValidationType.UNDERWATER);
	}

	@Override
	public boolean shouldIncludeForSelection(World world, int x, int y, int z, EnumFacing face, StructureTemplate template) {
		int startY = y;
		y = WorldStructureGenerator.getTargetY(world, x, z, true) + 1;
		int water = startY - y;
		return !(water < getPropertyValue(MIN_WATER_DEPTH) || water > getPropertyValue(MAX_WATER_DEPTH));
	}

	@Override
	public int getAdjustedSpawnY(World world, int x, int y, int z, EnumFacing face, StructureTemplate template, StructureBB bb) {
		return WorldStructureGenerator.getTargetY(world, x, z, true) + 1;
	}

	@Override
	public boolean validatePlacement(World world, int x, int y, int z, EnumFacing face, StructureTemplate template, StructureBB bb) {
		int minY = getMinY(template, bb);
		int maxY = getMaxY(template, bb);
		return validateBorderBlocks(world, bb, minY, maxY, true);
	}

	@Override
	public void preGeneration(World world, BlockPos pos, EnumFacing face, StructureTemplate template, StructureBB bb) {
		prePlacementBorder(world, template, bb);
		prePlacementUnderfill(world, bb);
	}

	@Override
	public void handleClearAction(World world, BlockPos pos, StructureTemplate template, StructureBB bb) {
		world.setBlockState(pos, Blocks.WATER.getDefaultState());
	}

}
