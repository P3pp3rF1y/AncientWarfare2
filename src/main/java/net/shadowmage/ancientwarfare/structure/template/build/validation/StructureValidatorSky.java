package net.shadowmage.ancientwarfare.structure.template.build.validation;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;

import static net.shadowmage.ancientwarfare.structure.template.build.validation.properties.StructureValidationProperties.*;

public class StructureValidatorSky extends StructureValidator {
	public StructureValidatorSky() {
		super(StructureValidationType.SKY);
	}

	@Override
	protected void setDefaultSettings(StructureTemplate template) {
		//noop
	}

	@Override
	public boolean shouldIncludeForSelection(World world, int x, int y, int z, EnumFacing face, StructureTemplate template) {
		int remainingHeight = world.getActualHeight() - getMinFlyingHeight() - (template.getSize().getY() - template.getOffset().getY());
		return y < remainingHeight;
	}

	@Override
	public int getAdjustedSpawnY(World world, int x, int y, int z, EnumFacing face, StructureTemplate template, StructureBB bb) {
		int range = getMaxGenerationHeight() - getMinGenerationHeight() + 1;
		return y + getMinFlyingHeight() + world.rand.nextInt(range);
	}

	@Override
	public boolean validatePlacement(World world, int x, int y, int z, EnumFacing face, StructureTemplate template, StructureBB bb) {
		int maxY = getMinGenerationHeight() - getMinFlyingHeight();
		return validateBorderBlocks(world, bb, 0, maxY, false);
	}

	@Override
	public void preGeneration(World world, BlockPos pos, EnumFacing face, StructureTemplate template, StructureBB bb) {
		//noop
	}

	private int getMinFlyingHeight() {
		return getPropertyValue(MIN_FLYING_HEIGHT);
	}

	private int getMaxGenerationHeight() {
		return getPropertyValue(MAX_GENERATION_HEIGHT);
	}

	private int getMinGenerationHeight() {
		return getPropertyValue(MIN_GENERATION_HEIGHT);
	}
}
