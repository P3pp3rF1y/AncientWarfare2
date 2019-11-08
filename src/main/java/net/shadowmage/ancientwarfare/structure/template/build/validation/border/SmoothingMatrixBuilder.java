package net.shadowmage.ancientwarfare.structure.template.build.validation.border;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.structure.api.TemplateRuleBlock;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;
import net.shadowmage.ancientwarfare.structure.template.build.validation.border.points.BorderPoint;
import net.shadowmage.ancientwarfare.structure.template.build.validation.border.points.PointType;
import net.shadowmage.ancientwarfare.structure.template.build.validation.border.points.SmoothingPoint;
import net.shadowmage.ancientwarfare.structure.worldgen.WorldStructureGenerator;

import java.util.Optional;
import java.util.Set;
import java.util.function.IntBinaryOperator;

public class SmoothingMatrixBuilder {
	private final World world;
	private StructureBB bb;
	private final int borderSize;
	private final int turns;
	private final int groundY;
	private StructureTemplate template;
	private final SmoothingMatrix smoothingMatrix;

	public SmoothingMatrixBuilder(World world, StructureBB bb, int borderSize, EnumFacing face, StructureTemplate template) {
		this.world = world;
		this.bb = bb;
		this.borderSize = borderSize;
		this.turns = (face.getHorizontalIndex() + 2) % 4;
		this.groundY = bb.min.getY() + template.getOffset().getY() - 1;
		this.template = template;

		BorderMatrix borderMatrix = BorderMatrixCache.getBorderMatrix(bb.getXSize(), bb.getZSize(), borderSize);
		smoothingMatrix = new SmoothingMatrix(borderMatrix, bb.min, borderSize);

		convertPointsToSmoothingMatrix(this.groundY, borderMatrix, PointType.STRUCTURE_BORDER);
		convertPointsToSmoothingMatrix(0, borderMatrix, PointType.REFERENCE_POINT);
		convertPointsToSmoothingMatrix(0, borderMatrix, PointType.OUTER_BORDER);
		convertPointsToSmoothingMatrix(0, borderMatrix, PointType.SMOOTHED_BORDER);
	}

	private void convertPointsToSmoothingMatrix(int groundY, BorderMatrix borderMatrix, PointType type) {
		for (BorderPoint point : borderMatrix.getPointsOfType(type)) {
			SmoothingPoint smoothingPoint;
			if (type == PointType.SMOOTHED_BORDER) {
				smoothingPoint = addPoint(point.getX(), point.getZ(), PointType.SMOOTHED_BORDER,
						(x, z) -> getYBelowFloatingIsland(getMinPos().getX() + x, BlockTools.getTopFilledHeight(world, getMinPos().getX() + x, getMinPos().getZ() + z, false), getMinPos().getZ() + z, false));
				BorderPoint outerBorder = point.getOuterBorderPoint();
				BorderPoint ref = point.getReferencePoint();
				Optional<SmoothingPoint> outerBorderPoint = smoothingMatrix.getPoint(outerBorder.getX(), outerBorder.getZ());
				Optional<SmoothingPoint> refPoint = smoothingMatrix.getPoint(ref.getX(), ref.getZ());
				if (outerBorderPoint.isPresent() && refPoint.isPresent()) {
					smoothingPoint.setOuterBorderAndReferencePoint(outerBorderPoint.get(), refPoint.get());
				}
			} else {
				smoothingPoint = addPoint(point.getX(), point.getZ(), point.getType(), groundY);
			}
			if (type == PointType.OUTER_BORDER || type == PointType.SMOOTHED_BORDER) {
				BorderPoint borderPoint = point.getClosestBorderPoint();
				smoothingMatrix.getPoint(borderPoint.getX(), borderPoint.getZ()).ifPresent(p -> smoothingPoint.setStructureBorder(p, point.getStructureBorderDistance()));
			}
		}
	}

	public SmoothingMatrix build() {
		calculateNewYLevelsAndBlocks();
		processSmoothing();

		return smoothingMatrix;
	}

	private void processSmoothing() {
		for (SmoothingPoint point : smoothingMatrix.getPointsOfType(PointType.SMOOTHED_BORDER)) {
			int totalY = point.getSmoothedPos().getY();
			BlockPos currentPos = point.getSmoothedPos();
			HorizontalCoords current = new HorizontalCoords(point.getX(), point.getZ());
			for (HorizontalCoords adjacent : HorizontalCoords.ADJACENT_OFFSETS) {
				Optional<SmoothingPoint> adjacentPoint = smoothingMatrix.getPoint(current.add(adjacent));
				if (adjacentPoint.isPresent()) {
					totalY += adjacentPoint.get().getSmoothedPos().getY();
				}
			}
			int averageY = Math.round(totalY / 5f);
			if (currentPos.getY() != averageY) {
				point.setSmoothedPos(new BlockPos(currentPos.getX(), averageY, currentPos.getZ()));
			}
		}
	}

