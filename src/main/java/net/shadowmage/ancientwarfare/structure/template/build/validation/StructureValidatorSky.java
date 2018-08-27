package net.shadowmage.ancientwarfare.structure.template.build.validation;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.StringTools;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

public class StructureValidatorSky extends StructureValidator {
	private int minGenerationHeight;
	private int maxGenerationHeight;
	private int minFlyingHeight;

	public StructureValidatorSky() {
		super(StructureValidationType.SKY);
	}

	@Override
	protected void readFromLines(List<String> lines) {
		for (String line : lines) {
			if (startLow(line, "mingenerationheight=")) {
				minGenerationHeight = StringTools.safeParseInt("=", line);
			} else if (startLow(line, "maxgenerationheight=")) {
				maxGenerationHeight = StringTools.safeParseInt("=", line);
			} else if (startLow(line, "minflyingheight=")) {
				minFlyingHeight = StringTools.safeParseInt("=", line);
			}
		}
	}

	@Override
	protected void write(BufferedWriter out) throws IOException {
		out.write("minGenerationHeight=" + minGenerationHeight);
		out.newLine();
		out.write("maxGenerationHeight=" + maxGenerationHeight);
		out.newLine();
		out.write("minFlyingHeight=" + minFlyingHeight);
		out.newLine();
	}

	@Override
	protected void setDefaultSettings(StructureTemplate template) {
		//noop
	}

	@Override
	public boolean shouldIncludeForSelection(World world, int x, int y, int z, EnumFacing face, StructureTemplate template) {
		int remainingHeight = world.getActualHeight() - minFlyingHeight - (template.ySize - template.yOffset);
		return y < remainingHeight;
	}

	@Override
	public int getAdjustedSpawnY(World world, int x, int y, int z, EnumFacing face, StructureTemplate template, StructureBB bb) {
		int range = maxGenerationHeight - minGenerationHeight + 1;
		return y + minFlyingHeight + world.rand.nextInt(range);
	}

	@Override
	public boolean validatePlacement(World world, int x, int y, int z, EnumFacing face, StructureTemplate template, StructureBB bb) {
		int maxY = minGenerationHeight - minFlyingHeight;
		return validateBorderBlocks(world, bb, 0, maxY, false);
	}

	@Override
	public void preGeneration(World world, BlockPos pos, EnumFacing face, StructureTemplate template, StructureBB bb) {
		//noop
	}
}
