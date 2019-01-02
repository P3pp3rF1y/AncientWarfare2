package net.shadowmage.ancientwarfare.structure.town;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.Trig;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateManager;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBuilder;
import net.shadowmage.ancientwarfare.structure.town.TownTemplate.TownStructureEntry;
import net.shadowmage.ancientwarfare.structure.worldgen.WorldGenTickHandler;
import net.shadowmage.ancientwarfare.structure.worldgen.WorldGenTickHandler.StructureGenerationCallbackTicket;
import net.shadowmage.ancientwarfare.structure.worldgen.WorldStructureGenerator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class TownGeneratorStructures {

	public static void generateStructures(final TownGenerator gen) {
		final List<TownPartBlock> blocks = new ArrayList<>();
		for (TownPartQuadrant tq : gen.quadrants) {
			tq.addBlocks(blocks);
		}
		blocks.sort(new TownPartBlockComparator());
		generateUniques(blocks, gen.uniqueTemplatesToGenerate, gen);
		generateMains(blocks, gen.mainTemplatesToGenerate, gen);
		generateHouses(blocks, gen.houseTemplatesToGenerate, gen);
		generateCosmetics(blocks, gen.cosmeticTemplatesToGenerate, gen);

		if (gen.template.getExteriorSize() > 0) {
			blocks.clear();
			for (TownPartQuadrant tq : gen.externalQuadrants) {
				tq.addBlocks(blocks);
			}
			generateExteriorStructures(blocks, gen.exteriorTemplatesToGenerate, gen);
		}

		WorldGenTickHandler.INSTANCE.addStructureGenCallback(new StructureGenerationCallbackTicket() {
			@Override
			public void call() {
				TownGeneratorStructures.generateLamps(blocks, gen.template.getLamp(), gen);
				WorldStructureGenerator.sprinkleSnow(gen.world, gen.maximalBounds, 0);
				gen.generateVillagers();
			}
		});
	}

	private static void generateUniques(List<TownPartBlock> blocks, List<StructureTemplate> templatesToGenerate, TownGenerator gen) {
		outer:
		for (TownPartBlock block : blocks) {
			for (TownPartPlot plot : block.plots)//iterate through plots, gen on the first valid plot for the block, then break to the next block
			{
				if (plot.closed) {
					continue;
				}
				if (!plot.hasRoadBorder()) {
					continue;
				}//no borders
				if (templatesToGenerate.isEmpty()) {
					break outer;
				}
				if (generateStructureForPlot(gen, plot, templatesToGenerate.get(0), false)) {
					templatesToGenerate.remove(0);
				}
			}
		}
	}

	private static void generateMains(List<TownPartBlock> blocks, List<StructureTemplate> templatesToGenerate, TownGenerator gen) {
		outer:
		for (TownPartBlock block : blocks) {
			for (TownPartPlot plot : block.plots)//iterate through plots, gen on the first valid plot for the block, then break to the next block
			{
				if (plot.closed) {
					continue;
				}
				if (!plot.hasRoadBorder()) {
					continue;
				}//no borders
				if (templatesToGenerate.isEmpty()) {
					break outer;
				}
				if (generateStructureForPlot(gen, plot, templatesToGenerate.get(0), false)) {
					templatesToGenerate.remove(0);
				}
			}
		}
	}

	private static void generateHouses(List<TownPartBlock> blocks, List<StructureTemplate> templatesToGenerate, TownGenerator gen) {
		outer:
		for (TownPartBlock block : blocks) {
			for (TownPartPlot plot : block.plots) {
				if (templatesToGenerate.isEmpty()) {
					break outer;
				}
				if (plot.closed || !plot.hasRoadBorder()) {
					continue;
				}
				if (gen.template.getInteriorEmtpyPlotChance() > 0) {
					if (gen.rng.nextInt(100) < gen.template.getInteriorEmtpyPlotChance()) {
						plot.skipped = true;//mark skipped, so it is skipped by cosmetic generation as well (do not close, allow expansion onto this plot).
						continue;
					}
				}
				generateStructureForPlot(gen, plot, getRandomTemplate(templatesToGenerate, gen.rng), false);
			}
		}
	}

	private static void generateCosmetics(List<TownPartBlock> blocks, List<StructureTemplate> templatesToGenerate, TownGenerator gen) {
		outer:
		for (TownPartBlock block : blocks) {
			for (TownPartPlot plot : block.plots) {
				if (plot.closed || plot.skipped) {
					continue;
				}
				if (templatesToGenerate.isEmpty()) {
					break outer;
				}
				generateStructureForPlot(gen, plot, getRandomTemplate(templatesToGenerate, gen.rng), true);
			}
		}
	}

	private static void generateExteriorStructures(List<TownPartBlock> blocks, List<StructureTemplate> templatesToGenerate, TownGenerator gen) {
		float l1 = gen.exteriorBounds.getXSize() / 2.f;
		float l2 = gen.exteriorBounds.getZSize() / 2.f;
		float maxDistance = Trig.getDistance(l1, 0, l2, 0, 0, 0);
		l1 = gen.wallsBounds.getXSize() / 2.f;
		l2 = gen.wallsBounds.getZSize() / 2.f;
		float minDistance = l1 < l2 ? l1 : l2;
		float minMaxDelta = maxDistance - minDistance;
		float plotDistance, distPercent;

		outer:
		for (TownPartBlock block : blocks) {
			for (TownPartPlot plot : block.plots) {
				if (plot.closed) {
					continue;
				}
				if (templatesToGenerate.isEmpty()) {
					break outer;
				}
				plotDistance = Trig.getDistance(plot.bb.getCenterX(), 0, plot.bb.getCenterZ(), gen.maximalBounds.getCenterX(), 0, gen.maximalBounds.getCenterZ()) - minDistance;
				distPercent = plotDistance / minMaxDelta;
				distPercent = 1.f - distPercent;
				distPercent *= distPercent;
				if (gen.rng.nextFloat() < distPercent) {
					generateStructureForPlot(gen, plot, getRandomTemplate(templatesToGenerate, gen.rng), true);
				}
			}
		}
	}

	private static void generateLamps(List<TownPartBlock> blocks, TownStructureEntry templateToGenerate, final TownGenerator gen) {
		if (templateToGenerate == null) {
			return;
		}
		StructureTemplateManager.getTemplate(templateToGenerate.templateName).ifPresent(lamp -> {
			for (TownPartBlock block : blocks) {
				generateLamps(gen.world, block, lamp, gen.structureDoors);
			}
		});
	}

	private static void generateLamps(World world, TownPartBlock block, StructureTemplate lamp, List<BlockPos> doors) {
		Direction xDir = block.quadrant.getXDir();
		Direction zDir = block.quadrant.getZDir();
		int xStart;
		int zStart;
		int xMove;
		int zMove;
		int size;
		int x;
		int z;
		int xBits;
		int zBits;

		size = 5;
		xBits = (block.bb.getXSize() - 1) / size;
		zBits = (block.bb.getZSize() - 1) / size;

		if (block.bb.getXSize() % size == size - 1) {
			xBits--;
		}//ensures two lamps are not adjacent near the corner of the road
		if (block.bb.getZSize() % size == size - 1) {
			zBits--;
		}//ensures two lamps are not adjacent near the corner of the road

		if (xDir == Direction.WEST) {
			xStart = block.bb.max.getX();
			xMove = -size;
		} else {
			xStart = block.bb.min.getX();
			xMove = size;
		}

		if (zDir == Direction.NORTH) {
			zStart = block.bb.max.getZ();
			zMove = -size;
		} else {
			zStart = block.bb.min.getZ();
			zMove = size;
		}

		if (block.hasRoadBorder(Direction.NORTH)) {
			for (int xBit = 0; xBit <= xBits; xBit++) {
				x = xBit * xMove + xStart;
				generateLamp(world, lamp, doors, x, block.bb.min.getY(), block.bb.min.getZ(), Direction.EAST);
			}
		}

		if (block.hasRoadBorder(Direction.SOUTH)) {
			for (int xBit = 0; xBit <= xBits; xBit++) {
				x = xBit * xMove + xStart;
				generateLamp(world, lamp, doors, x, block.bb.min.getY(), block.bb.max.getZ(), Direction.EAST);
			}
		}

		if (block.hasRoadBorder(Direction.WEST)) {
			for (int zBit = 0; zBit <= zBits; zBit++) {
				z = zBit * zMove + zStart;
				generateLamp(world, lamp, doors, block.bb.min.getX(), block.bb.min.getY(), z, Direction.EAST);
			}
		}

		if (block.hasRoadBorder(Direction.EAST)) {
			for (int zBit = 0; zBit <= zBits; zBit++) {
				z = zBit * zMove + zStart;
				generateLamp(world, lamp, doors, block.bb.max.getX(), block.bb.min.getY(), z, Direction.EAST);
			}
		}
	}

	private static void generateLamp(World world, StructureTemplate t, List<BlockPos> doors, int x, int y, int z, Direction streetSide) {
		if (checkForNeighboringDoor(doors, x, z, streetSide.getOpposite())) {
			return;
		}
		int minX = x;
		int minZ = z;
		int maxX = x + t.getSize().getX() - 1;
		int maxY = y + (t.getSize().getY() - 1 - t.getOffset().getY());
		int maxZ = z + t.getSize().getZ() - 1;

		for (int x1 = minX; x1 <= maxX; x1++) {
			for (int z1 = minZ; z1 <= maxZ; z1++) {
				for (int y1 = y; y1 <= maxY; y1++) {
					if (!world.isAirBlock(new BlockPos(x1, y1, z1))) {
						return;
					}//skip construction if it would overwrite any non-valid block (should ALL be air)
				}
			}
		}
		x -= (t.getSize().getX() / 2);
		z -= (t.getSize().getZ() / 2);
		x += t.getOffset().getX();
		z += t.getOffset().getZ();
		WorldGenTickHandler.INSTANCE.addStructureForGeneration(new StructureBuilder(world, t, EnumFacing.SOUTH, new BlockPos(x, y, z)));
	}

	private static boolean checkForNeighboringDoor(List<BlockPos> doors, int x, int z, Direction dir) {
		int x1 = x + dir.xDirection;
		int z1 = z + dir.zDirection;
		for (BlockPos p : doors) {
			if (p.getX() == x && p.getZ() == z) {
				return true;
			} else if (p.getX() == x1 && p.getZ() == z1) {
				return true;
			}
		}
		return false;
	}

	//************************************************ UTILITY METHODS *******************************************************//

	/*
	 * attempt to generate a structure at the given plot
	 *
	 * @return true if generated
	 */
	private static boolean generateStructureForPlot(TownGenerator gen, TownPartPlot plot, StructureTemplate template, boolean centerLength) {
		int expansion = gen.template.getTownBuildingWidthExpansion();
		EnumFacing face = EnumFacing.HORIZONTALS[gen.rng.nextInt(4)];//select random face
		for (int i = 0; i < 4; i++) {//and then iterate until a valid face is found
			face = face.rotateY();
			if (plot.roadBorders[face.getHorizontalIndex()]) {
				break;
			}
		}
		face = face.getOpposite();//reverse face from road edge...
		int width = face.getAxis() == EnumFacing.Axis.Z ? template.getSize().getX() : template.getSize().getZ();
		int length = face.getAxis() == EnumFacing.Axis.Z ? template.getSize().getZ() : template.getSize().getX();
		if (face == EnumFacing.SOUTH || face == EnumFacing.NORTH) {
			width += expansion;
		}//temporarily expand the size of the bb by the town-template building expansion size, ensures there is room around buildings
		else {
			length += expansion;
		}
		if (plot.getWidth() < width || plot.getLength() < length) {
			if (!plot.expand(width, length)) {
				return false;
			}
		}
		plot.markClosed();
		if (face == EnumFacing.SOUTH || face == EnumFacing.NORTH) {
			width -= expansion;
		} else {
			length -= expansion;
		}
		generateStructure(gen, plot, template, face, width, length, centerLength);
		return true;
	}

	/*
	 * @param gen    the town generator being used
	 * @param plot     the pre-expanded plot that will have the structure generated on it
	 * @param template the template to be generated
	 * @param face     generation orientation for the structure
	 * @param width    rotated structure x-dimension
	 * @param length   rotated structure z-dimension
	 * @param center   should the structure be centered in plot, or placed along road-edge?
	 */
	private static void generateStructure(TownGenerator gen, TownPartPlot plot, StructureTemplate template, EnumFacing face, int width, int length, boolean center) {
		int plotWidth = plot.getWidth();
		int plotLength = plot.getLength();
		int extraWidth = plotWidth - width;//unused width portion of the plot
		int extraLength = plotLength - length;//unused length portion of the plot

		int wAdj;
		int lAdj;

		if (center) {
			wAdj = extraWidth / 2;
			lAdj = extraLength / 2;
		} else {
			wAdj = face.getAxis() == EnumFacing.Axis.Z ? extraWidth / 2 : face == EnumFacing.WEST ? extraWidth : 0;
			lAdj = face.getAxis() == EnumFacing.Axis.X ? extraLength / 2 : face == EnumFacing.NORTH ? extraLength : 0;
		}

		//find corners of the bb for the structure
		BlockPos min = new BlockPos(plot.bb.min.getX() + wAdj, gen.townBounds.min.getY(), plot.bb.min.getZ() + lAdj);
		BlockPos max = new BlockPos(min.getX() + (width - 1), min.getY() + template.getSize().getY(), min.getZ() + (length - 1));
		StructureBB bb = new StructureBB(min, max);

		BlockPos buildKey = bb.getRLCorner(face, BlockPos.ORIGIN).offset(face.rotateY(), template.getOffset().getX()).offset(face.getOpposite(), template.getOffset().getZ()).up(gen.townBounds.min.getY() - template.getOffset().getY());
		bb.add(0, -template.getOffset().getY(), 0);
		gen.structureDoors.add(buildKey);
		WorldGenTickHandler.INSTANCE.addStructureForGeneration(new StructureBuilder(gen.world, template, face, buildKey, bb));
	}

	/*
	 * pull a random template from the input generation list, does not remove
	 */
	private static StructureTemplate getRandomTemplate(List<StructureTemplate> templatesToGenerate, Random rng) {
		if (templatesToGenerate.isEmpty()) {
			return null;
		}
		int roll = rng.nextInt(templatesToGenerate.size());
		return templatesToGenerate.get(roll);
	}

	public static class TownPartBlockComparator implements Comparator<TownPartBlock> {
		@Override
		public int compare(TownPartBlock o1, TownPartBlock o2) {
			if (o1.distFromTownCenter < o2.distFromTownCenter) {
				return -1;
			} else if (o1.distFromTownCenter > o2.distFromTownCenter) {
				return 1;
			}
			return 0;
		}
	}
}
