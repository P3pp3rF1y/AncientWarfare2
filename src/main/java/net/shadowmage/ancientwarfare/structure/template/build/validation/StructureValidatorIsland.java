package net.shadowmage.ancientwarfare.structure.template.build.validation;

import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.StringTools;
import net.shadowmage.ancientwarfare.structure.config.AWStructureStatics;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;
import net.shadowmage.ancientwarfare.structure.worldgen.WorldStructureGenerator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

public class StructureValidatorIsland extends StructureValidator {

	private int minWaterDepth;
	private int maxWaterDepth;

	public StructureValidatorIsland() {
		super(StructureValidationType.ISLAND);
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
		out.write("minWaterDepth=" + maxWaterDepth);
		out.newLine();
	}

	@Override
	protected void setDefaultSettings(StructureTemplate template) {
		this.minWaterDepth = template.yOffset / 2;
		this.maxWaterDepth = template.yOffset;
	}

	@Override
	public boolean shouldIncludeForSelection(World world, int x, int y, int z, EnumFacing face, StructureTemplate template) {
		int startY = y - 1;
		y = WorldStructureGenerator.getTargetY(world, x, z, true) + 1;
		int water = startY - y + 1;
		return !(water < minWaterDepth || water > maxWaterDepth);
	}

	@Override
	public boolean validatePlacement(World world, int x, int y, int z, EnumFacing face, StructureTemplate template, StructureBB bb) {
		int minY = y - maxWaterDepth;
		int maxY = y - minWaterDepth;
		return validateBorderBlocks(world, bb, minY, maxY, true);
	}

	@Override
	public void preGeneration(World world, BlockPos pos, EnumFacing face, StructureTemplate template, StructureBB bb) {
		for (int bx = bb.min.getX(); bx <= bb.max.getX(); bx++) {
			for (int bz = bb.min.getZ(); bz <= bb.max.getZ(); bz++) {
				for (int by = bb.min.getY() - 1; by > 0; by--) {
					BlockPos currentPos = new BlockPos(bx, by, bz);
					if (AWStructureStatics.isValidTargetBlock(world.getBlockState(currentPos))) {
						break;
					} else {
						world.setBlockState(currentPos, Blocks.DIRT.getDefaultState());
					}
				}
			}
		}
	}

	@Override
	public void handleClearAction(World world, BlockPos pos, StructureTemplate template, StructureBB bb) {
		int maxWaterY = bb.min.getY() + template.yOffset - 1;
		if (pos.getY() <= maxWaterY) {
			world.setBlockState(pos, Blocks.WATER.getDefaultState());
		} else {
			super.handleClearAction(world, pos, template, bb);
		}
	}

}
