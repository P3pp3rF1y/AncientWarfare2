package net.shadowmage.ancientwarfare.structure.template.build.validation;

import net.minecraft.init.Blocks;
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

public class StructureValidatorUnderwater extends StructureValidator {

	private int minWaterDepth;
	private int maxWaterDepth;

	public StructureValidatorUnderwater() {
		super(StructureValidationType.UNDERWATER);
	}

	@Override
	protected void readFromLines(List<String> lines) {
		for (String line : lines) {
			if (startLow(line, "minwaterdepth=")) {
				minWaterDepth = StringTools.safeParseInt("=", line);
			} else if (startLow(line, "maxwaterdepth=")) {
				maxWaterDepth = StringTools.safeParseInt("=", line);
			}
		}
	}

	@Override
	protected void write(BufferedWriter out) throws IOException {
		out.write("minWaterDepth=" + minWaterDepth);
		out.newLine();
		out.write("maxWaterDepth=" + maxWaterDepth);
		out.newLine();
	}

	@Override
	public boolean shouldIncludeForSelection(World world, int x, int y, int z, EnumFacing face, StructureTemplate template) {
		int water = 0;
		int startY = y;
		y = WorldStructureGenerator.getTargetY(world, x, z, true) + 1;
		water = startY - y;
		return !(water < minWaterDepth || water > maxWaterDepth);
	}

	@Override
	public int getAdjustedSpawnY(World world, int x, int y, int z, EnumFacing face, StructureTemplate template, StructureBB bb) {
		return WorldStructureGenerator.getTargetY(world, x, z, true) + 1;
	}

	@Override
	public boolean validatePlacement(World world, int x, int y, int z, EnumFacing face, StructureTemplate template, StructureBB bb) {
		int minY = getMinY(template, bb);
		int maxY = getMaxY(template, bb);
		return validateBorderBlocks(world, template, bb, minY, maxY, true);
	}

	@Override
	public void preGeneration(World world, BlockPos pos, EnumFacing face, StructureTemplate template, StructureBB bb) {
		prePlacementBorder(world, template, bb);
		prePlacementUnderfill(world, template, bb);
	}

	@Override
	public void handleClearAction(World world, BlockPos pos, StructureTemplate template, StructureBB bb) {
		world.setBlockState(pos, Blocks.WATER.getDefaultState());
	}

}