	private void calculateNewYLevelsAndBlocks() {
		for (SmoothingPoint pointToSmooth : smoothingMatrix.getPointsOfType(PointType.SMOOTHED_BORDER)) {
			SmoothingPoint referencePoint = pointToSmooth.getReferencePoint();
			SmoothingPoint outerBorderPoint = pointToSmooth.getOuterBorderPoint();
			double outerDistToStructure = outerBorderPoint.getStructureBorderDistance();
			int structureBorderY = outerBorderPoint.getClosestBorderPoint().getWorldPos().getY();
			int yDiff = outerBorderPoint.getWorldPos().getY() - structureBorderY;
			boolean steepFormula = yDiff >= 0 ? referencePoint.getWorldPos().getY() - outerBorderPoint.getWorldPos().getY() > 2 : referencePoint.getWorldPos().getY() - outerBorderPoint.getWorldPos().getY() < -2;
			double pointDist = pointToSmooth.getStructureBorderDistance();
			if (steepFormula) {
				double ratio = pointDist / outerDistToStructure;
				setNewSmoothedY(pointToSmooth, structureBorderY + (int) Math.round(yDiff * ratio * ratio));
			} else {
				double halfDist = outerDistToStructure / 2;
				int halfYDiff = yDiff / 2;
				if (pointDist <= halfDist) {
					double ratio = pointDist / halfDist;
					setNewSmoothedY(pointToSmooth, structureBorderY + (int) Math.round(halfYDiff * ratio * ratio));
				} else {
					double ratio = (outerDistToStructure - pointDist) / halfDist;
					setNewSmoothedY(pointToSmooth, outerBorderPoint.getWorldPos().getY() - (int) Math.round(halfYDiff * ratio * ratio));
				}
			}
			setBlockStateForPoint(outerBorderPoint, outerDistToStructure, yDiff, pointToSmooth, pointDist);
		}
	}

	private void setBlockStateForPoint(SmoothingPoint outerBorderPoint, double outerDistToStructure, int yDiff, SmoothingPoint pointToSmooth, double pointDist) {
		if (!pointToSmooth.getClosestBorderPoint().useStateForBlending()) {
			pointToSmooth.setBlockState(outerBorderPoint.getBlockState());
			return;
		}

		if (Math.round(yDiff / outerDistToStructure) < 2) {
			pointToSmooth.setBlockState(
					world.rand.nextDouble() > pointDist / outerDistToStructure ? pointToSmooth.getClosestBorderPoint().getBlockState() : outerBorderPoint.getBlockState());
		} else {
			pointToSmooth.setBlockState(world.getBiome(pointToSmooth.getWorldPos()).topBlock);
		}
	}

	private void setNewSmoothedY(SmoothingPoint pointToSmooth, int newY) {
		pointToSmooth.setSmoothedPos(new BlockPos(pointToSmooth.getWorldPos().getX(), newY, pointToSmooth.getWorldPos().getZ()));
	}

	private SmoothingPoint addPoint(int x, int z, PointType type, int yLevel) {
		return addPoint(x, z, type, (xCoord, zCoord) -> getY(xCoord, zCoord, yLevel));
	}

	private SmoothingPoint addPoint(int x, int z, PointType type, IntBinaryOperator getY) {
		BlockPos pos = new BlockPos(getMinPos().getX() + x, getY.applyAsInt(x, z), getMinPos().getZ() + z);
		Optional<IBlockState> state = getPointBlockState(pos, type);
		return state.map(iBlockState -> smoothingMatrix.addPoint(x, z, pos, type, iBlockState)).orElseGet(() -> smoothingMatrix.addPoint(x, z, pos, type));
	}

	private int getY(int x, int z, int yLevel) {
		int y = yLevel;
		if (yLevel == 0) {
			y = WorldStructureGenerator.getTargetY(world, getMinPos().getX() + x, getMinPos().getZ() + z, true);
			y = getYBelowFloatingIsland(getMinPos().getX() + x, y, getMinPos().getZ() + z);
		}
		return y;
	}

	private BlockPos getMinPos() {
		return smoothingMatrix.getMinPos();
	}

	private static final Set<Block> STRUCTURE_BORDER_BLOCK_WHITELIST = ImmutableSet.of(
			Blocks.STONE,
			Blocks.GRAVEL,
			Blocks.DIRT,
			Blocks.GRASS,
			Blocks.SAND,
			Blocks.SANDSTONE,
			Blocks.COBBLESTONE,
			Blocks.MOSSY_COBBLESTONE,
			Blocks.OBSIDIAN,
			Blocks.SNOW,
			Blocks.NETHERRACK,
			Blocks.SOUL_SAND,
			Blocks.MYCELIUM,
			Blocks.END_STONE,
			Blocks.HARDENED_CLAY,
			Blocks.CLAY,
			Blocks.GRASS_PATH,
			Blocks.ICE,
			Blocks.PACKED_ICE,
			Blocks.WATER,
			Blocks.LAVA,
			Blocks.RED_SANDSTONE,
			Blocks.STAINED_HARDENED_CLAY
	);

	private Optional<IBlockState> getPointBlockState(BlockPos pos, PointType type) {
		IBlockState state = type == PointType.STRUCTURE_BORDER ? getStateFromTemplate(pos) : world.getBlockState(pos);
		if (type == PointType.STRUCTURE_BORDER && !STRUCTURE_BORDER_BLOCK_WHITELIST.contains(state.getBlock())) {
			return Optional.empty();
		}
		return Optional.of(state);
	}

	private IBlockState getStateFromTemplate(BlockPos pos) {
		Optional<TemplateRuleBlock> rule = template.getRuleAt(BlockTools.rotateInArea(pos.add(-bb.min.getX(), -bb.min.getY(), -bb.min.getZ()), template.getSize().getX(), template.getSize().getZ(), -turns));
		return rule.map(r -> r.getState(turns)).orElse(Blocks.DIRT.getDefaultState());
	}

	private int getYBelowFloatingIsland(int x, int y, int z) {
		return getYBelowFloatingIsland(x, y, z, true);
	}

	private int getYBelowFloatingIsland(int x, int y, int z, boolean useSkippables) {
		if (y - groundY > borderSize) {
			//try restarting search in between groundY and found Y
			int startAtY = groundY + ((y - groundY) / 2);
			int newY = useSkippables ? WorldStructureGenerator.getTargetY(world, x, z, true, startAtY) :
					BlockTools.getTopFilledHeight(world, x, z, false, startAtY);
			if (newY != startAtY) {
				return newY;
			}
		}
		return y;
	}
}
