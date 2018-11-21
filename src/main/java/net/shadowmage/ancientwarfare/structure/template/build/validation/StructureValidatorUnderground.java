package net.shadowmage.ancientwarfare.structure.template.build.validation;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;
import net.shadowmage.ancientwarfare.structure.worldgen.WorldStructureGenerator;

import static net.shadowmage.ancientwarfare.structure.template.build.validation.properties.StructureValidationProperties.*;

public class StructureValidatorUnderground extends StructureValidator {
	public StructureValidatorUnderground() {
		super(StructureValidationType.UNDERGROUND);
	}

	@Override
	public boolean shouldIncludeForSelection(World world, int x, int y, int z, EnumFacing face, StructureTemplate template) {
		int tHeight = (template.getSize().getY() - template.getOffset().getY());
		int low = getMinGenerationDepth() + tHeight + getMinOverfill();
		return WorldStructureGenerator.getTargetY(world, x, z, true) > low;
	}

	@Override
	public int getAdjustedSpawnY(World world, int x, int y, int z, EnumFacing face, StructureTemplate template, StructureBB bb) {
		int range = getMaxGenerationDepth() - getMinGenerationDepth() + 1;
		int tHeight = (template.getSize().getY() - template.getOffset().getY());
		return WorldStructureGenerator.getTargetY(world, x, z, true) - getMinOverfill() - world.rand.nextInt(range) - tHeight;
	}

	private int getMaxGenerationDepth() {
		return getPropertyValue(MAX_GENERATION_DEPTH);
	}

	private int getMinGenerationDepth() {
		return getPropertyValue(MIN_GENERATION_DEPTH);
	}

	@Override
	public boolean validatePlacement(World world, int x, int y, int z, EnumFacing face, StructureTemplate template, StructureBB bb) {
		int minY = bb.min.getY() + template.getOffset().getY() + getMinOverfill();
		int topBlockY;
		for (int bx = bb.min.getX(); bx <= bb.max.getX(); bx++) {
			for (int bz = bb.min.getZ(); bz <= bb.max.getZ(); bz++) {
				topBlockY = WorldStructureGenerator.getTargetY(world, bx, bz, true);
				if (topBlockY <= minY) {
					return false;
				}
			}
		}
		return true;
	}

	private int getMinOverfill() {
		return getPropertyValue(MIN_OVERFILL);
	}

	@Override
	public void preGeneration(World world, BlockPos pos, EnumFacing face, StructureTemplate template, StructureBB bb) {
		//noop
	}

}
