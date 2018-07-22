package net.shadowmage.ancientwarfare.structure.template.build.validation;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.StringTools;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;
import net.shadowmage.ancientwarfare.structure.worldgen.WorldStructureGenerator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

public class StructureValidatorUnderground extends StructureValidator {

	private int minGenerationDepth;
	private int maxGenerationDepth;
	private int minOverfill;

	public StructureValidatorUnderground() {
		super(StructureValidationType.UNDERGROUND);
	}

	@Override
	protected void readFromLines(List<String> lines) {
		for (String line : lines) {
			if (startLow(line, "mingenerationdepth=")) {
				minGenerationDepth = StringTools.safeParseInt("=", line);
			} else if (startLow(line, "maxgenerationdepth=")) {
				maxGenerationDepth = StringTools.safeParseInt("=", line);
			} else if (startLow(line, "minoverfill=")) {
				minOverfill = StringTools.safeParseInt("=", line);
			}
		}
	}

	@Override
	protected void write(BufferedWriter out) throws IOException {
		out.write("minGenerationDepth=" + minGenerationDepth);
		out.newLine();
		out.write("maxGenerationDepth=" + maxGenerationDepth);
		out.newLine();
		out.write("minOverfill=" + minOverfill);
		out.newLine();
	}

	@Override
	public boolean shouldIncludeForSelection(World world, int x, int y, int z, EnumFacing face, StructureTemplate template) {
		y = WorldStructureGenerator.getTargetY(world, x, z, true);
		int tHeight = (template.ySize - template.yOffset);
		int low = minGenerationDepth + tHeight + minOverfill;
		return y > low;
	}

	@Override
	public int getAdjustedSpawnY(World world, int x, int y, int z, EnumFacing face, StructureTemplate template, StructureBB bb) {
		y = WorldStructureGenerator.getTargetY(world, x, z, true);
		int range = maxGenerationDepth - minGenerationDepth + 1;
		int tHeight = (template.ySize - template.yOffset);
		return y - minOverfill - world.rand.nextInt(range) - tHeight;
	}

	@Override
	public boolean validatePlacement(World world, int x, int y, int z, EnumFacing face, StructureTemplate template, StructureBB bb) {
		int minY = bb.min.getY() + template.yOffset + minOverfill;
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

	@Override
	public void preGeneration(World world, BlockPos pos, EnumFacing face, StructureTemplate template, StructureBB bb) {
		//noop
	}

}
